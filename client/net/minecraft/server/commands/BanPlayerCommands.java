package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;

public class BanPlayerCommands {
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(Component.translatable("commands.ban.failed"));

   public BanPlayerCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", GameProfileArgument.gameProfile()).executes((var0x) -> {
         return banPlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets"), (Component)null);
      })).then(Commands.argument("reason", MessageArgument.message()).executes((var0x) -> {
         return banPlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets"), MessageArgument.getMessage(var0x, "reason"));
      }))));
   }

   private static int banPlayers(CommandSourceStack var0, Collection<GameProfile> var1, @Nullable Component var2) throws CommandSyntaxException {
      UserBanList var3 = var0.getServer().getPlayerList().getBans();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         GameProfile var6 = (GameProfile)var5.next();
         if (!var3.isBanned(var6)) {
            UserBanListEntry var7 = new UserBanListEntry(var6, (Date)null, var0.getTextName(), (Date)null, var2 == null ? null : var2.getString());
            var3.add(var7);
            ++var4;
            var0.sendSuccess(() -> {
               return Component.translatable("commands.ban.success", Component.literal(var6.getName()), var7.getReason());
            }, true);
            ServerPlayer var8 = var0.getServer().getPlayerList().getPlayer(var6.getId());
            if (var8 != null) {
               var8.connection.disconnect(Component.translatable("multiplayer.disconnect.banned"));
            }
         }
      }

      if (var4 == 0) {
         throw ERROR_ALREADY_BANNED.create();
      } else {
         return var4;
      }
   }
}
