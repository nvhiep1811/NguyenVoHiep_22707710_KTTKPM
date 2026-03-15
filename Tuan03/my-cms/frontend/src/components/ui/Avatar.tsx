import type { UserRecord } from '../../types/cms';

type AvatarProps = Pick<UserRecord, 'initials' | 'avatarBg' | 'avatarColor'>;

export function Avatar({ initials, avatarBg, avatarColor }: AvatarProps) {
  return (
    <span
      className="avatar"
      style={{
        background: avatarBg,
        color: avatarColor,
      }}
    >
      {initials}
    </span>
  );
}
