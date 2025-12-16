import { reactive } from 'vue';

export const transitionState = reactive({
  isActive: false,
  color: '#ffffff',
  title: '',
  startAnimation(color: string, title: string) {
    this.color = color;
    this.title = title;
    this.isActive = true;
  },
  endAnimation() {
    this.isActive = false;
    // Reset color to avoid persistence of previous color if next one fails or is delayed
    // Use a small timeout to allow the fade-out to complete before resetting
    setTimeout(() => {
      this.color = '#ffffff'; 
    }, 500);
  }
});
