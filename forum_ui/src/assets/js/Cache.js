class Cache {
  set(key, value) {
    localStorage.setItem(key, value);
  }

  get(key) {
    let value = localStorage.getItem(key);
    return value === null ? '' : value;
  }

  del(key) {
    localStorage.removeItem(key);
  }
}
export default new Cache();
