CREATE TABLE availability (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    professional_id UUID NOT NULL REFERENCES professionals(id),
    day_of_week     VARCHAR(20) NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_availability_time_range CHECK (end_time > start_time)
);

CREATE INDEX idx_availability_professional_day
    ON availability(professional_id, day_of_week);
