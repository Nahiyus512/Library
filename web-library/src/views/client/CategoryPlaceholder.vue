<template>
  <div class="placeholder-container">
    <h1 class="title">Category Collection</h1>
    <p class="subtitle">Curated selection for your inspiration</p>
    <div class="content-box">
      <!-- Content placeholder -->
      <div class="line" style="width: 60%"></div>
      <div class="line" style="width: 80%"></div>
      <div class="line" style="width: 50%"></div>
    </div>
  </div>
</template>

<style scoped>
.placeholder-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  font-family: 'Inter', sans-serif;
  animation: fadeIn 1s ease;
}

.title {
  font-size: 32px;
  font-weight: 300;
  letter-spacing: 2px;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 14px;
  color: #888;
  margin-bottom: 40px;
}

.content-box {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.line {
  height: 2px;
  background-color: #f0f0f0;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>