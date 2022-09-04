CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company(id, name) values(1, 'company1');
insert into company(id, name) values(2, 'company2');
insert into company(id, name) values(3, 'company3');
insert into company(id, name) values(4, 'company4');
insert into company(id, name) values(5, 'company5');
insert into company(id, name) values(6, 'company6');

insert into person(id, name, company_id) values(1, 'person1', 1);
insert into person(id, name, company_id) values(2, 'person2', 1);
insert into person(id, name, company_id) values(3, 'person3', 2);
insert into person(id, name, company_id) values(4, 'person4', 3);
insert into person(id, name, company_id) values(5, 'person5', 4);
insert into person(id, name, company_id) values(6, 'person6', 6);
insert into person(id, name, company_id) values(7, 'person7', 6);
insert into person(id, name, company_id) values(8, 'person8', 5);
insert into person(id, name, company_id) values(9, 'person9', 5);

select p.name, c.name
from person as p
join company as c
on p.company_id = c.id
and p.company_id != 5;

select c.name, count(*) as counter
from person as p
join company as c
on c.id = p.company_id
group by c.name
having count(*) = (select count(*) size
from person
GROUP BY company_id
ORDER BY size DESC
limit 1);