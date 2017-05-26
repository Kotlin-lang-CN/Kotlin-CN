import Cookie from './Cookie.js';
import Cache from './Cache.js';

class LoginMgr {
  constructor() {
    this.isLogin = true;
    this.uid = Cookie.get('uid');
    this.token = Cookie.get('token');
    this.username = Cache.get('username');
    this.email = Cache.get('email');
    if (this.uid.length === 0 || this.token.length === 0) {
      this.logout();
    }
  }

  login(data) {
    this.isLogin = true;
    this.uid = data.uid;
    this.username = data.username;
    this.token = data.token;
    this.email = data.email;
    Cookie.set('uid', data.uid);
    Cookie.set('token', data.token);
    Cache.set('username', data.username);
    Cache.set('email', data.email);
  }

  logout() {
    this.isLogin = false;
  }
}
export default new LoginMgr();
