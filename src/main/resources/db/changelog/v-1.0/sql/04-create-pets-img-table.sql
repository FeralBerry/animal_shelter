CREATE TABLE pets_img (
    id bigint not null primary key,
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    pet_id bigint not null ,
    file_id text
)
GO