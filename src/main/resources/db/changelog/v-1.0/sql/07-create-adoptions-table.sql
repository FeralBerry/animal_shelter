CREATE TABLE adoptions (
    id bigint not null primary key,
    pet_id bigint,
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    user_chat_id bigint,
    FOREIGN KEY (user_chat_id) REFERENCES users(chat_id),
    adopt_at bigint
)
GO