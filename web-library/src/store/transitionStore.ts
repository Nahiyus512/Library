import { reactive } from 'vue';

export const transitionState = reactive({
  isActive: false,
  color: '#ffffff',
  textColor: '#ffffff',
  title: '',
  startAnimation(color: string, title: string, textColor: string = '#ffffff') {
    this.color = color;
    this.title = title;
    this.textColor = textColor;
    this.isActive = true;
  },
  endAnimation() {
    this.isActive = false;
    // Reset color to avoid persistence of previous color if next one fails or is delayed
    // Use a small timeout to allow the fade-out to complete before resetting
    setTimeout(() => {
      this.color = '#ffffff'; 
      this.textColor = '#ffffff';
    }, 500);
  }
});
