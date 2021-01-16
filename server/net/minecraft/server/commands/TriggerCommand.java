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
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(new TranslatableComponent("commands.trigger.failed.unprimed"));
   private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(new TranslatableComponent("commands.trigger.failed.invalid"));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests((var0x, var1) -> {
         return suggestObjectives((CommandSourceStack)var0x.getSource(), var1);
      }).executes((var0x) -> {
         return simpleTrigger((CommandSourceStack)var0x.getSource(), getScore(((CommandSourceStack)var0x.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(var0x, "objective")));
      })).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((var0x) -> {
         return addValue((CommandSourceStack)var0x.getSource(), getScore(((CommandSourceStack)var0x.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(var0x, "objective")), IntegerArgumentType.getInteger(var0x, "value"));
      })))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((var0x) -> {
         return setValue((CommandSourceStack)var0x.getSource(), getScore(((CommandSourceStack)var0x.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(var0x, "objective")), IntegerArgumentType.getInteger(var0x, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> suggestObjectives(CommandSourceStack var0, SuggestionsBuilder var1) {
      Entity var2 = var0.getEntity();
      ArrayList var3 = Lists.newArrayList();
      if (var2 != null) {
         ServerScoreboard var4 = var0.getServer().getScoreboard();
         String var5 = var2.getScoreboardName();
         Iterator var6 = var4.getObjectives().iterator();

         while(var6.hasNext()) {
            Objective var7 = (Objective)var6.next();
            if (var7.getCriteria() == ObjectiveCriteria.TRIGGER && var4.hasPlayerScore(var5, var7)) {
               Score var8 = var4.getOrCreatePlayerScore(var5, var7);
               if (!var8.isLocked()) {
                  var3.add(var7.getName());
               }
            }
         }
      }

      return SharedSuggestionProvider.suggest((Iterable)var3, var1);
   }

   private static int addValue(CommandSourceStack var0, Score var1, int var2) {
      var1.add(var2);
      var0.sendSuccess(new TranslatableComponent("commands.trigger.add.success", new Object[]{var1.getObjective().getFormattedDisplayName(), var2}), true);
      return var1.getScore();
   }

   private static int setValue(CommandSourceStack var0, Score var1, int var2) {
      var1.setScore(var2);
      var0.sendSuccess(new TranslatableComponent("commands.trigger.set.success", new Object[]{var1.getObjective().getFormattedDisplayName(), var2}), true);
      return var2;
   }

   private static int simpleTrigger(CommandSourceStack var0, Score var1) {
      var1.add(1);
      var0.sendSuccess(new TranslatableComponent("commands.trigger.simple.success", new Object[]{var1.getObjective().getFormattedDisplayName()}), true);
      return var1.getScore();
   }

   private static Score getScore(ServerPlayer var0, Objective var1) throws CommandSyntaxException {
      if (var1.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_INVALID_OBJECTIVE.create();
      } else {
         Scoreboard var2 = var0.getScoreboard();
         String var3 = var0.getScoreboardName();
         if (!var2.hasPlayerScore(var3, var1)) {
            throw ERROR_NOT_PRIMED.create();
         } else {
            Score var4 = var2.getOrCreatePlayerScore(var3, var1);
            if (var4.isLocked()) {
               throw ERROR_NOT_PRIMED.create();
            } else {
               var4.setLocked(true);
               return var4;
            }
         }
      }
   }
}
