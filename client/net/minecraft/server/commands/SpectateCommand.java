package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class SpectateCommand {
   private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType(Component.translatable("commands.spectate.self"));
   private static final DynamicCommandExceptionType ERROR_NOT_SPECTATOR = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.spectate.not_spectator", var0));
   private static final DynamicCommandExceptionType ERROR_CANNOT_SPECTATE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("commands.spectate.cannot_spectate", var0));

   public SpectateCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spectate").requires(Commands.hasPermission(2))).executes((var0x) -> spectate((CommandSourceStack)var0x.getSource(), (Entity)null, ((CommandSourceStack)var0x.getSource()).getPlayerOrException()))).then(((RequiredArgumentBuilder)Commands.argument("target", EntityArgument.entity()).executes((var0x) -> spectate((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), ((CommandSourceStack)var0x.getSource()).getPlayerOrException()))).then(Commands.argument("player", EntityArgument.player()).executes((var0x) -> spectate((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), EntityArgument.getPlayer(var0x, "player"))))));
   }

   private static int spectate(CommandSourceStack var0, @Nullable Entity var1, ServerPlayer var2) throws CommandSyntaxException {
      if (var2 == var1) {
         throw ERROR_SELF.create();
      } else if (!var2.isSpectator()) {
         throw ERROR_NOT_SPECTATOR.create(var2.getDisplayName());
      } else if (var1 != null && var1.getType().clientTrackingRange() == 0) {
         throw ERROR_CANNOT_SPECTATE.create(var1.getDisplayName());
      } else {
         var2.setCamera(var1);
         if (var1 != null) {
            var0.sendSuccess(() -> Component.translatable("commands.spectate.success.started", var1.getDisplayName()), false);
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.spectate.success.stopped"), false);
         }

         return 1;
      }
   }
}
