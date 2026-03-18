-- V3__add_stripe_payment_intent_column.sql

ALTER TABLE payments ADD COLUMN stripe_payment_intent_id VARCHAR(255);

-- Créer un index sur stripe_payment_intent_id pour les requêtes rapides
CREATE INDEX idx_payments_stripe_payment_intent_id ON payments(stripe_payment_intent_id);
