package net.minecraft.commands;

import net.minecraft.server.commands.PermissionCheck;

public interface PermissionSource {
   boolean hasPermission(int var1);

   default boolean allowsSelectors() {
      return this.hasPermission(2);
   }

   public static record Check<T extends PermissionSource>(int requiredLevel) implements PermissionCheck<T> {
      public Check(int var1) {
         super();
         this.requiredLevel = var1;
      }

      public boolean test(T var1) {
         return var1.hasPermission(this.requiredLevel);
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((PermissionSource)var1);
      }
   }
}
