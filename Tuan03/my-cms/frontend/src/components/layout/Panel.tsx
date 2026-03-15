import type { ReactNode } from 'react';

interface PanelProps {
  title: string;
  aside?: ReactNode;
  children: ReactNode;
}

export function Panel({ title, aside, children }: PanelProps) {
  return (
    <section className="panel">
      <div className="panel__header">
        <div>
          <h3>{title}</h3>
        </div>
        {aside ? <div className="panel__aside">{aside}</div> : null}
      </div>
      {children}
    </section>
  );
}
