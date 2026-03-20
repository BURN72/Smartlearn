-- V6__rename_order_to_order_index.sql
-- Rename "order" columns to order_index for consistency with JPA naming

-- Rename "order" column to order_index in modules table
ALTER TABLE modules RENAME COLUMN "order" TO order_index;

-- Rename "order" column to order_index in lessons table
ALTER TABLE lessons RENAME COLUMN "order" TO order_index;
