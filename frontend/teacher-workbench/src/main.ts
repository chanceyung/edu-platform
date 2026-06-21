import { createApp } from 'vue'
import { createPinia } from 'pinia'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
// element-plus 组件由 unplugin-vue-components 自动按需导入，无需 app.use(ElementPlus)
app.mount('#app')
