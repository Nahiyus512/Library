import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import type { Router, RouteLocationNormalizedLoaded } from 'vue-router'

type FeatureEventType = 'enter' | 'heartbeat' | 'checkpoint' | 'rate' | 'exit' | 'recommend_accept'

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

interface FeatureContext {
  routePath: string
  bookName: string
  source: string
  traceId?: string
  sessionId: string
  enteredAt: number
}

const FEATURE_ROUTE_BOOK: Record<string, string> = {
  '/book/AmusingOurselvesToDeath': '娱乐至死',
  '/book/BraveNewWorld': '美丽新世界',
  '/book/Dune': '沙丘',
  '/book/GridSystems': '平面设计中的网格系统',
  '/book/HitchhikersGuide': '银河系漫游指南',
  '/book/InteractionOfColor': '色彩互动学',
  '/book/JourneyToTheWest': '西游记',
  '/book/Life30': '生命3.0',
  '/book/NineteenEightyFour': '1984',
  '/book/NonDesignersDesignBook': '写给大家看的设计书',
  '/book/Sapiens': '人类简史',
  '/book/ThreeBody': '三体',
  '/book/ThreeKingdoms': '三国演义',
  '/book/Zen': '禅与摩托车维修艺术'
}

const STORAGE_KEY = 'feature_page_event_queue_v1'
const FLUSH_INTERVAL_MS = 2000
const FLUSH_SIZE = 5
const MAX_BATCH_SIZE = 50
const HEARTBEAT_INTERVAL_MS = 15000

const randomId = (): string => {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `evt_${Date.now()}_${Math.random().toString(36).slice(2, 10)}`
}

const createSessionId = (): string => `fp_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`

const readQueue = (): FeatureEventPayload[] => {
  const raw = sessionStorage.getItem(STORAGE_KEY)
  if (!raw) return []
  try {
    return JSON.parse(raw) as FeatureEventPayload[]
  } catch {
    return []
  }
}

const writeQueue = (queue: FeatureEventPayload[]) => {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(queue))
}

const parseUserIdFromCookie = (): number => {
  const raw = document.cookie.match(/(?:^|;\s*)userId=([^;]+)/)?.[1]
  if (!raw) return 0
  const parsed = Number(decodeURIComponent(raw))
  return Number.isFinite(parsed) && parsed > 0 ? parsed : 0
}

const parseRequestData = (config: InternalAxiosRequestConfig): Record<string, unknown> => {
  const data = config.data
  if (!data) return {}
  if (typeof data === 'string') {
    try {
      return JSON.parse(data) as Record<string, unknown>
    } catch {
      return {}
    }
  }
  if (typeof data === 'object') {
    return data as Record<string, unknown>
  }
  return {}
}

const getScrollDepth = (): number => {
  const doc = document.documentElement
  const body = document.body
  const scrollTop = window.scrollY || doc.scrollTop || body.scrollTop || 0
  const scrollHeight = Math.max(doc.scrollHeight, body.scrollHeight, 1)
  const clientHeight = Math.max(window.innerHeight || 0, doc.clientHeight || 0, 1)
  if (scrollHeight <= clientHeight) return 1
  const depth = (scrollTop + clientHeight) / scrollHeight
  return Math.max(0, Math.min(1, depth))
}

const buildContext = (route: RouteLocationNormalizedLoaded): FeatureContext | null => {
  const bookName = FEATURE_ROUTE_BOOK[route.path]
  if (!bookName) return null
  const source = typeof route.query.source === 'string' ? route.query.source : 'direct'
  const traceId = typeof route.query.traceId === 'string' ? route.query.traceId : undefined
  return {
    routePath: route.path,
    bookName,
    source,
    traceId,
    sessionId: createSessionId(),
    enteredAt: Date.now()
  }
}

export const setupFeatureEventTracking = (router: Router, axios: AxiosInstance) => {
  let currentContext: FeatureContext | null = null
  let heartbeatTimer: number | null = null
  let flushTimer: number | null = null
  let pageExited = false

  const enqueue = (payload: FeatureEventPayload) => {
    const queue = readQueue()
    queue.push(payload)
    writeQueue(queue)
  }

  const buildEvent = (
    context: FeatureContext,
    eventType: FeatureEventType,
    patch: Partial<FeatureEventPayload> = {},
    overrideUserId?: number
  ): FeatureEventPayload | null => {
    const userId = overrideUserId || parseUserIdFromCookie()
    if (!userId) return null
    return {
      eventId: randomId(),
      userId,
      sessionId: context.sessionId,
      traceId: context.traceId,
      bookName: context.bookName,
      routePath: context.routePath,
      source: context.source,
      eventType,
      createdAt: new Date().toISOString(),
      ...patch
    }
  }

  const flush = async () => {
    const queue = readQueue()
    if (queue.length === 0) return
    const chunk = queue.slice(0, MAX_BATCH_SIZE)
    try {
      const res = await axios.post('/feature/events/batch', { events: chunk })
      if (res.data?.code === 200) {
        writeQueue(queue.slice(chunk.length))
      }
    } catch {
      // keep queue for retry
    }
  }

  const flushByBeacon = () => {
    if (typeof navigator.sendBeacon !== 'function') return
    const queue = readQueue()
    if (queue.length === 0) return
    const chunk = queue.slice(0, MAX_BATCH_SIZE)
    const baseURL = String(axios.defaults.baseURL || '').replace(/\/$/, '')
    const url = `${baseURL}/feature/events/batch`
    const ok = navigator.sendBeacon(url, new Blob([JSON.stringify({ events: chunk })], { type: 'application/json' }))
    if (ok) {
      writeQueue(queue.slice(chunk.length))
    }
  }

  const track = (
    context: FeatureContext,
    eventType: FeatureEventType,
    patch: Partial<FeatureEventPayload> = {},
    userId?: number
  ) => {
    const payload = buildEvent(context, eventType, patch, userId)
    if (!payload) return
    enqueue(payload)
  }

  const stopHeartbeat = () => {
    if (heartbeatTimer !== null) {
      window.clearInterval(heartbeatTimer)
      heartbeatTimer = null
    }
  }

  const startHeartbeat = () => {
    stopHeartbeat()
    heartbeatTimer = window.setInterval(() => {
      if (!currentContext) return
      track(currentContext, 'heartbeat')
    }, HEARTBEAT_INTERVAL_MS)
  }

  const trackExit = (context: FeatureContext) => {
    track(context, 'exit', {
      durationMs: Date.now() - context.enteredAt,
      scrollDepth: getScrollDepth()
    })
  }

  router.afterEach((to) => {
    const nextContext = buildContext(to as RouteLocationNormalizedLoaded)
    const prevContext = currentContext

    if (prevContext && (!nextContext || nextContext.routePath !== prevContext.routePath)) {
      trackExit(prevContext)
      currentContext = null
      stopHeartbeat()
    }

    if (nextContext && (!prevContext || nextContext.routePath !== prevContext.routePath)) {
      pageExited = false
      currentContext = nextContext
      track(nextContext, 'enter')
      startHeartbeat()
    }

    if (nextContext && prevContext && nextContext.routePath === prevContext.routePath) {
      currentContext = {
        ...prevContext,
        source: nextContext.source,
        traceId: nextContext.traceId
      }
    }
  })

  axios.interceptors.response.use(
    (response: AxiosResponse) => {
      const url = String(response.config.url || '')
      if (url.includes('/bookLike/like') && currentContext && response.data?.code === 200) {
        const body = parseRequestData(response.config)
        const likeLevel = Number(body.likeLevel)
        const userId = Number(body.userId)
        if ([0, 1, 2].includes(likeLevel)) {
          track(currentContext, 'rate', { likeLevel }, Number.isFinite(userId) && userId > 0 ? userId : undefined)
        }
      }
      return response
    },
    (error) => Promise.reject(error)
  )

  flushTimer = window.setInterval(() => {
    if (readQueue().length >= FLUSH_SIZE) {
      void flush()
    }
  }, FLUSH_INTERVAL_MS)

  const onVisibilityChange = () => {
    if (document.visibilityState === 'hidden') {
      if (currentContext && !pageExited) {
        pageExited = true
        trackExit(currentContext)
      }
      flushByBeacon()
    }
  }

  const onPageHide = () => {
    if (currentContext && !pageExited) {
      pageExited = true
      trackExit(currentContext)
    }
    flushByBeacon()
  }

  document.addEventListener('visibilitychange', onVisibilityChange)
  window.addEventListener('pagehide', onPageHide)
  window.addEventListener('beforeunload', onPageHide)

  void flush()

  return () => {
    stopHeartbeat()
    if (flushTimer !== null) {
      window.clearInterval(flushTimer)
    }
    document.removeEventListener('visibilitychange', onVisibilityChange)
    window.removeEventListener('pagehide', onPageHide)
    window.removeEventListener('beforeunload', onPageHide)
  }
}

