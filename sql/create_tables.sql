-- ============================================================
-- 1. CLEANUP (Drop tables in reverse dependency order)
-- ============================================================
DROP TABLE IF EXISTS products_categories;
DROP TABLE IF EXISTS localized_product;
DROP TABLE IF EXISTS opening_hours;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS shops;
DROP TABLE IF EXISTS translation;

-- ============================================================
-- 2. DDL (Create Tables with BIGSERIAL for Auto-Increment)
-- ============================================================

create table categories (
    id bigserial not null,
    name varchar(255),
    primary key (id)
);

create table shops (
    id bigserial not null,
    created_at date,
    in_vacations boolean,
    name varchar(255),
    primary key (id)
);

create table opening_hours (
    id bigserial not null,
    day int4 not null check (day>=1 AND day<=7),
    open_at time not null,
    close_at time not null,
    shop_id int8, -- Direct link to Shop (ManyToOne)
    primary key (id)
);

create table products (
    id bigserial not null,
    price int8, -- bigint for centimes
    shop_id int8,
    primary key (id)
);

create table localized_product (
    id bigserial not null,
    locale varchar(255) not null,
    name varchar(255),
    description varchar(255),
    product_id int8, -- Direct link to Product (ManyToOne)
    primary key (id)
);

create table products_categories (
    product_id int8 not null,
    category_id int8 not null
);

-- ============================================================
-- 3. CONSTRAINTS (Foreign Keys)
-- ============================================================

alter table opening_hours
    add constraint FK_openinghours_shop
    foreign key (shop_id) references shops;

alter table products
    add constraint FK_products_shop
    foreign key (shop_id) references shops;

alter table localized_product
    add constraint FK_localized_product_product
    foreign key (product_id) references products;

alter table products_categories
    add constraint FK_prodcat_category
    foreign key (category_id) references categories;

alter table products_categories
    add constraint FK_prodcat_product
    foreign key (product_id) references products;