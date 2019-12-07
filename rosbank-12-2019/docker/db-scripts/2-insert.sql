-- Удаление данных контейнера PostgreSQL:
-- docker stop $(docker ps -aq) && docker rm $(docker ps -aq) &&  docker volume rm docker_postgres-data -f

-- Добавляем тарифные планы
INSERT INTO tariff_plans (title)
VALUES ('default'),
       ('Тестовый тарифный план'),
       ('Тарифный план (1% на все покупки, 5% на 3 категории)');

-- Добавляем стратегии
INSERT INTO strategies (id, title, type, settings, tariff_plan_id)
VALUES (uuid_generate_v1(), 'Начисление бонусов на каждую покупку', 'INSTANT',
        '{"intervals":[{"from":0.0,"to":500.0,"ratio":0.01,"amount":null},{"from":500.0,"to":2000.0,"ratio":0.02,"amount":null},{"from":2000.0,"to":5000.0,"ratio":0.05,"amount":null},{"from":5000.0,"to":null,"ratio":null,"amount":12.0}],"mcc_list":[1111,2222,3333],"min_bonus":null,"max_bonus":10.0}',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тестовый тарифный план')),
       (uuid_generate_v1(), 'Начисление бонусов на сумму покупок за 7 дней', 'AGGREGATE_DATE',
        '{"intervals":[{"from":0.0,"to":500.0,"ratio":0.01,"amount":null},{"from":500.0,"to":2000.0,"ratio":0.02,"amount":null},{"from":2000.0,"to":5000.0,"ratio":0.05,"amount":null},{"from":5000.0,"to":null,"ratio":null,"amount":12.0}],"aggregate_time_settings":{"from_time":"2019-12-07T18:00:46.786+03:00","to_time":null,"time_unit":"DAYS","quantity":7},"aggregate_function":"SUM","mcc_list":[5111,2738,3921],"min_bonus":null,"max_bonus":10.0}',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тестовый тарифный план')),
       (uuid_generate_v1(), 'Начисление бонусов на количество покупок за месяц', 'AGGREGATE_DATE',
        '{"intervals":[{"from":0.0,"to":500.0,"ratio":0.01,"amount":null},{"from":500.0,"to":2000.0,"ratio":0.02,"amount":null},{"from":2000.0,"to":5000.0,"ratio":0.05,"amount":null},{"from":5000.0,"to":null,"ratio":null,"amount":12.0}],"aggregate_time_settings":{"from_time":"2019-12-07T18:00:46.786+03:00","to_time":null,"time_unit":"DAYS","quantity":7},"aggregate_function":"COUNT","mcc_list":[1111,2222,3333],"min_bonus":null,"max_bonus":10.0}',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тестовый тарифный план'));

-- Пример стратегии от организаторов:
-- Здесь для примера: 1% на все покупки, 5% на 3 категории. Категории определяются по MCC
-- Или если сумма покупок 10000 руб, то платим 1% кэша, если сумма покупок >10000, то платим 2% кэша от этой суммы
INSERT INTO strategies (id, title, type, settings, tariff_plan_id)
VALUES (uuid_generate_v1(), '1% на все покупки', 'INSTANT',
        '{"intervals":[{"from":0.0,"to":null,"ratio":0.01,"amount":null}],"mcc_list":null,"min_bonus":null,"max_bonus":null}',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тарифный план (1% на все покупки, 5% на 3 категории)')),
       (uuid_generate_v1(), '5% на 3 категории', 'INSTANT',
        '{"intervals":[{"from":0.0,"to":null,"ratio":0.05,"amount":null}],"mcc_list":[1234,5678,6543],"min_bonus":null,"max_bonus":null}',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тарифный план (1% на все покупки, 5% на 3 категории)'));

-- Добавляем клиента и привязываем его к тарифному плану
INSERT INTO clients (id, first_name, last_name, tariff_plan_id)
VALUES (uuid_generate_v1(), 'Nikita', 'Chernetsov',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тестовый тарифный план'));

INSERT INTO clients (id, first_name, last_name, tariff_plan_id)
VALUES (uuid_generate_v1(), 'Kirill', 'Machechin',
        (SELECT id FROM tariff_plans tp WHERE tp.title = 'Тарифный план (1% на все покупки, 5% на 3 категории)'));


-- INSERT INTO clients (id, first_name, last_name, tariff_plan_id)
-- SELECT uuid_generate_v1() id, 'asd' first_name, 'asdf' last_name, (SELECT id from tariff_plans limit 1) tariff_plan_id
-- FROM generate_series(1, 100);
