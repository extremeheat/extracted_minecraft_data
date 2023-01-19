package net.minecraft.server.commands;

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;

public class BanIpCommands {
   private static final SimpleCommandExceptionType ERROR_INVALID_IP = new SimpleCommandExceptionType(Component.translatable("commands.banip.invalid"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(Component.translatable("commands.banip.failed"));

   public BanIpCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban-ip").requires(var0x -> var0x.hasPermission(3)))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("target", StringArgumentType.word())
                     .executes(var0x -> banIpOrName((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "target"), null)))
                  .then(
                     Commands.argument("reason", MessageArgument.message())
                        .executes(
                           var0x -> banIpOrName(
                                 (CommandSourceStack)var0x.getSource(),
                                 StringArgumentType.getString(var0x, "target"),
                                 MessageArgument.getMessage(var0x, "reason")
                              )
                        )
                  )
            )
      );
   }

   private static int banIpOrName(CommandSourceStack var0, String var1, @Nullable Component var2) throws CommandSyntaxException {
      if (InetAddresses.isInetAddress(var1)) {
         return banIp(var0, var1, var2);
      } else {
         ServerPlayer var3 = var0.getServer().getPlayerList().getPlayerByName(var1);
         if (var3 != null) {
            return banIp(var0, var3.getIpAddress(), var2);
         } else {
            throw ERROR_INVALID_IP.create();
         }
      }
   }

   private static int banIp(CommandSourceStack var0, String var1, @Nullable Component var2) throws CommandSyntaxException {
      IpBanList var3 = var0.getServer().getPlayerList().getIpBans();
      if (var3.isBanned(var1)) {
         throw ERROR_ALREADY_BANNED.create();
      } else {
         List var4 = var0.getServer().getPlayerList().getPlayersWithAddress(var1);
         IpBanListEntry var5 = new IpBanListEntry(var1, null, var0.getTextName(), null, var2 == null ? null : var2.getString());
         var3.add(var5);
         var0.sendSuccess(Component.translatable("commands.banip.success", var1, var5.getReason()), true);
         if (!var4.isEmpty()) {
            var0.sendSuccess(Component.translatable("commands.banip.info", var4.size(), EntitySelector.joinNames(var4)), true);
         }

         for(ServerPlayer var7 : var4) {
            var7.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned"));
         }

         return var4.size();
      }
   }
}
