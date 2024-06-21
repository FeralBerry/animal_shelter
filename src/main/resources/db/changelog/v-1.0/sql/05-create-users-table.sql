CREATE TABLE users (
    chat_id bigint not null primary key,
    first_name varchar(100) default null,
    last_name varchar(100) default null,
    user_name varchar(100) default null,
    role varchar(50) default null,
    location_user_on_app varchar(255) default null,
    pet_id bigint,
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    added_pet_id bigint,
    FOREIGN KEY (added_pet_id) REFERENCES pets(id)
)
GO