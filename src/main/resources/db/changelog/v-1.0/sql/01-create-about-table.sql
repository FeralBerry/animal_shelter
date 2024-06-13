CREATE TABLE about (
    id bigint not null primary key,
    shelter_name varchar(100) default null,
    schedule text default null,
    security_contacts text default null,
    safety_precautions text default null
)
GO