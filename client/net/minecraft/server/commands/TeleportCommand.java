package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.teleport.invalidPosition"));

   public TeleportCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires((var0x) -> var0x.hasPermission(2))).then(Commands.argument("location", Vec3Argument.vec3()).executes((var0x) -> teleportToPos((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException()), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, (LookAt)null)))).then(Commands.argument("destination", EntityArgument.entity()).executes((var0x) -> teleportToEntity((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException()), EntityArgument.getEntity(var0x, "destination"))))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes((var0x) -> teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, (LookAt)null))).then(Commands.argument("rotation", RotationArgument.rotation()).executes((var0x) -> teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), RotationArgument.getRotation(var0x, "rotation"), (LookAt)null)))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes((var0x) -> teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, new LookAt.LookAtEntity(EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.Anchor.FEET)))).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((var0x) -> teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, new LookAt.LookAtEntity(EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.getAnchor(var0x, "facingAnchor")))))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((var0x) -> teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, new LookAt.LookAtPosition(Vec3Argument.getVec3(var0x, "facingLocation")))))))).then(Commands.argument("destination", EntityArgument.entity()).executes((var0x) -> teleportToEntity((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), EntityArgument.getEntity(var0x, "destination"))))));
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires((var0x) -> var0x.hasPermission(2))).redirect(var1));
   }

   private static int teleportToEntity(CommandSourceStack var0, Collection<? extends Entity> var1, Entity var2) throws CommandSyntaxException {
      for(Entity var4 : var1) {
         performTeleport(var0, var4, (ServerLevel)var2.level(), var2.getX(), var2.getY(), var2.getZ(), EnumSet.noneOf(Relative.class), var2.getYRot(), var2.getXRot(), (LookAt)null);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.single", ((Entity)var1.iterator().next()).getDisplayName(), var2.getDisplayName()), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.multiple", var1.size(), var2.getDisplayName()), true);
      }

      return var1.size();
   }

   private static int teleportToPos(CommandSourceStack var0, Collection<? extends Entity> var1, ServerLevel var2, Coordinates var3, @Nullable Coordinates var4, @Nullable LookAt var5) throws CommandSyntaxException {
      Vec3 var6 = var3.getPosition(var0);
      Vec2 var7 = var4 == null ? null : var4.getRotation(var0);

      for(Entity var9 : var1) {
         Set var10 = getRelatives(var3, var4, var9.level().dimension() == var2.dimension());
         if (var7 == null) {
            performTeleport(var0, var9, var2, var6.x, var6.y, var6.z, var10, var9.getYRot(), var9.getXRot(), var5);
         } else {
            performTeleport(var0, var9, var2, var6.x, var6.y, var6.z, var10, var7.y, var7.x, var5);
         }
      }

      if (var1.size() == 1) {
         var0.sendSuccess(() -> Component.translatable("commands.teleport.success.location.single", ((Entity)var1.iterator().next()).getDisplayName(), formatDouble(var6.x), formatDouble(var6.y), formatDouble(var6.z)), true);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.teleport.success.location.multiple", var1.size(), formatDouble(var6.x), formatDouble(var6.y), formatDouble(var6.z)), true);
      }

      return var1.size();
   }

   private static Set<Relative> getRelatives(Coordinates var0, @Nullable Coordinates var1, boolean var2) {
      EnumSet var3 = EnumSet.noneOf(Relative.class);
      if (var0.isXRelative()) {
         var3.add(Relative.DELTA_X);
         if (var2) {
            var3.add(Relative.X);
         }
      }

      if (var0.isYRelative()) {
         var3.add(Relative.DELTA_Y);
         if (var2) {
            var3.add(Relative.Y);
         }
      }

      if (var0.isZRelative()) {
         var3.add(Relative.DELTA_Z);
         if (var2) {
            var3.add(Relative.Z);
         }
      }

      if (var1 == null || var1.isXRelative()) {
         var3.add(Relative.X_ROT);
      }

      if (var1 == null || var1.isYRelative()) {
         var3.add(Relative.Y_ROT);
      }

      return var3;
   }

   private static String formatDouble(double var0) {
      return String.format(Locale.ROOT, "%f", var0);
   }

   private static void performTeleport(CommandSourceStack var0, Entity var1, ServerLevel var2, double var3, double var5, double var7, Set<Relative> var9, float var10, float var11, @Nullable LookAt var12) throws CommandSyntaxException {
      BlockPos var13 = BlockPos.containing(var3, var5, var7);
      if (!Level.isInSpawnableBounds(var13)) {
         throw INVALID_POSITION.create();
      } else {
         double var14 = var9.contains(Relative.X) ? var3 - var1.getX() : var3;
         double var16 = var9.contains(Relative.Y) ? var5 - var1.getY() : var5;
         double var18 = var9.contains(Relative.Z) ? var7 - var1.getZ() : var7;
         float var20 = var9.contains(Relative.Y_ROT) ? var10 - var1.getYRot() : var10;
         float var21 = var9.contains(Relative.X_ROT) ? var11 - var1.getXRot() : var11;
         float var22 = Mth.wrapDegrees(var20);
         float var23 = Mth.wrapDegrees(var21);
         if (var1.teleportTo(var2, var14, var16, var18, var9, var22, var23, true)) {
            if (var12 != null) {
               var12.perform(var0, var1);
            }

            label46: {
               if (var1 instanceof LivingEntity) {
                  LivingEntity var24 = (LivingEntity)var1;
                  if (var24.isFallFlying()) {
                     break label46;
                  }
               }

               var1.setDeltaMovement(var1.getDeltaMovement().multiply(1.0, 0.0, 1.0));
               var1.setOnGround(true);
            }

            if (var1 instanceof PathfinderMob) {
               PathfinderMob var25 = (PathfinderMob)var1;
               var25.getNavigation().stop();
            }

         }
      }
   }
}
