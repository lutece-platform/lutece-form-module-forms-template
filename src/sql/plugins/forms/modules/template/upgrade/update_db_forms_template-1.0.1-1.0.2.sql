-- liquibase formatted sql
-- changeset forms-template:update_db_forms_template-1.0.1-1.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
DELETE FROM core_admin_right WHERE id_right = 'TEMPLATE_STEP_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TEMPLATE_STEP_MANAGEMENT','module.forms.template.adminFeature.manageTemplates.name',1,'jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp','module.forms.template.adminFeature.manageTemplates.description',0,'forms-template','MANAGERS',NULL,NULL,4);
