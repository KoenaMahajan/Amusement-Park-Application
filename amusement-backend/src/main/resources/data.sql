-- Sample Ticket Types
INSERT INTO ticket_types (name, description, price, validity_days, is_vip) VALUES
('Basic Day Pass', 'Access to all basic rides and attractions', 25.00, 1, false),
('Premium Day Pass', 'Access to all rides including premium attractions', 45.00, 1, false),
('VIP Day Pass', 'All-access pass with priority queues and exclusive areas', 75.00, 1, true),
('Family Pack (4 tickets)', '4 basic day passes at discounted rate', 80.00, 1, false),
('Weekend Pass', 'Valid for Saturday and Sunday', 60.00, 2, false),
('VIP Weekend Pass', 'VIP access for the entire weekend', 120.00, 2, true),
('Monthly Pass', 'Unlimited access for 30 days', 200.00, 30, false),
('VIP Monthly Pass', 'VIP unlimited access for 30 days', 350.00, 30, true);
