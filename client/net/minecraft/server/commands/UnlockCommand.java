package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.players.PlayerUnlock;

public class UnlockCommand {
   public UnlockCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("unlock").requires((var0x) -> var0x.hasPermission(2))).then(Commands.argument("unlock", ResourceArgument.resource(var1, Registries.PLAYER_UNLOCK)).executes((var0x) -> unlock((CommandSourceStack)var0x.getSource(), ResourceArgument.getResource(var0x, "unlock", Registries.PLAYER_UNLOCK)))));
   }

   private static int unlock(CommandSourceStack var0, Holder<PlayerUnlock> var1) throws CommandSyntaxException {
      if (((PlayerUnlock)var1.value()).parent().isPresent()) {
         unlock(var0, (Holder)((PlayerUnlock)var1.value()).parent().get());
      }

      var0.getPlayerOrException().forceUnlock(var1);
      return 1;
   }
}
