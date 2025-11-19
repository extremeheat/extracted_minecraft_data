package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;

public class GameRuleCommand {
   public GameRuleCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      final LiteralArgumentBuilder var2 = (LiteralArgumentBuilder)Commands.literal("gamerule").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
      (new GameRules(var1.enabledFeatures())).visitGameRuleTypes(new GameRuleTypeVisitor() {
         public <T> void visit(GameRule<T> var1) {
            LiteralArgumentBuilder var2x = Commands.literal(var1.id());
            LiteralArgumentBuilder var3 = Commands.literal(var1.getIdentifier().toString());
            ((LiteralArgumentBuilder)var2.then(GameRuleCommand.buildRuleArguments(var1, var2x))).then(GameRuleCommand.buildRuleArguments(var1, var3));
         }
      });
      var0.register(var2);
   }

   static <T> LiteralArgumentBuilder<CommandSourceStack> buildRuleArguments(GameRule<T> var0, LiteralArgumentBuilder<CommandSourceStack> var1) {
      return (LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.executes((var1x) -> queryRule((CommandSourceStack)var1x.getSource(), var0))).then(Commands.argument("value", var0.argument()).executes((var1x) -> setRule(var1x, var0)));
   }

   private static <T> int setRule(CommandContext<CommandSourceStack> var0, GameRule<T> var1) {
      CommandSourceStack var2 = (CommandSourceStack)var0.getSource();
      Object var3 = var0.getArgument("value", var1.valueClass());
      var2.getLevel().getGameRules().set(var1, var3, ((CommandSourceStack)var0.getSource()).getServer());
      var2.sendSuccess(() -> Component.translatable("commands.gamerule.set", var1.id(), var1.serialize(var3)), true);
      return var1.getCommandResult(var3);
   }

   private static <T> int queryRule(CommandSourceStack var0, GameRule<T> var1) {
      Object var2 = var0.getLevel().getGameRules().get(var1);
      var0.sendSuccess(() -> Component.translatable("commands.gamerule.query", var1.id(), var1.serialize(var2)), false);
      return var1.getCommandResult(var2);
   }
}
