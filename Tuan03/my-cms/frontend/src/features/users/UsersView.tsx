import { Panel } from '../../components/layout/Panel';
import { Avatar } from '../../components/ui/Avatar';
import { Badge } from '../../components/ui/Badge';
import { Table } from '../../components/ui/Table';
import type { UserRecord } from '../../types/cms';
import { ROLE_TEXT } from '../../utils/cms-labels';

interface UsersViewProps {
  users: UserRecord[];
}

export function UsersView({ users }: UsersViewProps) {
  return (
    <div className="page-stack">
      <Panel title="Quản lý người dùng">
        <Table
          heads={['Người dùng', 'Email', 'Vai trò', 'Trạng thái', 'Đăng nhập cuối']}
          rows={users.map((user) => [
            <div key={`${user.id}-name`} className="identity">
              <Avatar {...user} />
              <div>
                <strong>{user.name}</strong>
                <p className="cell-copy">{user.initials}</p>
              </div>
            </div>,
            user.email,
            <Badge key={`${user.id}-role`} tone={user.role}>
              {ROLE_TEXT[user.role]}
            </Badge>,
            <Badge key={`${user.id}-status`} tone={user.status}>
              {user.status === 'active' ? 'Hoạt động' : 'Không hoạt động'}
            </Badge>,
            user.lastLogin,
          ])}
        />
      </Panel>
    </div>
  );
}
