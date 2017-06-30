const I64BIT_TABLE = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-'.split('');

function hash(input) {
  let hash = 5381;
  let i = input.length - 1;

  if (typeof input === 'string') {
    for (; i > -1; i--)
      hash += (hash << 5) + input.charCodeAt(i);
  }
  else {
    for (; i > -1; i--)
      hash += (hash << 5) + input[i];
  }
  let value = hash & 0x7FFFFFFF;
  let retValue = '';
  do {
    retValue += I64BIT_TABLE[value & 0x3F];
  }
  while (value >>= 6);

  return retValue;
}

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
  },

  //generate anchor from title text
  anchorHash(text) {
    return hash(text.replace('#', '_'))
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
