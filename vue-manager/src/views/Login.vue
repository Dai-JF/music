<template>
  <div class="login-wrap">
    <div class="ms-title">{{nusicName}}</div>
    <div class="ms-login">
      <el-form
        ref="ruleForm"
        class="demo-ruleForm"
        :model="ruleForm"
        :rules="rules"
      >
        <el-form-item prop="username">
          <el-input v-model="ruleForm.username" placeholder="username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            type="password"
            placeholder="password"
            v-model="ruleForm.password"
            @keyup.enter="submitForm('ruleForm')"
          ></el-input>
        </el-form-item>
        <div class="login-btn">
          <el-button type="primary" @click="submitForm">登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mixin } from '../mixins'
import { HttpManager } from '../api/index'
import { INFO, MUSICNAME } from '../enums'

export default {
  mixins: [mixin],
  data: function () {
    return {
      nusicName: MUSICNAME,
      ruleForm: {
        username: 'admin',
        password: '123'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    submitForm () {
      let params = new URLSearchParams()
      params.append('name', this.ruleForm.username)
      params.append('password', this.ruleForm.password)
      HttpManager.getLoginStatus(params)
        .then(res => {
          if (res.code === 1) {
            this.routerManager(INFO, { path: INFO })
            this.$notify({
              title: '欢迎回来',
              type: 'success'
            })
          } else {
            this.$notify({
              title: '登录失败',
              type: 'error'
            })
          }
        })
        .catch(err => {
          console.error(err)
        })
    }
  }
}
</script>

<style scoped>
.login-wrap {
  position: relative;
  background-image: linear-gradient(#e66465, #9198e5);
  background-attachment: fixed;
  background-position: center;
  background-size: cover;
  width: 100%;
  height: 100%;
}

.ms-title {
  position: absolute;
  top: 50%;
  width: 100%;
  margin-top: -230px;
  text-align: center;
  font-size: 30px;
  font-weight: 600;
  color: #fff;
}

.ms-login {
  position: absolute;
  left: 50%;
  top: 50%;
  width: 300px;
  height: 160px;
  margin: -150px 0 0 -190px;
  padding: 40px;
  border-radius: 5px;
  background: #fff;
}

.login-btn {
  text-align: center;
}

.login-btn button {
  width: 100%;
  height: 36px;
}
</style>
