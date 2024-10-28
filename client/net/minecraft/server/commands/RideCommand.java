package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class RideCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_RIDING = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.ride.not_riding", var0);
   });
   private static final Dynamic2CommandExceptionType ERROR_ALREADY_RIDING = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.ride.already_riding", var0, var1);
   });
   private static final Dynamic2CommandExceptionType ERROR_MOUNT_FAILED = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.ride.mount.failure.generic", var0, var1);
   });
   private static final SimpleCommandExceptionType ERROR_MOUNTING_PLAYER = new SimpleCommandExceptionType(Component.translatable("commands.ride.mount.failure.cant_ride_players"));
   private static final SimpleCommandExceptionType ERROR_MOUNTING_LOOP = new SimpleCommandExceptionType(Component.translatable("commands.ride.mount.failure.loop"));
   private static final SimpleCommandExceptionType ERROR_WRONG_DIMENSION = new SimpleCommandExceptionType(Component.translatable("commands.ride.mount.failure.wrong_dimension"));

   public RideCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ride").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("target", EntityArgument.entity()).then(Commands.literal("mount").then(Commands.argument("vehicle", EntityArgument.entity()).executes((var0x) -> {
         return mount((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), EntityArgument.getEntity(var0x, "vehicle"));
      })))).then(Commands.literal("dismount").executes((var0x) -> {
         return dismount((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"));
      }))));
   }

   private static int mount(CommandSourceStack var0, Entity var1, Entity var2) throws CommandSyntaxException {
      Entity var3 = var1.getVehicle();
      if (var3 != null) {
         throw ERROR_ALREADY_RIDING.create(var1.getDisplayName(), var3.getDisplayName());
      } else if (var2.getType() == EntityType.PLAYER) {
         throw ERROR_MOUNTING_PLAYER.create();
      } else if (var1.getSelfAndPassengers().anyMatch((var1x) -> {
         return var1x == var2;
      })) {
         throw ERROR_MOUNTING_LOOP.create();
      } else if (var1.level() != var2.level()) {
         throw ERROR_WRONG_DIMENSION.create();
      } else if (!var1.startRiding(var2, true)) {
         throw ERROR_MOUNT_FAILED.create(var1.getDisplayName(), var2.getDisplayName());
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.ride.mount.success", var1.getDisplayName(), var2.getDisplayName());
         }, true);
         return 1;
      }
   }

   private static int dismount(CommandSourceStack var0, Entity var1) throws CommandSyntaxException {
      Entity var2 = var1.getVehicle();
      if (var2 == null) {
         throw ERROR_NOT_RIDING.create(var1.getDisplayName());
      } else {
         var1.stopRiding();
         var0.sendSuccess(() -> {
            return Component.translatable("commands.ride.dismount.success", var1.getDisplayName(), var2.getDisplayName());
         }, true);
         return 1;
      }
   }
}
