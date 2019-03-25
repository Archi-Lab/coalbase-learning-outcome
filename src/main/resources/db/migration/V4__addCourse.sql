create table course (uuid uuid not null, title varchar(255), description varchar(512), primary key (uuid));
alter table learning_space add column course_uuid uuid;
alter table learning_space add constraint course_to_learning_space foreign key (course_uuid) references course;
