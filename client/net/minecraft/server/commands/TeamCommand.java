package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Team;

public class TeamCommand {
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(
      Component.translatable("commands.team.add.duplicate")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType(
      Component.translatable("commands.team.empty.unchanged")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.name.unchanged")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.color.unchanged")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.friendlyfire.alreadyEnabled")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.friendlyfire.alreadyDisabled")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.seeFriendlyInvisibles.alreadyEnabled")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.seeFriendlyInvisibles.alreadyDisabled")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.nametagVisibility.unchanged")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.deathMessageVisibility.unchanged")
   );
   private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType(
      Component.translatable("commands.team.option.collisionRule.unchanged")
   );

   public TeamCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                    "team"
                                 )
                                 .requires(var0x -> var0x.hasPermission(2)))
                              .then(
                                 ((LiteralArgumentBuilder)Commands.literal("list").executes(var0x -> listTeams((CommandSourceStack)var0x.getSource())))
                                    .then(
                                       Commands.argument("team", TeamArgument.team())
                                          .executes(var0x -> listMembers((CommandSourceStack)var0x.getSource(), TeamArgument.getTeam(var0x, "team")))
                                    )
                              ))
                           .then(
                              Commands.literal("add")
                                 .then(
                                    ((RequiredArgumentBuilder)Commands.argument("team", StringArgumentType.word())
                                          .executes(var0x -> createTeam((CommandSourceStack)var0x.getSource(), StringArgumentType.getString(var0x, "team"))))
                                       .then(
                                          Commands.argument("displayName", ComponentArgument.textComponent())
                                             .executes(
                                                var0x -> createTeam(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      StringArgumentType.getString(var0x, "team"),
                                                      ComponentArgument.getComponent(var0x, "displayName")
                                                   )
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("remove")
                              .then(
                                 Commands.argument("team", TeamArgument.team())
                                    .executes(var0x -> deleteTeam((CommandSourceStack)var0x.getSource(), TeamArgument.getTeam(var0x, "team")))
                              )
                        ))
                     .then(
                        Commands.literal("empty")
                           .then(
                              Commands.argument("team", TeamArgument.team())
                                 .executes(var0x -> emptyTeam((CommandSourceStack)var0x.getSource(), TeamArgument.getTeam(var0x, "team")))
                           )
                     ))
                  .then(
                     Commands.literal("join")
                        .then(
                           ((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team())
                                 .executes(
                                    var0x -> joinTeam(
                                          (CommandSourceStack)var0x.getSource(),
                                          TeamArgument.getTeam(var0x, "team"),
                                          Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException())
                                       )
                                 ))
                              .then(
                                 Commands.argument("members", ScoreHolderArgument.scoreHolders())
                                    .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                    .executes(
                                       var0x -> joinTeam(
                                             (CommandSourceStack)var0x.getSource(),
                                             TeamArgument.getTeam(var0x, "team"),
                                             ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "members")
                                          )
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("leave")
                     .then(
                        Commands.argument("members", ScoreHolderArgument.scoreHolders())
                           .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                           .executes(
                              var0x -> leaveTeam((CommandSourceStack)var0x.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(var0x, "members"))
                           )
                     )
               ))
            .then(
               Commands.literal("modify")
                  .then(
                     ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                                   "team", TeamArgument.team()
                                                )
                                                .then(
                                                   Commands.literal("displayName")
                                                      .then(
                                                         Commands.argument("displayName", ComponentArgument.textComponent())
                                                            .executes(
                                                               var0x -> setDisplayName(
                                                                     (CommandSourceStack)var0x.getSource(),
                                                                     TeamArgument.getTeam(var0x, "team"),
                                                                     ComponentArgument.getComponent(var0x, "displayName")
                                                                  )
                                                            )
                                                      )
                                                ))
                                             .then(
                                                Commands.literal("color")
                                                   .then(
                                                      Commands.argument("value", ColorArgument.color())
                                                         .executes(
                                                            var0x -> setColor(
                                                                  (CommandSourceStack)var0x.getSource(),
                                                                  TeamArgument.getTeam(var0x, "team"),
                                                                  ColorArgument.getColor(var0x, "value")
                                                               )
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("friendlyFire")
                                                .then(
                                                   Commands.argument("allowed", BoolArgumentType.bool())
                                                      .executes(
                                                         var0x -> setFriendlyFire(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               TeamArgument.getTeam(var0x, "team"),
                                                               BoolArgumentType.getBool(var0x, "allowed")
                                                            )
                                                      )
                                                )
                                          ))
                                       .then(
                                          Commands.literal("seeFriendlyInvisibles")
                                             .then(
                                                Commands.argument("allowed", BoolArgumentType.bool())
                                                   .executes(
                                                      var0x -> setFriendlySight(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            TeamArgument.getTeam(var0x, "team"),
                                                            BoolArgumentType.getBool(var0x, "allowed")
                                                         )
                                                   )
                                             )
                                       ))
                                    .then(
                                       ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("nametagVisibility")
                                                   .then(
                                                      Commands.literal("never")
                                                         .executes(
                                                            var0x -> setNametagVisibility(
                                                                  (CommandSourceStack)var0x.getSource(),
                                                                  TeamArgument.getTeam(var0x, "team"),
                                                                  Team.Visibility.NEVER
                                                               )
                                                         )
                                                   ))
                                                .then(
                                                   Commands.literal("hideForOtherTeams")
                                                      .executes(
                                                         var0x -> setNametagVisibility(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               TeamArgument.getTeam(var0x, "team"),
                                                               Team.Visibility.HIDE_FOR_OTHER_TEAMS
                                                            )
                                                      )
                                                ))
                                             .then(
                                                Commands.literal("hideForOwnTeam")
                                                   .executes(
                                                      var0x -> setNametagVisibility(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            TeamArgument.getTeam(var0x, "team"),
                                                            Team.Visibility.HIDE_FOR_OWN_TEAM
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("always")
                                                .executes(
                                                   var0x -> setNametagVisibility(
                                                         (CommandSourceStack)var0x.getSource(), TeamArgument.getTeam(var0x, "team"), Team.Visibility.ALWAYS
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathMessageVisibility")
                                                .then(
                                                   Commands.literal("never")
                                                      .executes(
                                                         var0x -> setDeathMessageVisibility(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               TeamArgument.getTeam(var0x, "team"),
                                                               Team.Visibility.NEVER
                                                            )
                                                      )
                                                ))
                                             .then(
                                                Commands.literal("hideForOtherTeams")
                                                   .executes(
                                                      var0x -> setDeathMessageVisibility(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            TeamArgument.getTeam(var0x, "team"),
                                                            Team.Visibility.HIDE_FOR_OTHER_TEAMS
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("hideForOwnTeam")
                                                .executes(
                                                   var0x -> setDeathMessageVisibility(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         TeamArgument.getTeam(var0x, "team"),
                                                         Team.Visibility.HIDE_FOR_OWN_TEAM
                                                      )
                                                )
                                          ))
                                       .then(
                                          Commands.literal("always")
                                             .executes(
                                                var0x -> setDeathMessageVisibility(
                                                      (CommandSourceStack)var0x.getSource(), TeamArgument.getTeam(var0x, "team"), Team.Visibility.ALWAYS
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("collisionRule")
                                             .then(
                                                Commands.literal("never")
                                                   .executes(
                                                      var0x -> setCollision(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            TeamArgument.getTeam(var0x, "team"),
                                                            Team.CollisionRule.NEVER
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("pushOwnTeam")
                                                .executes(
                                                   var0x -> setCollision(
                                                         (CommandSourceStack)var0x.getSource(),
                                                         TeamArgument.getTeam(var0x, "team"),
                                                         Team.CollisionRule.PUSH_OWN_TEAM
                                                      )
                                                )
                                          ))
                                       .then(
                                          Commands.literal("pushOtherTeams")
                                             .executes(
                                                var0x -> setCollision(
                                                      (CommandSourceStack)var0x.getSource(),
                                                      TeamArgument.getTeam(var0x, "team"),
                                                      Team.CollisionRule.PUSH_OTHER_TEAMS
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("always")
                                          .executes(
                                             var0x -> setCollision(
                                                   (CommandSourceStack)var0x.getSource(), TeamArgument.getTeam(var0x, "team"), Team.CollisionRule.ALWAYS
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("prefix")
                                 .then(
                                    Commands.argument("prefix", ComponentArgument.textComponent())
                                       .executes(
                                          var0x -> setPrefix(
                                                (CommandSourceStack)var0x.getSource(),
                                                TeamArgument.getTeam(var0x, "team"),
                                                ComponentArgument.getComponent(var0x, "prefix")
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("suffix")
                              .then(
                                 Commands.argument("suffix", ComponentArgument.textComponent())
                                    .executes(
                                       var0x -> setSuffix(
                                             (CommandSourceStack)var0x.getSource(),
                                             TeamArgument.getTeam(var0x, "team"),
                                             ComponentArgument.getComponent(var0x, "suffix")
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }

   private static Component getFirstMemberName(Collection<ScoreHolder> var0) {
      return ((ScoreHolder)var0.iterator().next()).getFeedbackDisplayName();
   }

   private static int leaveTeam(CommandSourceStack var0, Collection<ScoreHolder> var1) {
      ServerScoreboard var2 = var0.getServer().getScoreboard();

      for(ScoreHolder var4 : var1) {
         var2.removePlayerFromTeam(var4.getScoreboardName());
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.team.leave.success.single", getFirstMemberName(var1)), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.team.leave.success.multiple", var1.size()), true);
      }

      return var1.size();
   }

   private static int joinTeam(CommandSourceStack var0, PlayerTeam var1, Collection<ScoreHolder> var2) {
      ServerScoreboard var3 = var0.getServer().getScoreboard();

      for(ScoreHolder var5 : var2) {
         var3.addPlayerToTeam(var5.getScoreboardName(), var1);
      }

      if (var2.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.team.join.success.single", getFirstMemberName(var2), var1.getFormattedDisplayName()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.team.join.success.multiple", var2.size(), var1.getFormattedDisplayName()), true);
      }

      return var2.size();
   }

   private static int setNametagVisibility(CommandSourceStack var0, PlayerTeam var1, Team.Visibility var2) throws CommandSyntaxException {
      if (var1.getNameTagVisibility() == var2) {
         throw ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
      } else {
         var1.setNameTagVisibility(var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.team.option.nametagVisibility.success", var1.getFormattedDisplayName(), var2.getDisplayName()), true
         );
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSourceStack var0, PlayerTeam var1, Team.Visibility var2) throws CommandSyntaxException {
      if (var1.getDeathMessageVisibility() == var2) {
         throw ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
      } else {
         var1.setDeathMessageVisibility(var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.team.option.deathMessageVisibility.success", var1.getFormattedDisplayName(), var2.getDisplayName()), true
         );
         return 0;
      }
   }

   private static int setCollision(CommandSourceStack var0, PlayerTeam var1, Team.CollisionRule var2) throws CommandSyntaxException {
      if (var1.getCollisionRule() == var2) {
         throw ERROR_TEAM_COLLISION_UNCHANGED.create();
      } else {
         var1.setCollisionRule(var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.team.option.collisionRule.success", var1.getFormattedDisplayName(), var2.getDisplayName()), true
         );
         return 0;
      }
   }

   private static int setFriendlySight(CommandSourceStack var0, PlayerTeam var1, boolean var2) throws CommandSyntaxException {
      if (var1.canSeeFriendlyInvisibles() == var2) {
         if (var2) {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
         }
      } else {
         var1.setSeeFriendlyInvisibles(var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.team.option.seeFriendlyInvisibles." + (var2 ? "enabled" : "disabled"), var1.getFormattedDisplayName()),
            true
         );
         return 0;
      }
   }

   private static int setFriendlyFire(CommandSourceStack var0, PlayerTeam var1, boolean var2) throws CommandSyntaxException {
      if (var1.isAllowFriendlyFire() == var2) {
         if (var2) {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
         }
      } else {
         var1.setAllowFriendlyFire(var2);
         var0.sendSuccess(
            () -> Component.translatable("commands.team.option.friendlyfire." + (var2 ? "enabled" : "disabled"), var1.getFormattedDisplayName()), true
         );
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack var0, PlayerTeam var1, Component var2) throws CommandSyntaxException {
      if (var1.getDisplayName().equals(var2)) {
         throw ERROR_TEAM_ALREADY_NAME.create();
      } else {
         var1.setDisplayName(var2);
         var0.sendSuccess(() -> Component.translatable("commands.team.option.name.success", var1.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setColor(CommandSourceStack var0, PlayerTeam var1, ChatFormatting var2) throws CommandSyntaxException {
      if (var1.getColor() == var2) {
         throw ERROR_TEAM_ALREADY_COLOR.create();
      } else {
         var1.setColor(var2);
         var0.sendSuccess(() -> Component.translatable("commands.team.option.color.success", var1.getFormattedDisplayName(), var2.getName()), true);
         return 0;
      }
   }

   private static int emptyTeam(CommandSourceStack var0, PlayerTeam var1) throws CommandSyntaxException {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      ArrayList var3 = Lists.newArrayList(var1.getPlayers());
      if (var3.isEmpty()) {
         throw ERROR_TEAM_ALREADY_EMPTY.create();
      } else {
         for(String var5 : var3) {
            var2.removePlayerFromTeam(var5, var1);
         }

         var0.sendSuccess(() -> Component.translatable("commands.team.empty.success", var3.size(), var1.getFormattedDisplayName()), true);
         return var3.size();
      }
   }

   private static int deleteTeam(CommandSourceStack var0, PlayerTeam var1) {
      ServerScoreboard var2 = var0.getServer().getScoreboard();
      var2.removePlayerTeam(var1);
      var0.sendSuccess(() -> Component.translatable("commands.team.remove.success", var1.getFormattedDisplayName()), true);
      return var2.getPlayerTeams().size();
   }

   private static int createTeam(CommandSourceStack var0, String var1) throws CommandSyntaxException {
      return createTeam(var0, var1, Component.literal(var1));
   }

   private static int createTeam(CommandSourceStack var0, String var1, Component var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.getServer().getScoreboard();
      if (var3.getPlayerTeam(var1) != null) {
         throw ERROR_TEAM_ALREADY_EXISTS.create();
      } else {
         PlayerTeam var4 = var3.addPlayerTeam(var1);
         var4.setDisplayName(var2);
         var0.sendSuccess(() -> Component.translatable("commands.team.add.success", var4.getFormattedDisplayName()), true);
         return var3.getPlayerTeams().size();
      }
   }

   private static int listMembers(CommandSourceStack var0, PlayerTeam var1) {
      Collection var2 = var1.getPlayers();
      if (var2.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.team.list.members.empty", var1.getFormattedDisplayName()), false);
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.team.list.members.success", var1.getFormattedDisplayName(), var2.size(), ComponentUtils.formatList(var2)),
            false
         );
      }

      return var2.size();
   }

   private static int listTeams(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getScoreboard().getPlayerTeams();
      if (var1.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.team.list.teams.empty"), false);
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.team.list.teams.success", var1.size(), ComponentUtils.formatList(var1, PlayerTeam::getFormattedDisplayName)),
            false
         );
      }

      return var1.size();
   }

   private static int setPrefix(CommandSourceStack var0, PlayerTeam var1, Component var2) {
      var1.setPlayerPrefix(var2);
      var0.sendSuccess(() -> Component.translatable("commands.team.option.prefix.success", var2), false);
      return 1;
   }

   private static int setSuffix(CommandSourceStack var0, PlayerTeam var1, Component var2) {
      var1.setPlayerSuffix(var2);
      var0.sendSuccess(() -> Component.translatable("commands.team.option.suffix.success", var2), false);
      return 1;
   }
}
