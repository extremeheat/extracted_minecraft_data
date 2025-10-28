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
            var2.then(((LiteralArgumentBuilder)var2x.executes((var1x) -> GameRuleCommand.queryRule((CommandSourceStack)var1x.getSource(), var1))).then(Commands.argument("value", var1.argument()).executes((var1x) -> GameRuleCommand.setRule(var1x, var1))));
         }
      });
      var0.register(var2);
   }

   static <T> int setRule(CommandContext<CommandSourceStack> var0, GameRule<T> var1) {
      CommandSourceStack var2 = (CommandSourceStack)var0.getSource();
      Object var3 = var0.getArgument("value", var1.valueClass());
      var2.getLevel().getGameRules().set(var1, var3, ((CommandSourceStack)var0.getSource()).getServer());
      var2.sendSuccess(() -> Component.translatable("commands.gamerule.set", var1.id(), var1.serialize(var3)), true);
      return var1.getCommandResult(var3);
   }

   static <T> int queryRule(CommandSourceStack var0, GameRule<T> var1) {
      Object var2 = var0.getLevel().getGameRules().get(var1);
      var0.sendSuccess(() -> Component.translatable("commands.gamerule.query", var1.id(), var1.serialize(var2)), false);
      return var1.getCommandResult(var2);
   }
}
