package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;

public class DeOpCommands {
   private static final SimpleCommandExceptionType ERROR_NOT_OP = new SimpleCommandExceptionType(Component.translatable("commands.deop.failed"));

   public DeOpCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deop").requires(var0x -> var0x.hasPermission(3)))
            .then(
               Commands.argument("targets", GameProfileArgument.gameProfile())
                  .suggests(
                     (var0x, var1) -> SharedSuggestionProvider.suggest(((CommandSourceStack)var0x.getSource()).getServer().getPlayerList().getOpNames(), var1)
                  )
                  .executes(var0x -> deopPlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets")))
            )
      );
   }

   private static int deopPlayers(CommandSourceStack var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      PlayerList var2 = var0.getServer().getPlayerList();
      int var3 = 0;

      for (GameProfile var5 : var1) {
         if (var2.isOp(var5)) {
            var2.deop(var5);
            var3++;
            var0.sendSuccess(() -> Component.translatable("commands.deop.success", ((GameProfile)var1.iterator().next()).getName()), true);
         }
      }

      if (var3 == 0) {
         throw ERROR_NOT_OP.create();
      } else {
         var0.getServer().kickUnlistedPlayers(var0);
         return var3;
      }
   }
}
