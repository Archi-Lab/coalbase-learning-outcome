-- Add PredefinedExamForm
create table predefined_exam_form (uuid uuid not null, type varchar(255), min_value integer, max_value integer, unit varchar(255), description varchar(2000), primary key (uuid));

-- Create Schedules
create table predefined_exam_form_schedules (predefined_exam_form_uuid uuid not null, value varchar(255));
alter table predefined_exam_form_schedules add constraint predefined_exam_form_schedule foreign key (predefined_exam_form_uuid) references predefined_exam_form;

-- Add ExamForm
alter table learning_space add type varchar(255);
alter table learning_space add min_value integer;
alter table learning_space add max_value integer;
alter table learning_space add unit varchar(255);
alter table learning_space add description varchar(2000);

-- Create Schedules
create table learning_space_schedules (learning_space_uuid uuid not null, value varchar(255));
alter table learning_space_schedules add constraint learning_space_schedule foreign key (learning_space_uuid) references learning_space;