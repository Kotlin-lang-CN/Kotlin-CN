<template>
  <div class="drawer">
    <div class="cont">
      <div class="title">
        <span class="logo">Kotlin China</span>
        <span class="hint">使用 PC 登录, 体验完整功能</span>
      </div>
      <ul>
        <li><a href="http://www.kotliner.cn/">博客</a></li>
        <li><a href="https://www.kotlincn.net/">中文站</a></li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="less">
  .cont {
    .title {
      position: absolute;
      top: 40px;
      left: 0;
      margin-left: 20px;

      .logo {
        font-size: .40rem;
        color: white;
        display: inline-block;
        margin-left: 12px;
      }

      .hint {
        font-size: .20rem;
        color: #999;
        display: block;
        margin-left: 12px;
      }
    }
    ul {
      position: absolute;
      top: 100px;
      left: 0;
      right: 0;
      list-style-type: none;
      a {
        line-height: 60px;
        height: 60px;
        padding-left: 28px;
        display: block;
        color: white;
        text-decoration: none;
        border-left: 8px transparent solid;
      }
      a.sel {
        border-left: 8px #2b75e1 solid;
        background-color: #2b3444;
      }
    }
  }
</style>
