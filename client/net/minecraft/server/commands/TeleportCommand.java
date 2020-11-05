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
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslatableComponent("commands.teleport.invalidPosition"));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes((var0x) -> {
         return teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, (TeleportCommand.LookAt)null);
      })).then(Commands.argument("rotation", RotationArgument.rotation()).executes((var0x) -> {
         return teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), RotationArgument.getRotation(var0x, "rotation"), (TeleportCommand.LookAt)null);
      }))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes((var0x) -> {
         return teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, new TeleportCommand.LookAt(EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.Anchor.FEET));
      })).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((var0x) -> {
         return teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, new TeleportCommand.LookAt(EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.getAnchor(var0x, "facingAnchor")));
      }))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((var0x) -> {
         return teleportToPos((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), (Coordinates)null, new TeleportCommand.LookAt(Vec3Argument.getVec3(var0x, "facingLocation")));
      }))))).then(Commands.argument("destination", EntityArgument.entity()).executes((var0x) -> {
         return teleportToEntity((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), EntityArgument.getEntity(var0x, "destination"));
      })))).then(Commands.argument("location", Vec3Argument.vec3()).executes((var0x) -> {
         return teleportToPos((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException()), ((CommandSourceStack)var0x.getSource()).getLevel(), Vec3Argument.getCoordinates(var0x, "location"), WorldCoordinates.current(), (TeleportCommand.LookAt)null);
      }))).then(Commands.argument("destination", EntityArgument.entity()).executes((var0x) -> {
         return teleportToEntity((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException()), EntityArgument.getEntity(var0x, "destination"));
      })));
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).redirect(var1));
   }

   private static int teleportToEntity(CommandSourceStack var0, Collection<? extends Entity> var1, Entity var2) throws CommandSyntaxException {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         performTeleport(var0, var4, (ServerLevel)var2.level, var2.getX(), var2.getY(), var2.getZ(), EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class), var2.yRot, var2.xRot, (TeleportCommand.LookAt)null);
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.single", new Object[]{((Entity)var1.iterator().next()).getDisplayName(), var2.getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.multiple", new Object[]{var1.size(), var2.getDisplayName()}), true);
      }

      return var1.size();
   }

   private static int teleportToPos(CommandSourceStack var0, Collection<? extends Entity> var1, ServerLevel var2, Coordinates var3, @Nullable Coordinates var4, @Nullable TeleportCommand.LookAt var5) throws CommandSyntaxException {
      Vec3 var6 = var3.getPosition(var0);
      Vec2 var7 = var4 == null ? null : var4.getRotation(var0);
      EnumSet var8 = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);
      if (var3.isXRelative()) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.X);
      }

      if (var3.isYRelative()) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Y);
      }

      if (var3.isZRelative()) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Z);
      }

      if (var4 == null) {
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
         var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
      } else {
         if (var4.isXRelative()) {
            var8.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
         }

         if (var4.isYRelative()) {
            var8.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
         }
      }

      Iterator var9 = var1.iterator();

      while(var9.hasNext()) {
         Entity var10 = (Entity)var9.next();
         if (var4 == null) {
            performTeleport(var0, var10, var2, var6.x, var6.y, var6.z, var8, var10.yRot, var10.xRot, var5);
         } else {
            performTeleport(var0, var10, var2, var6.x, var6.y, var6.z, var8, var7.y, var7.x, var5);
         }
      }

      if (var1.size() == 1) {
         var0.sendSuccess(new TranslatableComponent("commands.teleport.success.location.single", new Object[]{((Entity)var1.iterator().next()).getDisplayName(), var6.x, var6.y, var6.z}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.teleport.success.location.multiple", new Object[]{var1.size(), var6.x, var6.y, var6.z}), true);
      }

      return var1.size();
   }

   private static void performTeleport(CommandSourceStack var0, Entity var1, ServerLevel var2, double var3, double var5, double var7, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9, float var10, float var11, @Nullable TeleportCommand.LookAt var12) throws CommandSyntaxException {
      BlockPos var13 = new BlockPos(var3, var5, var7);
      if (!Level.isInSpawnableBounds(var13)) {
         throw INVALID_POSITION.create();
      } else {
         float var14 = Mth.wrapDegrees(var10);
         float var15 = Mth.wrapDegrees(var11);
         if (var1 instanceof ServerPlayer) {
            ChunkPos var16 = new ChunkPos(new BlockPos(var3, var5, var7));
            var2.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, var16, 1, var1.getId());
            var1.stopRiding();
            if (((ServerPlayer)var1).isSleeping()) {
               ((ServerPlayer)var1).stopSleepInBed(true, true);
            }

            if (var2 == var1.level) {
               ((ServerPlayer)var1).connection.teleport(var3, var5, var7, var14, var15, var9);
            } else {
               ((ServerPlayer)var1).teleportTo(var2, var3, var5, var7, var14, var15);
            }

            var1.setYHeadRot(var14);
         } else {
            float var18 = Mth.clamp(var15, -90.0F, 90.0F);
            if (var2 == var1.level) {
               var1.moveTo(var3, var5, var7, var14, var18);
               var1.setYHeadRot(var14);
            } else {
               var1.unRide();
               Entity var17 = var1;
               var1 = var1.getType().create(var2);
               if (var1 == null) {
                  return;
               }

               var1.restoreFrom(var17);
               var1.moveTo(var3, var5, var7, var14, var18);
               var1.setYHeadRot(var14);
               var2.addAndForceLoad(var1);
               var17.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
            }
         }

         if (var12 != null) {
            var12.perform(var0, var1);
         }

         if (!(var1 instanceof LivingEntity) || !((LivingEntity)var1).isFallFlying()) {
            var1.setDeltaMovement(var1.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            var1.setOnGround(true);
         }

         if (var1 instanceof PathfinderMob) {
            ((PathfinderMob)var1).getNavigation().stop();
         }

      }
   }

   static class LookAt {
      private final Vec3 position;
      private final Entity entity;
      private final EntityAnchorArgument.Anchor anchor;

      public LookAt(Entity var1, EntityAnchorArgument.Anchor var2) {
         super();
         this.entity = var1;
         this.anchor = var2;
         this.position = var2.apply(var1);
      }

      public LookAt(Vec3 var1) {
         super();
         this.entity = null;
         this.position = var1;
         this.anchor = null;
      }

      public void perform(CommandSourceStack var1, Entity var2) {
         if (this.entity != null) {
            if (var2 instanceof ServerPlayer) {
               ((ServerPlayer)var2).lookAt(var1.getAnchor(), this.entity, this.anchor);
            } else {
               var2.lookAt(var1.getAnchor(), this.position);
            }
         } else {
            var2.lookAt(var1.getAnchor(), this.position);
         }

      }
   }
}
