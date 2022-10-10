package net.minecraft.command.impl;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TextComponentTranslation;

public class TriggerCommand {
   private static final SimpleCommandExceptionType field_198857_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.trigger.failed.unprimed", new Object[0]));
   private static final SimpleCommandExceptionType field_198858_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.trigger.failed.invalid", new Object[0]));

   public static void func_198852_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)Commands.func_197057_a("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("objective", ObjectiveArgument.func_197157_a()).suggests((var0x, var1) -> {
         return func_198850_a((CommandSource)var0x.getSource(), var1);
      }).executes((var0x) -> {
         return func_201477_a((CommandSource)var0x.getSource(), func_198848_a(((CommandSource)var0x.getSource()).func_197035_h(), ObjectiveArgument.func_197158_a(var0x, "objective")));
      })).then(Commands.func_197057_a("add").then(Commands.func_197056_a("value", IntegerArgumentType.integer()).executes((var0x) -> {
         return func_201479_a((CommandSource)var0x.getSource(), func_198848_a(((CommandSource)var0x.getSource()).func_197035_h(), ObjectiveArgument.func_197158_a(var0x, "objective")), IntegerArgumentType.getInteger(var0x, "value"));
      })))).then(Commands.func_197057_a("set").then(Commands.func_197056_a("value", IntegerArgumentType.integer()).executes((var0x) -> {
         return func_201478_b((CommandSource)var0x.getSource(), func_198848_a(((CommandSource)var0x.getSource()).func_197035_h(), ObjectiveArgument.func_197158_a(var0x, "objective")), IntegerArgumentType.getInteger(var0x, "value"));
      })))));
   }

   public static CompletableFuture<Suggestions> func_198850_a(CommandSource var0, SuggestionsBuilder var1) {
      Entity var2 = var0.func_197022_f();
      ArrayList var3 = Lists.newArrayList();
      if (var2 != null) {
         ServerScoreboard var4 = var0.func_197028_i().func_200251_aP();
         String var5 = var2.func_195047_I_();
         Iterator var6 = var4.func_96514_c().iterator();

         while(var6.hasNext()) {
            ScoreObjective var7 = (ScoreObjective)var6.next();
            if (var7.func_96680_c() == ScoreCriteria.field_178791_c && var4.func_178819_b(var5, var7)) {
               Score var8 = var4.func_96529_a(var5, var7);
               if (!var8.func_178816_g()) {
                  var3.add(var7.func_96679_b());
               }
            }
         }
      }

      return ISuggestionProvider.func_197005_b(var3, var1);
   }

   private static int func_201479_a(CommandSource var0, Score var1, int var2) {
      var1.func_96649_a(var2);
      var0.func_197030_a(new TextComponentTranslation("commands.trigger.add.success", new Object[]{var1.func_96645_d().func_197890_e(), var2}), true);
      return var1.func_96652_c();
   }

   private static int func_201478_b(CommandSource var0, Score var1, int var2) {
      var1.func_96647_c(var2);
      var0.func_197030_a(new TextComponentTranslation("commands.trigger.set.success", new Object[]{var1.func_96645_d().func_197890_e(), var2}), true);
      return var2;
   }

   private static int func_201477_a(CommandSource var0, Score var1) {
      var1.func_96649_a(1);
      var0.func_197030_a(new TextComponentTranslation("commands.trigger.simple.success", new Object[]{var1.func_96645_d().func_197890_e()}), true);
      return var1.func_96652_c();
   }

   private static Score func_198848_a(EntityPlayerMP var0, ScoreObjective var1) throws CommandSyntaxException {
      if (var1.func_96680_c() != ScoreCriteria.field_178791_c) {
         throw field_198858_b.create();
      } else {
         Scoreboard var2 = var0.func_96123_co();
         String var3 = var0.func_195047_I_();
         if (!var2.func_178819_b(var3, var1)) {
            throw field_198857_a.create();
         } else {
            Score var4 = var2.func_96529_a(var3, var1);
            if (var4.func_178816_g()) {
               throw field_198857_a.create();
            } else {
               var4.func_178815_a(true);
               return var4;
            }
         }
      }
   }
}
