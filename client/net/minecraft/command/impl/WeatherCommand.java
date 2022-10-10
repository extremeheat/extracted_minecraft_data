package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class WeatherCommand {
   public static void func_198862_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("weather").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((LiteralArgumentBuilder)Commands.func_197057_a("clear").executes((var0x) -> {
         return func_198869_a((CommandSource)var0x.getSource(), 6000);
      })).then(Commands.func_197056_a("duration", IntegerArgumentType.integer(0, 1000000)).executes((var0x) -> {
         return func_198869_a((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.func_197057_a("rain").executes((var0x) -> {
         return func_198865_b((CommandSource)var0x.getSource(), 6000);
      })).then(Commands.func_197056_a("duration", IntegerArgumentType.integer(0, 1000000)).executes((var0x) -> {
         return func_198865_b((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.func_197057_a("thunder").executes((var0x) -> {
         return func_198863_c((CommandSource)var0x.getSource(), 6000);
      })).then(Commands.func_197056_a("duration", IntegerArgumentType.integer(0, 1000000)).executes((var0x) -> {
         return func_198863_c((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20);
      }))));
   }

   private static int func_198869_a(CommandSource var0, int var1) {
      var0.func_197023_e().func_72912_H().func_176142_i(var1);
      var0.func_197023_e().func_72912_H().func_76080_g(0);
      var0.func_197023_e().func_72912_H().func_76090_f(0);
      var0.func_197023_e().func_72912_H().func_76084_b(false);
      var0.func_197023_e().func_72912_H().func_76069_a(false);
      var0.func_197030_a(new TextComponentTranslation("commands.weather.set.clear", new Object[0]), true);
      return var1;
   }

   private static int func_198865_b(CommandSource var0, int var1) {
      var0.func_197023_e().func_72912_H().func_176142_i(0);
      var0.func_197023_e().func_72912_H().func_76080_g(var1);
      var0.func_197023_e().func_72912_H().func_76090_f(var1);
      var0.func_197023_e().func_72912_H().func_76084_b(true);
      var0.func_197023_e().func_72912_H().func_76069_a(false);
      var0.func_197030_a(new TextComponentTranslation("commands.weather.set.rain", new Object[0]), true);
      return var1;
   }

   private static int func_198863_c(CommandSource var0, int var1) {
      var0.func_197023_e().func_72912_H().func_176142_i(0);
      var0.func_197023_e().func_72912_H().func_76080_g(var1);
      var0.func_197023_e().func_72912_H().func_76090_f(var1);
      var0.func_197023_e().func_72912_H().func_76084_b(true);
      var0.func_197023_e().func_72912_H().func_76069_a(true);
      var0.func_197030_a(new TextComponentTranslation("commands.weather.set.thunder", new Object[0]), true);
      return var1;
   }
}
