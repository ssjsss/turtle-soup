const CACHE = 'turtlesoup-v1';
const ASSETS = [
  '/index.html', '/login.html', '/game.html',
  '/history.html', '/profile.html', '/leaderboard.html',
  '/upload.html', '/my-submissions.html',
  '/api.js', '/manifest.json'
];

self.addEventListener('install', e => {
  e.waitUntil(
    caches.open(CACHE).then(c => c.addAll(ASSETS))
  );
});

self.addEventListener('fetch', e => {
  // API 请求不缓存
  if (e.request.url.includes('/api/')) {
    return;
  }
  e.respondWith(
    caches.match(e.request).then(r => r || fetch(e.request))
  );
});
