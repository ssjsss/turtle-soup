// API 请求工具
const API_BASE = '';

const api = {
    token: localStorage.getItem('token') || '',

    setToken(token) {
        this.token = token;
        localStorage.setItem('token', token);
    },

    clearToken() {
        this.token = '';
        localStorage.removeItem('token');
    },

    getHeaders() {
        const headers = { 'Content-Type': 'application/json' };
        if (this.token) {
            headers['Authorization'] = 'Bearer ' + this.token;
        }
        return headers;
    },

    async request(method, url, body) {
        const opts = { method, headers: this.getHeaders() };
        if (body) opts.body = JSON.stringify(body);
        const res = await fetch(API_BASE + url, opts);
        const data = await res.json();
        if (data.code === 401) {
            this.clearToken();
            window.location.href = '/login.html';
            throw new Error(data.message);
        }
        if (data.code !== 200) {
            throw new Error(data.message || '请求失败');
        }
        return data.data;
    },

    get(url) { return this.request('GET', url); },
    post(url, body) { return this.request('POST', url, body); },
    put(url, body) { return this.request('PUT', url, body); }
};
