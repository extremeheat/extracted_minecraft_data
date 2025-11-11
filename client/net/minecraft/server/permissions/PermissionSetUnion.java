package net.minecraft.server.permissions;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;

public class PermissionSetUnion implements PermissionSet {
   private final ReferenceSet<PermissionSet> permissions = new ReferenceArraySet();

   PermissionSetUnion(PermissionSet var1, PermissionSet var2) {
      super();
      this.permissions.add(var1);
      this.permissions.add(var2);
      this.ensureNoUnionsWithinUnions();
   }

   private PermissionSetUnion(ReferenceSet<PermissionSet> var1, PermissionSet var2) {
      super();
      this.permissions.addAll(var1);
      this.permissions.add(var2);
      this.ensureNoUnionsWithinUnions();
   }

   private PermissionSetUnion(ReferenceSet<PermissionSet> var1, ReferenceSet<PermissionSet> var2) {
      super();
      this.permissions.addAll(var1);
      this.permissions.addAll(var2);
      this.ensureNoUnionsWithinUnions();
   }

   public boolean hasPermission(Permission var1) {
      ObjectIterator var2 = this.permissions.iterator();

      while(var2.hasNext()) {
         PermissionSet var3 = (PermissionSet)var2.next();
         if (var3.hasPermission(var1)) {
            return true;
         }
      }

      return false;
   }

   public PermissionSet union(PermissionSet var1) {
      if (var1 instanceof PermissionSetUnion var2) {
         return new PermissionSetUnion(this.permissions, var2.permissions);
      } else {
         return new PermissionSetUnion(this.permissions, var1);
      }
   }

   @VisibleForTesting
   public ReferenceSet<PermissionSet> getPermissions() {
      return new ReferenceArraySet(this.permissions);
   }

   private void ensureNoUnionsWithinUnions() {
      ObjectIterator var1 = this.permissions.iterator();

      while(var1.hasNext()) {
         PermissionSet var2 = (PermissionSet)var1.next();
         if (var2 instanceof PermissionSetUnion) {
            throw new IllegalArgumentException("Cannot have PermissionSetUnion within another PermissionSetUnion");
         }
      }

   }
}
