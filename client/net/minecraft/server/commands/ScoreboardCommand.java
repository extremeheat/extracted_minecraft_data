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
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType(
      Component.translatable("commands.scoreboard.objectives.add.duplicate")
   );
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType(
      Component.translatable("commands.scoreboard.objectives.display.alreadyEmpty")
   );
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType(
      Component.translatable("commands.scoreboard.objectives.display.alreadySet")
   );
   private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType(
      Component.translatable("commands.scoreboard.players.enable.failed")
   );
   private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType(
      Component.translatable("commands.scoreboard.players.enable.invalid")
   );
   private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatable("commands.scoreboard.players.get.null", var0, var1)
   );

   public ScoreboardCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires(var0x -> var0x.hasPermission(2)))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives")
                                 .then(Commands.literal("list").executes(var0x -> listObjectives((CommandSourceStack)var0x.getSource()))))
                              .then(
                                 Commands.literal("add")
                                    .then(
                                       Commands.argument("objective", StringArgumentType.word())
                                          .then(
                                             ((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.criteria())
                                                   .executes(
                                                      var0x -> addObjective(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            StringArgumentType.getString(var0x, "objective"),
                                                            ObjectiveCriteriaArgument.getCriteria(var0x, "criteria"),
                                                            Component.literal(StringArgumentType.getString(var0x, "objective"))
                                                         )
                                                   ))
                                                .then(
                                                   Commands.argument("displayName", ComponentArgument.textComponent())
                                                      .executes(
                                                         var0x -> addObjective(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               StringArgumentType.getString(var0x, "objective"),
                                                               ObjectiveCriteriaArgument.getCriteria(var0x, "criteria"),
                                                               ComponentArgument.getComponent(var0x, "displayName")
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("modify")
                                 .then(
                                    ((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective())
                                          .then(
                                             Commands.literal("displayname")
                                                .then(
                                                   Commands.argument("displayName", ComponentArgument.textComponent())
                                                      .executes(
                                                         var0x -> setDisplayName(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               ObjectiveArgument.getObjective(var0x, "objective"),
                                                               ComponentArgument.getComponent(var0x, "displayName")
                                                            )
                                                      )
                                                )
                                          ))
                                       .then(createRenderTypeModify())
                                 )
                           ))
                        .then(
                           Commands.literal("remove")
                              .then(
                                 Commands.argument("objective", ObjectiveArgument.objective())
                                    .executes(
                                       var0x -> removeObjective((CommandSourceStack)var0x.getSource(), ObjectiveArgument.getObjective(var0x, "objective"))
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("setdisplay")
                           .then(
                              ((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.displaySlot())
                                    .executes(
                                       var0x -> clearDisplaySlot((CommandSourceStack)var0x.getSource(), ScoreboardSlotArgument.getDisplaySlot(var0x, "slot"))
                                    ))
                                 .then(
                                    Commands.argument("objective", ObjectiveArgument.objective())
                                       .executes(
                                          var0x -> setDisplaySlot(
                                                (CommandSourceStack)var0x.getSource(),
                                                ScoreboardSlotArgument.getDisplaySlot(var0x, "slot"),
                                                ObjectiveArgument.getObjective(var0x, "objective")
                                             )
                                       )
                                 )
                           )
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                          "players"
                                       )
                                       .then(
                                          ((LiteralArgumentBuilder)Commands.literal("list")
                                                .executes(var0x -> listTrackedPlayers((CommandSourceStack)var0x.getSource())))
                                             .then(
                                                Commands.argument("target", ScoreHolderArgument.scoreHolder())
                                                   .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                   .executes(
                                                      var0x -> listTrackedPlayerScores(
                                                            (CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getName(var0x, "target")
                                                         )
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("set")
                                          .then(
                                             Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                                                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(
                                                   Commands.argument("objective", ObjectiveArgument.objective())
                                                      .then(
                                                         Commands.argument("score", IntegerArgumentType.integer())
                                                            .executes(
                                                               var0x -> setScore(
                                                                     (CommandSourceStack)var0x.getSource(),
                                                                     ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"),
                                                                     ObjectiveArgument.getWritableObjective(var0x, "objective"),
                                                                     IntegerArgumentType.getInteger(var0x, "score")
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("get")
                                       .then(
                                          Commands.argument("target", ScoreHolderArgument.scoreHolder())
                                             .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                             .then(
                                                Commands.argument("objective", ObjectiveArgument.objective())
                                                   .executes(
                                                      var0x -> getScore(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            ScoreHolderArgument.getName(var0x, "target"),
                                                            ObjectiveArgument.getObjective(var0x, "objective")
                                                         )
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("add")
                                    .then(
                                       Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                                          .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                          .then(
                                             Commands.argument("objective", ObjectiveArgument.objective())
                                                .then(
                                                   Commands.argument("score", IntegerArgumentType.integer(0))
                                                      .executes(
                                                         var0x -> addScore(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"),
                                                               ObjectiveArgument.getWritableObjective(var0x, "objective"),
                                                               IntegerArgumentType.getInteger(var0x, "score")
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("remove")
                                 .then(
                                    Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                                       .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                       .then(
                                          Commands.argument("objective", ObjectiveArgument.objective())
                                             .then(
                                                Commands.argument("score", IntegerArgumentType.integer(0))
                                                   .executes(
                                                      var0x -> removeScore(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"),
                                                            ObjectiveArgument.getWritableObjective(var0x, "objective"),
                                                            IntegerArgumentType.getInteger(var0x, "score")
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("reset")
                              .then(
                                 ((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                                       .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                       .executes(
                                          var0x -> resetScores(
                                                (CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets")
                                             )
                                       ))
                                    .then(
                                       Commands.argument("objective", ObjectiveArgument.objective())
                                          .executes(
                                             var0x -> resetScore(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"),
                                                   ObjectiveArgument.getObjective(var0x, "objective")
                                                )
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("enable")
                           .then(
                              Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                                 .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                 .then(
                                    Commands.argument("objective", ObjectiveArgument.objective())
                                       .suggests(
                                          (var0x, var1) -> suggestTriggers(
                                                (CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"), var1
                                             )
                                       )
                                       .executes(
                                          var0x -> enableTrigger(
                                                (CommandSourceStack)var0x.getSource(),
                                                ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"),
                                                ObjectiveArgument.getObjective(var0x, "objective")
                                             )
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("operation")
                        .then(
                           Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                              .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                              .then(
                                 Commands.argument("targetObjective", ObjectiveArgument.objective())
                                    .then(
                                       Commands.argument("operation", OperationArgument.operation())
                                          .then(
                                             Commands.argument("source", ScoreHolderArgument.scoreHolders())
                                                .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                .then(
                                                   Commands.argument("sourceObjective", ObjectiveArgument.objective())
                                                      .executes(
                                                         var0x -> performOperation(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "targets"),
                                                               ObjectiveArgument.getWritableObjective(var0x, "targetObjective"),
                                                               OperationArgument.getOperation(var0x, "operation"),
                                                               ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "source"),
                                                               ObjectiveArgument.getObjective(var0x, "sourceObjective")
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
      LiteralArgumentBuilder var0 = Commands.literal("rendertype");

      for(ObjectiveCriteria.RenderType var4 : ObjectiveCriteria.RenderType.values()) {
         var0.then(
            Commands.literal(var4.getId())
               .executes(var1 -> setRenderType((CommandSourceStack)var1.getSource(), ObjectiveArgument.getObjective(var1, "objective"), var4))
         );
      }

      return var0;
   }

   private static CompletableFuture<Suggestions> suggestTriggers(CommandSourceStack var0, Collection<String> var1, SuggestionsBuilder var2) {
      ArrayList var3 = Lists.newArrayList();
      ServerScoreboard var4 = var0.getServer().getScoreboard();

      for(Objective var6 : var4.getObjectives()) {
         if (var6.getCriteria() == ObjectiveCriteria.TRIGGER) {
            boolean var7 = false;

            for(String var9 : var1) {
               if (!var4.hasPlayerScore(var9, var6) || var4.getOrCreatePlayerScore(var9, var6).isLocked()) {
                  var7 = true;
                  break;
               }
            }

            if (var7) {
               var3.add(var6.getName());
            }
         }
      }

      return SharedSuggestionProvider.suggest(var3, var2);
   }

   private static int getScore(CommandSourceStack var0, String var1, Objective var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.getServer().getScoreboard();
      if (!var3.hasPlayerScore(var1, var2)) {
         throw ERROR_NO_VALUE.create(var2.getName(), var1);
      } else {
         Score var4 = var3.getOrCreatePlayerScore(var1, var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.get.success", var1, var4.getScore(), var2.getFormattedDisplayName()), false
         );
         return var4.getScore();
      }
   }

   private static int performOperation(
      CommandSourceStack var0, Collection<String> var1, Objective var2, OperationArgument.Operation var3, Collection<String> var4, Objective var5
   ) throws CommandSyntaxException {
      ServerScoreboard var6 = var0.getServer().getScoreboard();
      int var7 = 0;

      for(String var9 : var1) {
         Score var10 = var6.getOrCreatePlayerScore(var9, var2);

         for(String var12 : var4) {
            Score var13 = var6.getOrCreatePlayerScore(var12, var5);
            var3.apply(var10, var13);
         }

         var7 += var10.getScore();
      }

      if (var1.size() == 1) {
         int var14 = var7;
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.operation.success.single", var2.getFormattedDisplayName(), var1.iterator().next(), var14),
            true
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.operation.success.multiple", var2.getFormattedDisplayName(), var1.size()), true
         );
      }

      return var7;
   }

   private static int enableTrigger(CommandSourceStack var0, Collection<String> var1, Objective var2) throws CommandSyntaxException {
      if (var2.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_NOT_TRIGGER.create();
      } else {
         ServerScoreboard var3 = var0.getServer().getScoreboard();
         int var4 = 0;

         for(String var6 : var1) {
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
               var0.sendSuccess(
                  () -> Component.translatable("commands.scoreboard.players.enable.success.single", var2.getFormattedDisplayName(), var1.iterator().next()),
                  true
               );
            } else {
               var0.sendSuccess(
                  () -> Component.translatable("commands.scoreboard.players.enable.success.multiple", var2.getFormattedDisplayName(), var1.size()), true
               );
            }

            return var4;
         }
      }
   }

   private static int resetScores(CommandSourceStack var0, Collection<String> var1) {
      ServerScoreboard var2 = var0.getServer().getScoreboard();

      for(String var4 : var1) {
         var2.resetPlayerScore(var4, null);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.players.reset.all.single", var1.iterator().next()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.players.reset.all.multiple", var1.size()), true);
      }

      return var1.size();
   }

   private static int resetScore(CommandSourceStack var0, Collection<String> var1, Objective var2) {
      ServerScoreboard var3 = var0.getServer().getScoreboard();

      for(String var5 : var1) {
         var3.resetPlayerScore(var5, var2);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.reset.specific.single", var2.getFormattedDisplayName(), var1.iterator().next()), true
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.reset.specific.multiple", var2.getFormattedDisplayName(), var1.size()), true
         );
      }

      return var1.size();
   }

   private static int setScore(CommandSourceStack var0, Collection<String> var1, Objective var2, int var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();

      for(String var6 : var1) {
         Score var7 = var4.getOrCreatePlayerScore(var6, var2);
         var7.setScore(var3);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.set.success.single", var2.getFormattedDisplayName(), var1.iterator().next(), var3), true
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.set.success.multiple", var2.getFormattedDisplayName(), var1.size(), var3), true
         );
      }

      return var3 * var1.size();
   }

   private static int addScore(CommandSourceStack var0, Collection<String> var1, Objective var2, int var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      int var5 = 0;

      for(String var7 : var1) {
         Score var8 = var4.getOrCreatePlayerScore(var7, var2);
         var8.setScore(var8.getScore() + var3);
         var5 += var8.getScore();
      }

      if (var1.size() == 1) {
         int var9 = var5;
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.add.success.single", var3, var2.getFormattedDisplayName(), var1.iterator().next(), var9),
            true
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.add.success.multiple", var3, var2.getFormattedDisplayName(), var1.size()), true
         );
      }

      return var5;
   }

   private static int removeScore(CommandSourceStack var0, Collection<String> var1, Objective var2, int var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      int var5 = 0;

      for(String var7 : var1) {
         Score var8 = var4.getOrCreatePlayerScore(var7, var2);
         var8.setScore(var8.getScore() - var3);
         var5 += var8.getScore();
      }

      if (var1.size() == 1) {
         int var9 = var5;
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.scoreboard.players.remove.success.single", var3, var2.getFormattedDisplayName(), var1.iterator().next(), var9
               ),
            true
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.players.remove.success.multiple", var3, var2.getFormattedDisplayName(), var1.size()), true
         );
      }

      return var5;
   }

   private static int listTrackedPlayers(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getScoreboard().getTrackedPlayers();
      if (var1.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.players.list.empty"), false);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.players.list.success", var1.size(), ComponentUtils.formatList(var1)), false);
      }

      return var1.size();
   }

   private static int listTrackedPlayerScores(CommandSourceStack var0, String var1) {
      Map var2 = var0.getServer().getScoreboard().getPlayerScores(var1);
      if (var2.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.players.list.entity.empty", var1), false);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.players.list.entity.success", var1, var2.size()), false);

         for(Entry var4 : var2.entrySet()) {
            var0.sendSuccess(
               () -> Component.translatable(
                     "commands.scoreboard.players.list.entity.entry",
                     ((Objective)var4.getKey()).getFormattedDisplayName(),
                     ((Score)var4.getValue()).getScore()
                  ),
               false
            );
         }
      }

      return var2.size();
   }

   private static int clearDisplaySlot(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      if (var2.getDisplayObjective(var1) == null) {
         throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
      } else {
         var2.setDisplayObjective(var1, null);
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[var1]), true);
         return 0;
      }
   }

   private static int setDisplaySlot(CommandSourceStack var0, int var1, Objective var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.getServer().getScoreboard();
      if (var3.getDisplayObjective(var1) == var2) {
         throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
      } else {
         var3.setDisplayObjective(var1, var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[var1], var2.getDisplayName()), true
         );
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack var0, Objective var1, Component var2) {
      if (!var1.getDisplayName().equals(var2)) {
         var1.setDisplayName(var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.scoreboard.objectives.modify.displayname", var1.getName(), var1.getFormattedDisplayName()), true
         );
      }

      return 0;
   }

   private static int setRenderType(CommandSourceStack var0, Objective var1, ObjectiveCriteria.RenderType var2) {
      if (var1.getRenderType() != var2) {
         var1.setRenderType(var2);
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.objectives.modify.rendertype", var1.getFormattedDisplayName()), true);
      }

      return 0;
   }

   private static int removeObjective(CommandSourceStack var0, Objective var1) {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      var2.removeObjective(var1);
      var0.sendSuccess(() -> Component.translatable("commands.scoreboard.objectives.remove.success", var1.getFormattedDisplayName()), true);
      return var2.getObjectives().size();
   }

   private static int addObjective(CommandSourceStack var0, String var1, ObjectiveCriteria var2, Component var3) throws CommandSyntaxException {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      if (var4.getObjective(var1) != null) {
         throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
      } else {
         var4.addObjective(var1, var2, var3, var2.getDefaultRenderType());
         Objective var5 = var4.getObjective(var1);
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.objectives.add.success", var5.getFormattedDisplayName()), true);
         return var4.getObjectives().size();
      }
   }

   private static int listObjectives(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getScoreboard().getObjectives();
      if (var1.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.scoreboard.objectives.list.empty"), false);
      } else {
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.scoreboard.objectives.list.success", var1.size(), ComponentUtils.formatList(var1, Objective::getFormattedDisplayName)
               ),
            false
         );
      }

      return var1.size();
   }
}
