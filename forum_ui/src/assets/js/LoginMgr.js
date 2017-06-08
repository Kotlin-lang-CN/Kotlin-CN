import Cookie from './Cookie.js';
import Cache from './Cache.js';
import Event from './Event.js';


class LoginMgr {

  info() {
    let uid = Cookie.get("X-App-UID");
    let username = Cookie.get("X-App-Name");
    let email = Cookie.get("X-App-Email");
    let token = Cookie.get("X-App-Token");
    if (uid && uid.length > 0
      && username && username.length > 0
      && email && email.length > 0
      && token && token.length > 0) {
      return {
        uid: uid,
        username: username,
        email: email,
        token: token
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

  require(loginAlready) {
    if (this.info()) {
      loginAlready()
    } else {
      Event.emit('require_login', loginAlready)
    }
  }

  login(data) {
    console.log(data);
    Cookie.set('X-App-Name', data.username);
    Cookie.set('X-App-Email', data.email);
    Cookie.set('X-App-UID', data.uid);
    Cookie.set('X-App-Token', data.token);
    Event.emit('login');
  }

  logout() {
    Cookie.del('X-App-Email');
    Cookie.del('X-App-Token');
    Cookie.del('X-App-UID');
    Cookie.del('X-App-Name');
    Event.emit('login');
  }

}
export default new LoginMgr();
