CREATE TABLE IF NOT EXISTS t_dummy (
    id BIGINT,
    description varchar(1000)
);

insert into t_dummy(id, description) values(0, 'Description 1');
insert into t_dummy(id, description) values(1, 'Description 1');

select * from t_dummy;