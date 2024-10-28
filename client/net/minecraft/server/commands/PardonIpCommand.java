package net.minecraft.server.commands;

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.IpBanList;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("commands.pardonip.invalid"));
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(Component.translatable("commands.pardonip.failed"));

   public PardonIpCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon-ip").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.argument("target", StringArgumentType.word()).suggests((var0x, var1) -> {
         return SharedSuggestionProvider.suggest(((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getIpBans().getUserList(), var1);
      }).executes((var0x) -> {
         return unban((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "target"));
      })));
   }

   private static int unban(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      if (!InetAddresses.isInetAddress(var1)) {
         throw ERROR_INVALID.create();
      } else {
         IpBanList var2 = var0.getServer().getPlayerList().getIpBans();
         if (!var2.isBanned(var1)) {
            throw ERROR_NOT_BANNED.create();
         } else {
            var2.remove(var1);
            var0.sendSuccess(() -> {
               return Component.translatable("commands.pardonip.success", var1);
            }, true);
            return 1;
         }
      }
   }
}
