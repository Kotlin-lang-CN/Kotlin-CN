const Util = {

  isValidEmail(s) {
    let reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
    return reg.test(s);
  },

  isValidName(s) {
    let reg = /^[a-zA-Z]{2,20}$/;
    return reg.test(s);
  },

  isValidPass(s) {
    return s !== null && s.length > 5;
  },

  isMobile() {
    let ua = navigator.userAgent;
    return ua.match(/(Android)[\s\/]+([\d\.]+)/) !== null
      || ua.match(/(iPad|iPhone|iPod)\s+OS\s([\d_\.]+)/) !== null
      || ua.match(/(Windows\s+Phone)\s([\d\.]+)/) !== null;
  }

};
if (!String.prototype.format) {
  String.prototype.format = function () {
    let args = arguments;
    return this.replace(/{(\d+)}/g, function (match, number) {
      return typeof args[number] !== 'undefined'
        ? args[number]
        : match
        ;
    });
  };
}
export default Util;
