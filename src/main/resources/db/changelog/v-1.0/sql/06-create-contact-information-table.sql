CREATE TABLE contact_information (
    id bigint not null primary key,
    phone varchar(20) default null,
    name text default null,
    user_chat_id bigint,
    FOREIGN KEY (user_chat_id) REFERENCES users(chat_id)
)
GO