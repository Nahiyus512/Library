<template>
  <div class="brave-vn-container" :class="sceneClass">
    <div class="custom-cursor"></div>
    
    <!-- Global HUD -->
    <div class="hud" v-if="currentScene > 0">
      <div class="hud-top">
        <div class="date-display">A.F. 632</div>
        <div class="stability-meter">
          <span>安定值</span>
          <div class="bar-container">
            <div class="bar-fill" :style="{ width: stability + '%' }"></div>
          </div>
        </div>
      </div>
      <div class="return-btn" @click="goBack">返回图书馆</div>
    </div>

    <!-- Transition Overlay -->
    <transition name="fade">
      <div class="chapter-transition" v-if="showTransition">
        <div class="transition-content">
          <h2>{{ transitionTitle }}</h2>
          <p>{{ transitionSubtitle }}</p>
        </div>
      </div>
    </transition>

    <!-- Scene Content -->
    <transition name="fade-morph" mode="out-in">

      <!-- Scene 0: Intro (Home - Cover Style) -->
      <div v-if="currentScene === 0" key="scene0" class="scene scene-intro" @click="startJourney">
        <div class="cover-content">
          <h1 class="title-en floating-title">BRAVE<br>NEW<br>WORLD</h1>
          <div class="motto fade-in-delayed">COMMUNITY • IDENTITY • STABILITY</div>
          <h2 class="title-cn fade-in-delayed-2">美丽新世界</h2>
          <div class="start-hint fade-in-delayed-3">点击开启旅程</div>
        </div>
      </div>
      
      <!-- Scene 1: Hatchery (Identity Extraction) -->
      <div v-else-if="currentScene === 1" key="scene1" class="scene scene-decanting">
        <h2 class="scene-header">身份抽取</h2>
        <div class="identity-cards-container">
          <div 
            v-for="(id, index) in identities" 
            :key="index" 
            class="identity-card"
            :class="{ 
              'active': extractingIndex === index || finalIdentity === index, 
              'dimmed': finalIdentity !== null && finalIdentity !== index
            }"
            :style="{ borderColor: id.color }"
          >
            <div class="id-rank" :style="{ color: id.color }">{{ id.rank }}</div>
            <div class="id-desc">{{ id.desc }}</div>
          </div>
        </div>
        
        <div class="fingerprint-scanner" @click="startExtraction" :class="{ 'scanning': isExtracting, 'disabled': finalIdentity !== null }">
          <div class="fingerprint-icon">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M17.81,4.43c-3.14-1.85-7.46-1.54-9.52-0.58C5.58,5.1,4.5,6.65,4.5,6.65S3.43,8.44,3.14,10.23c-0.29,1.79,0.58,3.58,0.58,3.58s1.15,2.69,2.88,4.19c1.73,1.5,4.03,2.02,4.03,2.02s3.17,0.58,6.05-0.86c2.88-1.44,4.32-3.74,4.32-3.74s2.02-3.17,1.44-6.34C21.86,6.93,19.55,5.49,17.81,4.43z M12,18.5c-2.5,0-4.5-2-4.5-4.5s2-4.5,4.5-4.5s4.5,2,4.5,4.5S14.5,18.5,12,18.5z"/></svg>
          </div>
          <p>{{ finalIdentity !== null ? '身份确认：阿尔法' : '点击按压指纹定级' }}</p>
        </div>
      </div>

      <!-- Scene 2: Conditioning (Flower/Book/Money) -->
      <div v-else-if="currentScene === 2" key="scene2" class="scene scene-conditioning">
        <div class="conditioning-stage" v-if="conditioningStep === 0" @click="triggerPain('flower')">
          <div class="stimulus-icon">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12,22c4.97,0,9-4.03,9-9c0-4.97-9-13-9-13S3,8.03,3,13C3,17.97,7.03,22,12,22z M12,13c1.1,0,2-0.9,2-2c0-1.1-0.9-2-2-2s-2,0.9-2,2C10,12.1,10.9,13,12,13z"/></svg>
          </div>
          <p>鲜花</p>
        </div>
        <div class="conditioning-stage" v-else-if="conditioningStep === 1" @click="triggerPain('book')">
          <div class="stimulus-icon">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M18,2H6C4.9,2,4,2.9,4,4v16c0,1.1,0.9,2,2,2h12c1.1,0,2-0.9,2-2V4C20,2.9,19.1,2,18,2z M6,4h5v8l-2.5-1.5L6,12V4z"/></svg>
          </div>
          <p>图书</p>
        </div>
        <div class="conditioning-stage" v-else-if="conditioningStep === 2" @click="triggerPleasure">
          <div class="stimulus-icon money">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/></svg>
          </div>
          <p>消费</p>
        </div>
        
        <div class="pain-flash" v-if="showPain">
          <h1>拒绝</h1>
        </div>
        <div class="pleasure-flash" v-if="showPleasure">
          <h1>条件反射已建立</h1>
        </div>
      </div>

      <!-- Scene 3: Feelies (Love & Overload) -->
      <div v-else-if="currentScene === 3" key="scene3" class="scene scene-feelies">
        <div class="movie-screen" :class="{ 'overload': feeliesOverload }">
          <div class="heart-container" @click="pumpFeelies">
            <div class="heart-icon" :style="{ transform: `scale(${1 + feeliesProgress/50})` }">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
            </div>
            <p v-if="!feeliesOverload">点击积攒感官体验</p>
            <p v-else class="overload-text">点击高潮</p>
          </div>
          <div class="feelies-progress-bar">
            <div class="feelies-fill" :style="{ width: feeliesProgress + '%', background: feeliesOverload ? 'red' : '#d8b4fe' }"></div>
          </div>
        </div>
      </div>

      <!-- Scene 4: Hypnopaedia (Absorption) -->
      <div v-else-if="currentScene === 4" key="scene4" class="scene scene-hypno">
        <div class="hypno-text-container">
          <div 
            v-for="(text, index) in hypnoTexts" 
            :key="index"
            class="hypno-phrase"
            :class="{ 'vanished': hypnoProgress <= (100 - (index + 1) * (100/hypnoTexts.length)) }"
            :style="{ 
              top: text.top, 
              left: text.left,
              animationDelay: text.delay 
            }"
          >
            {{ text.content }}
          </div>
        </div>
        <div class="hypno-progress-container">
          <p>睡眠教学正在进行...</p>
          <div class="hypno-bar">
            <div class="hypno-fill" :style="{ width: hypnoProgress + '%' }"></div>
          </div>
        </div>
      </div>

      <!-- Scene 5: Intimacy (Swipe Cards) -->
      <div v-else-if="currentScene === 5" key="scene5" class="scene scene-intimacy">
        <h2 class="scene-title">"人人属于人人"</h2>
        <div class="swipe-deck-container">
            <div class="swipe-deck" v-if="currentPartnerIndex < intimacyPartners.length">
              <div class="partner-card-large" :class="[swipeAnimationClass, { 'no-transition': isResettingCard }]">
                <div class="partner-avatar-svg">
                  <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/></svg>
                </div>
                <div class="partner-name">{{ intimacyPartners[currentPartnerIndex].name }}</div>
                <div class="partner-status">Beta Plus</div>
              </div>
              
              <div class="swipe-controls">
                <button class="swipe-btn reject" @click="handleSwipe('left')">
                   <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="none"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
                <button class="swipe-btn accept" @click="handleSwipe('right')">
                   <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" stroke-width="2" fill="currentColor"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
                </button>
              </div>
            </div>
        </div>
        <div class="social-progress">
          <p>已结合伴侣: {{ acceptedCount }}/5</p>
          <div class="progress-dots">
            <span v-for="n in 5" :key="n" class="dot" :class="{ filled: n <= acceptedCount }"></span>
          </div>
        </div>
        <div class="toast-message" v-if="showToast">{{ toastMessage }}</div>
      </div>

      <!-- Scene 6: Solidarity (Growing Light) -->
      <div v-else-if="currentScene === 6" key="scene6" class="scene scene-solidarity">
        <div class="circle-container">
          <div 
            v-for="n in 12" 
            :key="n" 
            class="disciple"
            :style="{ 
              transform: `rotate(${n * 30}deg) translate(${150 + solidaritySize/2}px) rotate(-${n * 30}deg) scale(${1 - solidaritySize/200})` 
            }"
          >
            <svg viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="12" r="10"/></svg>
          </div>
          <div 
            class="light-orb" 
            :style="{ width: `${50 + solidaritySize}px`, height: `${50 + solidaritySize}px` }"
            @click="growLight"
          ></div>
        </div>
        <p class="chant-text" :style="{ opacity: solidaritySize/200 }">我听到了他来了...</p>
      </div>

      <!-- Scene 7: Reservation (Refined) -->
      <div v-else-if="currentScene === 7" key="scene7" class="scene scene-savage">
        <div class="dust-overlay"></div>
        <div class="scene-content">
          <h2 class="chapter-subtitle">野人保留地</h2>
          
          <div v-if="reservationStep === 0" class="reservation-step">
            <div class="horror-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M18 2h-8L2 12l8 10h8l8-10L18 2zM4.5 12L10.9 4h6.2l6.4 8-6.4 8h-6.2L4.5 12z"/></svg>
            </div>
            <div class="reservation-text">
              <p>高压电网将文明与野蛮隔开。</p>
              <p>这里没有索麻，没有清洁，没有青春永驻。</p>
              <p>只有尘土、疾病、衰老和...母亲。</p>
            </div>
            <button class="interaction-btn" @click="advanceReservation">进入保留地</button>
          </div>

          <div v-else-if="reservationStep === 1" class="reservation-step">
            <div class="horror-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2c5.52 0 10 4.48 10 10s-4.48 10-10 10S2 17.52 2 12 6.48 2 12 2zm0 2c-4.41 0-8 3.59-8 8s3.59 8 8 8 8-3.59 8-8-3.59-8-8-8zm0 3c1.1 0 2 .9 2 2s-.9 2-2 2-2-.9-2-2 .9-2 2-2zm0 10c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
            </div>
            <div class="reservation-text">
              <p>你看到了一个老男人。</p>
              <p>皮肤像松弛的皮革，牙齿脱落，脊背佝偻。</p>
              <p class="highlight-text">"在这里，人是会坏掉的。"</p>
              <p>列宁娜惊恐地捂住了嘴。</p>
            </div>
            <button class="interaction-btn" @click="advanceReservation">观看仪式</button>
          </div>

          <div v-else-if="reservationStep === 2" class="reservation-step">
             <div class="horror-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M11 15h2v2h-2zm0-8h2v6h-2zm.99-5C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"/></svg>
             </div>
             <div class="reservation-text">
               <p>鞭打，鲜血，蛇，鼓声。</p>
               <p>一个金发的年轻人冲了出来。</p>
               <p>"我想成为那个被鞭打的人！为了普勃！为了耶稣！"</p>
             </div>
             <button class="interaction-btn escape-btn" @click="transitionToNext(8, '索麻假日', '带野人约翰回伦敦')">
               带约翰离开
             </button>
          </div>
        </div>
      </div>

      <!-- Scene 8: Soma Holiday (Refined - Soma Riot) -->
      <div v-else-if="currentScene === 8" key="scene8" class="scene scene-soma">
        <div class="psychedelic-bg" :class="{ 'active': somaStep > 0 }"></div>
        <div class="soma-content">
          <h2 class="soma-title">索麻假日</h2>
          
          <div v-if="somaStep === 0" class="soma-phase">
            <p class="soma-desc">约翰的母亲琳达在医院去世了。</p>
            <p class="soma-desc">但周围只有领着闪电泡芙的孩子，他们在进行死亡适应训练。</p>
            <p class="soma-quote">"她死了！而你们却在这里吃东西！"</p>
            <button class="interaction-btn soma-btn" @click="takeSomaStep">分发索麻</button>
          </div>

          <div v-else-if="somaStep === 1" class="soma-phase">
            <h1 class="euphoria-text">暴动</h1>
            <p class="soma-desc">医院门口，得尔塔们在排队领取今天的配给。</p>
            <p class="soma-desc">约翰冲到了队伍前面。</p>
            <p class="highlight-text">"我给你们自由！"</p>
            <button class="interaction-btn soma-btn" @click="takeSomaStep">扔掉药片</button>
          </div>

          <div v-else class="soma-phase">
            <div class="soma-pill-container floating">
                <div class="soma-pill-3d large">
                <div class="half red">
</div>
                <div class="half yellow"></div>
                </div>
            </div>
            <p class="soma-desc">药片像雨点一样落下。</p>
            <p class="soma-desc">"你们难道不想做人吗？你们想做奴隶吗？"</p>
            <p class="soma-desc">警报响了。索麻气体喷雾开始弥漫。</p>
            <button class="interaction-btn soma-btn" @click="transitionToNext(9, '野人', '被捕：去见主宰者')">吸入气体</button>
          </div>

        </div>
      </div>

      <!-- Scene 9: The Savage (Refined - Debate) -->
      <div v-else-if="currentScene === 9" key="scene9" class="scene scene-encounter">
        <div class="debate-container">
            <div class="debate-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2.48-1.35L12 3zm1 14.5c-2 0-3-1-3-1s1-4 3-4 3 3 3 3-3 2-3 2zm-5.5-2.5l-2-1.09V11.5l2 1.09v2.41zm11 0l-2-1.09V11.5l2 1.09v2.41z"/></svg>
            </div>
            <h2 class="debate-title">世界主宰者</h2>
            
            <div class="dialogue-box">
                <div v-if="debateStep === 0" class="dialogue-step">
                    <p class="speaker mond">穆斯塔法·蒙德</p>
                    <p class="line">"因为我们的世界与《奥赛罗》的世界不同。没有钢，造不出汽车；没有社会的动荡，造不出悲剧。"</p>
                    <button class="next-btn" @click="advanceDebate">"那艺术呢？"</button>
                </div>
                <div v-else-if="debateStep === 1" class="dialogue-step">
                     <p class="speaker john">野蛮人约翰</p>
                    <p class="line">"那你们就为了安定，牺牲了高雅艺术？牺牲了莎士比亚？"</p>
                     <button class="next-btn" @click="advanceDebate">"那是必须的代价。"</button>
                </div>
                 <div v-else-if="debateStep === 2" class="dialogue-step">
                    <p class="speaker mond">穆斯塔法·蒙德</p>
                    <p class="line">"正是。还有科学，还有宗教。上帝现在在保险箱里。为了让轮子平稳转动，我们不能有任何颠簸。幸福是残酷的主人。"</p>
                    <button class="next-btn" @click="advanceDebate">"但我不需要..."</button>
                </div>
                <div v-else-if="debateStep === 3" class="dialogue-step">
                    <p class="speaker john">野蛮人约翰</p>
                    <p class="line">"但我不需要舒适。我要上帝，我要诗歌，我要真正的危险，我要自由，我要美好，我要罪恶。"</p>
                    <button class="choice-btn danger" @click="transitionToNext(10, '最终抉择', '为了不快乐的权利')">
                        "我要求不快乐的权利。"
                    </button>
                </div>
            </div>
        </div>
      </div>

      <!-- Scene 10: Ending (Choice) -->
      <div v-else-if="currentScene === 10" key="scene10" class="scene scene-ending-choice">
        <h1 class="ending-title">最终抉择</h1>
        <div class="ending-choices">
          <div class="ending-card soma-ending" @click="chooseEnding('soma')">
            <div class="ending-icon">
                <div class="soma-pill-3d small">
                  <div class="half red"></div>
                  <div class="half yellow"></div>
                </div>
            </div>
            <h3>服用索麻</h3>
            <p>拥抱快乐，遗忘痛苦，回归社会身体。</p>
          </div>
          <div class="ending-card freedom-ending" @click="chooseEnding('freedom')">
            <div class="ending-icon">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"/></svg>
            </div>
            <h3>拒绝文明</h3>
            <p>拥抱孤独，保留痛苦，成为真正的个体。</p>
          </div>
        </div>
      </div>

      <!-- Scene 11: Final State -->
      <div v-else-if="currentScene === 11" key="scene11" class="scene scene-final">
        <div v-if="finalChoice === 'soma'" class="final-soma">
          <h1>大家都快乐</h1>
          <p>共有 • 统一 • 安定</p>
          <div class="final-desc">
            <p>你选择了遗忘。在索麻的迷雾中，没有痛苦，只有永恒的当下。</p>
            <p>个体的棱角被磨平，融入了社会温暖的身体。</p>
          </div>
        </div>
        <div v-else class="final-freedom">
          <h1>美丽新世界</h1>
          <p>你获得了自由。</p>
          <div class="final-desc">
            <p>你选择了痛苦的权利。衰老、疾病、恐惧...以及诗歌与上帝。</p>
            <p>你在灯塔中孤独地守望，但你的灵魂属于你自己。</p>
          </div>
        </div>
        <button class="interaction-btn restart-btn" @click="goBack">结束旅程</button>
      </div>

    </transition>

    <!-- Bottom Navigation -->
    <div class="bottom-nav">
      <div 
        v-for="chapter in chapters" 
        :key="chapter.id"
        class="nav-dot-container"
        :class="{ active: currentScene === chapter.id }"
        @click="changeScene(chapter.id)"
      >
        <div class="nav-dot"></div>
        <span class="chapter-tooltip">{{ chapter.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentScene = ref(0);
const stability = ref(100);

// Transition System
const showTransition = ref(false);
const transitionTitle = ref('');
const transitionSubtitle = ref('');

const chapters = [
  { id: 0, name: '主页' },
  { id: 1, name: '孵化中心' },
  { id: 2, name: '条件反射' },
  { id: 3, name: '感官电影' },
  { id: 4, name: '睡眠教学' },
  { id: 5, name: '亲密义务' },
  { id: 6, name: '团结仪式' },
  { id: 7, name: '野人保留地' },
  { id: 8, name: '索麻假日' },
  { id: 9, name: '野人' },
  { id: 10, name: '结局' }
];

const sceneClass = computed(() => `scene-${currentScene.value}`);

const startJourney = () => {
  transitionToNext(1, '孵化中心', '欢迎来到中心伦敦孵化与条件反射中心');
};

// Scene 1: Hatchery
const identities = [
  { rank: 'Alpha', color: 'gold', desc: '天生领袖' },
  { rank: 'Beta', color: '#9d174d', desc: '高级技术' },
  { rank: 'Gamma', color: '#15803d', desc: '普通工作' },
  { rank: 'Delta', color: '#a16207', desc: '体力劳动' },
  { rank: 'Epsilon', color: '#000000', desc: '低端劳力' }
];
const extractingIndex = ref<number | null>(null);
const finalIdentity = ref<number | null>(null);
const isExtracting = ref(false);

const startExtraction = () => {
  if (finalIdentity.value !== null) {
    transitionToNext(2, '条件反射', '身份已确认：阿尔法+');
    return;
  }
  isExtracting.value = true;
  let count = 0;
  const interval = setInterval(() => {
    extractingIndex.value = Math.floor(Math.random() * 5);
    count++;
    if (count > 20) {
      clearInterval(interval);
      extractingIndex.value = 0; // Fix to Alpha
      finalIdentity.value = 0;
      isExtracting.value = false;
      
      // Auto advance after short delay
      setTimeout(() => {
        transitionToNext(2, '条件反射', '身份已确认：阿尔法+');
      }, 800);
    }
  }, 100);
};

// Scene 2: Conditioning
const conditioningStep = ref(0); // 0: Flower, 1: Book, 2: Money
const showPain = ref(false);
const showPleasure = ref(false);

const triggerPain = (type: string) => {
  showPain.value = true;
  setTimeout(() => {
    showPain.value = false;
    if (conditioningStep.value < 2) {
      conditioningStep.value++;
    }
  }, 1000);
};

const triggerPleasure = () => {
  showPleasure.value = true;
  setTimeout(() => {
    showPleasure.value = false;
    transitionToNext(3, '感官电影', '条件反射建立成功');
  }, 1500);
};

// Scene 3: Feelies
const feeliesProgress = ref(0);
const feeliesOverload = ref(false);

const pumpFeelies = () => {
  if (feeliesOverload.value) {
    transitionToNext(4, '睡眠教学', '全感官体验结束');
    return;
  }
  feeliesProgress.value += 10;
  if (feeliesProgress.value >= 100) {
    feeliesOverload.value = true;
  }
};

// Scene 4: Hypnopaedia
const hypnoProgress = ref(100);
const hypnoTexts = [
  { content: "阿尔法孩子们工作很努力", top: '20%', left: '10%', delay: '0s' },
  { content: "贝塔没那么聪明", top: '50%', left: '60%', delay: '1s' },
  { content: "伽马很愚蠢", top: '70%', left: '20%', delay: '2s' },
  { content: "人人属于人人", top: '30%', left: '50%', delay: '0.5s' },
  { content: "我很庆幸我不是伊普西隆", top: '15%', left: '70%', delay: '1.5s' },
  { content: "一克索麻胜过千言万语", top: '80%', left: '40%', delay: '2.5s' }
];
let hypnoTimer: number | null = null;

const startHypno = () => {
  hypnoProgress.value = 100;
  hypnoTimer = setInterval(() => {
    hypnoProgress.value -= 0.5; // Slower decay for better effect
    if (hypnoProgress.value <= 0) { // Exact finish
      if (hypnoTimer) clearInterval(hypnoTimer);
      transitionToNext(5, '亲密义务', '课程已内化');
    }
  }, 30); 
};

// Scene 5: Intimacy
const intimacyPartners = [
  { name: '列宁娜' },
  { name: '范妮' },
  { name: '亨利' },
  { name: '贝尼托' },
  { name: '波利' },
  { name: '爱丽丝' }
];
const currentPartnerIndex = ref(0);
const acceptedCount = ref(0);
const showToast = ref(false);
const toastMessage = ref('');
const swipeAnimationClass = ref('');
const isResettingCard = ref(false);

const handleSwipe = (direction: 'left' | 'right') => {
  if (isResettingCard.value) return; // Prevent double clicks
  
  // Set animation class
  swipeAnimationClass.value = direction === 'left' ? 'swipe-left' : 'swipe-right';

  // Wait for animation to finish before updating logic
  setTimeout(() => {
    if (direction === 'left') {
        showToast.value = true;
        toastMessage.value = "一夫一妻是反社会的！";
        setTimeout(() => showToast.value = false, 1500);
    } else {
        acceptedCount.value++;
    }
    
    // Prepare next card
    isResettingCard.value = true;
    currentPartnerIndex.value++;
    swipeAnimationClass.value = ''; // Reset class immediately but with no-transition
    
    // Force reflow/next tick to re-enable transition for next interaction
    setTimeout(() => {
        isResettingCard.value = false;
    }, 50);

    if (acceptedCount.value >= 5) {
        setTimeout(() => {
        transitionToNext(6, '团结仪式', '社交义务已履行');
        }, 500);
    } else if (currentPartnerIndex.value >= intimacyPartners.length) {
        // Reset if ran out but not enough
        currentPartnerIndex.value = 0;
    }
  }, 400); // Duration matches CSS transition
};

// Scene 6: Solidarity
const solidaritySize = ref(0);
const growLight = () => {
  solidaritySize.value += 40; // Increase by 40, so 5 clicks reach 200 (40*5=200)
  if (solidaritySize.value >= 200) {
    transitionToNext(7, '野人保留地', '福特降临');
  }
};

// Scene 7: Reservation (New Logic)
const reservationStep = ref(0);
const advanceReservation = () => {
    reservationStep.value++;
};

// Scene 8: Soma (New Logic)
const somaStep = ref(0);
const takeSomaStep = () => {
    somaStep.value++;
};

// Scene 9: Debate (New Logic)
const debateStep = ref(0);
const advanceDebate = () => {
    debateStep.value++;
};

// Scene 10: Ending
const finalChoice = ref('');
const chooseEnding = (choice: string) => {
  finalChoice.value = choice;
  currentScene.value = 11;
};

// Navigation Logic
const goBack = () => {
  router.push('/main');
};

const changeScene = (id: number) => {
  currentScene.value = id;
  resetSceneState(id);
};

const transitionToNext = (nextId: number, title: string, subtitle: string) => {
  transitionTitle.value = title;
  transitionSubtitle.value = subtitle;
  showTransition.value = true;
  
  setTimeout(() => {
    changeScene(nextId);
    setTimeout(() => {
      showTransition.value = false;
    }, 1000);
  }, 1500);
};

const resetSceneState = (id: number) => {
  // Reset states based on scene
  if (id === 1) {
    finalIdentity.value = null;
    extractingIndex.value = null;
  }
  if (id === 2) {
    conditioningStep.value = 0;
  }
  if (id === 3) {
    feeliesProgress.value = 0;
    feeliesOverload.value = false;
  }
  if (id === 4) {
    if (hypnoTimer) clearInterval(hypnoTimer);
    startHypno();
  }
  if (id === 5) {
    currentPartnerIndex.value = 0;
    acceptedCount.value = 0;
    swipeAnimationClass.value = '';
    isResettingCard.value = false;
  }
  if (id === 6) {
    solidaritySize.value = 0;
  }
  if (id === 7) {
      reservationStep.value = 0;
  }
  if (id === 8) {
      somaStep.value = 0;
  }
  if (id === 9) {
      debateStep.value = 0;
  }
};

// Initialize
onMounted(() => {
  // Check initial scene
  if (currentScene.value === 4) startHypno();
});

onUnmounted(() => {
  if (hypnoTimer) clearInterval(hypnoTimer);
});

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@300;500;700;900&family=Playfair+Display:ital@1&family=Share+Tech+Mono&family=Noto+Serif+SC:wght@400;700;900&display=swap');

/* Global Container */
.brave-vn-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  background: #000;
  font-family: 'Montserrat', 'Noto Serif SC', sans-serif;
  color: #cffafe;
  cursor: url('data:image/svg+xml;utf8,<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2V22M2 2H22" stroke="%23cffafe" stroke-width="2"/></svg>') 12 12, auto;
}

/* Common SVG styles */
svg {
    width: 100%;
    height: 100%;
}

/* Scene Styles */
.scene {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* Scene 0: Intro */
.scene-intro {
  background: radial-gradient(circle at center, #1a2a3a 0%, #000000 100%);
  cursor: pointer;
}
.cover-content {
  text-align: center;
  z-index: 10;
}
.floating-title {
  animation: float 4s ease-in-out infinite;
}
.start-hint {
  margin-top: 50px;
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 2px;
  text-transform: uppercase;
  animation: pulse-ring 2s infinite;
}
.fade-in-delayed { animation: fadeUp 1s ease-out 0.5s backwards; }
.fade-in-delayed-2 { animation: fadeUp 1s ease-out 1s backwards; }
.fade-in-delayed-3 { animation: fadeUp 1s ease-out 1.5s backwards; }

.title-en {
  font-family: 'Montserrat', sans-serif;
  font-weight: 900;
  font-size: 5rem;
  line-height: 0.9;
  letter-spacing: 0.5rem;
  background: linear-gradient(to right, #fff, #7dd3fc);
  -webkit-background-clip: text;
  color: transparent;
  margin-bottom: 20px;
}
.motto {
  font-size: 1.2rem;
  letter-spacing: 0.5rem;
  color: #38bdf8;
  margin-bottom: 20px;
}
.title-cn {
  font-family: 'Noto Serif SC', serif;
  font-size: 3rem;
  color: #fff;
  font-weight: 300;
}

/* Scene 1: Hatchery */
.scene-decanting {
  background: #1e1e1e;
}
.identity-cards-container {
  display: flex;
  gap: 20px;
  margin-bottom: 50px;
}
.identity-card {
  width: 120px;
  height: 180px;
  border: 2px solid;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(0,0,0,0.5);
  transition: all 0.3s;
}
.identity-card.active {
  background: rgba(255,255,255,0.1);
  box-shadow: 0 0 20px currentColor;
  transform: scale(1.1);
}
.identity-card.dimmed {
  opacity: 0.3;
}
.id-rank {
  font-size: 2rem;
  font-weight: 900;
}
.fingerprint-scanner {
  width: 80px;
  height: 80px;
  border: 2px solid #38bdf8;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  cursor: pointer;
  transition: all 0.3s;
  padding: 10px;
}
.fingerprint-icon {
  width: 40px;
  height: 40px;
  color: #38bdf8;
}
.fingerprint-scanner.scanning {
  animation: pulse-ring 1s infinite;
}
.fingerprint-scanner.disabled {
  opacity: 0.5;
  cursor: pointer;
}
.fingerprint-scanner p {
  position: absolute;
  bottom: 20%;
  width: 100%;
  text-align: center;
}

/* Scene 2: Conditioning */
.scene-conditioning {
  background: #2a0a0a;
}
.conditioning-stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.stimulus-icon {
  width: 100px;
  height: 100px;
  color: #fff;
  margin-bottom: 20px;
}
.stimulus-icon.money {
    color: #4ade80;
}
.pain-flash {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: red;
  display: flex;
  justify-content: center;
  align-items: center;
  animation: flash 0.2s infinite;
}
.pleasure-flash {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: #4ade80;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #000;
}

/* Scene 3: Feelies */
.scene-feelies {
  background: #2e1065;
}
.movie-screen {
  width: 300px;
  height: 300px;
  border: 4px solid #d8b4fe;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  box-shadow: 0 0 50px #d8b4fe;
  transition: all 0.1s;
}
.movie-screen.overload {
  animation: shake 0.1s infinite;
  border-color: red;
  box-shadow: 0 0 100px red;
}
.heart-container {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.heart-icon {
  width: 80px;
  height: 80px;
  color: #d8b4fe;
  transition: transform 0.1s;
}
.feelies-progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 10px;
  background: #4c1d95;
}
.feelies-fill {
  height: 100%;
  transition: width 0.1s;
}

/* Scene 4: Hypnopaedia */
.scene-hypno {
  background: #0f172a;
}
.hypno-text-container {
  position: absolute;
  width: 100%;
  height: 100%;
}
.hypno-phrase {
  position: absolute;
  color: rgba(255,255,255,0.5);
  font-size: 1.2rem;
  transition: opacity 0.5s;
}
.hypno-phrase.vanished {
  opacity: 0;
}
.hypno-progress-container {
  position: absolute;
  bottom: 100px;
  width: 300px;
  text-align: center;
}
.hypno-bar {
  width: 100%;
  height: 4px;
  background: #334155;
  margin-top: 10px;
}
.hypno-fill {
  height: 100%;
  background: #38bdf8;
  transition: width 0.1s linear;
}

/* Scene 5: Intimacy */
.scene-intimacy {
  background: #ec4899;
  color: #fff;
}
.swipe-deck-container {
    height: 400px;
    display: flex;
    justify-content: center;
    align-items: center;
}
.swipe-deck {
  position: relative;
  width: 300px;
  height: 400px;
}
.partner-card-large {
  width: 100%;
  height: 320px;
  background: #fff;
  border-radius: 20px;
  color: #000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  transition: transform 0.4s ease, opacity 0.4s ease;
}
.partner-card-large.swipe-left {
    transform: translateX(-150%) rotate(-30deg);
    opacity: 0;
}
.partner-card-large.swipe-right {
    transform: translateX(150%) rotate(30deg);
    opacity: 0;
}
.partner-avatar-svg {
    width: 100px;
    height: 100px;
    color: #ec4899;
    margin-bottom: 20px;
}
.partner-name {
  font-size: 2rem;
  font-weight: 700;
}
.partner-status {
    color: #888;
    margin-bottom: 20px;
}
.swipe-controls {
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-top: 20px;
  padding: 0 20px;
  box-sizing: border-box;
}
.swipe-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}
.swipe-btn:hover {
  transform: scale(1.1);
}
.swipe-btn.reject { color: #ef4444; }
.swipe-btn.accept { color: #22c55e; }
.progress-dots {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 10px;
}
.dot {
  width: 10px; height: 10px;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
}
.dot.filled {
  background: #fff;
}
.toast-message {
  position: absolute;
  top: 20%;
  background: rgba(0,0,0,0.8);
  padding: 10px 20px;
  border-radius: 20px;
  animation: fadeUp 0.3s;
}

/* Scene 6: Solidarity */
.scene-solidarity {
  background: #000;
}
.circle-container {
  position: relative;
  width: 400px;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.disciple {
  position: absolute;
  width: 30px;
  height: 30px;
  color: #fcd34d;
  transition: transform 0.5s;
}
.light-orb {
  border-radius: 50%;
  background: radial-gradient(circle, #fcd34d, transparent);
  box-shadow: 0 0 50px #fcd34d;
  cursor: pointer;
  z-index: 10;
  transition: all 0.5s;
}

/* Scene 5 additions */
.partner-card-large.no-transition {
    transition: none !important;
}

/* Scene 7: Reservation (Improved) */
.scene-savage {
  background: #3f2e18; /* Dusty brown */
  color: #d4b483;
}
.scene-content {
  position: relative;
  z-index: 1;
}
.reservation-step {
    display: flex;
    flex-direction: column;
    align-items: center;
    animation: fadeUp 0.5s;
}
.horror-icon {
    width: 80px;
    height: 80px;
    color: #a89060;
    margin-bottom: 20px;
}
.dust-overlay {
    position: absolute;
    width: 100%; height: 100%;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='0.1'/%3E%3C/svg%3E");
    opacity: 0.3;
}
.chapter-subtitle {
    font-size: 1.5rem;
    color: #a89060;
    margin-bottom: 2rem;
    text-transform: uppercase;
    letter-spacing: 2px;
}
.reservation-text {
    font-family: 'Noto Serif SC', serif;
    font-size: 1.2rem;
    line-height: 2;
    max-width: 600px;
    text-align: center;
    margin-bottom: 3rem;
}
.highlight-text {
    color: #ef4444;
    font-weight: bold;
    margin-top: 1rem;
}
.interaction-btn {
    border: 1px solid currentColor;
    background: transparent;
    color: inherit;
    padding: 10px 30px;
    cursor: pointer;
    font-family: 'Montserrat', sans-serif;
    transition: all 0.3s;
}
.interaction-btn:hover {
    background: rgba(255,255,255,0.1);
    transform: scale(1.05);
}
.escape-btn {
    border-color: #d4b483;
    color: #d4b483;
}
.escape-btn:hover {
    background: #d4b483;
    color: #3f2e18;
}

/* Scene 8: Soma Holiday (Improved) */
.scene-soma {
  background: #111;
  overflow: hidden;
}
.soma-phase {
    display: flex;
    flex-direction: column;
    align-items: center;
    animation: fadeUp 1s;
}
.psychedelic-bg {
    position: absolute;
    top: -50%; left: -50%; width: 200%; height: 200%;
    background: conic-gradient(from 0deg, #ff0000, #ffff00, #00ff00, #00ffff, #0000ff, #ff00ff, #ff0000);
    filter: blur(50px);
    opacity: 0.2;
    transition: opacity 2s, animation-duration 2s;
    animation: rotate 20s linear infinite;
}
.psychedelic-bg.active {
    opacity: 0.8;
    animation-duration: 5s;
}
.soma-content {
    z-index: 10;
    text-align: center;
}
.soma-title {
    font-size: 3rem;
    background: linear-gradient(to right, #fca5a5, #fcd34d);
    -webkit-background-clip: text;
    color: transparent;
    margin-bottom: 1rem;
}
.soma-quote {
    font-size: 1.5rem;
    font-style: italic;
    color: #fff;
    margin-bottom: 2rem;
}
.soma-desc {
    color: #ccc;
    margin-bottom: 2rem;
    max-width: 500px;
}
.euphoria-text {
    font-size: 4rem;
    color: #fff;
    text-shadow: 0 0 20px #fff;
    margin-bottom: 2rem;
}
.soma-pill-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
}
.soma-btn {
    border-color: #fcd34d;
    color: #fcd34d;
    font-size: 1.2rem;
}
.soma-btn:hover {
    background: #fcd34d;
    color: #000;
}
.floating {
    animation: float 3s ease-in-out infinite;
}

/* Scene 9: The Savage (Improved) */
.scene-encounter {
  background: #f8fafc; /* Stark white/grey for civilization */
  color: #0f172a;
}
.debate-container {
    max-width: 800px;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.debate-icon {
    width: 60px;
    height: 60px;
    color: #0f172a;
    margin-bottom: 1rem;
}
.debate-title {
    font-size: 2rem;
    margin-bottom: 3rem;
    font-weight: 900;
}
.dialogue-box {
    min-height: 200px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-bottom: 3rem;
    width: 100%;
}
.dialogue-step {
    animation: fadeUp 0.5s;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.speaker {
    font-size: 0.9rem;
    color: #64748b;
    margin-bottom: 1rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1px;
}
.speaker.mond { color: #0f172a; }
.speaker.john { color: #ef4444; }

.line {
    font-family: 'Noto Serif SC', serif;
    font-size: 1.5rem;
    line-height: 1.6;
    padding: 0 2rem;
    text-align: center;
    margin-bottom: 2rem;
}
.next-btn {
    border: none;
    background: #0f172a;
    color: #fff;
    padding: 10px 30px;
    cursor: pointer;
    font-family: 'Montserrat', sans-serif;
    transition: transform 0.2s;
}
.next-btn:hover {
    transform: translateY(-2px);
}

.choice-btn {
    padding: 15px 30px;
    font-size: 1.1rem;
    border: 2px solid #0f172a;
    background: transparent;
    cursor: pointer;
    transition: all 0.3s;
    color: #0f172a;
}
.choice-btn:hover {
    background: #0f172a;
    color: #fff;
}
.choice-btn.danger {
    border-color: #ef4444;
    color: #ef4444;
}
.choice-btn.danger:hover {
    background: #ef4444;
    color: #fff;
}

/* Scene 10: Ending */
.scene-ending-choice {
  background: #000;
}
.ending-choices {
  display: flex;
  gap: 30px;
}
.ending-card {
  width: 250px;
  height: 350px;
  border: 1px solid #333;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}
.ending-card:hover {
  border-color: #fff;
  background: #111;
}
.ending-icon {
    width: 80px;
    height: 80px;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
}
.freedom-ending .ending-icon {
    color: #fff;
}
.ending-card h3 {
  margin-bottom: 10px;
}
.ending-card p {
  color: #888;
  font-size: 0.9rem;
}

/* Final State */
.scene-final {
  background: #000;
  text-align: center;
}
.final-soma {
  color: #fcd34d;
}
.final-freedom {
  color: #fff;
}
.final-freedom h1,
.final-freedom p:first-of-type {
    color: #38bdf8;
}
.final-desc {
  margin-top: 2rem;
  font-family: 'Noto Serif SC', serif;
  font-size: 1.1rem;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.7);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}
.restart-btn {
  margin-top: 50px;
  padding: 10px 30px;
  background: transparent;
  border: 1px solid #fff;
  color: #fff;
  cursor: pointer;
}

/* Shared Components */
.soma-pill-3d {
  width: 60px;
  height: 30px;
  border-radius: 15px;
  position: relative;
  display: flex;
  overflow: hidden;
  box-shadow: 2px 5px 10px rgba(0,0,0,0.5), inset 0 2px 5px rgba(255,255,255,0.4);
}
.soma-pill-3d.large {
  width: 120px;
  height: 60px;
  border-radius: 30px;
}
.soma-pill-3d.small {
    width: 60px;
    height: 30px;
    border-radius: 15px;
}
.half { width: 50%; height: 100%; }
.half.red { background: #ef4444; }
.half.yellow { background: #fcd34d; }
.shine {
  position: absolute;
  top: 5%; left: 5%; width: 90%; height: 40%;
  background: linear-gradient(to bottom, rgba(255,255,255,0.8), transparent);
  border-radius: 30px;
}
.pill-label {
  margin-top: 15px;
  color: #94a3b8;
  font-size: 0.9rem;
}

/* Animations */
@keyframes pulse-ring {
  0% { box-shadow: 0 0 0 0 rgba(56, 189, 248, 0.7); }
  70% { box-shadow: 0 0 0 20px rgba(56, 189, 248, 0); }
  100% { box-shadow: 0 0 0 0 rgba(56, 189, 248, 0); }
}
@keyframes flash {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
@keyframes shake {
  0% { transform: translate(1px, 1px) rotate(0deg); }
  10% { transform: translate(-1px, -2px) rotate(-1deg); }
  20% { transform: translate(-3px, 0px) rotate(1deg); }
  30% { transform: translate(3px, 2px) rotate(0deg); }
  40% { transform: translate(1px, -1px) rotate(1deg); }
  50% { transform: translate(-1px, 2px) rotate(-1deg); }
  60% { transform: translate(-3px, 1px) rotate(0deg); }
  70% { transform: translate(3px, 1px) rotate(-1deg); }
  80% { transform: translate(-1px, -1px) rotate(1deg); }
  90% { transform: translate(1px, 2px) rotate(0deg); }
  100% { transform: translate(1px, -2px) rotate(-1deg); }
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}
@keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-10px); }
}

/* HUD */
.hud {
  position: absolute;
  top: 0; left: 0; width: 100%;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  box-sizing: border-box;
  z-index: 100;
  pointer-events: none; /* Let clicks pass through */
}
.hud-top {
  display: flex;
  gap: 30px;
}
.return-btn {
  pointer-events: auto;
  cursor: pointer;
  border: 1px solid rgba(255,255,255,0.3);
  padding: 5px 15px;
  border-radius: 20px;
  background: rgba(0,0,0,0.5);
  transition: background 0.3s;
}
.return-btn:hover {
  background: rgba(255,255,255,0.2);
}
.bar-container {
  width: 100px;
  height: 5px;
  background: #333;
  display: inline-block;
  margin-left: 10px;
  vertical-align: middle;
}
.bar-fill {
  height: 100%;
  background: #38bdf8;
}

/* Transition Overlay */
.chapter-transition {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: #000;
  z-index: 200;
  display: flex;
  justify-content: center;
  align-items: center;
}
.transition-content {
  text-align: center;
}
.chapter-transition h2 {
  font-size: 3rem;
  color: #fff;
  margin-bottom: 20px;
}
.chapter-transition p {
  color: #38bdf8;
  font-size: 1.2rem;
}

/* Bottom Nav */
.bottom-nav {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 15px;
  z-index: 100;
}
.nav-dot-container {
  position: relative;
  padding: 10px;
  cursor: pointer;
}
.nav-dot {
  width: 8px; height: 8px;
  background: rgba(255,255,255,0.3);
  border-radius: 50%;
  transition: all 0.3s;
}
.nav-dot-container.active .nav-dot {
  background: #38bdf8;
  transform: scale(1.5);
}
.chapter-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.8);
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 0.8rem;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s;
  margin-bottom: 5px;
}
.nav-dot-container:hover .chapter-tooltip {
  opacity: 1;
}

/* Vue Transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.fade-morph-enter-active, .fade-morph-leave-active {
  transition: opacity 0.5s, transform 0.5s;
}
.fade-morph-enter-from {
  opacity: 0;
  transform: scale(1.1);
}
.fade-morph-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
