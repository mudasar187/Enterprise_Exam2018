create sequence hibernate_sequence start with 1 increment by 1;
create table cinema (id bigint not null, location varchar(128), name varchar(128), primary key (id));
create table room (id bigint not null, name varchar(128), cinema_id bigint, primary key (id));
create table room_seats (room_id bigint not null, seats varchar(255));
alter table room add constraint FK838jvntrkjvmbpm310wsdad1r foreign key (cinema_id) references cinema;
alter table room_seats add constraint FKdjojun7gx4hwx0tj2nqlpnena foreign key (room_id) references room;