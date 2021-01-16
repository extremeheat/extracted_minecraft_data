package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.add.duplicate"));
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.display.alreadyEmpty"));
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.display.alreadySet"));
   private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.players.enable.failed"));
   private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.players.enable.invalid"));
   private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.scoreboard.players.get.null", new Object[]{var0, var1});
   });

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes((var0x) -> {
         return listObjectives((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.criteria()).executes((var0x) -> {
         return addObjective((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "objective"), ObjectiveCriteriaArgument.getCriteria(var0x, "criteria"), new TextComponent(StringArgumentType.getString(var0x, "objective")));
      })).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((var0x) -> {
         return addObjective((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "objective"), ObjectiveCriteriaArgument.getCriteria(var0x, "criteria"), ComponentArgument.getComponent(var0x, "displayName"));
      })))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((var0x) -> {
         return setDisplayName((CommandSourceStack)var0x.getSource(), ObjectiveArgument.getObjective(var0x, "objective"), ComponentArgument.getComponent(var0x, "displayName"));
      })))).then(createRenderTypeModify())))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes((var0x) -> {
         return removeObjective((CommandSourceStack)var0x.getSource(), ObjectiveArgument.getObjective(var0x, "objective"));
      })))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.displaySlot()).executes((var0x) -> {
         return clearDisplaySlot((CommandSourceStack)var0x.getSource(), ScoreboardSlotArgument.getDisplaySlot(var0x, "slot"));
      })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((var0x) -> {
         return setDisplaySlot((CommandSourceStack)var0x.getSource(), ScoreboardSlotArgument.getDisplaySlot(var0x, "slot"), ObjectiveArgument.getObjective(var0x, "objective"));
      })))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes((var0x) -> {
         return listTrackedPlayers((CommandSourceStack)var0x.getSource());
      })).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((var0x) -> {
         return listTrackedPlayerScores((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getName(var0x, "target"));
      })))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes((var0x) -> {
         return setScore((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), ObjectiveArgument.getWritableObjective(var0x, "objective"), IntegerArgumentType.getInteger(var0x, "score"));
      })))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((var0x) -> {
         return getScore((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getName(var0x, "target"), ObjectiveArgument.getObjective(var0x, "objective"));
      }))))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return addScore((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), ObjectiveArgument.getWritableObjective(var0x, "objective"), IntegerArgumentType.getInteger(var0x, "score"));
      })))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return removeScore((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), ObjectiveArgument.getWritableObjective(var0x, "objective"), IntegerArgumentType.getInteger(var0x, "score"));
      })))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((var0x) -> {
         return resetScores((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"));
      })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((var0x) -> {
         return resetScore((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), ObjectiveArgument.getObjective(var0x, "objective"));
      }))))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((var0x, var1) -> {
         return suggestTriggers((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), var1);
      }).executes((var0x) -> {
         return enableTrigger((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), ObjectiveArgument.getObjective(var0x, "objective"));
      }))))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes((var0x) -> {
         return performOperation((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), ObjectiveArgument.getWritableObjective(var0x, "targetObjective"), OperationArgument.getOperation(var0x, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "source"), ObjectiveArgument.getObjective(var0x, "sourceObjective"));
      })))))))));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
      LiteralArgumentBuilder var0 = Commands.literal("rendertype");
      ObjectiveCriteria.RenderType[] var1 = ObjectiveCriteria.RenderType.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ObjectiveCriteria.RenderType var4 = var1[var3];
         var0.then(Commands.literal(var4.getId()).executes((var1x) -> {
            return setRenderType((CommandSourceStack)var1x.getSource(), ObjectiveArgument.getObjective(var1x, "objective"), var4);
         }));
      }

      return var0;
   }

   private static CompletableFuture<Suggestions> suggestTriggers(CommandSourceStack var0, Collection<String> var1, SuggestionsBuilder var2) {
      ArrayList var3 = Lists.newArrayList();
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      Iterator var5 = var4.getObjectives().iterator();

      while(true) {
         Objective var6;
         do {
            if (!var5.hasNext()) {
               return SharedSuggestionProvider.suggest((Iterable)var3, var2);
            }

            var6 = (Objective)var5.next();
         } while(var6.getCriteria() != ObjectiveCriteria.TRIGGER);

         boolean var7 = false;
         Iterator var8 = var1.iterator();

         label32: {
            String var9;
            do {
               if (!var8.hasNext()) {
                  break label32;
               }

               var9 = (String)var8.next();
            } while(var4.hasPlayerScore(var9, var6) && !var4.getOrCreatePlayerScore(var9, var6).isLocked());

            var7 = true;
         }

         if (var7) {
            var3.add(var6.getName());
         }
      }
   }

   private static int getScore(CommandSourceStack var0, String var1, Objective var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.getServer().getScoreboard();
      if (!var3.hasPlayerScore(var1, var2)) {
         throw ERROR_NO_VALUE.create(var2.getName(), var1);
      } else {
         Score var4 = var3.getOrCreatePlayerScore(var1, var2);
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.get.success", new Object[]{var1, var4.getScore(), var2.getFormattedDisplayName()}), false);
         return var4.getScore();
      }
   }

   private static int performOperation(CommandSourceStack var0, Collection<String> var1, Objective var2, OperationArgument.Operation var3, Collection<String> var4, Objective var5) throws CommandSyntaxException {
      ServerScoreboard var6 = var0.getServer().getScoreboard();
      int var7 = 0;

      Score var10;
      for(Iterator var8 = var1.iterator(); var8.hasNext(); var7 += var10.getScore()) {
         String var9 = (String)var8.next();
         var10 = var6.getOrCreatePlayerScore(var9, var2);
         Iterator var11 = var4.iterator();

         while(var11.hasNext()) {
            String var12 = (String)var11.next();
            Score var13 = var6.getOrCreatePlayerScore(var12, var5);
            var3.apply(var10, var13);
         }
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.single", new Object[]{var2.getFormattedDisplayName(), var1.iterator().next(), var7}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.multiple", new Object[]{var2.getFormattedDisplayName(), var1.size()}), true);
      }

      return var7;
   }

   private static int enableTrigger(CommandSourceStack var0, Collection<String> var1, Objective var2) throws CommandSyntaxException {
      if (var2.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_NOT_TRIGGER.create();
      } else {
         ServerScoreboard var3 = var0.getServer().getScoreboard();
         int var4 = 0;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            Score var7 = var3.getOrCreatePlayerScore(var6, var2);
            if (var7.isLocked()) {
               var7.setLocked(false);
               ++var4;
            }
         }

         if (var4 == 0) {
            throw ERROR_TRIGGER_ALREADY_ENABLED.create();
         } else {
            if (var1.size() == 1) {
               var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.single", new Object[]{var2.getFormattedDisplayName(), var1.iterator().next()}), true);
            } else {
               var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.multiple", new Object[]{var2.getFormattedDisplayName(), var1.size()}), true);
            }

            return var4;
         }
      }
   }

   private static int resetScores(CommandSourceStack var0, Collection<String> var1) {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.resetPlayerScore(var4, (Objective)null);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.single", new Object[]{var1.iterator().next()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int resetScore(CommandSourceStack var0, Collection<String> var1, Objective var2) {
      ServerScoreboard var3 = var0.getServer().getScoreboard();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.resetPlayerScore(var5, var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.single", new Object[]{var2.getFormattedDisplayName(), var1.iterator().next()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.multiple", new Object[]{var2.getFormattedDisplayName(), var1.size()}), true);
      }

      return var1.size();
   }

   private static int setScore(CommandSourceStack var0, Collection<String> var1, Objective var2, int var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         Score var7 = var4.getOrCreatePlayerScore(var6, var2);
         var7.setScore(var3);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.single", new Object[]{var2.getFormattedDisplayName(), var1.iterator().next(), var3}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.multiple", new Object[]{var2.getFormattedDisplayName(), var1.size(), var3}), true);
      }

      return var3 * var1.size();
   }

   private static int addScore(CommandSourceStack var0, Collection<String> var1, Objective var2, int var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      int var5 = 0;

      Score var8;
      for(Iterator var6 = var1.iterator(); var6.hasNext(); var5 += var8.getScore()) {
         String var7 = (String)var6.next();
         var8 = var4.getOrCreatePlayerScore(var7, var2);
         var8.setScore(var8.getScore() + var3);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.single", new Object[]{var3, var2.getFormattedDisplayName(), var1.iterator().next(), var5}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.multiple", new Object[]{var3, var2.getFormattedDisplayName(), var1.size()}), true);
      }

      return var5;
   }

   private static int removeScore(CommandSourceStack var0, Collection<String> var1, Objective var2, int var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      int var5 = 0;

      Score var8;
      for(Iterator var6 = var1.iterator(); var6.hasNext(); var5 += var8.getScore()) {
         String var7 = (String)var6.next();
         var8 = var4.getOrCreatePlayerScore(var7, var2);
         var8.setScore(var8.getScore() - var3);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.single", new Object[]{var3, var2.getFormattedDisplayName(), var1.iterator().next(), var5}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.multiple", new Object[]{var3, var2.getFormattedDisplayName(), var1.size()}), true);
      }

      return var5;
   }

   private static int listTrackedPlayers(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getScoreboard().getTrackedPlayers();
      if (var1.isEmpty()) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.empty"), false);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.success", new Object[]{var1.size(), ComponentUtils.formatList(var1)}), false);
      }

      return var1.size();
   }

   private static int listTrackedPlayerScores(CommandSourceStack var0, String var1) {
      Map var2 = var0.getServer().getScoreboard().getPlayerScores(var1);
      if (var2.isEmpty()) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.empty", new Object[]{var1}), false);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.success", new Object[]{var1, var2.size()}), false);
         Iterator var3 = var2.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var0.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.entry", new Object[]{((Objective)var4.getKey()).getFormattedDisplayName(), ((Score)var4.getValue()).getScore()}), false);
         }
      }

      return var2.size();
   }

   private static int clearDisplaySlot(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      if (var2.getDisplayObjective(var1) == null) {
         throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
      } else {
         var2.setDisplayObjective(var1, (Objective)null);
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.cleared", new Object[]{Scoreboard.getDisplaySlotNames()[var1]}), true);
         return 0;
      }
   }

   private static int setDisplaySlot(CommandSourceStack var0, int var1, Objective var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.getServer().getScoreboard();
      if (var3.getDisplayObjective(var1) == var2) {
         throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
      } else {
         var3.setDisplayObjective(var1, var2);
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.set", new Object[]{Scoreboard.getDisplaySlotNames()[var1], var2.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack var0, Objective var1, Component var2) {
      if (!var1.getDisplayName().equals(var2)) {
         var1.setDisplayName(var2);
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.displayname", new Object[]{var1.getName(), var1.getFormattedDisplayName()}), true);
      }

      return 0;
   }

   private static int setRenderType(CommandSourceStack var0, Objective var1, ObjectiveCriteria.RenderType var2) {
      if (var1.getRenderType() != var2) {
         var1.setRenderType(var2);
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.rendertype", new Object[]{var1.getFormattedDisplayName()}), true);
      }

      return 0;
   }

   private static int removeObjective(CommandSourceStack var0, Objective var1) {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      var2.removeObjective(var1);
      var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.remove.success", new Object[]{var1.getFormattedDisplayName()}), true);
      return var2.getObjectives().size();
   }

   private static int addObjective(CommandSourceStack var0, String var1, ObjectiveCriteria var2, Component var3) throws CommandSyntaxException {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      if (var4.getObjective(var1) != null) {
         throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
      } else if (var1.length() > 16) {
         throw ObjectiveArgument.ERROR_OBJECTIVE_NAME_TOO_LONG.create(16);
      } else {
         var4.addObjective(var1, var2, var3, var2.getDefaultRenderType());
         Objective var5 = var4.getObjective(var1);
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.add.success", new Object[]{var5.getFormattedDisplayName()}), true);
         return var4.getObjectives().size();
      }
   }

   private static int listObjectives(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getScoreboard().getObjectives();
      if (var1.isEmpty()) {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.empty"), false);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.success", new Object[]{var1.size(), ComponentUtils.formatList(var1, Objective::getFormattedDisplayName)}), false);
      }

      return var1.size();
   }
}
