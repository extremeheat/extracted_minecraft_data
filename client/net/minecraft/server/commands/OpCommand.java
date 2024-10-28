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

public class OpCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OP = new SimpleCommandExceptionType(Component.translatable("commands.op.failed"));

   public OpCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("op").requires((var0x) -> {
         return var0x.hasPermission(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((var0x, var1) -> {
         PlayerList var2 = ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList();
         return SharedSuggestionProvider.suggest(var2.getPlayers().stream().filter((var1x) -> {
            return !var2.isOp(var1x.getGameProfile());
         }).map((var0) -> {
            return var0.getGameProfile().getName();
         }), var1);
      }).executes((var0x) -> {
         return opPlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets"));
      })));
   }

   private static int opPlayers(CommandSourceStack var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      PlayerList var2 = var0.getServer().getPlayerList();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (!var2.isOp(var5)) {
            var2.op(var5);
            ++var3;
            var0.sendSuccess(() -> {
               return Component.translatable("commands.op.success", ((GameProfile)var1.iterator().next()).getName());
            }, true);
         }
      }

      if (var3 == 0) {
         throw ERROR_ALREADY_OP.create();
      } else {
         return var3;
      }
   }
}
