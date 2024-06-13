CREATE TABLE pets (
    id bigint not null primary key,
    pet_name varchar(100) not null,
    description text default null
)
GO