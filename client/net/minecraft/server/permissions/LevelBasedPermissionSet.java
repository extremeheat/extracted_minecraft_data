package net.minecraft.server.permissions;

public interface LevelBasedPermissionSet extends PermissionSet {
   /** @deprecated */
   @Deprecated
   LevelBasedPermissionSet ALL = create(PermissionLevel.ALL);
   LevelBasedPermissionSet MODERATOR = create(PermissionLevel.MODERATORS);
   LevelBasedPermissionSet GAMEMASTER = create(PermissionLevel.GAMEMASTERS);
   LevelBasedPermissionSet ADMIN = create(PermissionLevel.ADMINS);
   LevelBasedPermissionSet OWNER = create(PermissionLevel.OWNERS);

   PermissionLevel level();

   default boolean hasPermission(Permission var1) {
      if (var1 instanceof Permission.HasCommandLevel var2) {
         return this.level().isEqualOrHigherThan(var2.level());
      } else {
         return var1.equals(Permissions.COMMANDS_ENTITY_SELECTORS) ? this.level().isEqualOrHigherThan(PermissionLevel.GAMEMASTERS) : false;
      }
   }

   default PermissionSet union(PermissionSet var1) {
      if (var1 instanceof LevelBasedPermissionSet var2) {
         return this.level().isEqualOrHigherThan(var2.level()) ? var2 : this;
      } else {
         return PermissionSet.super.union(var1);
      }
   }

   static LevelBasedPermissionSet forLevel(PermissionLevel var0) {
      LevelBasedPermissionSet var10000;
      switch (var0) {
         case ALL -> var10000 = ALL;
         case MODERATORS -> var10000 = MODERATOR;
         case GAMEMASTERS -> var10000 = GAMEMASTER;
         case ADMINS -> var10000 = ADMIN;
         case OWNERS -> var10000 = OWNER;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static LevelBasedPermissionSet create(final PermissionLevel var0) {
      return new LevelBasedPermissionSet() {
         public PermissionLevel level() {
            return var0;
         }

         public String toString() {
            return "permission level: " + var0.name();
         }
      };
   }
}
