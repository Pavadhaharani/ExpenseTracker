-- ===================================================================
-- Seed data — runs on every startup; INSERT IGNORE makes it idempotent
-- ===================================================================

-- Roles
INSERT IGNORE INTO roles (name) VALUES
  ('ROLE_USER'),
  ('ROLE_ADMIN');

-- Default expense categories
INSERT IGNORE INTO categories (name, icon, color, is_default, created_at) VALUES
  ('Food',          'bi-basket2-fill',      '#FF6B6B', 1, CURRENT_TIMESTAMP),
  ('Travel',        'bi-airplane-fill',     '#4ECDC4', 1, CURRENT_TIMESTAMP),
  ('Shopping',      'bi-bag-fill',          '#45B7D1', 1, CURRENT_TIMESTAMP),
  ('Bills',         'bi-receipt-cutoff',    '#96CEB4', 1, CURRENT_TIMESTAMP),
  ('Entertainment', 'bi-film',              '#FFEAA7', 1, CURRENT_TIMESTAMP),
  ('Others',        'bi-three-dots-circle', '#DDA0DD', 1, CURRENT_TIMESTAMP);

-- Repair older seed rows created with MySQL's zero datetime sentinel.
UPDATE categories
SET created_at = CURRENT_TIMESTAMP
WHERE CAST(created_at AS CHAR) LIKE '0000-00-00%';
