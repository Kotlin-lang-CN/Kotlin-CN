import bigJSON from './Parse.js';
import App from './Application';
class Util {

  ajax(i, e) {
    $.ajax({
      url: '' + i.url,
      cache: !1,
      type: i.type,
      dataType: "text",
      headers:App.Headers.get(),
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

  isValidEmail(s) {
    let reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
    return reg.test(s);
  }

  isValidName(s) {
    let reg = /^[a-zA-Z]{2,20}$/;
    return reg.test(s);
  }

  isValidPass(s) {
    return s !== null && s.length > 5;
  }
}
export default new Util();
