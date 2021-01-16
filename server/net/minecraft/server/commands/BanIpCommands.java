package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;

public class BanIpCommands {
   public static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType ERROR_INVALID_IP = new SimpleCommandExceptionType(new TranslatableComponent("commands.banip.invalid"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(new TranslatableComponent("commands.banip.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban-ip").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("target", StringArgumentType.word()).executes((var0x) -> {
         return banIpOrName((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "target"), (Component)null);
      })).then(Commands.argument("reason", MessageArgument.message()).executes((var0x) -> {
         return banIpOrName((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "target"), MessageArgument.getMessage(var0x, "reason"));
      }))));
   }

   private static int banIpOrName(CommandSourceStack var0, String var1, @Nullable Component var2) throws CommandSyntaxException {
      Matcher var3 = IP_ADDRESS_PATTERN.matcher(var1);
      if (var3.matches()) {
         return banIp(var0, var1, var2);
      } else {
         ServerPlayer var4 = var0.getServer().getPlayerList().getPlayerByName(var1);
         if (var4 != null) {
            return banIp(var0, var4.getIpAddress(), var2);
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
         IpBanListEntry var5 = new IpBanListEntry(var1, (Date)null, var0.getTextName(), (Date)null, var2 == null ? null : var2.getString());
         var3.add(var5);
         var0.sendSuccess(new TranslatableComponent("commands.banip.success", new Object[]{var1, var5.getReason()}), true);
         if (!var4.isEmpty()) {
            var0.sendSuccess(new TranslatableComponent("commands.banip.info", new Object[]{var4.size(), EntitySelector.joinNames(var4)}), true);
         }

         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            ServerPlayer var7 = (ServerPlayer)var6.next();
            var7.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.ip_banned"));
         }

         return var4.size();
      }
   }
}
