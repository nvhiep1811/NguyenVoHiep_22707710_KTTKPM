import { Avatar } from '../ui/Avatar'
import type { PageId } from '../../types/cms'
import { NAV_GROUPS } from '../../utils/cmsNavigation'

interface SidebarProps {
  activePage: PageId
  onNavigate: (page: PageId) => void
  installedPlugins: number
}

export function Sidebar({ activePage, onNavigate, installedPlugins }: SidebarProps) {
  return (
    <aside className="sidebar">
      <div className="sidebar__brand">
        <p className="eyebrow">CMS Admin</p>
        <h1>Plugin CMS</h1>
        <p className="sidebar__note">Quản lý nội dung, người dùng và plugin trên một giao diện duy nhất.</p>
      </div>

      <div className="sidebar__nav">
        {NAV_GROUPS.map((group) => (
          <div key={group.section} className="nav-group">
            <span className="nav-group__label">{group.section}</span>
            {group.items.map((item) => (
              <button
                key={item.id}
                className={`nav-item${activePage === item.id ? ' nav-item--active' : ''}`}
                onClick={() => onNavigate(item.id)}
                type="button"
              >
                <span className="nav-item__icon">{item.short}</span>
                <span>{item.label}</span>
              </button>
            ))}
          </div>
        ))}
      </div>

      <div className="sidebar__footer">
        <div className="sidebar__runtime">
          <span className="eyebrow">Plugins</span>
          <strong>{installedPlugins} plugin đang chạy</strong>
        </div>

        <div className="sidebar__admin">
          <Avatar initials="AD" avatarBg="#d7efe5" avatarColor="#0f5a46" />
          <div>
            <strong>Admin</strong>
            <p>System administrator</p>
          </div>
        </div>
      </div>
    </aside>
  )
}
