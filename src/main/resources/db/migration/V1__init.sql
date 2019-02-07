create table learning_outcome (uuid bytea not null, action varchar(255), taxonomy_level integer, value varchar(255), primary key (uuid));
create table learning_outcome_tools (learning_outcome_uuid bytea not null, value varchar(255));
alter table learning_outcome_tools add constraint FKn67wjwkrqsphxl3lg6bi0adc7 foreign key (learning_outcome_uuid) references learning_outcome;
