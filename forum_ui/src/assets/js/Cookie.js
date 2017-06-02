class Cookie {
  set(key, value) {
    const COOKIE_VALID_LIMIT = 30;
    let exp = new Date();
    exp.setTime(exp.getTime() + COOKIE_VALID_LIMIT * 24 * 60 * 60 * 1000);
    document.cookie = key + "=" + escape(value) + ";expires=" + exp.toGMTString();
  }

  get(key) {
    let arr, reg = new RegExp("(^| )" + key + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) {
      return unescape(arr[2]);
    }
    return '';
  }

  del(key) {
    let exp = new Date();
    exp.setTime(exp.getTime() - 1);
    let value = this.get(key);
    if (value !== null) {
      document.cookie = key + "=" + value + ";expires=" + exp.toGMTString();
    }
  }
}
export default new Cookie();
