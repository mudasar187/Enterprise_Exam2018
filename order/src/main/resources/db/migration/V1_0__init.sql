create sequence hibernate_sequence start 1 increment 1;
create table coupon (id int8 not null, code varchar(128), description varchar(512), expire_at timestamp not null, percentage int4 not null, primary key (id));
create table invoice (id int8 not null, now_playing_id int8 not null, order_date timestamp not null, paid boolean not null, total_price float8 not null, username varchar(255), coupon_id int8, primary key (id));
create table invoice_tickets (invoice_id int8 not null, tickets_id int8 not null, primary key (invoice_id, tickets_id));
create table ticket (id int8 not null, invoice_id int8 not null, price float8 not null, seat varchar(255), primary key (id));
alter table invoice_tickets add constraint UK_gnd5dg0b7gsedaig72y575s4g unique (tickets_id);
alter table invoice add constraint FKp1yt1o12ktj2fijkgai4hri9y foreign key (coupon_id) references coupon;
alter table invoice_tickets add constraint FKsi7ec07v43piql4wdviwis183 foreign key (tickets_id) references ticket;
alter table invoice_tickets add constraint FK4e9drsx4yi4xfcgrs5qaq2gm7 foreign key (invoice_id) references invoice;