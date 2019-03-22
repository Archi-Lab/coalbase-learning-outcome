create table course (uuid uuid not null, title varchar(255), description varchar(512),learning_space_uuid uuid not null, primary key (uuid));
alter table course add constraint course_to_learning_space foreign key (learning_space_uuid) references learning_space;
