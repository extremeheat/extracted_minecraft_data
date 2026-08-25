package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ComputeCommand {
   public ComputeCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)LootContextSources.addContextSources((LiteralArgumentBuilder)Commands.literal("compute").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)), (contextDecorator) -> ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("provider", ResourceOrIdArgument.numberProvider(context)).executes((c) -> computeAsFloat(c, contextDecorator, ResourceOrIdArgument.getNumberProvider(c, "provider"), 1.0F))).then(Commands.argument("scale", FloatArgumentType.floatArg()).executes((c) -> computeAsFloat(c, contextDecorator, ResourceOrIdArgument.getNumberProvider(c, "provider"), FloatArgumentType.getFloat(c, "scale"))))).then(Commands.literal("integer").executes((c) -> computeAsInt(c, contextDecorator, ResourceOrIdArgument.getNumberProvider(c, "provider"))))));
   }

   private static void printExactOutput(final CommandSourceStack source, final Holder<NumberProvider> provider, final int result) {
      source.sendSuccess(() -> (Component)provider.unwrapKey().map((key) -> Component.translatable("command.compute.result.named.exact", Component.translationArg(key.identifier()), result)).orElseGet(() -> Component.translatable("command.compute.result.unnamed.exact", result)), false);
   }

   private static void printRoundedOutput(final CommandSourceStack source, final Holder<NumberProvider> provider, final int result, final float original) {
      source.sendSuccess(() -> (Component)provider.unwrapKey().map((key) -> Component.translatable("command.compute.result.named.rounded", Component.translationArg(key.identifier()), original, result)).orElseGet(() -> Component.translatable("command.compute.result.unnamed.rounded", original, result)), false);
   }

   private static int computeAsInt(final CommandContext<CommandSourceStack> context, final LootContextSources.ContextDecorator decorator, final Holder<NumberProvider> provider) throws CommandSyntaxException {
      LootContext lootContext = decorator.createContext(context);
      int result = ((NumberProvider)provider.value()).getInt(lootContext);
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      printExactOutput(source, provider, result);
      return result;
   }

   private static int computeAsFloat(final CommandContext<CommandSourceStack> context, final LootContextSources.ContextDecorator decorator, final Holder<NumberProvider> provider, final float scale) throws CommandSyntaxException {
      LootContext lootContext = decorator.createContext(context);
      float original = ((NumberProvider)provider.value()).getFloat(lootContext);
      int result = Mth.floor(original * scale);
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      if ((float)result == original) {
         printExactOutput(source, provider, result);
      } else {
         printRoundedOutput(source, provider, result, original);
      }

      return result;
   }
}
