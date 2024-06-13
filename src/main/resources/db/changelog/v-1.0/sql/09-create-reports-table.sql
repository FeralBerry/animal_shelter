CREATE TABLE reports (
    id bigint not null primary key,
    text text,
    pet_id bigint,
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    user_chat_id bigint,
    FOREIGN KEY (user_chat_id) REFERENCES users(chat_id),
    updated_at bigint,
    checked boolean,
    looked boolean
)
GO