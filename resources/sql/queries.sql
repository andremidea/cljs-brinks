-- name: create-user!
-- creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- name: update-user!
-- update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id

-- name: create-project<!
-- create a project
insert into project
(name, goals, argument, expected_results)
values (:name, :goals, :argument, :expected)

-- name: list-projects
select * from project


-- name: get-project
select * from project where id = :id
