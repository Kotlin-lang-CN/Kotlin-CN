<template>
  <div class="dialog" v-if="showMode !==0">
    <div class="bg" v-on:click="hide"></div>
    <div class="cont">
      <div class="header"><span class="blue">Kotlin</span>China</div>
      <div class="content">
        <span>输入文章名称【{{}}】</span>
        <input v-model="nameInput" type="text" name="user" placeholder="用户名或邮箱"/>
        <span v-if="error && error.key == 'login-name'" class="error">{{ error.value }}</span>
        <input v-model="passwordInput" type="password" name="password" placeholder="用户密码"/>
        <span v-if="error && error.key == 'login-password'" class="error">{{ error.value }}</span>
        <button v-on:click="login" class="big-btn">登录</button>
        <div class="small-btn">
          <button v-on:click="forget">忘记密码</button>
          <button v-on:click="switchMode">我还没有账号</button>
        </div>
      </div>
      <div class="register" v-if="showMode === 2">
        <input v-model="emailInput" type="text" name="user" placeholder="邮箱"/><br>
        <span v-if="error && error.key == 'register-email'" class="error">{{ error.value }}</span>
        <input v-model="nameInput" type="text" name="user" placeholder="用户名"/><br>
        <span v-if="error && error.key == 'register-name'" class="error">{{ error.value }}</span>
        <input v-model="passwordInput" type="password" name="password" placeholder="密码"/><br>
        <span v-if="error && error.key == 'register-password'" class="error">{{ error.value }}</span>
        <input v-model="passwordRepeatInput" v-if="passwordInput.length >=8"
               type="password" name="password" placeholder="再次输入密码"/>
        <span v-if="error && error.key == 'register-password-repeat'" class="error">{{ error.value }}</span>
        <button v-on:click="register" class="big-btn">注册</button>
        <div class="small-btn">
          <button v-on:click="switchMode">已有账号，去登陆</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>


</script>

<style scoped lang="less">
</style>
