package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class WeatherCommand {
   private static final int DEFAULT_TIME = 6000;

   public WeatherCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather")
                     .requires(var0x -> var0x.hasPermission(2)))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("clear").executes(var0x -> setClear((CommandSourceStack)var0x.getSource(), 6000)))
                        .then(
                           Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                              .executes(var0x -> setClear((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20))
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("rain").executes(var0x -> setRain((CommandSourceStack)var0x.getSource(), 6000)))
                     .then(
                        Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                           .executes(var0x -> setRain((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20))
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("thunder").executes(var0x -> setThunder((CommandSourceStack)var0x.getSource(), 6000)))
                  .then(
                     Commands.argument("duration", IntegerArgumentType.integer(0, 1000000))
                        .executes(var0x -> setThunder((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "duration") * 20))
                  )
            )
      );
   }

   private static int setClear(CommandSourceStack var0, int var1) {
      var0.getLevel().setWeatherParameters(var1, 0, false, false);
      var0.sendSuccess(Component.translatable("commands.weather.set.clear"), true);
      return var1;
   }

   private static int setRain(CommandSourceStack var0, int var1) {
      var0.getLevel().setWeatherParameters(0, var1, true, false);
      var0.sendSuccess(Component.translatable("commands.weather.set.rain"), true);
      return var1;
   }

   private static int setThunder(CommandSourceStack var0, int var1) {
      var0.getLevel().setWeatherParameters(0, var1, true, true);
      var0.sendSuccess(Component.translatable("commands.weather.set.thunder"), true);
      return var1;
   }
}
