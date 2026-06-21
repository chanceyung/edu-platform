<script setup lang lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { request } from '../../api/http'
import { setToken } from '../../utils/auth'

const router = useRouter()
const loading = ref(false)
const form = ref({ username: '', password: '' })

async function doLogin() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const data = await request<{ token: string; username: string; realName: string; roles: string[] }>({
      url: '/v1/auth/login', method: 'post', data: form.value
    })
    setToken(data.token)
    localStorage.setItem('edu_user', JSON.stringify(data))
    ElMessage.success(`欢迎，${data.realName || data.username}`)
    router.push('/')
  } catch (e) {
    ElMessage.error((e as Error).message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card">
      <h1 class="title">智慧教学平台</h1>
      <p class="subtitle">老师的 AI 数字员工</p>
      <el-form @submit.prevent="doLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large"
                    show-password @keyup.enter="doLogin" />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="doLogin">
          登 录
        </el-button>
      </el-form>
      <p class="hint">默认管理员：admin / admin123</p>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px 30px;
  border-radius: 12px;
}
.title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 4px;
}
.subtitle {
  text-align: center;
  color: #999;
  margin-bottom: 30px;
}
.hint {
  text-align: center;
  color: #ccc;
  font-size: 12px;
  margin-top: 16px;
}
</style>
