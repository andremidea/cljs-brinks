create table project (id SERIAL primary key, name varchar, goals varchar, argument varchar, expected_results varchar, comments varchar, approved boolean);
create table step (id SERIAL primary key, name varchar);
create table step_by_step (id SERIAL primary key, project_id int references project, step_id int references step, description varchar);



