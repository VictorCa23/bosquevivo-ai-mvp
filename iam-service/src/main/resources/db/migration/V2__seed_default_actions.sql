-- Flyway Migration V2: Seed default IAM actions.

INSERT INTO iam_action (code, name, tenant_id, created_at, version)
SELECT 'CREATE', 'Create new entities', 'system', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE code = 'CREATE' AND tenant_id = 'system'
);

INSERT INTO iam_action (code, name, tenant_id, created_at, version)
SELECT 'READ', 'Read and view entities', 'system', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE code = 'READ' AND tenant_id = 'system'
);

INSERT INTO iam_action (code, name, tenant_id, created_at, version)
SELECT 'UPDATE', 'Update existing entities', 'system', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE code = 'UPDATE' AND tenant_id = 'system'
);

INSERT INTO iam_action (code, name, tenant_id, created_at, version)
SELECT 'DELETE', 'Delete entities', 'system', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE code = 'DELETE' AND tenant_id = 'system'
);

INSERT INTO iam_action (code, name, tenant_id, created_at, version)
SELECT 'EXECUTE', 'Execute operations and actions', 'system', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (
    SELECT 1 FROM iam_action WHERE code = 'EXECUTE' AND tenant_id = 'system'
);
