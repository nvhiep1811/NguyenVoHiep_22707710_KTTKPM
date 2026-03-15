import { Panel } from '../../components/layout/Panel';
import { Badge } from '../../components/ui/Badge';
import { Table } from '../../components/ui/Table';
import type { DashboardPayload, PluginManifest, PostRecord } from '../../types/cms';
import { STATUS_TEXT } from '../../utils/cms-labels';

interface DashboardViewProps {
  dashboard: DashboardPayload;
  posts: PostRecord[];
  plugins: PluginManifest[];
}

export function DashboardView({ dashboard, posts, plugins }: DashboardViewProps) {
  const activePlugins = plugins.filter((plugin) => plugin.enabled).length;

  return (
    <div className="page-stack">
      <section className="overview-card">
        <div>
          <p className="eyebrow">Tổng quan</p>
          <h3>Theo dõi nội dung, plugin và hoạt động hệ thống</h3>
          <p className="overview-card__copy">
            Trang này tập trung vào các tín hiệu quan trọng để kiểm tra bài viết,
            plugin đang chạy và trạng thái vận hành của CMS.
          </p>
        </div>

        <div className="overview-card__meta">
          <span className="pill">Plugin active: {activePlugins}</span>
          <span className="pill pill--ghost">Installed: {plugins.length}</span>
        </div>
      </section>

      <section className="metric-grid">
        {dashboard.metrics.map((metric) => (
          <article key={metric.label} className="metric-card">
            <span className="eyebrow">{metric.label}</span>
            <strong>{metric.value}</strong>
            <p>{metric.delta}</p>
          </article>
        ))}
      </section>

      <section className="signal-grid">
        {dashboard.pluginCards.map((card) => (
          <article key={card.title} className={`signal-card signal-card--${card.tone}`}>
            <span className="eyebrow">{card.title}</span>
            <strong>{card.value}</strong>
            <p>{card.detail}</p>
          </article>
        ))}
      </section>

      <div className="two-column">
        <Panel title="Bài viết gần đây">
          <Table
            heads={['Tiêu đề', 'Tác giả', 'Trạng thái']}
            rows={posts.slice(0, 4).map((post) => [
              <div key={`${post.id}-title`}>
                <strong>{post.title}</strong>
                <p className="cell-copy">{post.excerpt}</p>
              </div>,
              post.author,
              <Badge key={`${post.id}-status`} tone={post.status}>
                {STATUS_TEXT[post.status]}
              </Badge>,
            ])}
          />
        </Panel>

        <Panel title="Hoạt động hệ thống">
          <Table
            heads={['Sự kiện', 'Loại', 'Thời gian']}
            rows={dashboard.activity.map((entry) => [
              <div key={`${entry.id}-message`}>
                <strong>{entry.message}</strong>
              </div>,
              <span className="mono">{entry.eventType}</span>,
              entry.relativeTime,
            ])}
          />
        </Panel>
      </div>
    </div>
  );
}
