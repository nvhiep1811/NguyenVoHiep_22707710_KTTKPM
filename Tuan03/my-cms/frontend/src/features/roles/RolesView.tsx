import { Panel } from '../../components/layout/Panel';
import { Badge } from '../../components/ui/Badge';
import { Table } from '../../components/ui/Table';
import type { PermissionRow, RolesPayload } from '../../types/cms';

interface RolesViewProps {
  roles: RolesPayload;
}

interface PermissionsTableProps {
  permissions: PermissionRow[];
}

function PermissionsTable({ permissions }: PermissionsTableProps) {
  return (
    <Table
      heads={['Chức năng', 'Admin', 'Editor', 'Viewer']}
      rows={permissions.map((permission) => [
        permission.label,
        permission.admin ? 'Có' : 'Không',
        permission.editor ? 'Có' : 'Không',
        permission.viewer ? 'Có' : 'Không',
      ])}
    />
  );
}

export function RolesView({ roles }: RolesViewProps) {
  return (
    <div className="page-stack two-column">
      <Panel title="Vai trò trong CMS">
        <Table
          heads={['Vai trò', 'Mô tả quyền', 'Số user']}
          rows={roles.summary.map((role) => [
            <Badge key={role.slug} tone={role.slug}>
              {role.label}
            </Badge>,
            role.permissionsLabel,
            role.userCount,
          ])}
        />
      </Panel>

      <Panel title="Ma trận phân quyền">
        <PermissionsTable permissions={roles.permissions} />
      </Panel>
    </div>
  );
}
