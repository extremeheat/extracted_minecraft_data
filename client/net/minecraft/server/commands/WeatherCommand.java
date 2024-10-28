package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;

public class WeatherCommand {
   private static final int DEFAULT_TIME = -1;

   public WeatherCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((var0x) -> {
         return setClear((CommandSourceStack)var0x.getSource(), -1);
      })).then(Commands.argument("duration", TimeArgument.time(1)).executes((var0x) -> {
         return setClear((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes((var0x) -> {
         return setRain((CommandSourceStack)var0x.getSource(), -1);
      })).then(Commands.argument("duration", TimeArgument.time(1)).executes((var0x) -> {
         return setRain((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes((var0x) -> {
         return setThunder((CommandSourceStack)var0x.getSource(), -1);
      })).then(Commands.argument("duration", TimeArgument.time(1)).executes((var0x) -> {
         return setThunder((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration"));
      }))));
   }

   private static int getDuration(CommandSourceStack var0, int var1, IntProvider var2) {
      return var1 == -1 ? var2.sample(var0.getServer().overworld().getRandom()) : var1;
   }

   private static int setClear(CommandSourceStack var0, int var1) {
      var0.getServer().overworld().setWeatherParameters(getDuration(var0, var1, ServerLevel.RAIN_DELAY), 0, false, false);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.weather.set.clear");
      }, true);
      return var1;
   }

   private static int setRain(CommandSourceStack var0, int var1) {
      var0.getServer().overworld().setWeatherParameters(0, getDuration(var0, var1, ServerLevel.RAIN_DURATION), true, false);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.weather.set.rain");
      }, true);
      return var1;
   }

   private static int setThunder(CommandSourceStack var0, int var1) {
      var0.getServer().overworld().setWeatherParameters(0, getDuration(var0, var1, ServerLevel.THUNDER_DURATION), true, true);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.weather.set.thunder");
      }, true);
      return var1;
   }
}
