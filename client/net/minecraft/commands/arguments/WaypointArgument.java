package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.waypoints.WaypointTransmitter;

public class WaypointArgument {
   public static final SimpleCommandExceptionType ERROR_NOT_A_WAYPOINT = new SimpleCommandExceptionType(Component.translatable("argument.waypoint.invalid"));

   public WaypointArgument() {
      super();
   }

   public static WaypointTransmitter getWaypoint(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      Entity var2 = ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).findSingleEntity((CommandSourceStack)var0.getSource());
      if (var2 instanceof WaypointTransmitter var3) {
         return var3;
      } else {
         throw ERROR_NOT_A_WAYPOINT.create();
      }
   }
}
