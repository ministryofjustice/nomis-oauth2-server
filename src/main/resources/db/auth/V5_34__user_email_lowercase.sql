update users
set email = lower(email)
where email != lower(email)
