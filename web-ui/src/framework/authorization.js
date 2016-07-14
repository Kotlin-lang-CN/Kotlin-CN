const Cookies = require('js-cookie'),
    Conf = require('./config.js');

module.exports = {
    isLogin: function () {
        try {
            var cookie = JSON.parse(decodeURI(Cookies.get(Conf.cookie)));
            return "profile" in cookie && "token" in cookie
        } catch (e) {
            Cookies.remove(Conf.cookie);
            return false;
        }
    },
    logout: function () {
        Cookies.remove(Conf.cookie);
    },
    getProfile: function () {
        try {
            return JSON.parse(decodeURI(Cookies.get(Conf.cookie))).profile
        } catch (e) {
            Cookies.remove(Conf.cookie);
            return null;
        }
    },
    getToken: function () {
        try {
            return JSON.parse(decodeURI(Cookies.get(Conf.cookie))).token
        } catch (e) {
            Cookies.remove(Conf.cookie);
            return null;
        }
    }
};