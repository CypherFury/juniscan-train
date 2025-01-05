create table IF not exists module
(
    id          bigint
        primary key,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint UKf73dsvaor0f4cycvldyt2idf1
        unique (name)
);

create table IF not exists function
(
    id          bigint auto_increment
        primary key,
    module_id   bigint       not null,
    call_index  int          not null,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint FKs8d68m1i3l4tx3jneth6c2fv8
        foreign key (module_id) references module (id)
);
