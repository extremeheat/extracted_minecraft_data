package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(
      Component.translatable("commands.trigger.failed.unprimed")
   );
   private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(
      Component.translatable("commands.trigger.failed.invalid")
   );

   public TriggerCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)Commands.literal("trigger")
            .then(
               ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective())
                        .suggests((var0x, var1) -> suggestObjectives((CommandSourceStack)var0x.getSource(), var1))
                        .executes(
                           var0x -> simpleTrigger(
                                 (CommandSourceStack)var0x.getSource(),
                                 ((CommandSourceStack)var0x.getSource()).getPlayerOrException(),
                                 ObjectiveArgument.getObjective(var0x, "objective")
                              )
                        ))
                     .then(
                        Commands.literal("add")
                           .then(
                              Commands.argument("value", IntegerArgumentType.integer())
                                 .executes(
                                    var0x -> addValue(
                                          (CommandSourceStack)var0x.getSource(),
                                          ((CommandSourceStack)var0x.getSource()).getPlayerOrException(),
                                          ObjectiveArgument.getObjective(var0x, "objective"),
                                          IntegerArgumentType.getInteger(var0x, "value")
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("set")
                        .then(
                           Commands.argument("value", IntegerArgumentType.integer())
                              .executes(
                                 var0x -> setValue(
                                       (CommandSourceStack)var0x.getSource(),
                                       ((CommandSourceStack)var0x.getSource()).getPlayerOrException(),
                                       ObjectiveArgument.getObjective(var0x, "objective"),
                                       IntegerArgumentType.getInteger(var0x, "value")
                                    )
                              )
                        )
                  )
            )
      );
   }

   public static CompletableFuture<Suggestions> suggestObjectives(CommandSourceStack var0, SuggestionsBuilder var1) {
      Entity var2 = var0.getEntity();
      ArrayList var3 = Lists.newArrayList();
      if (var2 != null) {
         ServerScoreboard var4 = var0.getServer().getScoreboard();

         for(Objective var6 : var4.getObjectives()) {
            if (var6.getCriteria() == ObjectiveCriteria.TRIGGER) {
               ReadOnlyScoreInfo var7 = var4.getPlayerScoreInfo(var2, var6);
               if (var7 != null && !var7.isLocked()) {
                  var3.add(var6.getName());
               }
            }
         }
      }

      return SharedSuggestionProvider.suggest(var3, var1);
   }

   private static int addValue(CommandSourceStack var0, ServerPlayer var1, Objective var2, int var3) throws CommandSyntaxException {
      ScoreAccess var4 = getScore(var0.getServer().getScoreboard(), var1, var2);
      int var5 = var4.add(var3);
      var0.sendSuccess(() -> Component.translatable("commands.trigger.add.success", var2.getFormattedDisplayName(), var3), true);
      return var5;
   }

   private static int setValue(CommandSourceStack var0, ServerPlayer var1, Objective var2, int var3) throws CommandSyntaxException {
      ScoreAccess var4 = getScore(var0.getServer().getScoreboard(), var1, var2);
      var4.set(var3);
      var0.sendSuccess(() -> Component.translatable("commands.trigger.set.success", var2.getFormattedDisplayName(), var3), true);
      return var3;
   }

   private static int simpleTrigger(CommandSourceStack var0, ServerPlayer var1, Objective var2) throws CommandSyntaxException {
      ScoreAccess var3 = getScore(var0.getServer().getScoreboard(), var1, var2);
      int var4 = var3.add(1);
      var0.sendSuccess(() -> Component.translatable("commands.trigger.simple.success", var2.getFormattedDisplayName()), true);
      return var4;
   }

   private static ScoreAccess getScore(Scoreboard var0, ScoreHolder var1, Objective var2) throws CommandSyntaxException {
      if (var2.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_INVALID_OBJECTIVE.create();
      } else {
         ReadOnlyScoreInfo var3 = var0.getPlayerScoreInfo(var1, var2);
         if (var3 != null && !var3.isLocked()) {
            ScoreAccess var4 = var0.getOrCreatePlayerScore(var1, var2);
            var4.lock();
            return var4;
         } else {
            throw ERROR_NOT_PRIMED.create();
         }
      }
   }
}
