const req = require('reqwest'),
    conf = require('./config.js');
String.prototype.startWith = function (str) {
    var reg = new RegExp("^" + str);
    return reg.test(this);
};
const Request = {
    get: function (ajax) {
        req({
            url: this.getUrl(ajax),
            method: 'GET',
            headers: this.getHeaders(),
            success: function (resp) {
                if (!('status' in resp)) {
                    console.log('Unknown response:');
                    console.log(resp);
                    if ('fail' in ajax) ajax.fail({status: -1, message: ''});
                } else if (resp.status == 200) {
                    if ('success' in ajax) ajax.success(resp);
                } else {
                    if ('fail' in ajax) ajax.fail(resp);
                }
            },
            error: function (e) {
                if ('fail' in ajax) ajax.fail({status: -1, message: e.message})
            }
        })
    },
    post: function (ajax) {
        req({
            url: this.getUrl(ajax),
            method: 'POST',
            data: ajax.data,
            headers: this.getHeaders(),
            success: function (resp) {
                if (!('status' in resp)) {
                    console.log('Unknown response:' + resp);
                    if ('fail' in ajax) ajax.fail({status: -1, message: ''});
                } else if (resp.status == 200) {
                    if ('success' in ajax) ajax.success(resp);
                } else {
                    if ('fail' in ajax) ajax.fail(resp);
                }
            },
            error: function (e) {
                if ('fail' in ajax) ajax.fail({status: -1, message: e.message});
            }
        })
    },
    getUrl: function (ajax) {
        var url = ajax.url.startWith('http') ? ajax.url : conf.host + conf.api_version + ajax.url;
        if ('params' in ajax) {
            url += '?';
            for (var name in ajax.params) {
                url += name + '=' + ajax.params[name] + '&';
            }
            url = url.substring(0, url.length - 1);
        }
        return url;
    },
    getHeaders: function () {
        return {'X-TimeStamp': new Date().getTime()}
    }
};
module.exports = Request;