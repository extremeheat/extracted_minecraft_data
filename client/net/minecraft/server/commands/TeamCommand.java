package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class TeamCommand {
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(Component.translatable("commands.team.add.duplicate"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType(Component.translatable("commands.team.empty.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType(Component.translatable("commands.team.option.name.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType(Component.translatable("commands.team.option.color.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.friendlyfire.alreadyEnabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.friendlyfire.alreadyDisabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
   private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.nametagVisibility.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.deathMessageVisibility.unchanged"));
   private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType(Component.translatable("commands.team.option.collisionRule.unchanged"));

   public TeamCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("team").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(((LiteralArgumentBuilder)Commands.literal("list").executes((c) -> listTeams((CommandSourceStack)c.getSource()))).then(Commands.argument("team", TeamArgument.team()).executes((c) -> listMembers((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team")))))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("team", StringArgumentType.word()).executes((c) -> createTeam((CommandSourceStack)c.getSource(), StringArgumentType.getString(c, "team")))).then(Commands.argument("displayName", ComponentArgument.textComponent(context)).executes((c) -> createTeam((CommandSourceStack)c.getSource(), StringArgumentType.getString(c, "team"), ComponentArgument.getResolvedComponent(c, "displayName"))))))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((c) -> deleteTeam((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team")))))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((c) -> emptyTeam((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team")))))).then(Commands.literal("join").then(((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).executes((c) -> joinTeam((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Collections.singleton(((CommandSourceStack)c.getSource()).getEntityOrException())))).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((c) -> joinTeam((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), ScoreHolderArgument.getNamesWithDefaultWildcard(c, "members"))))))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((c) -> leaveTeam((CommandSourceStack)c.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(c, "members")))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.textComponent(context)).executes((c) -> setDisplayName((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), ComponentArgument.getResolvedComponent(c, "displayName")))))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((c) -> setColor((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), ColorArgument.getColor(c, "value")))))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((c) -> setFriendlyFire((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), BoolArgumentType.getBool(c, "allowed")))))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((c) -> setFriendlySight((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), BoolArgumentType.getBool(c, "allowed")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("nametagVisibility").then(Commands.literal("never").executes((c) -> setNametagVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.NEVER)))).then(Commands.literal("hideForOtherTeams").executes((c) -> setNametagVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS)))).then(Commands.literal("hideForOwnTeam").executes((c) -> setNametagVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM)))).then(Commands.literal("always").executes((c) -> setNametagVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((c) -> setDeathMessageVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.NEVER)))).then(Commands.literal("hideForOtherTeams").executes((c) -> setDeathMessageVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS)))).then(Commands.literal("hideForOwnTeam").executes((c) -> setDeathMessageVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM)))).then(Commands.literal("always").executes((c) -> setDeathMessageVisibility((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.Visibility.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("collisionRule").then(Commands.literal("never").executes((c) -> setCollision((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.CollisionRule.NEVER)))).then(Commands.literal("pushOwnTeam").executes((c) -> setCollision((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.CollisionRule.PUSH_OWN_TEAM)))).then(Commands.literal("pushOtherTeams").executes((c) -> setCollision((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS)))).then(Commands.literal("always").executes((c) -> setCollision((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), Team.CollisionRule.ALWAYS))))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.textComponent(context)).executes((c) -> setPrefix((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), ComponentArgument.getResolvedComponent(c, "prefix")))))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.textComponent(context)).executes((c) -> setSuffix((CommandSourceStack)c.getSource(), TeamArgument.getTeam(c, "team"), ComponentArgument.getResolvedComponent(c, "suffix"))))))));
   }

   private static Component getFirstMemberName(final Collection<ScoreHolder> members) {
      return ((ScoreHolder)members.iterator().next()).getFeedbackDisplayName();
   }

   private static int leaveTeam(final CommandSourceStack source, final Collection<ScoreHolder> members) {
      Scoreboard scoreboard = source.getServer().getScoreboard();

      for(ScoreHolder member : members) {
         scoreboard.removePlayerFromTeam(member.getScoreboardName());
      }

      if (members.size() == 1) {
         source.sendSuccess(() -> Component.translatable("commands.team.leave.success.single", getFirstMemberName(members)), true);
      } else {
         source.sendSuccess(() -> Component.translatable("commands.team.leave.success.multiple", members.size()), true);
      }

      return members.size();
   }

   private static int joinTeam(final CommandSourceStack source, final PlayerTeam team, final Collection<ScoreHolder> members) {
      Scoreboard scoreboard = source.getServer().getScoreboard();

      for(ScoreHolder member : members) {
         scoreboard.addPlayerToTeam(member.getScoreboardName(), team);
      }

      if (members.size() == 1) {
         source.sendSuccess(() -> Component.translatable("commands.team.join.success.single", getFirstMemberName(members), team.getFormattedDisplayName()), true);
      } else {
         source.sendSuccess(() -> Component.translatable("commands.team.join.success.multiple", members.size(), team.getFormattedDisplayName()), true);
      }

      return members.size();
   }

   private static int setNametagVisibility(final CommandSourceStack source, final PlayerTeam team, final Team.Visibility visibility) throws CommandSyntaxException {
      if (team.getNameTagVisibility() == visibility) {
         throw ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
      } else {
         team.setNameTagVisibility(visibility);
         source.sendSuccess(() -> Component.translatable("commands.team.option.nametagVisibility.success", team.getFormattedDisplayName(), visibility.getDisplayName()), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(final CommandSourceStack source, final PlayerTeam team, final Team.Visibility visibility) throws CommandSyntaxException {
      if (team.getDeathMessageVisibility() == visibility) {
         throw ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
      } else {
         team.setDeathMessageVisibility(visibility);
         source.sendSuccess(() -> Component.translatable("commands.team.option.deathMessageVisibility.success", team.getFormattedDisplayName(), visibility.getDisplayName()), true);
         return 0;
      }
   }

   private static int setCollision(final CommandSourceStack source, final PlayerTeam team, final Team.CollisionRule collision) throws CommandSyntaxException {
      if (team.getCollisionRule() == collision) {
         throw ERROR_TEAM_COLLISION_UNCHANGED.create();
      } else {
         team.setCollisionRule(collision);
         source.sendSuccess(() -> Component.translatable("commands.team.option.collisionRule.success", team.getFormattedDisplayName(), collision.getDisplayName()), true);
         return 0;
      }
   }

   private static int setFriendlySight(final CommandSourceStack source, final PlayerTeam team, final boolean allowed) throws CommandSyntaxException {
      if (team.canSeeFriendlyInvisibles() == allowed) {
         if (allowed) {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
         }
      } else {
         team.setSeeFriendlyInvisibles(allowed);
         source.sendSuccess(() -> Component.translatable("commands.team.option.seeFriendlyInvisibles." + (allowed ? "enabled" : "disabled"), team.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setFriendlyFire(final CommandSourceStack source, final PlayerTeam team, final boolean allowed) throws CommandSyntaxException {
      if (team.isAllowFriendlyFire() == allowed) {
         if (allowed) {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
         }
      } else {
         team.setAllowFriendlyFire(allowed);
         source.sendSuccess(() -> Component.translatable("commands.team.option.friendlyfire." + (allowed ? "enabled" : "disabled"), team.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setDisplayName(final CommandSourceStack source, final PlayerTeam team, final Component displayName) throws CommandSyntaxException {
      if (team.getDisplayName().equals(displayName)) {
         throw ERROR_TEAM_ALREADY_NAME.create();
      } else {
         team.setDisplayName(displayName);
         source.sendSuccess(() -> Component.translatable("commands.team.option.name.success", team.getFormattedDisplayName()), true);
         return 0;
      }
   }

   private static int setColor(final CommandSourceStack source, final PlayerTeam team, final ChatFormatting color) throws CommandSyntaxException {
      if (team.getColor() == color) {
         throw ERROR_TEAM_ALREADY_COLOR.create();
      } else {
         team.setColor(color);
         source.sendSuccess(() -> Component.translatable("commands.team.option.color.success", team.getFormattedDisplayName(), color.getName()), true);
         return 0;
      }
   }

   private static int emptyTeam(final CommandSourceStack source, final PlayerTeam team) throws CommandSyntaxException {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      Collection<String> members = Lists.newArrayList(team.getPlayers());
      if (members.isEmpty()) {
         throw ERROR_TEAM_ALREADY_EMPTY.create();
      } else {
         for(String member : members) {
            scoreboard.removePlayerFromTeam(member, team);
         }

         source.sendSuccess(() -> Component.translatable("commands.team.empty.success", members.size(), team.getFormattedDisplayName()), true);
         return members.size();
      }
   }

   private static int deleteTeam(final CommandSourceStack source, final PlayerTeam team) {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      scoreboard.removePlayerTeam(team);
      source.sendSuccess(() -> Component.translatable("commands.team.remove.success", team.getFormattedDisplayName()), true);
      return scoreboard.getPlayerTeams().size();
   }

   private static int createTeam(final CommandSourceStack source, final String name) throws CommandSyntaxException {
      return createTeam(source, name, Component.literal(name));
   }

   private static int createTeam(final CommandSourceStack source, final String name, final Component displayName) throws CommandSyntaxException {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      if (scoreboard.getPlayerTeam(name) != null) {
         throw ERROR_TEAM_ALREADY_EXISTS.create();
      } else {
         PlayerTeam team = scoreboard.addPlayerTeam(name);
         team.setDisplayName(displayName);
         source.sendSuccess(() -> Component.translatable("commands.team.add.success", team.getFormattedDisplayName()), true);
         return scoreboard.getPlayerTeams().size();
      }
   }

   private static int listMembers(final CommandSourceStack source, final PlayerTeam team) {
      Collection<String> members = team.getPlayers();
      if (members.isEmpty()) {
         source.sendSuccess(() -> Component.translatable("commands.team.list.members.empty", team.getFormattedDisplayName()), false);
      } else {
         source.sendSuccess(() -> Component.translatable("commands.team.list.members.success", team.getFormattedDisplayName(), members.size(), ComponentUtils.formatList(members)), false);
      }

      return members.size();
   }

   private static int listTeams(final CommandSourceStack source) {
      Collection<PlayerTeam> teams = source.getServer().getScoreboard().getPlayerTeams();
      if (teams.isEmpty()) {
         source.sendSuccess(() -> Component.translatable("commands.team.list.teams.empty"), false);
      } else {
         source.sendSuccess(() -> Component.translatable("commands.team.list.teams.success", teams.size(), ComponentUtils.formatList(teams, PlayerTeam::getFormattedDisplayName)), false);
      }

      return teams.size();
   }

   private static int setPrefix(final CommandSourceStack source, final PlayerTeam team, final Component prefix) {
      team.setPlayerPrefix(prefix);
      source.sendSuccess(() -> Component.translatable("commands.team.option.prefix.success", prefix), false);
      return 1;
   }

   private static int setSuffix(final CommandSourceStack source, final PlayerTeam team, final Component suffix) {
      team.setPlayerSuffix(suffix);
      source.sendSuccess(() -> Component.translatable("commands.team.option.suffix.success", suffix), false);
      return 1;
   }
}
