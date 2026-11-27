-- liquibase formatted sql
-- lutece runAfter:forms
-- changeset forms-template:init_core_forms-template.sql
-- preconditions onFail:MARK_RAN onError:WARN
DELETE FROM core_admin_right WHERE id_right = 'TEMPLATE_STEP_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TEMPLATE_STEP_MANAGEMENT','module.forms.template.adminFeature.manageTemplates.name',1,'jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp','module.forms.template.adminFeature.manageTemplates.description',0,'forms-template','MANAGERS','ti ti-template',NULL,4);

DELETE FROM core_user_right WHERE id_right = 'TEMPLATE_STEP_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TEMPLATE_STEP_MANAGEMENT',1);

DELETE FROM core_admin_role_resource WHERE role_key = 'forms_manager' AND resource_type = 'FORMS_TEMPLATE';
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES ('forms_manager','FORMS_TEMPLATE','*','*');
