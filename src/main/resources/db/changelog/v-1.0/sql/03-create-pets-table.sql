CREATE TABLE pets (
    id bigserial primary key,
    pet_name varchar(100) not null,
    description text default null
)
GO