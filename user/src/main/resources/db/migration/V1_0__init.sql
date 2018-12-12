create sequence hibernate_sequence start with 1 increment by 1;

create table users (username varchar(255) not null, date_of_birth date not null, email varchar(128), name varchar(128), primary key (username));