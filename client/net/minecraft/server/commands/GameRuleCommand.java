package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;

public class GameRuleCommand {
   public GameRuleCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      final LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.literal("gamerule").requires(var0x -> var0x.hasPermission(2));
      GameRules.visitGameRuleTypes(
         new GameRules.GameRuleTypeVisitor() {
            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1x, GameRules.Type<T> var2) {
               var1.then(
                  ((LiteralArgumentBuilder)Commands.literal(var1x.getId())
                        .executes(var1xxx -> GameRuleCommand.queryRule((CommandSourceStack)var1xxx.getSource(), var1x)))
                     .then(var2.createArgument("value").executes(var1xxx -> GameRuleCommand.setRule(var1xxx, var1x)))
               );
            }
         }
      );
      var0.register(var1);
   }

   static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> var0, GameRules.Key<T> var1) {
      CommandSourceStack var2 = (CommandSourceStack)var0.getSource();
      GameRules.Value var3 = var2.getServer().getGameRules().getRule(var1);
      var3.setFromArgument(var0, "value");
      var2.sendSuccess(Component.translatable("commands.gamerule.set", var1.getId(), var3.toString()), true);
      return var3.getCommandResult();
   }

   static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack var0, GameRules.Key<T> var1) {
      GameRules.Value var2 = var0.getServer().getGameRules().getRule(var1);
      var0.sendSuccess(Component.translatable("commands.gamerule.query", var1.getId(), var2.toString()), false);
      return var2.getCommandResult();
   }
}
