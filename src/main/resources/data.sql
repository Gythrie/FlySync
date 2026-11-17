INSERT INTO airports (airport_name, city, country, iata_code)
VALUES
('Cochin International Airport', 'Kochi', 'India', 'COK'),
('Indira Gandhi International Airport', 'Delhi', 'India', 'DEL'),
('Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India', 'BOM'),
('Kempegowda International Airport', 'Bengaluru', 'India', 'BLR'),
('Chennai International Airport', 'Chennai', 'India', 'MAA'),
('Rajiv Gandhi International Airport', 'Hyderabad', 'India', 'HYD'),
('Netaji Subhas Chandra Bose International Airport', 'Kolkata', 'India', 'CCU'),
('Goa International Airport', 'Goa', 'India', 'GOI'),
('Trivandrum International Airport', 'Thiruvananthapuram', 'India', 'TRV')
ON CONFLICT (iata_code) DO NOTHING;
