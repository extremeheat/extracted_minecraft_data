package net.minecraft.server.commands;

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
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;

public class OpCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OP = new SimpleCommandExceptionType(Component.translatable("commands.op.failed"));

   public OpCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("op").requires(Commands.hasPermission(Commands.LEVEL_ADMINS))).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((var0x, var1) -> {
         PlayerList var2 = ((CommandSourceStack)var0x.getSource()).getServer().getPlayerList();
         return SharedSuggestionProvider.suggest(var2.getPlayers().stream().filter((var1x) -> !var2.isOp(var1x.nameAndId())).map((var0) -> var0.getGameProfile().name()), var1);
      }).executes((var0x) -> opPlayers((CommandSourceStack)var0x.getSource(), GameProfileArgument.getGameProfiles(var0x, "targets")))));
   }

   private static int opPlayers(CommandSourceStack var0, Collection<NameAndId> var1) throws CommandSyntaxException {
      PlayerList var2 = var0.getServer().getPlayerList();
      int var3 = 0;

      for(NameAndId var5 : var1) {
         if (!var2.isOp(var5)) {
            var2.op(var5);
            ++var3;
            var0.sendSuccess(() -> Component.translatable("commands.op.success", var5.name()), true);
         }
      }

      if (var3 == 0) {
         throw ERROR_ALREADY_OP.create();
      } else {
         return var3;
      }
   }
}
