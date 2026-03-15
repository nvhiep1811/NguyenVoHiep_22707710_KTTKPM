import { permissionMatrix, roleCatalog } from '../extension/authz.js';

export function createAuthService({ repositories }) {
  return {
    async listUsers() {
      return repositories.users.list();
    },
    async getRoleMatrix() {
      const users = await repositories.users.list();

      return {
        summary: roleCatalog.map((role) => ({
          slug: role.slug,
          label: role.label,
          permissionsLabel: role.permissionsLabel,
          userCount: users.filter((user) => user.role === role.slug).length,
        })),
        permissions: permissionMatrix,
      };
    },
  };
}
