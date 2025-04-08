package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.HexColorArgument;
import net.minecraft.commands.arguments.WaypointArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointTransmitter;

public class WaypointCommand {
   public WaypointCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("waypoint").requires((var0x) -> var0x.hasPermission(2))).then(Commands.literal("list").executes((var0x) -> listWaypoints((CommandSourceStack)var0x.getSource())))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("waypoint", EntityArgument.entity()).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("color").then(Commands.argument("color", ColorArgument.color()).executes((var0x) -> setWaypointColor((CommandSourceStack)var0x.getSource(), WaypointArgument.getWaypoint(var0x, "waypoint"), ColorArgument.getColor(var0x, "color"))))).then(Commands.literal("hex").then(Commands.argument("color", HexColorArgument.hexColor()).executes((var0x) -> setWaypointColor((CommandSourceStack)var0x.getSource(), WaypointArgument.getWaypoint(var0x, "waypoint"), HexColorArgument.getHexColor(var0x, "color")))))).then(Commands.literal("reset").executes((var0x) -> resetWaypointColor((CommandSourceStack)var0x.getSource(), WaypointArgument.getWaypoint(var0x, "waypoint")))))).then(((LiteralArgumentBuilder)Commands.literal("fade").then(Commands.literal("reset").executes((var0x) -> setWaypointAlphaFade((CommandSourceStack)var0x.getSource(), WaypointArgument.getWaypoint(var0x, "waypoint"), Waypoint.Icon.Fade.DEFAULT)))).then(Commands.argument("fade_start", IntegerArgumentType.integer(0, 60000000)).then(Commands.argument("alpha_start", FloatArgumentType.floatArg(0.0F, 1.0F)).then(Commands.argument("fade_end", IntegerArgumentType.integer(0, 60000000)).then(Commands.argument("alpha_end", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((var0x) -> setWaypointAlphaFade((CommandSourceStack)var0x.getSource(), WaypointArgument.getWaypoint(var0x, "waypoint"), new Waypoint.Icon.Fade(IntegerArgumentType.getInteger(var0x, "fade_start"), IntegerArgumentType.getInteger(var0x, "fade_end"), FloatArgumentType.getFloat(var0x, "alpha_start"), FloatArgumentType.getFloat(var0x, "alpha_end"))))))))))));
   }

   private static int setWaypointAlphaFade(CommandSourceStack var0, WaypointTransmitter var1, Waypoint.Icon.Fade var2) {
      mutateIcon(var0, var1, (var1x) -> var1x.alphaFade = var2);
      var0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.fade"), false);
      return 0;
   }

   private static int setWaypointColor(CommandSourceStack var0, WaypointTransmitter var1, ChatFormatting var2) {
      mutateIcon(var0, var1, (var1x) -> var1x.color = Optional.of(var2.getColor()));
      var0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.color", Component.literal(var2.getName()).withStyle(var2)), false);
      return 0;
   }

   private static int setWaypointColor(CommandSourceStack var0, WaypointTransmitter var1, Integer var2) {
      mutateIcon(var0, var1, (var1x) -> var1x.color = Optional.of(var2));
      var0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.color", Component.literal(String.format("%06X", ARGB.color(0, var2))).withColor(var2)), false);
      return 0;
   }

   private static int resetWaypointColor(CommandSourceStack var0, WaypointTransmitter var1) {
      mutateIcon(var0, var1, (var0x) -> var0x.color = Optional.empty());
      var0.sendSuccess(() -> Component.translatable("commands.waypoint.modify.color.reset"), false);
      return 0;
   }

   private static int listWaypoints(CommandSourceStack var0) {
      ServerLevel var1 = var0.getLevel();
      Set var2 = var1.getWaypointManager().transmitters();
      String var3 = var1.dimension().location().toString();
      if (var2.isEmpty()) {
         var0.sendSuccess(() -> Component.translatable("commands.waypoint.list.empty", var3), false);
         return 0;
      } else {
         Component var4 = ComponentUtils.formatList(var2.stream().map((var1x) -> {
            if (var1x instanceof LivingEntity var2) {
               BlockPos var3x = var2.blockPosition();
               return var2.getFeedbackDisplayName().copy().withStyle((UnaryOperator)((var3xx) -> var3xx.withClickEvent(new ClickEvent.SuggestCommand("/execute in " + var3 + " run tp @s " + var3x.getX() + " " + var3x.getY() + " " + var3x.getZ())).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip"))).withColor((Integer)var1x.waypointIcon().color.orElse(-1))));
            } else {
               return Component.literal(var1x.toString());
            }
         }).toList(), Function.identity());
         var0.sendSuccess(() -> Component.translatable("commands.waypoint.list.success", var2.size(), var3, var4), false);
         return var2.size();
      }
   }

   private static void mutateIcon(CommandSourceStack var0, WaypointTransmitter var1, Consumer<Waypoint.Icon> var2) {
      ServerLevel var3 = var0.getLevel();
      var3.getWaypointManager().untrackWaypoint(var1);
      var2.accept(var1.waypointIcon());
      var3.getWaypointManager().trackWaypoint(var1);
   }
}
