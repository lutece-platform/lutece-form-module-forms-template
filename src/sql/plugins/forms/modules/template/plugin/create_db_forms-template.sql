
DROP TABLE IF EXISTS template_step;
CREATE TABLE template_step (
	id_template int AUTO_INCREMENT,
	title varchar(255) default '' NOT NULL,
	description varchar(255) default '',
	PRIMARY KEY (id_template)
);

DROP TABLE IF EXISTS template_question;
CREATE TABLE template_question (
	id_question int AUTO_INCREMENT,
	title varchar(255) default '' NOT NULL,
	code varchar(100) default '' NOT NULL,
	description LONG VARCHAR,
	id_entry int default '0',
	id_template int default '0',
	is_visible_multiview_global SMALLINT default 0 NOT NULL,
	is_visible_multiview_form_selected SMALLINT default 0 NOT NULL,
	multiview_column_order INT default 0 NOT NULL,
	column_title varchar(255) default '' NOT NULL,
	is_filterable_multiview_global SMALLINT default 0 NOT NULL,
	is_filterable_multiview_form_selected SMALLINT default 0 NOT NULL,
	PRIMARY KEY (id_question)
);

DROP TABLE IF EXISTS template_group;
CREATE TABLE template_group (
	id_group int AUTO_INCREMENT,
	title varchar(255) default '' NOT NULL,
	description varchar(512) default '',
	id_template int default '0',
	iteration_min int default '1',
	iteration_max int default '1',
	iteration_add_label varchar(255) default '',
	iteration_remove_label varchar(255) default '',
	PRIMARY KEY (id_group)
);

DROP TABLE IF EXISTS template_display;
CREATE TABLE template_display (
	id_display int AUTO_INCREMENT,
	id_template int default '0',
	id_composite int default '0',
	id_parent int default '0',
	display_order int default '0',
	composite_type varchar(255) default '',
	display_depth int default '0',
	PRIMARY KEY (id_display)
);

DROP TABLE IF EXISTS template_control;
CREATE TABLE template_control (
	id_control int AUTO_INCREMENT,
	value varchar(255),
	error_message varchar(512) default '',
	validator_name varchar(255) NOT NULL,
	control_type varchar(255) NOT NULL,
	id_control_target int default '0' NOT NULL,
	PRIMARY KEY (id_control)
);


DROP TABLE IF EXISTS template_control_question;
	CREATE TABLE template_control_question (
	id_control int NOT NULL,
	id_question int NOT NULL,
	PRIMARY KEY (id_control, id_question)
);

DROP TABLE IF EXISTS template_control_question_mapping;
CREATE TABLE template_control_question_mapping (
	id_control int NOT NULL,
	id_question int NOT NULL,
	value varchar(255),
	PRIMARY KEY (id_control, id_question, value)
);

DROP TABLE IF EXISTS template_entry;
CREATE TABLE template_entry (
	id_entry int AUTO_INCREMENT NOT NULL,
	id_type int default 0 NOT NULL,
	title long varchar,
	code varchar(100) default NULL,	
	help_message long varchar,
	comment long varchar,
	mandatory smallint default NULL,
	fields_in_line smallint default NULL,
	pos int default NULL,
	field_unique smallint default NULL,
	css_class varchar(255) default NULL,
	pos_conditional int default 0,
	error_message long varchar default NULL,
    is_only_display_back smallint DEFAULT '0',
	is_indexed smallint DEFAULT '0',
    PRIMARY KEY (id_entry)
);

DROP TABLE IF EXISTS template_field;
CREATE TABLE template_field (
	id_field int AUTO_INCREMENT,
	id_entry int default 0 NOT NULL,
	title varchar(255),
	code varchar(100) default NULL,
	value long varchar,
	default_value smallint default NULL,
	pos int default NULL,
	value_type_date date NULL,
	no_display_title smallint default NULL,
	comment long varchar default null,
	PRIMARY KEY (id_field)
);

DROP TABLE IF EXISTS template_referenceitem_field;
CREATE TABLE template_referenceitem_field (
	id_field int default 0 NOT NULL,
	id_item int default 0 NOT NULL,
	PRIMARY KEY( id_field )
);