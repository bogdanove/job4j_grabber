create table if not exists post (
    id serial primary key,
    name varchar(100),
    text text,
    link varchar(100) unique,
    created timestamp
);