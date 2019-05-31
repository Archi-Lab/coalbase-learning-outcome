-- Create Learning Outcomes
create table learning_outcome (uuid uuid not null, action varchar(255), taxonomy_level integer, value varchar(255), primary key (uuid));

-- Create Requirements
create table learning_outcome_requirements (learning_outcome_uuid uuid not null, value varchar(255), taxonomy_level integer);
alter table learning_outcome_requirements add constraint learning_outcome_requirement foreign key (learning_outcome_uuid) references learning_outcome;

-- Create Abilities
create table learning_outcome_abilities (learning_outcome_uuid uuid not null, value varchar(255), taxonomy_level integer);
alter table learning_outcome_abilities add constraint learning_outcome_ability foreign key (learning_outcome_uuid) references learning_outcome;

-- Create Purposes
create table learning_outcome_purposes (learning_outcome_uuid uuid not null, value varchar(255), taxonomy_level integer);
alter table learning_outcome_purposes add constraint learning_outcome_purpose foreign key (learning_outcome_uuid) references learning_outcome;

-- Create Learning Space
create table learning_space (uuid uuid not null, title varchar(255),learning_outcome_uuid uuid, requirement_uuid uuid, primary key (uuid));
alter table learning_space add constraint learning_space_to_learning_outcome foreign key (learning_outcome_uuid) references learning_outcome;
alter table learning_space add constraint learning_space_requirement foreign key (requirement_uuid) references learning_space;

-- Create Course
create table course (uuid uuid not null, title varchar(255), short_title varchar(255), description varchar(512), author varchar(255), primary key (uuid));
alter table learning_space add column course_uuid uuid;
alter table learning_space add constraint course_to_learning_space foreign key (course_uuid) references course;