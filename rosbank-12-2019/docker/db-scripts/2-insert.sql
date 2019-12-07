INSERT INTO tariff_plans (title)
values ('default');

INSERT INTO clients (id, first_name, last_name, tariff_plan_id)
SELECT uuid_generate_v1() id, 'asd' first_name, 'asdf' last_name, (SELECT id from tariff_plans limit 1) tariff_plan_id
FROM generate_series(1, 100);
