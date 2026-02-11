package net.minecraft.server.permissions;

import java.util.Set;

public class Permissions {
   public static final Permission COMMANDS_MODERATOR;
   public static final Permission COMMANDS_GAMEMASTER;
   public static final Permission COMMANDS_ADMIN;
   public static final Permission COMMANDS_OWNER;
   public static final Permission COMMANDS_ENTITY_SELECTORS;
   public static final Permission CHAT_SEND_MESSAGES;
   public static final Permission CHAT_SEND_COMMANDS;
   public static final Permission CHAT_RECEIVE_PLAYER_MESSAGES;
   public static final Permission CHAT_RECEIVE_SYSTEM_MESSAGES;
   public static final Set<Permission> CHAT_PERMISSIONS;

   public Permissions() {
      super();
   }

   static {
      COMMANDS_MODERATOR = new Permission.HasCommandLevel(PermissionLevel.MODERATORS);
      COMMANDS_GAMEMASTER = new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS);
      COMMANDS_ADMIN = new Permission.HasCommandLevel(PermissionLevel.ADMINS);
      COMMANDS_OWNER = new Permission.HasCommandLevel(PermissionLevel.OWNERS);
      COMMANDS_ENTITY_SELECTORS = Permission.Atom.create("commands/entity_selectors");
      CHAT_SEND_MESSAGES = Permission.Atom.create("chat/send_messages");
      CHAT_SEND_COMMANDS = Permission.Atom.create("chat/send_commands");
      CHAT_RECEIVE_PLAYER_MESSAGES = Permission.Atom.create("chat/receive_player_messages");
      CHAT_RECEIVE_SYSTEM_MESSAGES = Permission.Atom.create("chat/receive_system_messages");
      CHAT_PERMISSIONS = Set.of(CHAT_SEND_MESSAGES, CHAT_SEND_COMMANDS, CHAT_RECEIVE_PLAYER_MESSAGES, CHAT_RECEIVE_SYSTEM_MESSAGES);
   }
}
