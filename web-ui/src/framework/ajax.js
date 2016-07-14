const $ = require('jquery'),
    conf = require('./config.js');

String.prototype.startWith = function (str) {
    var reg = new RegExp("^" + str);
    return reg.test(this);
};

const handleRequest = function (ajax) {
    const success = ajax.success;
    const fail = ajax.fail;
    if (!ajax.url.startWith('http')) ajax.url = conf.api_version + ajax.url;
    ajax.success = function (resp) {
        if (!('status' in resp)) {
            console.log('Unknown response:');
            console.log(resp);
            if (fail != undefined) fail({status: -1, message: ''});
        } else if (resp.status == 200) {
            if (success != undefined) success(resp);
        } else {
            if (fail != undefined) fail(resp);
        }
    };
    ajax.error = function (e) {
        if (fail != undefined) fail({status: -1, message: e.message})
    };
};

const Request = {
    get: function (ajax) {
        handleRequest(ajax);
        ajax.method = 'get';
        $.ajax(ajax)
    },

    post: function (ajax) {
        ajax.contentType = 'application/json';
        ajax.data = (ajax.data == undefined) ? undefined : JSON.stringify(ajax.data);
        handleRequest(ajax);
        ajax.method = 'post';
        $.ajax(ajax)
    }
};
module.exports.get = Request.get;
module.exports.post = Request.post;