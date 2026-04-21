/* ============================================================
   ELO — script.js
   Drives both login.html and app.html
   Backend: GET /products  |  POST /select (user=X&id=Y)
   ============================================================ */

'use strict';

/* ──────────────────────────────────────────
   CONSTANTS
   ────────────────────────────────────────── */
const API = 'http://localhost:8080';

const USERS = [
  { id: 'User_1', name: 'AlexR',  role: 'Tech Enthusiast',   avatar: 'https://randomuser.me/api/portraits/men/32.jpg'   },
  { id: 'User_2', name: 'Megan',  role: 'Book Lover',         avatar: 'https://randomuser.me/api/portraits/women/44.jpg' },
  { id: 'User_3', name: 'Jason',  role: 'Gamer & Movie Buff', avatar: 'https://randomuser.me/api/portraits/men/75.jpg'   },
  { id: 'User_4', name: 'Sophia', role: 'Beauty & Fashion',   avatar: 'https://randomuser.me/api/portraits/women/68.jpg' },
  { id: 'User_5', name: 'Liam',   role: 'Sports Fanatic',     avatar: 'https://randomuser.me/api/portraits/men/10.jpg'   },
];

// Unsplash photo IDs mapped to backend product IDs
const PROD_IMG = {
  1: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600&q=80', // Thriller Novel
  2: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=600&q=80', // Mystery Book
  3: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&q=80', // Sci-Fi Book
  4: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&q=80', // Bose Headphones
  5: 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600&q=80', // PS5 Controller
  6: 'https://images.unsplash.com/photo-1543512214-318c7553f230?w=600&q=80', // Smart Speaker
  7: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=600&q=80', // Leather Jacket
  8: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80', // Sneakers
};

const FALLBACK_IMG = 'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=600&q=80';

/* ──────────────────────────────────────────
   PAGE DETECTION
   ────────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
  if (document.getElementById('user-grid')) {
    initLogin();
  } else if (document.getElementById('app-page')) {
    initApp();
  }
});

/* ══════════════════════════════════════════
   LOGIN PAGE
   ══════════════════════════════════════════ */

function initLogin() {
  spawnParticles();
  buildUserCards();
}

/* ── Particle canvas ── */
function spawnParticles() {
  const canvas = document.getElementById('particles');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');

  function resize() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;
  }
  resize();
  window.addEventListener('resize', resize);

  const count = 60;
  const particles = Array.from({ length: count }, () => makeParticle());

  function makeParticle() {
    return {
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight + window.innerHeight,
      size: Math.random() * 2 + 0.6,
      speed: Math.random() * 0.6 + 0.25,
      opacity: Math.random() * 0.55 + 0.15,
      drift: (Math.random() - 0.5) * 0.4,
    };
  }

  function tick() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(p => {
      p.y  -= p.speed;
      p.x  += p.drift;
      if (p.y < -10) Object.assign(p, makeParticle(), { y: canvas.height + 10 });

      ctx.beginPath();
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255,0,102,${p.opacity})`;
      ctx.fill();
    });
    requestAnimationFrame(tick);
  }
  tick();
}

/* ── Build user cards ── */
function buildUserCards() {
  const grid = document.getElementById('user-grid');
  USERS.forEach((user, i) => {
    const card = document.createElement('div');
    card.className = 'user-card';
    card.style.animationDelay = `${i * 0.1}s`;
    card.innerHTML = `
      <div class="av-wrap">
        <img
          src="${user.avatar}"
          alt="${user.name}"
          class="av-img"
          onerror="this.src='https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}&background=ff0066&color=fff&size=200'"
        />
        <div class="av-ring"></div>
      </div>
      <div class="u-name">${user.name}</div>
      <div class="u-role">${user.role}</div>
      <div class="u-badge">
        <span class="u-dot"></span>ELO ACTIVE
      </div>
    `;
    card.addEventListener('click', () => loginAs(user));
    grid.appendChild(card);
  });
}

/* ── Select user → navigate ── */
function loginAs(user) {
  localStorage.setItem('eloUser', JSON.stringify(user));
  document.body.classList.add('fade-out');
  setTimeout(() => { window.location.href = 'app.html'; }, 580);
}

/* ══════════════════════════════════════════
   APP PAGE
   ══════════════════════════════════════════ */

/* State */
let allProducts  = [];
let activeFilter = null;   // null = default view (top-per-category)
let searchQuery  = '';

async function initApp() {
  // Auth guard
  const stored = localStorage.getItem('eloUser');
  if (!stored) { window.location.href = 'login.html'; return; }
  const user = JSON.parse(stored);

  // Render nav user
  const navUser = document.getElementById('nav-user');
  if (navUser) {
    navUser.innerHTML = `
      <img
        src="${user.avatar}"
        alt="${user.name}"
        class="nav-avatar"
        onerror="this.src='https://ui-avatars.com/api/?name=${encodeURIComponent(user.name)}&background=ff0066&color=fff&size=80'"
      />
      <span>${user.name}</span>
    `;
  }

  // Navbar scroll effect
  const navbar = document.getElementById('navbar');
  window.addEventListener('scroll', () => {
    navbar && navbar.classList.toggle('scrolled', window.scrollY > 50);
  }, { passive: true });

  // Hero CTA buttons
  document.getElementById('btn-explore')?.addEventListener('click', () =>
    document.getElementById('categories')?.scrollIntoView({ behavior: 'smooth' })
  );
  document.getElementById('btn-how')?.addEventListener('click', () =>
    document.getElementById('how-elo')?.scrollIntoView({ behavior: 'smooth' })
  );

  // Search — real-time filter
  const searchInput = document.getElementById('search-input');
  if (searchInput) {
    searchInput.addEventListener('input', e => {
      searchQuery = e.target.value.trim().toLowerCase();
      // If search is cleared, revert to active category or default
      if (!searchQuery) activeFilter = null;
      renderProducts();
    });
  }

  // Search button
  document.querySelector('.search-btn')?.addEventListener('click', () => {
    const v = searchInput?.value.trim().toLowerCase() ?? '';
    searchQuery = v;
    if (!v) activeFilter = null;
    renderProducts();
  });

  // Category cards
  document.querySelectorAll('.cat-card').forEach(card => {
    card.addEventListener('click', () => {
      const cat = card.dataset.category;
      // Toggle: clicking same category returns to default
      if (activeFilter === cat) {
        activeFilter = null;
        card.classList.remove('active');
      } else {
        activeFilter = cat;
        document.querySelectorAll('.cat-card').forEach(c => c.classList.remove('active'));
        card.classList.add('active');
      }
      // Clear search when browsing by category
      searchQuery = '';
      if (searchInput) searchInput.value = '';
      renderProducts();
      // Smooth scroll to products
      document.querySelector('.prod-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
  });

  // Scroll reveal
  initReveal();

  // Load products from backend
  await loadProducts();
}

/* ── Load products ── */
async function loadProducts() {
  showSkeleton();
  try {
    const res = await fetch(`${API}/products`);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    allProducts = await res.json();
    renderProducts();
  } catch (err) {
    console.error('[ELO] Failed to load products:', err);
    showGridError();
  }
}

/* ── Derive displayed product list ── */
function getDisplayList() {
  let list = [...allProducts];

  // Search: overrides everything
  if (searchQuery) {
    return list.filter(p =>
      p.name.toLowerCase().includes(searchQuery) ||
      p.category.toLowerCase().includes(searchQuery)
    );
  }

  // Category filter
  if (activeFilter) {
    return list.filter(p => p.category === activeFilter)
               .sort((a, b) => b.rating - a.rating);
  }

  // Default: top-1 per category
  const best = {};
  list.forEach(p => {
    if (!best[p.category] || p.rating > best[p.category].rating) {
      best[p.category] = p;
    }
  });
  return Object.values(best).sort((a, b) => b.rating - a.rating);
}

/* ── Render product grid ── */
function renderProducts() {
  const grid  = document.getElementById('prod-grid');
  const title = document.getElementById('prod-title');
  if (!grid) return;

  const list = getDisplayList();

  // Update section title
  if (title) {
    if (searchQuery)       title.textContent = 'Search Results';
    else if (activeFilter) title.textContent = activeFilter;
    else                   title.textContent = 'Top Rated Products';
  }

  if (list.length === 0) {
    grid.innerHTML = `
      <div class="grid-msg">
        <div class="gm-icon">🔍</div>
        <h3>No Results</h3>
        <p>No products match "<strong>${searchQuery}</strong>"</p>
      </div>
    `;
    return;
  }

  grid.innerHTML = list.map((p, i) => productCardHTML(p, i)).join('');

  // Wire choose buttons
  grid.querySelectorAll('.choose-btn').forEach(btn => {
    btn.addEventListener('click', () => chooseProduct(+btn.dataset.id, btn));
  });

  // Stagger entrance animation
  grid.querySelectorAll('.pc').forEach((card, i) => {
    card.style.animationDelay = `${i * 0.07}s`;
  });
}

/* ── Build product card HTML ── */
function productCardHTML(p, index) {
  const img     = PROD_IMG[p.id] ?? FALLBACK_IMG;
  const rating  = Math.round(p.rating);
  // Bar width: 0% at 800, 100% at 1200
  const barPct  = Math.min(100, Math.max(0, ((p.rating - 800) / 400) * 100));

  return `
    <div class="pc" style="animation-delay:${index * 0.07}s">
      <div class="pc-img-wrap">
        <img
          src="${img}"
          alt="${p.name}"
          class="pc-img"
          loading="lazy"
          onerror="this.src='${FALLBACK_IMG}'"
        />
        <div class="pc-overlay"></div>
        <span class="pc-cat-tag">${p.category}</span>
      </div>
      <div class="pc-body">
        <h3 class="pc-name">${p.name}</h3>
        <div class="pc-elo-row">
          <div class="pc-elo-score">
            <span class="pc-elo-lbl">ELO</span>
            <span class="pc-elo-val">${rating}</span>
          </div>
          <div class="pc-bar-wrap">
            <div class="pc-bar" style="width:${barPct}%"></div>
          </div>
        </div>
        <button class="choose-btn" data-id="${p.id}">
          <span>Choose This</span>
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" stroke-width="2.5">
            <polyline points="9,18 15,12 9,6"/>
          </svg>
        </button>
      </div>
    </div>
  `;
}

/* ── Choose product → POST /select ── */
async function chooseProduct(productId, btn) {
  const stored = localStorage.getItem('eloUser');
  if (!stored) return;
  const user = JSON.parse(stored);

  // Button loading state
  if (btn) {
    btn.classList.add('busy');
    btn.innerHTML = '<span class="spinner"></span>';
  }

  try {
    const res = await fetch(`${API}/select`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: `user=${encodeURIComponent(user.id)}&id=${productId}`,
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    allProducts = await res.json();
    toast('ELO ratings updated! 🎯');
    renderProducts();

  } catch (err) {
    console.error('[ELO] Select failed:', err);
    toast('Could not reach the server. Is it running on :8080?', true);
    // Restore button
    if (btn) {
      btn.classList.remove('busy');
      btn.innerHTML = `<span>Choose This</span>
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
             stroke="currentColor" stroke-width="2.5">
          <polyline points="9,18 15,12 9,6"/>
        </svg>`;
    }
  }
}

/* ── Skeleton loading state ── */
function showSkeleton() {
  const grid = document.getElementById('prod-grid');
  if (!grid) return;
  grid.innerHTML = Array(3).fill(0).map(() => `
    <div class="pc skeleton">
      <div class="sk-img"></div>
      <div class="sk-body">
        <div class="sk-line w80"></div>
        <div class="sk-line w60"></div>
        <div class="sk-line w40"></div>
      </div>
    </div>
  `).join('');
}

/* ── Error state ── */
function showGridError() {
  const grid = document.getElementById('prod-grid');
  if (!grid) return;
  grid.innerHTML = `
    <div class="grid-msg">
      <div class="gm-icon">⚠️</div>
      <h3>Backend Offline</h3>
      <p>Make sure the Java server is running on <strong>port 8080</strong></p>
      <button class="retry-btn" onclick="loadProducts()">Retry Connection</button>
    </div>
  `;
}

/* ── Toast notification ── */
function toast(msg, isErr = false) {
  document.querySelector('.toast')?.remove();
  const el = document.createElement('div');
  el.className = `toast${isErr ? ' err' : ''}`;
  el.textContent = msg;
  document.body.appendChild(el);
  requestAnimationFrame(() => requestAnimationFrame(() => el.classList.add('show')));
  setTimeout(() => {
    el.classList.remove('show');
    setTimeout(() => el.remove(), 420);
  }, 3200);
}

/* ── Scroll reveal ── */
function initReveal() {
  const obs = new IntersectionObserver(entries => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.classList.add('in');
        obs.unobserve(e.target);
      }
    });
  }, { threshold: 0.1 });

  document.querySelectorAll('.reveal').forEach(el => obs.observe(el));
}