-- V5__add_payment_timestamps.sql

-- Add created_at column to payments table
ALTER TABLE payments ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add refunded_at column to payments table
ALTER TABLE payments ADD COLUMN refunded_at TIMESTAMP;

-- Create index on created_at for faster queries
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(created_at);
