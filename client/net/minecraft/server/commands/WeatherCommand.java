package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class WeatherCommand {
   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((var0x) -> {
         return setClear((CommandSourceStack)var0x.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((var0x) -> {
         return setClear((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes((var0x) -> {
         return setRain((CommandSourceStack)var0x.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((var0x) -> {
         return setRain((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes((var0x) -> {
         return setThunder((CommandSourceStack)var0x.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((var0x) -> {
         return setThunder((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20);
      }))));
   }

   private static int setClear(CommandSourceStack var0, int var1) {
      var0.getLevel().getLevelData().setClearWeatherTime(var1);
      var0.getLevel().getLevelData().setRainTime(0);
      var0.getLevel().getLevelData().setThunderTime(0);
      var0.getLevel().getLevelData().setRaining(false);
      var0.getLevel().getLevelData().setThundering(false);
      var0.sendSuccess(new TranslatableComponent("commands.weather.set.clear", new Object[0]), true);
      return var1;
   }

   private static int setRain(CommandSourceStack var0, int var1) {
      var0.getLevel().getLevelData().setClearWeatherTime(0);
      var0.getLevel().getLevelData().setRainTime(var1);
      var0.getLevel().getLevelData().setThunderTime(var1);
      var0.getLevel().getLevelData().setRaining(true);
      var0.getLevel().getLevelData().setThundering(false);
      var0.sendSuccess(new TranslatableComponent("commands.weather.set.rain", new Object[0]), true);
      return var1;
   }

   private static int setThunder(CommandSourceStack var0, int var1) {
      var0.getLevel().getLevelData().setClearWeatherTime(0);
      var0.getLevel().getLevelData().setRainTime(var1);
      var0.getLevel().getLevelData().setThunderTime(var1);
      var0.getLevel().getLevelData().setRaining(true);
      var0.getLevel().getLevelData().setThundering(true);
      var0.sendSuccess(new TranslatableComponent("commands.weather.set.thunder", new Object[0]), true);
      return var1;
   }
}
