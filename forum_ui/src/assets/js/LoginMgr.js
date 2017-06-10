import Cookie from './Cookie.js';
import Event from './Event.js';


class LoginMgr {

  info() {
    let uid = Cookie.get("X-App-UID");
    let username = Cookie.get("X-App-Name");
    let email = Cookie.get("X-App-Email");
    let token = Cookie.get("X-App-Token");
    let role = Cookie.get('X-App-Role');
    if (uid && uid.length > 0
      && username && username.length > 0
      && email && email.length > 0
      && token && token.length > 0) {
      return {
        uid: uid,
        username: username,
        email: email,
        token: token,
        role: role
      }
    } else {
      return false
    }
  }

  check(loginMode, guestMode) {
    let userInfo = this.info();
    if (userInfo) {
      return loginMode ? loginMode(userInfo) : '';
    } else {
      return guestMode ? guestMode() : '';
    }
  }

  isAdmin() {
    let info = this.info();
    return info && info.role === '1'
  }

  require(loginAlready) {
    let info = this.info();
    if (info) {
      loginAlready(info)
    } else {
      Event.emit('request_login', loginAlready)
    }
  }

  login(data) {
    console.log(data);
    Cookie.set('X-App-Name', data.username);
    Cookie.set('X-App-Email', data.email);
    Cookie.set('X-App-UID', data.uid);
    Cookie.set('X-App-Token', data.token);
    Cookie.set('X-App-Role', data.role);
    Event.emit('login');
  }

  logout() {
    Cookie.del('X-App-Email');
    Cookie.del('X-App-Token');
    Cookie.del('X-App-UID');
    Cookie.del('X-App-Name');
    Cookie.del('X-App-Role');
    Event.emit('login');
  }

}
export default new LoginMgr();
