import { Badge } from '../../components/ui/Badge';
import type { PluginManifest } from '../../types/cms';

interface PluginsViewProps {
  plugins: PluginManifest[];
  togglingSlug: string | null;
  onToggle: (slug: string) => Promise<void>;
}

export function PluginsView({ plugins, togglingSlug, onToggle }: PluginsViewProps) {
  return (
    <div className="page-stack">
      <section className="plugin-grid">
        {plugins.map((plugin) => (
          <article key={plugin.slug} className={`plugin-card${plugin.enabled ? ' plugin-card--active' : ''}`}>
            <div className="plugin-card__top">
              <span className="plugin-card__icon" style={{ background: plugin.accent }}>
                {plugin.icon}
              </span>
              <Badge tone={plugin.enabled ? 'success' : 'neutral'}>
                {plugin.enabled ? 'Active' : 'Disabled'}
              </Badge>
            </div>

            <div>
              <h3>{plugin.name}</h3>
              <p>{plugin.description}</p>
            </div>

            <div className="plugin-card__meta">
              <span className="pill pill--ghost">v{plugin.version}</span>
              <span className="pill pill--ghost">{plugin.hookCount} hooks</span>
              <span className="pill pill--ghost">{plugin.category}</span>
            </div>

            <div className="chip-row">
              {plugin.capabilities.map((capability) => (
                <span key={capability} className="chip">
                  {capability}
                </span>
              ))}
            </div>

            <button
              className="button"
              disabled={togglingSlug === plugin.slug}
              onClick={() => void onToggle(plugin.slug)}
              type="button"
            >
              {togglingSlug === plugin.slug
                ? 'Đang cập nhật...'
                : plugin.enabled
                  ? 'Tắt plugin'
                  : 'Bật plugin'}
            </button>
          </article>
        ))}
      </section>
    </div>
  );
}
