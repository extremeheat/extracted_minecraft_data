package net.minecraft.server.permissions;

public class Permissions {
   public static final Permission COMMANDS_MODERATOR;
   public static final Permission COMMANDS_GAMEMASTER;
   public static final Permission COMMANDS_ADMIN;
   public static final Permission COMMANDS_OWNER;
   public static final Permission COMMANDS_ENTITY_SELECTORS;

   public Permissions() {
      super();
   }

   static {
      COMMANDS_MODERATOR = new Permission.HasCommandLevel(PermissionLevel.MODERATORS);
      COMMANDS_GAMEMASTER = new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS);
      COMMANDS_ADMIN = new Permission.HasCommandLevel(PermissionLevel.ADMINS);
      COMMANDS_OWNER = new Permission.HasCommandLevel(PermissionLevel.OWNERS);
      COMMANDS_ENTITY_SELECTORS = Permission.Atom.create("commands/entity_selectors");
   }
}
