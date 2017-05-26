'use strict';

const App = {};
class Headers {
  constructor() {
    this.system = '';
    this.vendor = '';
    this.platform = 0;
    this.contentType = 'application/x-www-form-urlencoded';
    this.deviceId = '';

    let match = /(msie|firefox|chrome|opera|version).*?([\d.]+)/;
    let info = navigator.userAgent.toLowerCase().match(match);
    this.system = navigator.platform;
    this.vendor = info[1].replace(/version/, "'safari") + info[2];

    let key = 'deviceId';
    let deviceId = App.Cookie.get(key);
    if (deviceId.length === 0) {
      deviceId = "WEB-" + Date.now().toString(36);
      deviceId += Math.random().toString(36).slice(2);
      deviceId += Math.random().toString(36).slice(3);
      deviceId = deviceId.toUpperCase();
      Cookie.set(key, deviceId);
    }
    this.deviceId = deviceId;
  }

  get() {
    return {
      'X-App-Device': this.deviceId,
      'X-App-Platform': this.platform,
      'X-App-Vendor': this.vendor,
      'X-App-System': this.system,
      'Content-Type': this.contentType,
      'X-App-Token': App.LoginMgr.token
    }
  }
}

class Cookie {
  static set(key, value) {
    let COOKIE_VALID_LIMIT = 30;
    let exp = new Date();
    exp.setTime(exp.getTime() + COOKIE_VALID_LIMIT * 24 * 60 * 60 * 1000);
    document.cookie = key + "=" + escape(value) + ";expires=" + exp.toGMTString();
  }

  static get(key) {
    let arr, reg = new RegExp("(^| )" + key + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg)) {
      return unescape(arr[2]);
    }
    return '';
  }

  static del(key) {
    let exp = new Date();
    exp.setTime(exp.getTime() - 1);
    let value = this.get(key);
    if (value !== null) {
      document.cookie = key + "=" + value + ";expires=" + exp.toGMTString();
    }
  }
}

class Cache {
  static set(key, value) {
    localStorage.setItem(key, value);
  }

  static get(key) {
    let value = localStorage.getItem(key);
    return value === null ? '' : value;
  }

  static del(key) {
    localStorage.removeItem(key);
  }
}

class LoginMgr {
  constructor() {
    this.isLogin = true;
    this.uid = App.Cookie.get('uid');
    this.token = App.Cookie.get('token');
    this.username = App.Cache.get('username');
    this.email = App.Cache.get('email');
    if (this.uid.length === 0 || this.token.length === 0) {
      this.logout();
    }
  }

  login(data) {
    this.isLogin = true;
    this.uid = data.uid;
    this.username = data.username;
    this.token = data.token;
    this.email = data.email;
    App.Cookie.set('uid', data.uid);
    App.Cookie.set('token', data.token);
    App.Cache.set('username', data.username);
    App.Cache.set('email', data.email);
  }

  logout() {
    this.isLogin = false;
  }
}

//App.Headers = new Headers();
//App.Cache = new Cache();
//App.Cookie = new Cookie();
//App.LoginMgr = new LoginMgr();

export default App;
