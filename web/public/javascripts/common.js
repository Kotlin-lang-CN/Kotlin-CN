'use strict';

/**-----tools start-------**/
var Tool = {};
Tool.isValidEmail = function(s) {
    var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
    return reg.test(s);
}
Tool.isValidName = function(s) {
    var patrn = /^[a-zA-Z]{2,20}$/;
    if (!patrn.exec(s)) return false
    return true
}
Tool.isValidPass = function(s) {
    return s != null && s.length > 5;
}

/**------simple logger-----**/
var Lg = {};
Lg.v = function(msg) {
    console.log(msg);
}
Lg.d = function(msg) {
    console.log(msg);
}
Lg.e = function(msg) {
    console.log(msg);
}
