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

   default boolean hasPermission(final Permission permission) {
      if (permission instanceof Permission.HasCommandLevel levelCheck) {
         return this.level().isEqualOrHigherThan(levelCheck.level());
      } else {
         return permission.equals(Permissions.COMMANDS_ENTITY_SELECTORS) ? this.level().isEqualOrHigherThan(PermissionLevel.GAMEMASTERS) : false;
      }
   }

   default PermissionSet union(final PermissionSet other) {
      if (other instanceof LevelBasedPermissionSet otherSet) {
         return this.level().isEqualOrHigherThan(otherSet.level()) ? otherSet : this;
      } else {
         return PermissionSet.super.union(other);
      }
   }

   static LevelBasedPermissionSet forLevel(final PermissionLevel level) {
      LevelBasedPermissionSet var10000;
      switch (level) {
         case ALL -> var10000 = ALL;
         case MODERATORS -> var10000 = MODERATOR;
         case GAMEMASTERS -> var10000 = GAMEMASTER;
         case ADMINS -> var10000 = ADMIN;
         case OWNERS -> var10000 = OWNER;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static LevelBasedPermissionSet create(final PermissionLevel level) {
      return new LevelBasedPermissionSet() {
         public PermissionLevel level() {
            return level;
         }

         public String toString() {
            return "permission level: " + level.name();
         }
      };
   }
}
