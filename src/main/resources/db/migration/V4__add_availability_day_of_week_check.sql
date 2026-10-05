ALTER TABLE availability
    ADD CONSTRAINT chk_availability_day_of_week
    CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));
