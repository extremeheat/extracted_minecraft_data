package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomSequences;

public class RandomCommand {
   private static final SimpleCommandExceptionType ERROR_RANGE_TOO_LARGE = new SimpleCommandExceptionType(Component.translatable("commands.random.error.range_too_large"));
   private static final SimpleCommandExceptionType ERROR_RANGE_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("commands.random.error.range_too_small"));

   public RandomCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("random").then(drawRandomValueTree("value", false))).then(drawRandomValueTree("roll", true))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reset").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("*").executes((var0x) -> {
         return resetAllSequences((CommandSourceStack)var0x.getSource());
      })).then(((RequiredArgumentBuilder)Commands.argument("seed", IntegerArgumentType.integer()).executes((var0x) -> {
         return resetAllSequencesAndSetNewDefaults((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "seed"), true, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("includeWorldSeed", BoolArgumentType.bool()).executes((var0x) -> {
         return resetAllSequencesAndSetNewDefaults((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "seed"), BoolArgumentType.getBool(var0x, "includeWorldSeed"), true);
      })).then(Commands.argument("includeSequenceId", BoolArgumentType.bool()).executes((var0x) -> {
         return resetAllSequencesAndSetNewDefaults((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "seed"), BoolArgumentType.getBool(var0x, "includeWorldSeed"), BoolArgumentType.getBool(var0x, "includeSequenceId"));
      })))))).then(((RequiredArgumentBuilder)Commands.argument("sequence", ResourceLocationArgument.id()).suggests(RandomCommand::suggestRandomSequence).executes((var0x) -> {
         return resetSequence((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "sequence"));
      })).then(((RequiredArgumentBuilder)Commands.argument("seed", IntegerArgumentType.integer()).executes((var0x) -> {
         return resetSequence((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "sequence"), IntegerArgumentType.getInteger(var0x, "seed"), true, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("includeWorldSeed", BoolArgumentType.bool()).executes((var0x) -> {
         return resetSequence((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "sequence"), IntegerArgumentType.getInteger(var0x, "seed"), BoolArgumentType.getBool(var0x, "includeWorldSeed"), true);
      })).then(Commands.argument("includeSequenceId", BoolArgumentType.bool()).executes((var0x) -> {
         return resetSequence((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "sequence"), IntegerArgumentType.getInteger(var0x, "seed"), BoolArgumentType.getBool(var0x, "includeWorldSeed"), BoolArgumentType.getBool(var0x, "includeSequenceId"));
      })))))));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> drawRandomValueTree(String var0, boolean var1) {
      return (LiteralArgumentBuilder)Commands.literal(var0).then(((RequiredArgumentBuilder)Commands.argument("range", RangeArgument.intRange()).executes((var1x) -> {
         return randomSample((CommandSourceStack)var1x.getSource(), RangeArgument.Ints.getRange(var1x, "range"), (ResourceLocation)null, var1);
      })).then(((RequiredArgumentBuilder)Commands.argument("sequence", ResourceLocationArgument.id()).suggests(RandomCommand::suggestRandomSequence).requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var1x) -> {
         return randomSample((CommandSourceStack)var1x.getSource(), RangeArgument.Ints.getRange(var1x, "range"), ResourceLocationArgument.getId(var1x, "sequence"), var1);
      })));
   }

   private static CompletableFuture<Suggestions> suggestRandomSequence(CommandContext<CommandSourceStack> var0, SuggestionsBuilder var1) {
      ArrayList var2 = Lists.newArrayList();
      ((CommandSourceStack)var0.getSource()).getLevel().getRandomSequences().forAllSequences((var1x, var2x) -> {
         var2.add(var1x.toString());
      });
      return SharedSuggestionProvider.suggest((Iterable)var2, var1);
   }

   private static int randomSample(CommandSourceStack var0, MinMaxBounds.Ints var1, @Nullable ResourceLocation var2, boolean var3) throws CommandSyntaxException {
      RandomSource var4;
      if (var2 != null) {
         var4 = var0.getLevel().getRandomSequence(var2);
      } else {
         var4 = var0.getLevel().getRandom();
      }

      int var5 = (Integer)var1.min().orElse(-2147483648);
      int var6 = (Integer)var1.max().orElse(2147483647);
      long var7 = (long)var6 - (long)var5;
      if (var7 == 0L) {
         throw ERROR_RANGE_TOO_SMALL.create();
      } else if (var7 >= 2147483647L) {
         throw ERROR_RANGE_TOO_LARGE.create();
      } else {
         int var9 = Mth.randomBetweenInclusive(var4, var5, var6);
         if (var3) {
            var0.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("commands.random.roll", var0.getDisplayName(), var9, var5, var6), false);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.random.sample.success", var9);
            }, false);
         }

         return var9;
      }
   }

   private static int resetSequence(CommandSourceStack var0, ResourceLocation var1) throws CommandSyntaxException {
      var0.getLevel().getRandomSequences().reset(var1);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.random.reset.success", Component.translationArg(var1));
      }, false);
      return 1;
   }

   private static int resetSequence(CommandSourceStack var0, ResourceLocation var1, int var2, boolean var3, boolean var4) throws CommandSyntaxException {
      var0.getLevel().getRandomSequences().reset(var1, var2, var3, var4);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.random.reset.success", Component.translationArg(var1));
      }, false);
      return 1;
   }

   private static int resetAllSequences(CommandSourceStack var0) {
      int var1 = var0.getLevel().getRandomSequences().clear();
      var0.sendSuccess(() -> {
         return Component.translatable("commands.random.reset.all.success", var1);
      }, false);
      return var1;
   }

   private static int resetAllSequencesAndSetNewDefaults(CommandSourceStack var0, int var1, boolean var2, boolean var3) {
      RandomSequences var4 = var0.getLevel().getRandomSequences();
      var4.setSeedDefaults(var1, var2, var3);
      int var5 = var4.clear();
      var0.sendSuccess(() -> {
         return Component.translatable("commands.random.reset.all.success", var5);
      }, false);
      return var5;
   }
}
