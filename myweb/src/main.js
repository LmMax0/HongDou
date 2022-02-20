
import { createApp } from "vue/dist/vue.esm-bundler";
import App from './App.vue'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css';
import Chat from 'vue3-beautiful-chat'


const app = createApp(App)
app.use(Chat)
app.mount('#root')
