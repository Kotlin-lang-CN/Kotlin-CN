import Vue from 'vue';
class Event {
  //https://vuejs.org/v2/guide/components.html#Non-Parent-Child-Communication
  constructor() {
    this.bus = new Vue();
  }

  on(key,func){
    this.bus.$on(key,func);
  }

  emit(key,value){
    this.bus.$emit(key,value);
  }
}
export default new Event;
