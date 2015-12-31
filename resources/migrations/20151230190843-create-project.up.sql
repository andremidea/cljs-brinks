create table projects (id SERIAL primary key, nome varchar, goals varchar, argument varchar, expected_results varchar);
create table step (id SERIAL primary key, name varchar);
create table step_by_step (id SERIAL primary key, project_id int references projects, step_id int references step, description varchar);



