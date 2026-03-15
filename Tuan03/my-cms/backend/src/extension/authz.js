export const permissionMatrix = [
  { label: 'Tạo bài viết', admin: true, editor: true, viewer: false },
  { label: 'Duyệt bài', admin: true, editor: true, viewer: false },
  { label: 'Quản lý user', admin: true, editor: false, viewer: false },
  { label: 'Cài plugin', admin: true, editor: false, viewer: false },
];

export const roleCatalog = [
  { slug: 'admin', label: 'Admin', permissionsLabel: 'Toàn quyền' },
  { slug: 'editor', label: 'Editor', permissionsLabel: 'Tạo, sửa, duyệt' },
  { slug: 'viewer', label: 'Viewer', permissionsLabel: 'Chỉ xem' },
];
