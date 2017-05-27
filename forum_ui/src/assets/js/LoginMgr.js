import Cookie from './Cookie.js';
import Cache from './Cache.js';
import Event from './Event.js';

class LoginMgr {
  constructor() {
    this.isLogin = true;
    this.uid = Cookie.get('uid');
    this.username = Cache.get('username');
    this.email = Cache.get('email');
    if (this.uid.length === 0 ||this.getToken().length === 0) {
      this.logout();
    }
  }

  login(data) {
    this.isLogin = true;
    this.uid = data.uid;
    this.username = data.username;
    this.email = data.email;
    Cookie.set('uid', data.uid);
    Cookie.set('token', data.token);
    Cache.set('username', data.username);
    Cache.set('email', data.email);
    Event.emit('login');
  }

  logout() {
    Cookie.del('uid');
    Cookie.del('token');
    Cache.del('username');
    Cache.del('email');
    this.uid = '';
    this.username = '';
    this.email = '';
    this.isLogin = false;
    Event.emit('login');
  }

  getToken(){
    return Cookie.get('token');
  }
}
export default new LoginMgr();
