var Cookies = require('js-cookie');

var Authorization = {
    key: 'kotlin_cn',
    isLogin: function () {
        try {
            return JSON.parse(decodeURI(Cookies.get(this.key))) != null
        } catch (e) {
            this.logout();
            return false;
        }
    },
    logout: function () {
        Cookies.remove(this.key);
    },
    getProfile: function () {
        try {
            return JSON.parse(decodeURI(Cookies.get(this.key))).profile
        } catch (e) {
            this.logout();
            return null;
        }
    },
    getToken: function () {
        try {
            return JSON.parse(decodeURI(Cookies.get(this.key))).token
        } catch (e) {
            this.logout();
            return null;
        }
    }
};

module.exports = Authorization;