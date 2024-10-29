package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;

public class RotateCommand {
   public RotateCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("rotate").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("target", EntityArgument.entity()).then(Commands.argument("rotation", RotationArgument.rotation()).executes((var0x) -> {
         return rotate((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), RotationArgument.getRotation(var0x, "rotation"));
      }))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes((var0x) -> {
         return rotate((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), (LookAt)(new LookAt.LookAtEntity(EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.Anchor.FEET)));
      })).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((var0x) -> {
         return rotate((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), (LookAt)(new LookAt.LookAtEntity(EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.getAnchor(var0x, "facingAnchor"))));
      }))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((var0x) -> {
         return rotate((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "target"), (LookAt)(new LookAt.LookAtPosition(Vec3Argument.getVec3(var0x, "facingLocation"))));
      })))));
   }

   private static int rotate(CommandSourceStack var0, Entity var1, Coordinates var2) {
      Vec2 var3 = var2.getRotation(var0);
      var1.forceSetRotation(var3.y, var3.x);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.rotate.success", var1.getDisplayName());
      }, true);
      return 1;
   }

   private static int rotate(CommandSourceStack var0, Entity var1, LookAt var2) {
      var2.perform(var0, var1);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.rotate.success", var1.getDisplayName());
      }, true);
      return 1;
   }
}
