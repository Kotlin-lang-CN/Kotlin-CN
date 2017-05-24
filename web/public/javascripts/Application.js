'use strict';

//assert namespace : App 
var App = {};
App.Headers = {
    system: '',
    vendor: '',
    platform: 0,
    contentType: 'application/x-www-form-urlencoded',
    deviceId: '',
    init: function() {
        var match = /(msie|firefox|chrome|opera|version).*?([\d.]+)/
        var info = navigator.userAgent.toLowerCase().match(match);
        this.system = navigator.platform;
        this.vendor = info[1].replace(/version/, "'safari") + info[2];

        var key = 'deviceId';
        var deviceId = App.Cookie.get(key);
        if (deviceId.length == 0) {
            deviceId = "WEB-" + Date.now().toString(36);
            deviceId += Math.random().toString(36).slice(2);
            deviceId += Math.random().toString(36).slice(3);
            deviceId = deviceId.toUpperCase()
            App.Cookie.set(key, deviceId);
        }
        this.deviceId = deviceId;
    },
    get: function() {
        return {
            'X-App-Device': this.deviceId,
            'X-App-Platform': this.platform,
            'X-App-Vendor': this.vendor,
            'X-App-System': this.system,
            'Content-Type': this.contentType,
            'X-App-Token': App.LoginMgr.token
        }
    }
};

App.Cookie = {
    //cookie valid in 30 days
    COOKIE_VALID_LIMIT: 30,
    set: function(key, value) {
        var exp = new Date();
        exp.setTime(exp.getTime() + this.COOKIE_VALID_LIMIT * 24 * 60 * 60 * 1000);
        document.cookie = key + "=" + escape(value) + ";expires=" + exp.toGMTString();
    },
    get: function(key) {
        var arr, reg = new RegExp("(^| )" + key + "=([^;]*)(;|$)");
        if (arr = document.cookie.match(reg)) {
            return unescape(arr[2]);
        }
        return '';
    },
    del: function(key) {
        var exp = new Date();
        exp.setTime(exp.getTime() - 1);
        var cval = this.get(key);
        if (cval != null) {
            document.cookie = key + "=" + cval + ";expires=" + exp.toGMTString();
        }
    }
};

App.Cache = {
    set: function(key, value) {
        localStorage.setItem(key, value);
    },
    get: function(key) {
        var value = localStorage.getItem(key);
        return value == null ? '' : value;
    },
    del: function(key) {
        localStorage.removeItem(key);
    }
}

App.LoginMgr = {
    uid: '',
    username: '',
    token: '',
    email: '',
    isLogin: true,

    init: function() {
        this.uid = App.Cookie.get('uid');
        this.token = App.Cookie.get('token');
        this.username = App.Cache.get('username');
        this.email = App.Cache.get('email');
        if (this.uid.length == 0 || this.token.length == 0) {
            this.logout();
        }
    },
    login: function(data) {
        this.isLogin = true;
        this.uid = data.uid;
        this.username = data.username;
        this.token = data.token;
        this.email = data.email;
        this.isLogin = true;
        App.Cookie.set('uid', data.uid);
        App.Cookie.set('token', data.token);
        App.Cache.set('username', data.username);
        App.Cache.set('email', data.email);
    },
    logout: function() {
        this.isLogin = false;
    }
};

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

//global init
App.Headers.init();
App.LoginMgr.init();
