package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Arrays;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.util.TimeUtil;

public class TickCommand {
   private static final float MAX_TICKRATE = 10000.0F;
   private static final String DEFAULT_TICKRATE = String.valueOf(20);

   public TickCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                 "tick"
                              )
                              .requires(var0x -> var0x.hasPermission(3)))
                           .then(Commands.literal("query").executes(var0x -> tickQuery((CommandSourceStack)var0x.getSource()))))
                        .then(
                           Commands.literal("rate")
                              .then(
                                 Commands.argument("rate", FloatArgumentType.floatArg(1.0F, 10000.0F))
                                    .suggests((var0x, var1) -> SharedSuggestionProvider.suggest(new String[]{DEFAULT_TICKRATE}, var1))
                                    .executes(var0x -> setTickingRate((CommandSourceStack)var0x.getSource(), FloatArgumentType.getFloat(var0x, "rate")))
                              )
                        ))
                     .then(
                        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("step")
                                 .executes(var0x -> step((CommandSourceStack)var0x.getSource(), 1)))
                              .then(Commands.literal("stop").executes(var0x -> stopStepping((CommandSourceStack)var0x.getSource()))))
                           .then(
                              Commands.argument("time", TimeArgument.time(1))
                                 .suggests((var0x, var1) -> SharedSuggestionProvider.suggest(new String[]{"1t", "1s"}, var1))
                                 .executes(var0x -> step((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time")))
                           )
                     ))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("sprint")
                           .then(Commands.literal("stop").executes(var0x -> stopSprinting((CommandSourceStack)var0x.getSource()))))
                        .then(
                           Commands.argument("time", TimeArgument.time(1))
                              .suggests((var0x, var1) -> SharedSuggestionProvider.suggest(new String[]{"60s", "1d", "3d"}, var1))
                              .executes(var0x -> sprint((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time")))
                        )
                  ))
               .then(Commands.literal("unfreeze").executes(var0x -> setFreeze((CommandSourceStack)var0x.getSource(), false))))
            .then(Commands.literal("freeze").executes(var0x -> setFreeze((CommandSourceStack)var0x.getSource(), true)))
      );
   }

   private static String nanosToMilisString(long var0) {
      return String.format("%.1f", (float)var0 / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND);
   }

   private static int setTickingRate(CommandSourceStack var0, float var1) {
      ServerTickRateManager var2 = var0.getServer().tickRateManager();
      var2.setTickRate(var1);
      String var3 = String.format("%.1f", var1);
      var0.sendSuccess(() -> Component.translatable("commands.tick.rate.success", var3), true);
      return (int)var1;
   }

   private static int tickQuery(CommandSourceStack var0) {
      ServerTickRateManager var1 = var0.getServer().tickRateManager();
      String var2 = nanosToMilisString(var0.getServer().getAverageTickTimeNanos());
      float var3 = var1.tickrate();
      String var4 = String.format("%.1f", var3);
      if (var1.isSprinting()) {
         var0.sendSuccess(() -> Component.translatable("commands.tick.status.sprinting"), false);
         var0.sendSuccess(() -> Component.translatable("commands.tick.query.rate.sprinting", var4, var2), false);
      } else {
         if (var1.isFrozen()) {
            var0.sendSuccess(() -> Component.translatable("commands.tick.status.frozen"), false);
         } else if (var1.nanosecondsPerTick() < var0.getServer().getAverageTickTimeNanos()) {
            var0.sendSuccess(() -> Component.translatable("commands.tick.status.lagging"), false);
         } else {
            var0.sendSuccess(() -> Component.translatable("commands.tick.status.running"), false);
         }

         String var5 = nanosToMilisString(var1.nanosecondsPerTick());
         var0.sendSuccess(() -> Component.translatable("commands.tick.query.rate.running", var4, var2, var5), false);
      }

      long[] var9 = Arrays.copyOf(var0.getServer().getTickTimesNanos(), var0.getServer().getTickTimesNanos().length);
      Arrays.sort(var9);
      String var6 = nanosToMilisString(var9[var9.length / 2]);
      String var7 = nanosToMilisString(var9[(int)((double)var9.length * 0.95)]);
      String var8 = nanosToMilisString(var9[(int)((double)var9.length * 0.99)]);
      var0.sendSuccess(() -> Component.translatable("commands.tick.query.percentiles", var6, var7, var8, var9.length), false);
      return (int)var3;
   }

   private static int sprint(CommandSourceStack var0, int var1) {
      boolean var2 = var0.getServer().tickRateManager().requestGameToSprint(var1);
      if (var2) {
         var0.sendSuccess(() -> Component.translatable("commands.tick.sprint.stop.success"), true);
      }

      var0.sendSuccess(() -> Component.translatable("commands.tick.status.sprinting"), true);
      return 1;
   }

   private static int setFreeze(CommandSourceStack var0, boolean var1) {
      ServerTickRateManager var2 = var0.getServer().tickRateManager();
      if (var1) {
         if (var2.isSprinting()) {
            var2.stopSprinting();
         }

         if (var2.isSteppingForward()) {
            var2.stopStepping();
         }
      }

      var2.setFrozen(var1);
      if (var1) {
         var0.sendSuccess(() -> Component.translatable("commands.tick.status.frozen"), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.tick.status.running"), true);
      }

      return var1 ? 1 : 0;
   }

   private static int step(CommandSourceStack var0, int var1) {
      ServerTickRateManager var2 = var0.getServer().tickRateManager();
      boolean var3 = var2.stepGameIfPaused(var1);
      if (var3) {
         var0.sendSuccess(() -> Component.translatable("commands.tick.step.success", var1), true);
      } else {
         var0.sendFailure(Component.translatable("commands.tick.step.fail"));
      }

      return 1;
   }

   private static int stopStepping(CommandSourceStack var0) {
      ServerTickRateManager var1 = var0.getServer().tickRateManager();
      boolean var2 = var1.stopStepping();
      if (var2) {
         var0.sendSuccess(() -> Component.translatable("commands.tick.step.stop.success"), true);
         return 1;
      } else {
         var0.sendFailure(Component.translatable("commands.tick.step.stop.fail"));
         return 0;
      }
   }

   private static int stopSprinting(CommandSourceStack var0) {
      ServerTickRateManager var1 = var0.getServer().tickRateManager();
      boolean var2 = var1.stopSprinting();
      if (var2) {
         var0.sendSuccess(() -> Component.translatable("commands.tick.sprint.stop.success"), true);
         return 1;
      } else {
         var0.sendFailure(Component.translatable("commands.tick.sprint.stop.fail"));
         return 0;
      }
   }
}
