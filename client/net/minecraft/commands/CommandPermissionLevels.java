package net.minecraft.commands;

import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;

public class CommandPermissionLevels {
   public static final PermissionCheck LEVEL_ALL;
   public static final PermissionCheck LEVEL_MODERATORS;
   public static final PermissionCheck LEVEL_GAMEMASTERS;
   public static final PermissionCheck LEVEL_ADMINS;
   public static final PermissionCheck LEVEL_OWNERS;

   public CommandPermissionLevels() {
      super();
   }

   static {
      LEVEL_ALL = PermissionCheck.AlwaysPass.INSTANCE;
      LEVEL_MODERATORS = new PermissionCheck.Require(Permissions.COMMANDS_MODERATOR);
      LEVEL_GAMEMASTERS = new PermissionCheck.Require(Permissions.COMMANDS_GAMEMASTER);
      LEVEL_ADMINS = new PermissionCheck.Require(Permissions.COMMANDS_ADMIN);
      LEVEL_OWNERS = new PermissionCheck.Require(Permissions.COMMANDS_OWNER);
   }
}
