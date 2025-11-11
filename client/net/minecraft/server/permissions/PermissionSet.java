package net.minecraft.server.permissions;

public interface PermissionSet {
   PermissionSet NO_PERMISSIONS = (var0) -> false;
   PermissionSet ALL_PERMISSIONS = (var0) -> true;

   boolean hasPermission(Permission var1);

   default PermissionSet union(PermissionSet var1) {
      return (PermissionSet)(var1 instanceof PermissionSetUnion ? var1.union(this) : new PermissionSetUnion(this, var1));
   }
}
