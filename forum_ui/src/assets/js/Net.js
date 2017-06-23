import bigJSON from './Parse.js';
import Cookie from 'js-cookie';
import Event from './Event.js';

function generateHeaders() {
  let deviceId = Cookie.get("X-App-Device");
  if (deviceId === undefined) {
    deviceId = "WEB-" + Date.now().toString(36);
    deviceId += Math.random().toString(36).slice(2);
    deviceId += Math.random().toString(36).slice(3);
    deviceId = deviceId.toUpperCase();
    Cookie.set("X-App-Device", deviceId);
  }
  let platform = 0;
  let contentType = 'application/x-www-form-urlencoded';
  let system = navigator.platform;
  let info = navigator.userAgent.toLowerCase().match(/(msie|firefox|chrome|opera|version).*?([\d.]+)/);
  let vendor = (info && info.length >= 3) ? info[1].replace(/version/, "'safari") + info[2] : "null";
  let token = Cookie.get("X-App-Token");
  return {
    'X-App-Device': deviceId,
    'X-App-Platform': platform,
    'X-App-Vendor': vendor,
    'X-App-System': system,
    'Content-Type': contentType,
    'X-App-Token': token
  }
}

class Net {
  ajax(request, success, fail) {
    let errHandler = function (error) {
      Event.emit('error', error.msg ? error.msg + '(' + error.code + ')' : error);
      console.log(error);
      if (fail) fail(error)
    };
    $.ajax({
      url: request.url,
      cache: !1,
      type: request.type,
      dataType: "text",
      headers: generateHeaders(),
      data: request.condition,
      success: (data) => {
        try {
          let resp = bigJSON.parse(data);
          if (0 !== resp.code) {
            errHandler(resp);
          } else {
            if (success) success(resp);
          }
        } catch (err) {
          errHandler(err)
        }
      },
      error: errHandler
    })
  }

  get(request, success, fail) {
    request.type = 'GET';
    this.ajax(request, success, fail)
  }

  post(request, success, fail) {
    request.type = 'POST';
    this.ajax(request, success, fail)
  }

}

export default new Net();

