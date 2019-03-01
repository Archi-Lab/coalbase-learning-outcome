create table learning_space (uuid uuid not null, title varchar(255),learning_outcome_uuid uuid,requirement_uuid uuid, primary key (uuid));
alter table learning_space add constraint learning_space_to_learning_outcome foreign key (learning_outcome_uuid) references learning_outcome;
alter table learning_space add constraint learning_space_requirement foreign key (requirement_uuid) references learning_space;
