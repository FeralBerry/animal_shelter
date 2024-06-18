CREATE TABLE calls (
    id bigserial primary key,
    admin_chat_id bigint,
    FOREIGN KEY (admin_chat_id) REFERENCES users(chat_id),
    user_chat_id bigint,
    FOREIGN KEY (user_chat_id) REFERENCES users(chat_id),
    updated_at bigint
)
GO