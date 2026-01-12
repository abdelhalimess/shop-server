-- --- SHOPS ---
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (1, 'Boutique 1', '2021-11-28', false);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (5, 'Boutique 2', '2012-06-25', true);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (10, 'Boutique 3', '2022-01-09', false);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (13, 'Boutique 4', '2020-04-05', false);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (17, 'Boutique 5', '2017-12-15', true);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (21, 'Boutique 6', '2010-01-03', true);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (27, 'Boutique 7', '2015-08-20', false);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (30, 'Boutique 8', '2019-06-28', false);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (34, 'Boutique 9', '2016-05-16', false);
INSERT INTO shops (id, name, created_at, in_vacations) VALUES (39, 'Boutique 10', '2015-02-17', false);

-- --- OPENING HOURS (Linked via shop_id) ---
-- Shop 1
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (1, '09:00:00', '18:00:00', 1);
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (2, '09:00:00', '18:00:00', 1);
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (5, '08:00:00', '17:00:00', 1);
-- Shop 5
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (1, '08:45:00', '17:30:00', 5);
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (2, '08:45:00', '17:00:00', 5);
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (3, '08:45:00', '18:30:00', 5);
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (4, '08:45:00', '12:30:00', 5);
-- Shop 10
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (6, '08:45:00', '20:30:00', 10);
INSERT INTO opening_hours (day, open_at, close_at, shop_id) VALUES (7, '08:15:00', '20:00:00', 10);

-- --- CATEGORIES ---
INSERT INTO categories (id, name) VALUES (41, 'Nourriture'), (42, 'Multimédia'), (43, 'Vêtement'), (44, 'Chaussure'), (45, 'Electroménager'), (46, 'Boisson'), (47, 'Bio'), (48, 'Cuisine'), (49, 'Salle de bain'), (50, 'Meuble'), (51, 'Maquillage'), (52, 'Parfum');

-- --- PRODUCTS & LOCALIZED PRODUCTS ---

-- Product 53 (Shop 1, Price 10.99)
INSERT INTO products (id, price, shop_id) VALUES (53, 1099, 1);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 1', 'Desc FR P1', 53);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('EN', 'Product 1', 'Desc EN P1', 53);
INSERT INTO products_categories (product_id, category_id) VALUES (53, 43);

-- Product 56
INSERT INTO products (id, price, shop_id) VALUES (56, 199, 1);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 2', 'Desc FR P2', 56);
INSERT INTO products_categories (product_id, category_id) VALUES (56, 46);

-- Product 58
INSERT INTO products (id, price, shop_id) VALUES (58, 199, 5);
INSERT INTO localized_product (locale, name, product_id) VALUES ('FR', 'Produit 3', 58);

-- Product 60 (No Shop)
INSERT INTO products (id, price, shop_id) VALUES (60, 499, null);
INSERT INTO localized_product (locale, name, product_id) VALUES ('FR', 'Produit 4', 60);
INSERT INTO localized_product (locale, name, product_id) VALUES ('EN', 'Product 4', 60);
INSERT INTO products_categories (product_id, category_id) VALUES (60, 48);

-- Product 63
INSERT INTO products (id, price, shop_id) VALUES (63, 10000, 1);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 5', 'Desc FR P5', 63);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('EN', 'Product 5', 'Desc EN P5', 63);

-- Product 66
INSERT INTO products (id, price, shop_id) VALUES (66, 2199, 34);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 6', 'Desc FR P6', 66);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('EN', 'Product 6', 'Desc EN P6', 66);

-- Product 69
INSERT INTO products (id, price, shop_id) VALUES (69, 999, 10);
INSERT INTO localized_product (locale, name, product_id) VALUES ('FR', 'Produit 7', 69);
INSERT INTO products_categories (product_id, category_id) VALUES (69, 41);

-- Product 71
INSERT INTO products (id, price, shop_id) VALUES (71, 1500, 27);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 8', 'Desc FR P8', 71);

-- Product 77
INSERT INTO products (id, price, shop_id) VALUES (77, 10000, 13);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 11', 'Desc FR P11', 77);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('EN', 'Product 11', 'Desc EN P11', 77);

-- Product 80
INSERT INTO products (id, price, shop_id) VALUES (80, 9999, 27);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 12', 'Desc FR P12', 80);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('EN', 'Product 12', 'Desc EN P12', 80);
INSERT INTO products_categories (product_id, category_id) VALUES (80, 43);

-- Product 85
INSERT INTO products (id, price, shop_id) VALUES (85, 99, 1);
INSERT INTO localized_product (locale, name, description, product_id) VALUES ('FR', 'Produit 14', 'Desc FR P14', 85);

-- Product 91
INSERT INTO products (id, price, shop_id) VALUES (91, 199, 21);
INSERT INTO localized_product (locale, name, product_id) VALUES ('FR', 'Produit 17', 91);
INSERT INTO localized_product (locale, name, product_id) VALUES ('EN', 'Produit 17', 91);


-- ============================================================
-- 5. SEQUENCE RESET (Mandatory for BIGSERIAL + Hardcoded IDs)
-- ============================================================
-- This tells Postgres: "The next ID you generate must be greater than the ones I just inserted manually"

SELECT setval('shops_id_seq', (SELECT MAX(id) FROM shops));
SELECT setval('opening_hours_id_seq', (SELECT MAX(id) FROM opening_hours));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('localized_product_id_seq', (SELECT MAX(id) FROM localized_product));