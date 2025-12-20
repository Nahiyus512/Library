<template>
  <el-dialog
    v-model="visible"
    width="600px"
    align-center
    class="book-dialog"
    :show-close="false"
    @close="handleClose"
  >
    <div class="dialog-content-wrapper">
      <div class="dialog-top-section">
        <div class="dialog-cover">
          <img :src="'http://localhost:8080/common/download?name=' + bookData.bookImage" alt="cover">
        </div>
        
        <div class="dialog-details">
          <h2 class="detail-title">{{ bookData.bookName }}</h2>
          
          <div class="detail-meta">
            <div class="meta-item">
              <span class="label">分类</span>
              <span class="value">{{ bookData.bookClassify }}</span>
            </div>
            <div class="meta-item">
              <span class="label">作者</span>
              <span class="value">{{ bookData.bookAuthor }}</span>
            </div>
            <div class="meta-item">
              <span class="label">出版社</span>
              <span class="value">{{ bookData.bookPublic }}</span>
            </div>
          </div>

          <div class="rating-action-row">
            <div class="rating-group">
              <span class="section-label">您的评分</span>
              <div class="stars" @click="rate($event)">
                <input
                  v-for="n in 5"
                  :key="n"
                  type="radio"
                  :id="'star' + n"
                  :value="n"
                  hidden
                />
                <label
                  v-for="n in 5"
                  :key="n"
                  :for="'star' + n"
                  :class="{ filled: n <= ratingData.rating }"
                >
                  ★
                </label>
              </div>
            </div>

            <div class="action-right">
              <button class="action-btn primary small-btn" @click="addToBookshelf" v-if="likeLevel !== 2">
                加入书架
              </button>
              <button class="action-btn primary small-btn" disabled v-else>
                已加入
              </button>
            </div>
          </div>
        </div>
      </div>
      <BookComments :bookId="bookData.bookId" v-if="visible" />
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import myAxios from '@/api/index';
import { useCookies } from '@vueuse/integrations/useCookies';
import BookComments from '@/components/business/BookComments.vue';

const props = defineProps<{
  modelValue: boolean;
  book: any;
}>();

const emit = defineEmits(['update:modelValue']);

const visible = ref(props.modelValue);
const cookie = useCookies();
const likeLevel = ref(-1);

const ratingData = reactive({
  rating: 0,
  userId: 0,
  bookId: 0,
  bookName: '',
});

const bookData = reactive({
  bookId: '',
  bookName: '',
  bookPublic: '',
  bookClassify: '',
  bookImage: '',
  bookNum: 0,
  bookAuthor: '',
});

watch(() => props.modelValue, (val) => {
  visible.value = val;
  if (val && props.book) {
    initData();
  }
});

watch(visible, (val) => {
  emit('update:modelValue', val);
});

const handleClose = () => {
  visible.value = false;
};

const initData = async () => {
  if (!props.book) return;
  
  bookData.bookId = props.book.bookId || props.book.id;
  bookData.bookName = props.book.bookName || props.book.title;
  bookData.bookAuthor = props.book.bookAuthor || props.book.author || 'Unknown';
  bookData.bookPublic = props.book.bookPublic || 'Unknown';
  bookData.bookClassify = props.book.bookClassify || props.book.category || 'General';
  bookData.bookImage = props.book.cover || props.book.bookImge || '';
  bookData.bookNum = props.book.bookNum !== undefined ? props.book.bookNum : 999;

  ratingData.bookId = Number(bookData.bookId);
  ratingData.bookName = bookData.bookName;

  const username = cookie.get('username');
  let userId = cookie.get('userId');

  if (!userId && username) {
    try {
      let res = await myAxios.get('http://localhost:8080/user/getUserByName', {
        params: { name: username }
      });
      userId = res.data.data.id;
    } catch (e) {
      console.error(e);
    }
  }
  
  if (userId) {
    ratingData.userId = userId;
    findRating();
    getLikeStatus();
  }
};

const getLikeStatus = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookLike/status', {
      params: {
        userId: ratingData.userId,
        bookId: bookData.bookId
      }
    });
    likeLevel.value = res.data.data;
  } catch (e) {
    console.log(e);
  }
};

const addToBookshelf = async () => {
  const username = cookie.get('username');
  try {
    let res = await myAxios.put('http://localhost:8080/bookLike/like', {
      userId: ratingData.userId,
      userName: username,
      bookId: bookData.bookId,
      bookName: bookData.bookName,
      likeLevel: 2
    });
    if (res.data.code === 200) {
        ElMessage.success("加入书架成功");
        likeLevel.value = 2;
    } else {
        ElMessage.error(res.data.msg || "操作失败");
    }
  } catch (e) {
    console.log(e);
  }
};

const rate = async (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (target.tagName === 'LABEL') {
    const starIndex = parseInt(target.getAttribute('for')!.slice(-1), 10);
    ratingData.rating = starIndex;
    await inputScore();
  }
};

const inputScore = async () => {
  try {
    let rs = await myAxios.put('http://localhost:8080/bookScore/updateScore', {
      userId: ratingData.userId,
      bookId: ratingData.bookId,
      score: ratingData.rating
    });
    ElMessage.success(rs.data.data);
  } catch (e) {
    console.log(e);
  }
};

const findRating = async () => {
  try {
    let res = await myAxios.get('http://localhost:8080/bookScore/findScore', {
      params: {
        userId: ratingData.userId,
        bookId: ratingData.bookId
      }
    });
    ratingData.rating = res.data.data;
  } catch (e) {
    console.log(e);
  }
};
</script>

<style scoped>
.dialog-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dialog-top-section {
  display: flex;
  gap: 30px;
}

.dialog-cover {
  width: 180px;
  flex-shrink: 0;
}

.dialog-cover img {
  width: 100%;
  border-radius: 4px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.dialog-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 20px 0;
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.meta-item .label {
  color: #999;
}

.meta-item .value {
  font-weight: 500;
}

.rating-action-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: auto;
}

.rating-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.section-label {
  display: block;
  font-size: 12px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stars {
  display: flex;
  gap: 5px;
  cursor: pointer;
}

.stars label {
  font-size: 24px;
  color: #eee;
  cursor: pointer;
  transition: color 0.2s;
}

.stars label:hover,
.stars label:hover ~ label {
  color: #ddd;
}

.stars label.filled {
  color: #000;
}

.action-right {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 4px;
}

.action-btn.small-btn {
  padding: 8px 16px;
  height: 40px;
}

.action-btn.primary {
  background: #000;
  color: #fff;
}

.action-btn.primary:hover {
  background: #333;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>