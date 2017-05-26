import bigJSON from './Parse.js';
class Util {

  constructor(options) {

  }
  ajax(i, e) {
    let s = this.headers;
    $.ajax({
      url: ADMIN_URL + i.url,
      cache: !1,
      type: i.type,
      dataType: "text",
      data: i.condition,
      beforeSend: function(e) {
        s && (e.setRequestHeader("X-Appid", s["X-Appid"]), e.setRequestHeader("X-Device-Token", s["X-Device-Token"]), e.setRequestHeader("X-Device", s["X-Device"]), e.setRequestHeader("X-App-Token", s["X-App-Token"]), e.setRequestHeader("X-App-Uid", s["X-App-Uid"]))
      },
      success: function(i) {
        if(i = bigJSON.parse(i), 0 != i.code) throw i.msg;
        e(i)
      },
      error: function(i) {
        throw "error" + i
      }
    })
  }
}
export default new Util();
