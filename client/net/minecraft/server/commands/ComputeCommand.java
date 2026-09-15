package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

public class ComputeCommand {
   private static final Dynamic2CommandExceptionType INVALID_NAMED_VALUE = new Dynamic2CommandExceptionType((provider, value) -> Component.translatableEscape("command.compute.result.named.invalid", provider, value));
   private static final DynamicCommandExceptionType INVALID_UNNAMED_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("command.compute.result.unnamed.invalid", value));

   public ComputeCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)LootContextSources.addContextSources((LiteralArgumentBuilder)Commands.literal("compute").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)), (contextDecorator, output) -> {
         output.accept(Commands.literal("float").then(((RequiredArgumentBuilder)Commands.argument("provider", ResourceOrIdArgument.floatProvider(context)).executes((c) -> computeAsFloat(c, contextDecorator, ResourceOrIdArgument.getFloatProvider(c, "provider"), 1.0F))).then(Commands.argument("scale", FloatArgumentType.floatArg()).executes((c) -> computeAsFloat(c, contextDecorator, ResourceOrIdArgument.getFloatProvider(c, "provider"), FloatArgumentType.getFloat(c, "scale"))))));
         output.accept(Commands.literal("integer").then(Commands.argument("provider", ResourceOrIdArgument.intProvider(context)).executes((c) -> computeAsInt(c, contextDecorator, ResourceOrIdArgument.getIntProvider(c, "provider")))));
      }));
   }

   private static void printExactOutput(final CommandSourceStack source, final Holder<?> provider, final int result) {
      source.sendSuccess(() -> (Component)provider.unwrapKey().map((key) -> Component.translatable("command.compute.result.named.exact", Component.translationArg(key.identifier()), result)).orElseGet(() -> Component.translatable("command.compute.result.unnamed.exact", result)), false);
   }

   private static void printRoundedOutput(final CommandSourceStack source, final Holder<?> provider, final int result, final float original) {
      source.sendSuccess(() -> (Component)provider.unwrapKey().map((key) -> Component.translatable("command.compute.result.named.rounded", Component.translationArg(key.identifier()), original, result)).orElseGet(() -> Component.translatable("command.compute.result.unnamed.rounded", original, result)), false);
   }

   private static int computeAsInt(final CommandContext<CommandSourceStack> context, final LootContextSources.ContextDecorator decorator, final Holder<ContextIntProvider> provider) throws CommandSyntaxException {
      LootContext lootContext = decorator.createContext(context);

      try {
         int result = ((ContextIntProvider)provider.value()).getIntUnsafe(lootContext);
         CommandSourceStack source = (CommandSourceStack)context.getSource();
         printExactOutput(source, provider, result);
         return result;
      } catch (ArithmeticException e) {
         throw throwInvalidValue(provider, e.getMessage());
      }
   }

   private static int computeAsFloat(final CommandContext<CommandSourceStack> context, final LootContextSources.ContextDecorator decorator, final Holder<ContextFloatProvider> provider, final float scale) throws CommandSyntaxException {
      LootContext lootContext = decorator.createContext(context);

      float original;
      try {
         original = ((ContextFloatProvider)provider.value()).getFloatUnsafe(lootContext);
      } catch (ArithmeticException e) {
         throw throwInvalidValue(provider, e.getMessage());
      }

      if (!Float.isFinite(original)) {
         String invalidValue = Float.toString(original);
         throw throwInvalidValue(provider, invalidValue);
      } else {
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

   private static CommandSyntaxException throwInvalidValue(final Holder<?> provider, final String invalidValue) {
      return (CommandSyntaxException)provider.unwrapKey().map((key) -> INVALID_NAMED_VALUE.create(Component.translationArg(key.identifier()), invalidValue)).orElseGet(() -> INVALID_UNNAMED_VALUE.create(invalidValue));
   }
}
