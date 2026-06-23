import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 1. 注册 Pinia
app.use(createPinia())
// 2. 注册路由
app.use(router)
// 3. 注册 Element Plus
app.use(ElementPlus)

// 4. 全局注册所有图标 (这样在任何页面都可以直接使用 <el-icon><User /></el-icon>)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')