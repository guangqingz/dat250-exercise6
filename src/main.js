import { mount } from 'svelte'
import './app.css'
// @ts-ignore
import App from './PollApp.svelte'
const app = mount(App, {
  target: document.getElementById('app'),
})

export default app
