create table users (id SERIAL primary key, name varchar, school varchar, city varchar, email varchar, login varchar, password varchar);

alter table project add column user_id int references users;
