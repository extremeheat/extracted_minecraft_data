package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.alreadyOn"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.alreadyOff"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.add.failed"));
   private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.remove.failed"));

   public WhitelistCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.literal("on").executes((var0x) -> {
         return enableWhitelist((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("off").executes((var0x) -> {
         return disableWhitelist((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("list").executes((var0x) -> {
         return showList((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((var0x, var1) -> {
         PlayerList var2 = ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList();
         return SharedSuggestionProvider.suggest(var2.getPlayers().stream().filter((var1x) -> {
            return !var2.getWhiteList().isWhiteListed(var1x.getGameProfile());
         }).map((var0) -> {
            return var0.getGameProfile().getName();
         }), var1);
      }).executes((var0x) -> {
         return addPlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets"));
      })))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((var0x, var1) -> {
         return SharedSuggestionProvider.suggest(((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getWhiteListNames(), var1);
      }).executes((var0x) -> {
         return removePlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets"));
      })))).then(Commands.literal("reload").executes((var0x) -> {
         return reload((CommandSourceStack)var0x.getSource());
      })));
   }

   private static int reload(CommandSourceStack var0) {
      var0.getServer().getPlayerList().reloadWhiteList();
      var0.sendSuccess(() -> {
         return Component.translatable("commands.whitelist.reloaded");
      }, true);
      var0.getServer().kickUnlistedPlayers(var0);
      return 1;
   }

   private static int addPlayers(CommandSourceStack var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      UserWhiteList var2 = var0.getServer().getPlayerList().getWhiteList();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (!var2.isWhiteListed(var5)) {
            UserWhiteListEntry var6 = new UserWhiteListEntry(var5);
            var2.add(var6);
            var0.sendSuccess(() -> {
               return Component.translatable("commands.whitelist.add.success", Component.literal(var5.getName()));
            }, true);
            ++var3;
         }
      }

      if (var3 == 0) {
         throw ERROR_ALREADY_WHITELISTED.create();
      } else {
         return var3;
      }
   }

   private static int removePlayers(CommandSourceStack var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      UserWhiteList var2 = var0.getServer().getPlayerList().getWhiteList();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (var2.isWhiteListed(var5)) {
            UserWhiteListEntry var6 = new UserWhiteListEntry(var5);
            var2.remove(var6);
            var0.sendSuccess(() -> {
               return Component.translatable("commands.whitelist.remove.success", Component.literal(var5.getName()));
            }, true);
            ++var3;
         }
      }

      if (var3 == 0) {
         throw ERROR_NOT_WHITELISTED.create();
      } else {
         var0.getServer().kickUnlistedPlayers(var0);
         return var3;
      }
   }

   private static int enableWhitelist(CommandSourceStack var0) throws CommandSyntaxException {
      PlayerList var1 = var0.getServer().getPlayerList();
      if (var1.isUsingWhitelist()) {
         throw ERROR_ALREADY_ENABLED.create();
      } else {
         var1.setUsingWhiteList(true);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.whitelist.enabled");
         }, true);
         var0.getServer().kickUnlistedPlayers(var0);
         return 1;
      }
   }

   private static int disableWhitelist(CommandSourceStack var0) throws CommandSyntaxException {
      PlayerList var1 = var0.getServer().getPlayerList();
      if (!var1.isUsingWhitelist()) {
         throw ERROR_ALREADY_DISABLED.create();
      } else {
         var1.setUsingWhiteList(false);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.whitelist.disabled");
         }, true);
         return 1;
      }
   }

   private static int showList(CommandSourceStack var0) {
      String[] var1 = var0.getServer().getPlayerList().getWhiteListNames();
      if (var1.length == 0) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.whitelist.none");
         }, false);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.whitelist.list", var1.length, String.join(", ", var1));
         }, false);
      }

      return var1.length;
   }
}
