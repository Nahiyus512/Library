import { onMounted, onUnmounted } from 'vue'
import myAxios from '@/api/index'

type FeatureEventType = 'enter' | 'heartbeat' | 'checkpoint' | 'rate' | 'exit' | 'recommend_accept'

interface TrackerOptions {
  userId?: number
  bookId?: number
  bookName: string
  routePath: string
  source?: string
  traceId?: string
  sessionId?: string
  heartbeatEnabled?: boolean
  heartbeatIntervalMs?: number
}

interface FeatureEventPayload {
  eventId: string
  userId: number
  sessionId: string
  traceId?: string
  bookId?: number
  bookName: string
  routePath: string
  source: string
  eventType: FeatureEventType
  checkpointKey?: string
  likeLevel?: number
  durationMs?: number
  scrollDepth?: number
  createdAt: string
}

interface BatchReportResponse {
  code?: number
}

const STORAGE_KEY = 'feature_page_event_queue_v1'
const FLUSH_SIZE = 5
const MAX_BATCH_SIZE = 50
const FLUSH_INTERVAL_MS = 2000
const DEFAULT_HEARTBEAT_INTERVAL_MS = 15000

const safeJsonParse = <T>(raw: string | null, fallback: T): T => {
  if (!raw) return fallback
  try {
    return JSON.parse(raw) as T
  } catch {
    return fallback
  }
}

const randomId = (): string => {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `evt_${Date.now()}_${Math.random().toString(36).slice(2, 10)}`
}

const buildSessionId = (): string => `fp_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`

const getScrollDepth = (): number => {
  const doc = document.documentElement
  const body = document.body
  const scrollTop = window.scrollY || doc.scrollTop || body.scrollTop || 0
  const scrollHeight = Math.max(doc.scrollHeight, body.scrollHeight, 1)
  const clientHeight = Math.max(window.innerHeight || 0, doc.clientHeight || 0, 1)
  if (scrollHeight <= clientHeight) return 1
  const depth = (scrollTop + clientHeight) / scrollHeight
  return Math.min(1, Math.max(0, depth))
}

export function useFeatureEventTracker(options: TrackerOptions) {
  const startTime = Date.now()
  const sessionId = options.sessionId || buildSessionId()
  const source = options.source || 'direct'
  const heartbeatEnabled = options.heartbeatEnabled ?? true
  const heartbeatIntervalMs = options.heartbeatIntervalMs || DEFAULT_HEARTBEAT_INTERVAL_MS
  let heartbeatTimer: number | null = null
  let flushTimer: number | null = null
  let exited = false

  const resolveUserId = (): number => {
    if (options.userId) return Number(options.userId)
    const raw = sessionStorage.getItem('userId') || localStorage.getItem('userId') || ''
    const parsed = Number(raw)
    return Number.isFinite(parsed) && parsed > 0 ? parsed : 0
  }

  const readQueue = (): FeatureEventPayload[] => safeJsonParse<FeatureEventPayload[]>(sessionStorage.getItem(STORAGE_KEY), [])
  const writeQueue = (queue: FeatureEventPayload[]) => sessionStorage.setItem(STORAGE_KEY, JSON.stringify(queue))

  const enqueue = (event: FeatureEventPayload) => {
    const queue = readQueue()
    queue.push(event)
    writeQueue(queue)
  }

  const buildEvent = (eventType: FeatureEventType, patch: Partial<FeatureEventPayload> = {}): FeatureEventPayload | null => {
    const userId = resolveUserId()
    if (!userId) return null

    return {
      eventId: randomId(),
      userId,
      sessionId,
      traceId: options.traceId,
      bookId: options.bookId,
      bookName: options.bookName,
      routePath: options.routePath,
      source,
      eventType,
      createdAt: new Date().toISOString(),
      ...patch
    }
  }

  const flushByBeacon = () => {
    if (typeof navigator === 'undefined' || typeof navigator.sendBeacon !== 'function') return
    const queue = readQueue()
    if (queue.length === 0) return

    const chunk = queue.slice(0, MAX_BATCH_SIZE)
    const baseURL = String(myAxios.defaults.baseURL || '').replace(/\/$/, '')
    const url = `${baseURL}/feature/events/batch`
    const body = JSON.stringify({ events: chunk })
    const ok = navigator.sendBeacon(url, new Blob([body], { type: 'application/json' }))
    if (ok) {
      writeQueue(queue.slice(chunk.length))
    }
  }

  const flush = async () => {
    const queue = readQueue()
    if (queue.length === 0) return

    const chunk = queue.slice(0, MAX_BATCH_SIZE)
    try {
      const res = await myAxios.post<BatchReportResponse>('/feature/events/batch', { events: chunk })
      if (res.data?.code === 200) {
        writeQueue(queue.slice(chunk.length))
      }
    } catch {
      // Keep queue for retry.
    }
  }

  const trackEnter = () => {
    const payload = buildEvent('enter')
    if (!payload) return
    enqueue(payload)
  }

  const trackHeartbeat = () => {
    const payload = buildEvent('heartbeat')
    if (!payload) return
    enqueue(payload)
  }

  const trackCheckpoint = (checkpointKey: string) => {
    const payload = buildEvent('checkpoint', { checkpointKey })
    if (!payload) return
    enqueue(payload)
  }

  const trackRate = (likeLevel: number) => {
    const payload = buildEvent('rate', { likeLevel })
    if (!payload) return
    enqueue(payload)
  }

  const trackRecommendAccept = () => {
    const payload = buildEvent('recommend_accept')
    if (!payload) return
    enqueue(payload)
  }

  const trackExit = () => {
    if (exited) return
    exited = true
    const payload = buildEvent('exit', {
      durationMs: Date.now() - startTime,
      scrollDepth: getScrollDepth()
    })
    if (!payload) return
    enqueue(payload)
  }

  const onVisibilityChange = () => {
    if (document.visibilityState === 'hidden') {
      trackExit()
      flushByBeacon()
    }
  }

  const stop = () => {
    if (heartbeatTimer !== null) {
      window.clearInterval(heartbeatTimer)
      heartbeatTimer = null
    }
    if (flushTimer !== null) {
      window.clearInterval(flushTimer)
      flushTimer = null
    }
    window.removeEventListener('pagehide', flushByBeacon)
    window.removeEventListener('beforeunload', flushByBeacon)
    document.removeEventListener('visibilitychange', onVisibilityChange)
  }

  onMounted(() => {
    trackEnter()

    if (heartbeatEnabled) {
      heartbeatTimer = window.setInterval(() => {
        trackHeartbeat()
      }, heartbeatIntervalMs)
    }

    flushTimer = window.setInterval(() => {
      const queue = readQueue()
      if (queue.length >= FLUSH_SIZE) {
        void flush()
      }
    }, FLUSH_INTERVAL_MS)

    window.addEventListener('pagehide', flushByBeacon)
    window.addEventListener('beforeunload', flushByBeacon)
    document.addEventListener('visibilitychange', onVisibilityChange)

    void flush()
  })

  onUnmounted(() => {
    trackExit()
    stop()
    void flush()
  })

  return {
    sessionId,
    trackEnter,
    trackHeartbeat,
    trackCheckpoint,
    trackRate,
    trackExit,
    trackRecommendAccept,
    flush,
    stop
  }
}

