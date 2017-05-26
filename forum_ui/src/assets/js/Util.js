class Util {

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
