import type { ReactNode } from 'react';

import type { PostStatus, UserRole } from '../../types/cms';

export type BadgeTone =
  | PostStatus
  | UserRole
  | 'active'
  | 'inactive'
  | 'success'
  | 'info'
  | 'warning'
  | 'neutral';

interface BadgeProps {
  tone: BadgeTone;
  children: ReactNode;
}

export function Badge({ tone, children }: BadgeProps) {
  return <span className={`badge badge--${tone}`}>{children}</span>;
}
