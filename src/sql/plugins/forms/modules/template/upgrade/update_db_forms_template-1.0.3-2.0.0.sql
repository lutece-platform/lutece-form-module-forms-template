-- liquibase formatted sql
-- changeset forms-template:update_db_forms_template-1.0.3-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url = 'ti ti-template' WHERE id_right = 'TEMPLATE_STEP_MANAGEMENT';
