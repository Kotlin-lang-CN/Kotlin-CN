import Vue from 'vue';

const bus = new Vue();

const Event = {
  on: function (key, func) {
    bus.$on(key, func);
  },
  emit: function (key, value) {
    bus.$emit(key, value);
  }
};
export default Event;
