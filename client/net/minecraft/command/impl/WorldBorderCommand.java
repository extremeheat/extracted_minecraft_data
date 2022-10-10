package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType field_198911_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.center.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198912_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.set.failed.nochange", new Object[0]));
   private static final SimpleCommandExceptionType field_198913_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.set.failed.small.", new Object[0]));
   private static final SimpleCommandExceptionType field_198914_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.set.failed.big.", new Object[0]));
   private static final SimpleCommandExceptionType field_198915_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.warning.time.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198916_f = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.warning.distance.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198917_g = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.damage.buffer.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198918_h = new SimpleCommandExceptionType(new TextComponentTranslation("commands.worldborder.damage.amount.failed", new Object[0]));

   public static void func_198894_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("worldborder").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("add").then(((RequiredArgumentBuilder)Commands.func_197056_a("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((var0x) -> {
         return func_198895_a((CommandSource)var0x.getSource(), ((CommandSource)var0x.getSource()).func_197023_e().func_175723_af().func_177741_h() + (double)FloatArgumentType.getFloat(var0x, "distance"), 0L);
      })).then(Commands.func_197056_a("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198895_a((CommandSource)var0x.getSource(), ((CommandSource)var0x.getSource()).func_197023_e().func_175723_af().func_177741_h() + (double)FloatArgumentType.getFloat(var0x, "distance"), ((CommandSource)var0x.getSource()).func_197023_e().func_175723_af().func_177732_i() + (long)IntegerArgumentType.getInteger(var0x, "time") * 1000L);
      }))))).then(Commands.func_197057_a("set").then(((RequiredArgumentBuilder)Commands.func_197056_a("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((var0x) -> {
         return func_198895_a((CommandSource)var0x.getSource(), (double)FloatArgumentType.getFloat(var0x, "distance"), 0L);
      })).then(Commands.func_197056_a("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198895_a((CommandSource)var0x.getSource(), (double)FloatArgumentType.getFloat(var0x, "distance"), (long)IntegerArgumentType.getInteger(var0x, "time") * 1000L);
      }))))).then(Commands.func_197057_a("center").then(Commands.func_197056_a("pos", Vec2Argument.func_197296_a()).executes((var0x) -> {
         return func_198896_a((CommandSource)var0x.getSource(), Vec2Argument.func_197295_a(var0x, "pos"));
      })))).then(((LiteralArgumentBuilder)Commands.func_197057_a("damage").then(Commands.func_197057_a("amount").then(Commands.func_197056_a("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((var0x) -> {
         return func_198904_b((CommandSource)var0x.getSource(), FloatArgumentType.getFloat(var0x, "damagePerBlock"));
      })))).then(Commands.func_197057_a("buffer").then(Commands.func_197056_a("distance", FloatArgumentType.floatArg(0.0F)).executes((var0x) -> {
         return func_198898_a((CommandSource)var0x.getSource(), FloatArgumentType.getFloat(var0x, "distance"));
      }))))).then(Commands.func_197057_a("get").executes((var0x) -> {
         return func_198910_a((CommandSource)var0x.getSource());
      }))).then(((LiteralArgumentBuilder)Commands.func_197057_a("warning").then(Commands.func_197057_a("distance").then(Commands.func_197056_a("distance", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198899_b((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "distance"));
      })))).then(Commands.func_197057_a("time").then(Commands.func_197056_a("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198902_a((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time"));
      })))));
   }

   private static int func_198898_a(CommandSource var0, float var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.func_197023_e().func_175723_af();
      if (var2.func_177742_m() == (double)var1) {
         throw field_198917_g.create();
      } else {
         var2.func_177724_b((double)var1);
         var0.func_197030_a(new TextComponentTranslation("commands.worldborder.damage.buffer.success", new Object[]{String.format(Locale.ROOT, "%.2f", var1)}), true);
         return (int)var1;
      }
   }

   private static int func_198904_b(CommandSource var0, float var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.func_197023_e().func_175723_af();
      if (var2.func_177727_n() == (double)var1) {
         throw field_198918_h.create();
      } else {
         var2.func_177744_c((double)var1);
         var0.func_197030_a(new TextComponentTranslation("commands.worldborder.damage.amount.success", new Object[]{String.format(Locale.ROOT, "%.2f", var1)}), true);
         return (int)var1;
      }
   }

   private static int func_198902_a(CommandSource var0, int var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.func_197023_e().func_175723_af();
      if (var2.func_177740_p() == var1) {
         throw field_198915_e.create();
      } else {
         var2.func_177723_b(var1);
         var0.func_197030_a(new TextComponentTranslation("commands.worldborder.warning.time.success", new Object[]{var1}), true);
         return var1;
      }
   }

   private static int func_198899_b(CommandSource var0, int var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.func_197023_e().func_175723_af();
      if (var2.func_177748_q() == var1) {
         throw field_198916_f.create();
      } else {
         var2.func_177747_c(var1);
         var0.func_197030_a(new TextComponentTranslation("commands.worldborder.warning.distance.success", new Object[]{var1}), true);
         return var1;
      }
   }

   private static int func_198910_a(CommandSource var0) {
      double var1 = var0.func_197023_e().func_175723_af().func_177741_h();
      var0.func_197030_a(new TextComponentTranslation("commands.worldborder.get", new Object[]{String.format(Locale.ROOT, "%.0f", var1)}), false);
      return MathHelper.func_76128_c(var1 + 0.5D);
   }

   private static int func_198896_a(CommandSource var0, Vec2f var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.func_197023_e().func_175723_af();
      if (var2.func_177731_f() == (double)var1.field_189982_i && var2.func_177721_g() == (double)var1.field_189983_j) {
         throw field_198911_a.create();
      } else {
         var2.func_177739_c((double)var1.field_189982_i, (double)var1.field_189983_j);
         var0.func_197030_a(new TextComponentTranslation("commands.worldborder.center.success", new Object[]{String.format(Locale.ROOT, "%.2f", var1.field_189982_i), String.format("%.2f", var1.field_189983_j)}), true);
         return 0;
      }
   }

   private static int func_198895_a(CommandSource var0, double var1, long var3) throws CommandSyntaxException {
      WorldBorder var5 = var0.func_197023_e().func_175723_af();
      double var6 = var5.func_177741_h();
      if (var6 == var1) {
         throw field_198912_b.create();
      } else if (var1 < 1.0D) {
         throw field_198913_c.create();
      } else if (var1 > 6.0E7D) {
         throw field_198914_d.create();
      } else {
         if (var3 > 0L) {
            var5.func_177738_a(var6, var1, var3);
            if (var1 > var6) {
               var0.func_197030_a(new TextComponentTranslation("commands.worldborder.set.grow", new Object[]{String.format(Locale.ROOT, "%.1f", var1), Long.toString(var3 / 1000L)}), true);
            } else {
               var0.func_197030_a(new TextComponentTranslation("commands.worldborder.set.shrink", new Object[]{String.format(Locale.ROOT, "%.1f", var1), Long.toString(var3 / 1000L)}), true);
            }
         } else {
            var5.func_177750_a(var1);
            var0.func_197030_a(new TextComponentTranslation("commands.worldborder.set.immediate", new Object[]{String.format(Locale.ROOT, "%.1f", var1)}), true);
         }

         return (int)(var1 - var6);
      }
   }
}
