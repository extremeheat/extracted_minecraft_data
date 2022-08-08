package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_CENTER = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.center.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_SIZE = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.set.failed.nochange"));
   private static final SimpleCommandExceptionType ERROR_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.set.failed.small"));
   private static final SimpleCommandExceptionType ERROR_TOO_BIG = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.set.failed.big", 5.9999968E7));
   private static final SimpleCommandExceptionType ERROR_TOO_FAR_OUT = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.set.failed.far", 2.9999984E7));
   private static final SimpleCommandExceptionType ERROR_SAME_WARNING_TIME = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.warning.time.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_WARNING_DISTANCE = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.warning.distance.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_BUFFER = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.damage.buffer.failed"));
   private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_AMOUNT = new SimpleCommandExceptionType(Component.translatable("commands.worldborder.damage.amount.failed"));

   public WorldBorderCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("worldborder").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("distance", DoubleArgumentType.doubleArg(-5.9999968E7, 5.9999968E7)).executes((var0x) -> {
         return setSize((CommandSourceStack)var0x.getSource(), ((CommandSourceStack)var0x.getSource()).getLevel().getWorldBorder().getSize() + DoubleArgumentType.getDouble(var0x, "distance"), 0L);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setSize((CommandSourceStack)var0x.getSource(), ((CommandSourceStack)var0x.getSource()).getLevel().getWorldBorder().getSize() + DoubleArgumentType.getDouble(var0x, "distance"), ((CommandSourceStack)var0x.getSource()).getLevel().getWorldBorder().getLerpRemainingTime() + (long)IntegerArgumentType.getInteger(var0x, "time") * 1000L);
      }))))).then(Commands.literal("set").then(((RequiredArgumentBuilder)Commands.argument("distance", DoubleArgumentType.doubleArg(-5.9999968E7, 5.9999968E7)).executes((var0x) -> {
         return setSize((CommandSourceStack)var0x.getSource(), DoubleArgumentType.getDouble(var0x, "distance"), 0L);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setSize((CommandSourceStack)var0x.getSource(), DoubleArgumentType.getDouble(var0x, "distance"), (long)IntegerArgumentType.getInteger(var0x, "time") * 1000L);
      }))))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes((var0x) -> {
         return setCenter((CommandSourceStack)var0x.getSource(), Vec2Argument.getVec2(var0x, "pos"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((var0x) -> {
         return setDamageAmount((CommandSourceStack)var0x.getSource(), FloatArgumentType.getFloat(var0x, "damagePerBlock"));
      })))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((var0x) -> {
         return setDamageBuffer((CommandSourceStack)var0x.getSource(), FloatArgumentType.getFloat(var0x, "distance"));
      }))))).then(Commands.literal("get").executes((var0x) -> {
         return getSize((CommandSourceStack)var0x.getSource());
      }))).then(((LiteralArgumentBuilder)Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setWarningDistance((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "distance"));
      })))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setWarningTime((CommandSourceStack)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time"));
      })))));
   }

   private static int setDamageBuffer(CommandSourceStack var0, float var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.getServer().overworld().getWorldBorder();
      if (var2.getDamageSafeZone() == (double)var1) {
         throw ERROR_SAME_DAMAGE_BUFFER.create();
      } else {
         var2.setDamageSafeZone((double)var1);
         var0.sendSuccess(Component.translatable("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", var1)), true);
         return (int)var1;
      }
   }

   private static int setDamageAmount(CommandSourceStack var0, float var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.getServer().overworld().getWorldBorder();
      if (var2.getDamagePerBlock() == (double)var1) {
         throw ERROR_SAME_DAMAGE_AMOUNT.create();
      } else {
         var2.setDamagePerBlock((double)var1);
         var0.sendSuccess(Component.translatable("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", var1)), true);
         return (int)var1;
      }
   }

   private static int setWarningTime(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.getServer().overworld().getWorldBorder();
      if (var2.getWarningTime() == var1) {
         throw ERROR_SAME_WARNING_TIME.create();
      } else {
         var2.setWarningTime(var1);
         var0.sendSuccess(Component.translatable("commands.worldborder.warning.time.success", var1), true);
         return var1;
      }
   }

   private static int setWarningDistance(CommandSourceStack var0, int var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.getServer().overworld().getWorldBorder();
      if (var2.getWarningBlocks() == var1) {
         throw ERROR_SAME_WARNING_DISTANCE.create();
      } else {
         var2.setWarningBlocks(var1);
         var0.sendSuccess(Component.translatable("commands.worldborder.warning.distance.success", var1), true);
         return var1;
      }
   }

   private static int getSize(CommandSourceStack var0) {
      double var1 = var0.getServer().overworld().getWorldBorder().getSize();
      var0.sendSuccess(Component.translatable("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", var1)), false);
      return Mth.floor(var1 + 0.5);
   }

   private static int setCenter(CommandSourceStack var0, Vec2 var1) throws CommandSyntaxException {
      WorldBorder var2 = var0.getServer().overworld().getWorldBorder();
      if (var2.getCenterX() == (double)var1.x && var2.getCenterZ() == (double)var1.y) {
         throw ERROR_SAME_CENTER.create();
      } else if (!((double)Math.abs(var1.x) > 2.9999984E7) && !((double)Math.abs(var1.y) > 2.9999984E7)) {
         var2.setCenter((double)var1.x, (double)var1.y);
         var0.sendSuccess(Component.translatable("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", var1.x), String.format(Locale.ROOT, "%.2f", var1.y)), true);
         return 0;
      } else {
         throw ERROR_TOO_FAR_OUT.create();
      }
   }

   private static int setSize(CommandSourceStack var0, double var1, long var3) throws CommandSyntaxException {
      WorldBorder var5 = var0.getServer().overworld().getWorldBorder();
      double var6 = var5.getSize();
      if (var6 == var1) {
         throw ERROR_SAME_SIZE.create();
      } else if (var1 < 1.0) {
         throw ERROR_TOO_SMALL.create();
      } else if (var1 > 5.9999968E7) {
         throw ERROR_TOO_BIG.create();
      } else {
         if (var3 > 0L) {
            var5.lerpSizeBetween(var6, var1, var3);
            if (var1 > var6) {
               var0.sendSuccess(Component.translatable("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", var1), Long.toString(var3 / 1000L)), true);
            } else {
               var0.sendSuccess(Component.translatable("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", var1), Long.toString(var3 / 1000L)), true);
            }
         } else {
            var5.setSize(var1);
            var0.sendSuccess(Component.translatable("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", var1)), true);
         }

         return (int)(var1 - var6);
      }
   }
}
