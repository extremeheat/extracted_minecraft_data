package net.minecraft.command.impl;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.ObjectiveCriteriaArgument;
import net.minecraft.command.arguments.OperationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.ScoreboardSlotArgument;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType field_198663_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.objectives.add.duplicate", new Object[0]));
   private static final SimpleCommandExceptionType field_198666_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.objectives.display.alreadyEmpty", new Object[0]));
   private static final SimpleCommandExceptionType field_198667_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.objectives.display.alreadySet", new Object[0]));
   private static final SimpleCommandExceptionType field_198668_f = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.players.enable.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198669_g = new SimpleCommandExceptionType(new TextComponentTranslation("commands.scoreboard.players.enable.invalid", new Object[0]));
   private static final Dynamic2CommandExceptionType field_198670_h = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.scoreboard.players.get.null", new Object[]{var0, var1});
   });

   public static void func_198647_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("scoreboard").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("objectives").then(Commands.func_197057_a("list").executes((var0x) -> {
         return func_198662_b((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("add").then(Commands.func_197056_a("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.func_197056_a("criteria", ObjectiveCriteriaArgument.func_197162_a()).executes((var0x) -> {
         return func_198629_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "objective"), ObjectiveCriteriaArgument.func_197161_a(var0x, "criteria"), new TextComponentString(StringArgumentType.getString(var0x, "objective")));
      })).then(Commands.func_197056_a("displayName", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_198629_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "objective"), ObjectiveCriteriaArgument.func_197161_a(var0x, "criteria"), ComponentArgument.func_197068_a(var0x, "displayName"));
      })))))).then(Commands.func_197057_a("modify").then(((RequiredArgumentBuilder)Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).then(Commands.func_197057_a("displayname").then(Commands.func_197056_a("displayName", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_211749_a((CommandSource)var0x.getSource(), ObjectiveArgument.func_197158_a(var0x, "objective"), ComponentArgument.func_197068_a(var0x, "displayName"));
      })))).then(func_211915_a())))).then(Commands.func_197057_a("remove").then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).executes((var0x) -> {
         return func_198637_a((CommandSource)var0x.getSource(), ObjectiveArgument.func_197158_a(var0x, "objective"));
      })))).then(Commands.func_197057_a("setdisplay").then(((RequiredArgumentBuilder)Commands.func_197056_a("slot", ScoreboardSlotArgument.func_197219_a()).executes((var0x) -> {
         return func_198632_a((CommandSource)var0x.getSource(), ScoreboardSlotArgument.func_197217_a(var0x, "slot"));
      })).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).executes((var0x) -> {
         return func_198659_a((CommandSource)var0x.getSource(), ScoreboardSlotArgument.func_197217_a(var0x, "slot"), ObjectiveArgument.func_197158_a(var0x, "objective"));
      })))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("players").then(((LiteralArgumentBuilder)Commands.func_197057_a("list").executes((var0x) -> {
         return func_198661_a((CommandSource)var0x.getSource());
      })).then(Commands.func_197056_a("target", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).executes((var0x) -> {
         return func_198643_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_197211_a(var0x, "target"));
      })))).then(Commands.func_197057_a("set").then(Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).then(Commands.func_197056_a("score", IntegerArgumentType.integer()).executes((var0x) -> {
         return func_198653_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), ObjectiveArgument.func_197156_b(var0x, "objective"), IntegerArgumentType.getInteger(var0x, "score"));
      })))))).then(Commands.func_197057_a("get").then(Commands.func_197056_a("target", ScoreHolderArgument.func_197209_a()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).executes((var0x) -> {
         return func_198634_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_197211_a(var0x, "target"), ObjectiveArgument.func_197158_a(var0x, "objective"));
      }))))).then(Commands.func_197057_a("add").then(Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).then(Commands.func_197056_a("score", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198633_b((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), ObjectiveArgument.func_197156_b(var0x, "objective"), IntegerArgumentType.getInteger(var0x, "score"));
      })))))).then(Commands.func_197057_a("remove").then(Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).then(Commands.func_197056_a("score", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198651_c((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), ObjectiveArgument.func_197156_b(var0x, "objective"), IntegerArgumentType.getInteger(var0x, "score"));
      })))))).then(Commands.func_197057_a("reset").then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).executes((var0x) -> {
         return func_198654_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"));
      })).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).executes((var0x) -> {
         return func_198656_b((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), ObjectiveArgument.func_197158_a(var0x, "objective"));
      }))))).then(Commands.func_197057_a("enable").then(Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).suggests((var0x, var1) -> {
         return func_198641_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), var1);
      }).executes((var0x) -> {
         return func_198644_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), ObjectiveArgument.func_197158_a(var0x, "objective"));
      }))))).then(Commands.func_197057_a("operation").then(Commands.func_197056_a("targets", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("targetObjective", ObjectiveArgument.func_197157_a()).then(Commands.func_197056_a("operation", OperationArgument.func_197184_a()).then(Commands.func_197056_a("source", ScoreHolderArgument.func_197214_b()).suggests(ScoreHolderArgument.field_201326_a).then(Commands.func_197056_a("sourceObjective", ObjectiveArgument.func_197157_a()).executes((var0x) -> {
         return func_198658_a((CommandSource)var0x.getSource(), ScoreHolderArgument.func_211707_c(var0x, "targets"), ObjectiveArgument.func_197156_b(var0x, "targetObjective"), OperationArgument.func_197179_a(var0x, "operation"), ScoreHolderArgument.func_211707_c(var0x, "source"), ObjectiveArgument.func_197158_a(var0x, "sourceObjective"));
      })))))))));
   }

   private static LiteralArgumentBuilder<CommandSource> func_211915_a() {
      LiteralArgumentBuilder var0 = Commands.func_197057_a("rendertype");
      ScoreCriteria.RenderType[] var1 = ScoreCriteria.RenderType.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ScoreCriteria.RenderType var4 = var1[var3];
         var0.then(Commands.func_197057_a(var4.func_211838_a()).executes((var1x) -> {
            return func_211910_a((CommandSource)var1x.getSource(), ObjectiveArgument.func_197158_a(var1x, "objective"), var4);
         }));
      }

      return var0;
   }

   private static CompletableFuture<Suggestions> func_198641_a(CommandSource var0, Collection<String> var1, SuggestionsBuilder var2) {
      ArrayList var3 = Lists.newArrayList();
      ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
      Iterator var5 = var4.func_96514_c().iterator();

      while(true) {
         ScoreObjective var6;
         do {
            if (!var5.hasNext()) {
               return ISuggestionProvider.func_197005_b(var3, var2);
            }

            var6 = (ScoreObjective)var5.next();
         } while(var6.func_96680_c() != ScoreCriteria.field_178791_c);

         boolean var7 = false;
         Iterator var8 = var1.iterator();

         label32: {
            String var9;
            do {
               if (!var8.hasNext()) {
                  break label32;
               }

               var9 = (String)var8.next();
            } while(var4.func_178819_b(var9, var6) && !var4.func_96529_a(var9, var6).func_178816_g());

            var7 = true;
         }

         if (var7) {
            var3.add(var6.func_96679_b());
         }
      }
   }

   private static int func_198634_a(CommandSource var0, String var1, ScoreObjective var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.func_197028_i().func_200251_aP();
      if (!var3.func_178819_b(var1, var2)) {
         throw field_198670_h.create(var2.func_96679_b(), var1);
      } else {
         Score var4 = var3.func_96529_a(var1, var2);
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.get.success", new Object[]{var1, var4.func_96652_c(), var2.func_197890_e()}), false);
         return var4.func_96652_c();
      }
   }

   private static int func_198658_a(CommandSource var0, Collection<String> var1, ScoreObjective var2, OperationArgument.IOperation var3, Collection<String> var4, ScoreObjective var5) throws CommandSyntaxException {
      ServerScoreboard var6 = var0.func_197028_i().func_200251_aP();
      int var7 = 0;

      Score var10;
      for(Iterator var8 = var1.iterator(); var8.hasNext(); var7 += var10.func_96652_c()) {
         String var9 = (String)var8.next();
         var10 = var6.func_96529_a(var9, var2);
         Iterator var11 = var4.iterator();

         while(var11.hasNext()) {
            String var12 = (String)var11.next();
            Score var13 = var6.func_96529_a(var12, var5);
            var3.apply(var10, var13);
         }
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.operation.success.single", new Object[]{var2.func_197890_e(), var1.iterator().next(), var7}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.operation.success.multiple", new Object[]{var2.func_197890_e(), var1.size()}), true);
      }

      return var7;
   }

   private static int func_198644_a(CommandSource var0, Collection<String> var1, ScoreObjective var2) throws CommandSyntaxException {
      if (var2.func_96680_c() != ScoreCriteria.field_178791_c) {
         throw field_198669_g.create();
      } else {
         ServerScoreboard var3 = var0.func_197028_i().func_200251_aP();
         int var4 = 0;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            Score var7 = var3.func_96529_a(var6, var2);
            if (var7.func_178816_g()) {
               var7.func_178815_a(false);
               ++var4;
            }
         }

         if (var4 == 0) {
            throw field_198668_f.create();
         } else {
            if (var1.size() == 1) {
               var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.enable.success.single", new Object[]{var2.func_197890_e(), var1.iterator().next()}), true);
            } else {
               var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.enable.success.multiple", new Object[]{var2.func_197890_e(), var1.size()}), true);
            }

            return var4;
         }
      }
   }

   private static int func_198654_a(CommandSource var0, Collection<String> var1) {
      ServerScoreboard var2 = var0.func_197028_i().func_200251_aP();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.func_178822_d(var4, (ScoreObjective)null);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.reset.all.single", new Object[]{var1.iterator().next()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.reset.all.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198656_b(CommandSource var0, Collection<String> var1, ScoreObjective var2) {
      ServerScoreboard var3 = var0.func_197028_i().func_200251_aP();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.func_178822_d(var5, var2);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.reset.specific.single", new Object[]{var2.func_197890_e(), var1.iterator().next()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.reset.specific.multiple", new Object[]{var2.func_197890_e(), var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198653_a(CommandSource var0, Collection<String> var1, ScoreObjective var2, int var3) {
      ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         Score var7 = var4.func_96529_a(var6, var2);
         var7.func_96647_c(var3);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.set.success.single", new Object[]{var2.func_197890_e(), var1.iterator().next(), var3}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.set.success.multiple", new Object[]{var2.func_197890_e(), var1.size(), var3}), true);
      }

      return var3 * var1.size();
   }

   private static int func_198633_b(CommandSource var0, Collection<String> var1, ScoreObjective var2, int var3) {
      ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
      int var5 = 0;

      Score var8;
      for(Iterator var6 = var1.iterator(); var6.hasNext(); var5 += var8.func_96652_c()) {
         String var7 = (String)var6.next();
         var8 = var4.func_96529_a(var7, var2);
         var8.func_96647_c(var8.func_96652_c() + var3);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.add.success.single", new Object[]{var3, var2.func_197890_e(), var1.iterator().next(), var5}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.add.success.multiple", new Object[]{var3, var2.func_197890_e(), var1.size()}), true);
      }

      return var5;
   }

   private static int func_198651_c(CommandSource var0, Collection<String> var1, ScoreObjective var2, int var3) {
      ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
      int var5 = 0;

      Score var8;
      for(Iterator var6 = var1.iterator(); var6.hasNext(); var5 += var8.func_96652_c()) {
         String var7 = (String)var6.next();
         var8 = var4.func_96529_a(var7, var2);
         var8.func_96647_c(var8.func_96652_c() - var3);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.remove.success.single", new Object[]{var3, var2.func_197890_e(), var1.iterator().next(), var5}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.remove.success.multiple", new Object[]{var3, var2.func_197890_e(), var1.size()}), true);
      }

      return var5;
   }

   private static int func_198661_a(CommandSource var0) {
      Collection var1 = var0.func_197028_i().func_200251_aP().func_96526_d();
      if (var1.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.list.empty", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.list.success", new Object[]{var1.size(), TextComponentUtils.func_197678_a(var1)}), false);
      }

      return var1.size();
   }

   private static int func_198643_a(CommandSource var0, String var1) {
      Map var2 = var0.func_197028_i().func_200251_aP().func_96510_d(var1);
      if (var2.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.list.entity.empty", new Object[]{var1}), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.list.entity.success", new Object[]{var1, var2.size()}), false);
         Iterator var3 = var2.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.players.list.entity.entry", new Object[]{((ScoreObjective)var4.getKey()).func_197890_e(), ((Score)var4.getValue()).func_96652_c()}), false);
         }
      }

      return var2.size();
   }

   private static int func_198632_a(CommandSource var0, int var1) throws CommandSyntaxException {
      ServerScoreboard var2 = var0.func_197028_i().func_200251_aP();
      if (var2.func_96539_a(var1) == null) {
         throw field_198666_d.create();
      } else {
         var2.func_96530_a(var1, (ScoreObjective)null);
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.display.cleared", new Object[]{Scoreboard.func_178821_h()[var1]}), true);
         return 0;
      }
   }

   private static int func_198659_a(CommandSource var0, int var1, ScoreObjective var2) throws CommandSyntaxException {
      ServerScoreboard var3 = var0.func_197028_i().func_200251_aP();
      if (var3.func_96539_a(var1) == var2) {
         throw field_198667_e.create();
      } else {
         var3.func_96530_a(var1, var2);
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.display.set", new Object[]{Scoreboard.func_178821_h()[var1], var2.func_96678_d()}), true);
         return 0;
      }
   }

   private static int func_211749_a(CommandSource var0, ScoreObjective var1, ITextComponent var2) {
      if (!var1.func_96678_d().equals(var2)) {
         var1.func_199864_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.modify.displayname", new Object[]{var1.func_96679_b(), var1.func_197890_e()}), true);
      }

      return 0;
   }

   private static int func_211910_a(CommandSource var0, ScoreObjective var1, ScoreCriteria.RenderType var2) {
      if (var1.func_199865_f() != var2) {
         var1.func_199866_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.modify.rendertype", new Object[]{var1.func_197890_e()}), true);
      }

      return 0;
   }

   private static int func_198637_a(CommandSource var0, ScoreObjective var1) {
      ServerScoreboard var2 = var0.func_197028_i().func_200251_aP();
      var2.func_96519_k(var1);
      var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.remove.success", new Object[]{var1.func_197890_e()}), true);
      return var2.func_96514_c().size();
   }

   private static int func_198629_a(CommandSource var0, String var1, ScoreCriteria var2, ITextComponent var3) throws CommandSyntaxException {
      ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
      if (var4.func_96518_b(var1) != null) {
         throw field_198663_a.create();
      } else if (var1.length() > 16) {
         throw ObjectiveArgument.field_200379_a.create(16);
      } else {
         var4.func_199868_a(var1, var2, var3, var2.func_178790_c());
         ScoreObjective var5 = var4.func_96518_b(var1);
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.add.success", new Object[]{var5.func_197890_e()}), true);
         return var4.func_96514_c().size();
      }
   }

   private static int func_198662_b(CommandSource var0) {
      Collection var1 = var0.func_197028_i().func_200251_aP().func_96514_c();
      if (var1.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.list.empty", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.scoreboard.objectives.list.success", new Object[]{var1.size(), TextComponentUtils.func_197677_b(var1, ScoreObjective::func_197890_e)}), false);
      }

      return var1.size();
   }
}
