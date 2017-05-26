import bigJSON from './Parse.js';
import Cookie from './Cookie.js';
import Config from './Config.js';
import LoginMgr from './LoginMgr.js';

class Net {
  constructor() {
    this.Header = {};

    let match = /(msie|firefox|chrome|opera|version).*?([\d.]+)/;
    let info = navigator.userAgent.toLowerCase().match(match);
    let key = 'deviceId';
    let deviceId = Cookie.get(key);
    if (deviceId.length === 0) {
      deviceId = "WEB-" + Date.now().toString(36);
      deviceId += Math.random().toString(36).slice(2);
      deviceId += Math.random().toString(36).slice(3);
      deviceId = deviceId.toUpperCase();
      Cookie.set(key, deviceId);
    }

    this.Header.platform = 0;
    this.Header.contentType = 'application/x-www-form-urlencoded';
    this.Header.system = navigator.platform;
    this.Header.vendor = info[1].replace(/version/, "'safari") + info[2];
    this.Header.deviceId = deviceId;
    this.Header.get = function () {
      return {
        'X-App-Device': this.deviceId,
        'X-App-Platform': this.platform,
        'X-App-Vendor': this.vendor,
        'X-App-System': this.system,
        'Content-Type': this.contentType,
        'X-App-Token': LoginMgr.token
      }
    }
  }

  ajax(i, e) {
    let header = this.Header.get();
    $.ajax({
      url: Config.API + i.url,
      cache: !1,
      type: i.type,
      dataType: "text",
      headers: header,
      data: i.condition,
      success: function (i) {
        if (i = bigJSON.parse(i), 0 !== i.code) throw i.msg;
        e(i)
      },
      error: function (i) {
        throw "error" + i;
      }
    })
  }
}
export default new Net();

