package net.minecraft.server.commands;

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.StringArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.IpBanList;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("commands.pardonip.invalid"));
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(Component.translatable("commands.pardonip.failed"));
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_BANNED_PLAYER = SuggestionProviders.serverOnly((server, var1, builder) -> SuggestionContext.suggest(server.getPlayerList().getIpBans().getUserList(), builder));

   public PardonIpCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon-ip").requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_ADMINS))).then(Commands.argument("target", StringArgument.word()).suggests(SUGGEST_BANNED_PLAYER).executes((c) -> unban((CommandSourceStack)c.getSource(), StringArgument.getString(c, "target")))));
   }

   private static int unban(final CommandSourceStack source, final String ip) throws CommandSyntaxException {
      if (!InetAddresses.isInetAddress(ip)) {
         throw ERROR_INVALID.create();
      } else {
         IpBanList bans = source.getServer().getPlayerList().getIpBans();
         if (!bans.isBanned(ip)) {
            throw ERROR_NOT_BANNED.create();
         } else {
            bans.remove(ip);
            source.sendSuccess(() -> Component.translatable("commands.pardonip.success", ip), true);
            return 1;
         }
      }
   }
}
