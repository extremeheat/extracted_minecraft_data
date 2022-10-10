package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;

public class TeamCommand {
   private static final SimpleCommandExceptionType field_198793_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.add.duplicate", new Object[0]));
   private static final DynamicCommandExceptionType field_198794_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.team.add.longName", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_198796_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.empty.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_211921_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_198797_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_198798_f = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.friendlyfire.alreadyEnabled", new Object[0]));
   private static final SimpleCommandExceptionType field_198799_g = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.friendlyfire.alreadyDisabled", new Object[0]));
   private static final SimpleCommandExceptionType field_198800_h = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.seeFriendlyInvisibles.alreadyEnabled", new Object[0]));
   private static final SimpleCommandExceptionType field_198801_i = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.seeFriendlyInvisibles.alreadyDisabled", new Object[0]));
   private static final SimpleCommandExceptionType field_198802_j = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.nametagVisibility.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_198803_k = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.deathMessageVisibility.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_198804_l = new SimpleCommandExceptionType(new TextComponentTranslation("commands.team.option.collisionRule.unchanged", new Object[0]));

   public static void func_198771_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("team").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((LiteralArgumentBuilder)Commands.func_197057_a("list").executes((var0x) -> {
         return func_198792_a((CommandSource)var0x.getSource());
      })).then(Commands.func_197056_a("team", TeamArgument.func_197227_a()).executes((var0x) -> {
         return func_198782_c((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"));
      })))).then(Commands.func_197057_a("add").then(((RequiredArgumentBuilder)Commands.func_197056_a("team", StringArgumentType.word()).executes((var0x) -> {
         return func_211916_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "team"));
      })).then(Commands.func_197056_a("displayName", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_211917_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "team"), ComponentArgument.func_197068_a(var0x, "displayName"));
      }))))).then(Commands.func_197057_a("remove").then(Commands.func_197056_a("team", TeamArgument.func_197227_a()).executes((var0x) -> {
         return func_198784_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"));
      })))).then(Commands.func_197057_a("empty").then(Commands.func_197056_a("team", TeamArgument.func_197227_a()).executes((var0x) -> {
         return func_198788_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"));
      })))).then(Commands.func_197057_a("join").then(((RequiredArgumentBuilder)Commands.func_197056_a("team", TeamArgument.func_197227_a()).executes((var0x) -> {
         return func_198768_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Collections.singleton(((CommandSource)var0x.getSource()).func_197027_g().func_195047_I_()));
      })).then(Commands.func_197056_a("members", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).executes((var0x) -> {
         return func_198768_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), ScoreHolderArgument.func_211707_c(var0x, "members"));
      }))))).then(Commands.func_197057_a("leave").then(Commands.func_197056_a("members", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).executes((var0x) -> {
         return func_198786_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "members"));
      })))).then(Commands.func_197057_a("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("team", TeamArgument.func_197227_a()).then(Commands.func_197057_a("displayName").then(Commands.func_197056_a("displayName", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_211920_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), ComponentArgument.func_197068_a(var0x, "displayName"));
      })))).then(Commands.func_197057_a("color").then(Commands.func_197056_a("value", ColorArgument.func_197063_a()).executes((var0x) -> {
         return func_198757_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), ColorArgument.func_197064_a(var0x, "value"));
      })))).then(Commands.func_197057_a("friendlyFire").then(Commands.func_197056_a("allowed", BoolArgumentType.bool()).executes((var0x) -> {
         return func_198781_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), BoolArgumentType.getBool(var0x, "allowed"));
      })))).then(Commands.func_197057_a("seeFriendlyInvisibles").then(Commands.func_197056_a("allowed", BoolArgumentType.bool()).executes((var0x) -> {
         return func_198783_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), BoolArgumentType.getBool(var0x, "allowed"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("nametagVisibility").then(Commands.func_197057_a("never").executes((var0x) -> {
         return func_198777_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.NEVER);
      }))).then(Commands.func_197057_a("hideForOtherTeams").executes((var0x) -> {
         return func_198777_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.HIDE_FOR_OTHER_TEAMS);
      }))).then(Commands.func_197057_a("hideForOwnTeam").executes((var0x) -> {
         return func_198777_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.HIDE_FOR_OWN_TEAM);
      }))).then(Commands.func_197057_a("always").executes((var0x) -> {
         return func_198777_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.ALWAYS);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("deathMessageVisibility").then(Commands.func_197057_a("never").executes((var0x) -> {
         return func_198776_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.NEVER);
      }))).then(Commands.func_197057_a("hideForOtherTeams").executes((var0x) -> {
         return func_198776_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.HIDE_FOR_OTHER_TEAMS);
      }))).then(Commands.func_197057_a("hideForOwnTeam").executes((var0x) -> {
         return func_198776_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.HIDE_FOR_OWN_TEAM);
      }))).then(Commands.func_197057_a("always").executes((var0x) -> {
         return func_198776_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.EnumVisible.ALWAYS);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("collisionRule").then(Commands.func_197057_a("never").executes((var0x) -> {
         return func_198787_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.CollisionRule.NEVER);
      }))).then(Commands.func_197057_a("pushOwnTeam").executes((var0x) -> {
         return func_198787_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      }))).then(Commands.func_197057_a("pushOtherTeams").executes((var0x) -> {
         return func_198787_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      }))).then(Commands.func_197057_a("always").executes((var0x) -> {
         return func_198787_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), Team.CollisionRule.ALWAYS);
      })))).then(Commands.func_197057_a("prefix").then(Commands.func_197056_a("prefix", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_207515_a((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), ComponentArgument.func_197068_a(var0x, "prefix"));
      })))).then(Commands.func_197057_a("suffix").then(Commands.func_197056_a("suffix", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_207517_b((CommandSource)var0x.getSource(), TeamArgument.func_197228_a(var0x, "team"), ComponentArgument.func_197068_a(var0x, "suffix"));
      }))))));
   }

   private static int func_198786_a(CommandSource var0, Collection<String> var1) {
      ServerScoreboard var2 = var0.func_197028_i().func_200251_aP();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.func_96524_g(var4);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.team.leave.success.single", new Object[]{var1.iterator().next()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.team.leave.success.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198768_a(CommandSource var0, ScorePlayerTeam var1, Collection<String> var2) {
      ServerScoreboard var3 = var0.func_197028_i().func_200251_aP();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.func_197901_a(var5, var1);
      }

      if (var2.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.team.join.success.single", new Object[]{var2.iterator().next(), var1.func_197892_d()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.team.join.success.multiple", new Object[]{var2.size(), var1.func_197892_d()}), true);
      }

      return var2.size();
   }

   private static int func_198777_a(CommandSource var0, ScorePlayerTeam var1, Team.EnumVisible var2) throws CommandSyntaxException {
      if (var1.func_178770_i() == var2) {
         throw field_198802_j.create();
      } else {
         var1.func_178772_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.nametagVisibility.success", new Object[]{var1.func_197892_d(), var2.func_197910_b()}), true);
         return 0;
      }
   }

   private static int func_198776_b(CommandSource var0, ScorePlayerTeam var1, Team.EnumVisible var2) throws CommandSyntaxException {
      if (var1.func_178771_j() == var2) {
         throw field_198803_k.create();
      } else {
         var1.func_178773_b(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.deathMessageVisibility.success", new Object[]{var1.func_197892_d(), var2.func_197910_b()}), true);
         return 0;
      }
   }

   private static int func_198787_a(CommandSource var0, ScorePlayerTeam var1, Team.CollisionRule var2) throws CommandSyntaxException {
      if (var1.func_186681_k() == var2) {
         throw field_198804_l.create();
      } else {
         var1.func_186682_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.collisionRule.success", new Object[]{var1.func_197892_d(), var2.func_197907_b()}), true);
         return 0;
      }
   }

   private static int func_198783_a(CommandSource var0, ScorePlayerTeam var1, boolean var2) throws CommandSyntaxException {
      if (var1.func_98297_h() == var2) {
         if (var2) {
            throw field_198800_h.create();
         } else {
            throw field_198801_i.create();
         }
      } else {
         var1.func_98300_b(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.seeFriendlyInvisibles." + (var2 ? "enabled" : "disabled"), new Object[]{var1.func_197892_d()}), true);
         return 0;
      }
   }

   private static int func_198781_b(CommandSource var0, ScorePlayerTeam var1, boolean var2) throws CommandSyntaxException {
      if (var1.func_96665_g() == var2) {
         if (var2) {
            throw field_198798_f.create();
         } else {
            throw field_198799_g.create();
         }
      } else {
         var1.func_96660_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.friendlyfire." + (var2 ? "enabled" : "disabled"), new Object[]{var1.func_197892_d()}), true);
         return 0;
      }
   }

   private static int func_211920_a(CommandSource var0, ScorePlayerTeam var1, ITextComponent var2) throws CommandSyntaxException {
      if (var1.func_96669_c().equals(var2)) {
         throw field_211921_d.create();
      } else {
         var1.func_96664_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.name.success", new Object[]{var1.func_197892_d()}), true);
         return 0;
      }
   }

   private static int func_198757_a(CommandSource var0, ScorePlayerTeam var1, TextFormatting var2) throws CommandSyntaxException {
      if (var1.func_178775_l() == var2) {
         throw field_198797_e.create();
      } else {
         var1.func_178774_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.option.color.success", new Object[]{var1.func_197892_d(), var2.func_96297_d()}), true);
         return 0;
      }
   }

   private static int func_198788_a(CommandSource var0, ScorePlayerTeam var1) throws CommandSyntaxException {
      ServerScoreboard var2 = var0.func_197028_i().func_200251_aP();
      ArrayList var3 = Lists.newArrayList(var1.func_96670_d());
      if (var3.isEmpty()) {
         throw field_198796_d.create();
      } else {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            var2.func_96512_b(var5, var1);
         }

         var0.func_197030_a(new TextComponentTranslation("commands.team.empty.success", new Object[]{var3.size(), var1.func_197892_d()}), true);
         return var3.size();
      }
   }

   private static int func_198784_b(CommandSource var0, ScorePlayerTeam var1) {
      ServerScoreboard var2 = var0.func_197028_i().func_200251_aP();
      var2.func_96511_d(var1);
      var0.func_197030_a(new TextComponentTranslation("commands.team.remove.success", new Object[]{var1.func_197892_d()}), true);
      return var2.func_96525_g().size();
   }

   private static int func_211916_a(CommandSource var0, String var1) throws CommandSyntaxException {
      return func_211917_a(var0, var1, new TextComponentString(var1));
   }

   private static int func_211917_a(CommandSource var0, String var1, ITextComponent var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.func_197028_i().func_200251_aP();
      if (var3.func_96508_e(var1) != null) {
         throw field_198793_a.create();
      } else if (var1.length() > 16) {
         throw field_198794_b.create(16);
      } else {
         ScorePlayerTeam var4 = var3.func_96527_f(var1);
         var4.func_96664_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.team.add.success", new Object[]{var4.func_197892_d()}), true);
         return var3.func_96525_g().size();
      }
   }

   private static int func_198782_c(CommandSource var0, ScorePlayerTeam var1) {
      Collection var2 = var1.func_96670_d();
      if (var2.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.team.list.members.empty", new Object[]{var1.func_197892_d()}), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.team.list.members.success", new Object[]{var1.func_197892_d(), var2.size(), TextComponentUtils.func_197678_a(var2)}), false);
      }

      return var2.size();
   }

   private static int func_198792_a(CommandSource var0) {
      Collection var1 = var0.func_197028_i().func_200251_aP().func_96525_g();
      if (var1.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.team.list.teams.empty", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.team.list.teams.success", new Object[]{var1.size(), TextComponentUtils.func_197677_b(var1, ScorePlayerTeam::func_197892_d)}), false);
      }

      return var1.size();
   }

   private static int func_207515_a(CommandSource var0, ScorePlayerTeam var1, ITextComponent var2) {
      var1.func_207408_a(var2);
      var0.func_197030_a(new TextComponentTranslation("commands.team.option.prefix.success", new Object[]{var2}), false);
      return 1;
   }

   private static int func_207517_b(CommandSource var0, ScorePlayerTeam var1, ITextComponent var2) {
      var1.func_207409_b(var2);
      var0.func_197030_a(new TextComponentTranslation("commands.team.option.suffix.success", new Object[]{var2}), false);
      return 1;
   }
}
