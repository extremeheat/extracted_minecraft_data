package net.minecraft.world.entity.vehicle;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NewMinecartBehavior extends MinecartBehavior {
   public static final int POS_ROT_LERP_TICKS = 3;
   public static final double ON_RAIL_Y_OFFSET = 0.1;
   public static final double OPPOSING_SLOPES_REST_AT_SPEED_THRESHOLD = 0.005;
   @Nullable
   private StepPartialTicks cacheIndexAlpha;
   private int cachedLerpDelay;
   private float cachedPartialTick;
   private int lerpDelay = 0;
   public final List<MinecartStep> lerpSteps = new LinkedList();
   public final List<MinecartStep> currentLerpSteps = new LinkedList();
   public double currentLerpStepsTotalWeight = 0.0;
   public MinecartStep oldLerp;

   public NewMinecartBehavior(AbstractMinecart var1) {
      super(var1);
      this.oldLerp = NewMinecartBehavior.MinecartStep.ZERO;
   }

   public void tick() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         BlockPos var5 = this.minecart.getCurrentBlockPosOrRailBelow();
         BlockState var3 = this.level().getBlockState(var5);
         if (this.minecart.isFirstTick()) {
            this.minecart.setOnRails(BaseRailBlock.isRail(var3));
            this.adjustToRails(var5, var3, true);
         }

         this.minecart.applyGravity();
         this.minecart.moveAlongTrack(var1);
      } else {
         this.lerpClientPositionAndRotation();
         boolean var4 = BaseRailBlock.isRail(this.level().getBlockState(this.minecart.getCurrentBlockPosOrRailBelow()));
         this.minecart.setOnRails(var4);
      }
   }

   private void lerpClientPositionAndRotation() {
      if (--this.lerpDelay <= 0) {
         this.setOldLerpValues();
         this.currentLerpSteps.clear();
         if (!this.lerpSteps.isEmpty()) {
            this.currentLerpSteps.addAll(this.lerpSteps);
            this.lerpSteps.clear();
            this.currentLerpStepsTotalWeight = 0.0;

            for(MinecartStep var2 : this.currentLerpSteps) {
               this.currentLerpStepsTotalWeight += (double)var2.weight;
            }

            this.lerpDelay = this.currentLerpStepsTotalWeight == 0.0 ? 0 : 3;
         }
      }

      if (this.cartHasPosRotLerp()) {
         this.setPos(this.getCartLerpPosition(1.0F));
         this.setDeltaMovement(this.getCartLerpMovements(1.0F));
         this.setXRot(this.getCartLerpXRot(1.0F));
         this.setYRot(this.getCartLerpYRot(1.0F));
      }

   }

   public void setOldLerpValues() {
      this.oldLerp = new MinecartStep(this.position(), this.getDeltaMovement(), this.getYRot(), this.getXRot(), 0.0F);
   }

   public boolean cartHasPosRotLerp() {
      return !this.currentLerpSteps.isEmpty();
   }

   public float getCartLerpXRot(float var1) {
      StepPartialTicks var2 = this.getCurrentLerpStep(var1);
      return Mth.rotLerp(var2.partialTicksInStep, var2.previousStep.xRot, var2.currentStep.xRot);
   }

   public float getCartLerpYRot(float var1) {
      StepPartialTicks var2 = this.getCurrentLerpStep(var1);
      return Mth.rotLerp(var2.partialTicksInStep, var2.previousStep.yRot, var2.currentStep.yRot);
   }

   public Vec3 getCartLerpPosition(float var1) {
      StepPartialTicks var2 = this.getCurrentLerpStep(var1);
      return Mth.lerp((double)var2.partialTicksInStep, var2.previousStep.position, var2.currentStep.position);
   }

   public Vec3 getCartLerpMovements(float var1) {
      StepPartialTicks var2 = this.getCurrentLerpStep(var1);
      return Mth.lerp((double)var2.partialTicksInStep, var2.previousStep.movement, var2.currentStep.movement);
   }

   private StepPartialTicks getCurrentLerpStep(float var1) {
      if (var1 == this.cachedPartialTick && this.lerpDelay == this.cachedLerpDelay && this.cacheIndexAlpha != null) {
         return this.cacheIndexAlpha;
      } else {
         float var2 = ((float)(3 - this.lerpDelay) + var1) / 3.0F;
         float var3 = 0.0F;
         float var5 = 1.0F;
         boolean var6 = false;

         int var4;
         for(var4 = 0; var4 < this.currentLerpSteps.size(); ++var4) {
            float var7 = ((MinecartStep)this.currentLerpSteps.get(var4)).weight;
            if (!(var7 <= 0.0F)) {
               var3 += var7;
               if ((double)var3 >= this.currentLerpStepsTotalWeight * (double)var2) {
                  float var8 = var3 - var7;
                  var5 = (float)(((double)var2 * this.currentLerpStepsTotalWeight - (double)var8) / (double)var7);
                  var6 = true;
                  break;
               }
            }
         }

         if (!var6) {
            var4 = this.currentLerpSteps.size() - 1;
         }

         MinecartStep var9 = (MinecartStep)this.currentLerpSteps.get(var4);
         MinecartStep var10 = var4 > 0 ? (MinecartStep)this.currentLerpSteps.get(var4 - 1) : this.oldLerp;
         this.cacheIndexAlpha = new StepPartialTicks(var5, var9, var10);
         this.cachedLerpDelay = this.lerpDelay;
         this.cachedPartialTick = var1;
         return this.cacheIndexAlpha;
      }
   }

   public void adjustToRails(BlockPos var1, BlockState var2, boolean var3) {
      if (BaseRailBlock.isRail(var2)) {
         RailShape var4 = (RailShape)var2.getValue(((BaseRailBlock)var2.getBlock()).getShapeProperty());
         Pair var5 = AbstractMinecart.exits(var4);
         Vec3 var6 = (new Vec3((Vec3i)var5.getFirst())).scale(0.5);
         Vec3 var7 = (new Vec3((Vec3i)var5.getSecond())).scale(0.5);
         Vec3 var8 = var6.horizontal();
         Vec3 var9 = var7.horizontal();
         if (this.getDeltaMovement().length() > 9.999999747378752E-6 && this.getDeltaMovement().dot(var8) < this.getDeltaMovement().dot(var9) || this.isDecending(var9, var4)) {
            Vec3 var10 = var8;
            var8 = var9;
            var9 = var10;
         }

         float var20 = 180.0F - (float)(Math.atan2(var8.z, var8.x) * 180.0 / 3.141592653589793);
         var20 += this.minecart.isFlipped() ? 180.0F : 0.0F;
         Vec3 var11 = this.position();
         boolean var13 = var6.x() != var7.x() && var6.z() != var7.z();
         Vec3 var12;
         if (var13) {
            Vec3 var14 = var7.subtract(var6);
            Vec3 var15 = var11.subtract(var1.getBottomCenter()).subtract(var6);
            Vec3 var16 = var14.scale(var14.dot(var15) / var14.dot(var14));
            var12 = var1.getBottomCenter().add(var6).add(var16);
            var20 = 180.0F - (float)(Math.atan2(var16.z, var16.x) * 180.0 / 3.141592653589793);
            var20 += this.minecart.isFlipped() ? 180.0F : 0.0F;
         } else {
            boolean var23 = var6.subtract(var7).x != 0.0;
            boolean var25 = var6.subtract(var7).z != 0.0;
            var12 = new Vec3(var25 ? var1.getCenter().x : var11.x, (double)var1.getY(), var23 ? var1.getCenter().z : var11.z);
         }

         Vec3 var24 = var12.subtract(var11);
         this.setPos(var11.add(var24));
         float var26 = 0.0F;
         boolean var27 = var6.y() != var7.y();
         if (var27) {
            Vec3 var17 = var1.getBottomCenter().add(var9);
            double var18 = var17.distanceTo(this.position());
            this.setPos(this.position().add(0.0, var18 + 0.1, 0.0));
            var26 = this.minecart.isFlipped() ? 45.0F : -45.0F;
         } else {
            this.setPos(this.position().add(0.0, 0.1, 0.0));
         }

         this.setRotation(var20, var26);
         double var28 = var11.distanceTo(this.position());
         if (var28 > 0.0) {
            this.lerpSteps.add(new MinecartStep(this.position(), this.getDeltaMovement(), this.getYRot(), this.getXRot(), var3 ? 0.0F : (float)var28));
         }

      }
   }

   private void setRotation(float var1, float var2) {
      double var3 = (double)Math.abs(var1 - this.getYRot());
      if (var3 >= 175.0 && var3 <= 185.0) {
         this.minecart.setFlipped(!this.minecart.isFlipped());
         var1 -= 180.0F;
         var2 *= -1.0F;
      }

      var2 = Math.clamp(var2, -45.0F, 45.0F);
      this.setXRot(var2 % 360.0F);
      this.setYRot(var1 % 360.0F);
   }

   public void moveAlongTrack(ServerLevel var1) {
      for(TrackIteration var2 = new TrackIteration(); var2.shouldIterate() && this.minecart.isAlive(); var2.firstIteration = false) {
         Vec3 var3 = this.getDeltaMovement();
         BlockPos var4 = this.minecart.getCurrentBlockPosOrRailBelow();
         BlockState var5 = this.level().getBlockState(var4);
         boolean var6 = BaseRailBlock.isRail(var5);
         if (this.minecart.isOnRails() != var6) {
            this.minecart.setOnRails(var6);
            this.adjustToRails(var4, var5, false);
         }

         if (var6) {
            this.minecart.resetFallDistance();
            this.minecart.setOldPosAndRot();
            if (var5.is(Blocks.ACTIVATOR_RAIL)) {
               this.minecart.activateMinecart(var4.getX(), var4.getY(), var4.getZ(), (Boolean)var5.getValue(PoweredRailBlock.POWERED));
            }

            RailShape var7 = (RailShape)var5.getValue(((BaseRailBlock)var5.getBlock()).getShapeProperty());
            Vec3 var8 = this.calculateTrackSpeed(var1, var3.horizontal(), var2, var4, var5, var7);
            if (var2.firstIteration) {
               var2.movementLeft = var8.horizontalDistance();
            } else {
               var2.movementLeft += var8.horizontalDistance() - var3.horizontalDistance();
            }

            this.setDeltaMovement(var8);
            var2.movementLeft = this.minecart.makeStepAlongTrack(var4, var7, var2.movementLeft);
         } else {
            this.minecart.comeOffTrack(var1);
            var2.movementLeft = 0.0;
         }

         Vec3 var13 = this.position();
         Vec3 var14 = var13.subtract(this.minecart.oldPosition());
         double var9 = var14.length();
         if (var9 > 9.999999747378752E-6) {
            if (!(var14.horizontalDistanceSqr() > 9.999999747378752E-6)) {
               if (!this.minecart.isOnRails()) {
                  this.setXRot(this.minecart.onGround() ? 0.0F : Mth.rotLerp(0.2F, this.getXRot(), 0.0F));
               }
            } else {
               float var11 = 180.0F - (float)(Math.atan2(var14.z, var14.x) * 180.0 / 3.141592653589793);
               float var12 = this.minecart.onGround() && !this.minecart.isOnRails() ? 0.0F : 90.0F - (float)(Math.atan2(var14.horizontalDistance(), var14.y) * 180.0 / 3.141592653589793);
               var11 += this.minecart.isFlipped() ? 180.0F : 0.0F;
               var12 *= this.minecart.isFlipped() ? -1.0F : 1.0F;
               this.setRotation(var11, var12);
            }

            this.lerpSteps.add(new MinecartStep(var13, this.getDeltaMovement(), this.getYRot(), this.getXRot(), (float)Math.min(var9, this.getMaxSpeed(var1))));
         } else if (var3.horizontalDistanceSqr() > 0.0) {
            this.lerpSteps.add(new MinecartStep(var13, this.getDeltaMovement(), this.getYRot(), this.getXRot(), 1.0F));
         }

         if (var9 > 9.999999747378752E-6 || var2.firstIteration) {
            this.minecart.applyEffectsFromBlocks();
            this.minecart.applyEffectsFromBlocks();
         }
      }

   }

   private Vec3 calculateTrackSpeed(ServerLevel var1, Vec3 var2, TrackIteration var3, BlockPos var4, BlockState var5, RailShape var6) {
      Vec3 var7 = var2;
      if (!var3.hasGainedSlopeSpeed) {
         Vec3 var8 = this.calculateSlopeSpeed(var2, var6);
         if (var8.horizontalDistanceSqr() != var2.horizontalDistanceSqr()) {
            var3.hasGainedSlopeSpeed = true;
            var7 = var8;
         }
      }

      if (var3.firstIteration) {
         Vec3 var10 = this.calculatePlayerInputSpeed(var7);
         if (var10.horizontalDistanceSqr() != var7.horizontalDistanceSqr()) {
            var3.hasHalted = true;
            var7 = var10;
         }
      }

      if (!var3.hasHalted) {
         Vec3 var11 = this.calculateHaltTrackSpeed(var7, var5);
         if (var11.horizontalDistanceSqr() != var7.horizontalDistanceSqr()) {
            var3.hasHalted = true;
            var7 = var11;
         }
      }

      if (var3.firstIteration) {
         var7 = this.minecart.applyNaturalSlowdown(var7);
         if (var7.lengthSqr() > 0.0) {
            double var12 = Math.min(var7.length(), this.minecart.getMaxSpeed(var1));
            var7 = var7.normalize().scale(var12);
         }
      }

      if (!var3.hasBoosted) {
         Vec3 var13 = this.calculateBoostTrackSpeed(var7, var4, var5);
         if (var13.horizontalDistanceSqr() != var7.horizontalDistanceSqr()) {
            var3.hasBoosted = true;
            var7 = var13;
         }
      }

      return var7;
   }

   private Vec3 calculateSlopeSpeed(Vec3 var1, RailShape var2) {
      double var3 = Math.max(0.0078125, var1.horizontalDistance() * 0.02);
      if (this.minecart.isInWater()) {
         var3 *= 0.2;
      }

      Vec3 var10000;
      switch (var2) {
         case ASCENDING_EAST -> var10000 = var1.add(-var3, 0.0, 0.0);
         case ASCENDING_WEST -> var10000 = var1.add(var3, 0.0, 0.0);
         case ASCENDING_NORTH -> var10000 = var1.add(0.0, 0.0, var3);
         case ASCENDING_SOUTH -> var10000 = var1.add(0.0, 0.0, -var3);
         default -> var10000 = var1;
      }

      return var10000;
   }

   private Vec3 calculatePlayerInputSpeed(Vec3 var1) {
      Entity var3 = this.minecart.getFirstPassenger();
      if (var3 instanceof ServerPlayer var2) {
         Vec3 var7 = var2.getLastClientMoveIntent();
         if (var7.lengthSqr() > 0.0) {
            Vec3 var4 = var7.normalize();
            double var5 = var1.horizontalDistanceSqr();
            if (var4.lengthSqr() > 0.0 && var5 < 0.01) {
               return var1.add((new Vec3(var4.x, 0.0, var4.z)).normalize().scale(0.001));
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   private Vec3 calculateHaltTrackSpeed(Vec3 var1, BlockState var2) {
      if (var2.is(Blocks.POWERED_RAIL) && !(Boolean)var2.getValue(PoweredRailBlock.POWERED)) {
         return var1.length() < 0.03 ? Vec3.ZERO : var1.scale(0.5);
      } else {
         return var1;
      }
   }

   private Vec3 calculateBoostTrackSpeed(Vec3 var1, BlockPos var2, BlockState var3) {
      if (var3.is(Blocks.POWERED_RAIL) && (Boolean)var3.getValue(PoweredRailBlock.POWERED)) {
         if (var1.length() > 0.01) {
            return var1.normalize().scale(var1.length() + 0.06);
         } else {
            Vec3 var4 = this.minecart.getRedstoneDirection(var2);
            return var4.lengthSqr() <= 0.0 ? var1 : var4.scale(var1.length() + 0.2);
         }
      } else {
         return var1;
      }
   }

   public double stepAlongTrack(BlockPos var1, RailShape var2, double var3) {
      if (var3 < 9.999999747378752E-6) {
         return 0.0;
      } else {
         Vec3 var5 = this.position();
         Pair var6 = AbstractMinecart.exits(var2);
         Vec3i var7 = (Vec3i)var6.getFirst();
         Vec3i var8 = (Vec3i)var6.getSecond();
         Vec3 var9 = this.getDeltaMovement().horizontal();
         if (var9.length() < 9.999999747378752E-6) {
            this.setDeltaMovement(Vec3.ZERO);
            return 0.0;
         } else {
            boolean var10 = var7.getY() != var8.getY();
            Vec3 var11 = (new Vec3(var8)).scale(0.5).horizontal();
            Vec3 var12 = (new Vec3(var7)).scale(0.5).horizontal();
            if (var9.dot(var12) < var9.dot(var11)) {
               var12 = var11;
            }

            Vec3 var13 = var1.getBottomCenter().add(var12).add(0.0, 0.1, 0.0).add(var12.normalize().scale(9.999999747378752E-6));
            if (var10 && !this.isDecending(var9, var2)) {
               var13 = var13.add(0.0, 1.0, 0.0);
            }

            Vec3 var14 = var13.subtract(this.position()).normalize();
            var9 = var14.scale(var9.length() / var14.horizontalDistance());
            Vec3 var15 = var5.add(var9.normalize().scale(var3 * (double)(var10 ? Mth.SQRT_OF_TWO : 1.0F)));
            if (var5.distanceToSqr(var13) <= var5.distanceToSqr(var15)) {
               var3 = var13.subtract(var15).horizontalDistance();
               var15 = var13;
            } else {
               var3 = 0.0;
            }

            this.minecart.move(MoverType.SELF, var15.subtract(var5));
            BlockState var16 = this.level().getBlockState(BlockPos.containing(var15));
            if (var10) {
               if (BaseRailBlock.isRail(var16)) {
                  RailShape var17 = (RailShape)var16.getValue(((BaseRailBlock)var16.getBlock()).getShapeProperty());
                  if (this.restAtVShape(var2, var17)) {
                     return 0.0;
                  }
               }

               double var23 = var13.horizontal().distanceTo(this.position().horizontal());
               double var19 = var13.y + (this.isDecending(var9, var2) ? var23 : -var23);
               if (this.position().y < var19) {
                  this.setPos(this.position().x, var19, this.position().z);
               }
            }

            if (this.position().distanceTo(var5) < 9.999999747378752E-6 && var15.distanceTo(var5) > 9.999999747378752E-6) {
               this.setDeltaMovement(Vec3.ZERO);
               return 0.0;
            } else {
               this.setDeltaMovement(var9);
               return var3;
            }
         }
      }
   }

   private boolean restAtVShape(RailShape var1, RailShape var2) {
      if (this.getDeltaMovement().lengthSqr() < 0.005 && var2.isSlope() && this.isDecending(this.getDeltaMovement(), var1) && !this.isDecending(this.getDeltaMovement(), var2)) {
         this.setDeltaMovement(Vec3.ZERO);
         return true;
      } else {
         return false;
      }
   }

   public double getMaxSpeed(ServerLevel var1) {
      return (double)var1.getGameRules().getInt(GameRules.RULE_MINECART_MAX_SPEED) * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0;
   }

   private boolean isDecending(Vec3 var1, RailShape var2) {
      boolean var10000;
      switch (var2) {
         case ASCENDING_EAST -> var10000 = var1.x < 0.0;
         case ASCENDING_WEST -> var10000 = var1.x > 0.0;
         case ASCENDING_NORTH -> var10000 = var1.z > 0.0;
         case ASCENDING_SOUTH -> var10000 = var1.z < 0.0;
         default -> var10000 = false;
      }

      return var10000;
   }

   public double getSlowdownFactor() {
      return this.minecart.isVehicle() ? 0.997 : 0.975;
   }

   public boolean pushAndPickupEntities() {
      boolean var1 = this.pickupEntities(this.minecart.getBoundingBox().inflate(0.2, 0.0, 0.2));
      if (!this.minecart.horizontalCollision && !this.minecart.verticalCollision) {
         return false;
      } else {
         boolean var2 = this.pushEntities(this.minecart.getBoundingBox().inflate(1.0E-7));
         return var1 && !var2;
      }
   }

   public boolean pickupEntities(AABB var1) {
      if (this.minecart.isRideable() && !this.minecart.isVehicle()) {
         List var2 = this.level().getEntities(this.minecart, var1, EntitySelector.pushableBy(this.minecart));
         if (!var2.isEmpty()) {
            for(Entity var4 : var2) {
               if (!(var4 instanceof Player) && !(var4 instanceof IronGolem) && !(var4 instanceof AbstractMinecart) && !this.minecart.isVehicle() && !var4.isPassenger()) {
                  boolean var5 = var4.startRiding(this.minecart);
                  if (var5) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean pushEntities(AABB var1) {
      boolean var2 = false;
      if (this.minecart.isRideable()) {
         List var3 = this.level().getEntities(this.minecart, var1, EntitySelector.pushableBy(this.minecart));
         if (!var3.isEmpty()) {
            for(Entity var5 : var3) {
               if (var5 instanceof Player || var5 instanceof IronGolem || var5 instanceof AbstractMinecart || this.minecart.isVehicle() || var5.isPassenger()) {
                  var5.push((Entity)this.minecart);
                  var2 = true;
               }
            }
         }
      } else {
         for(Entity var7 : this.level().getEntities(this.minecart, var1)) {
            if (!this.minecart.hasPassenger(var7) && var7.isPushable() && var7 instanceof AbstractMinecart) {
               var7.push((Entity)this.minecart);
               var2 = true;
            }
         }
      }

      return var2;
   }

   public static record MinecartStep(Vec3 position, Vec3 movement, float yRot, float xRot, float weight) {
      final Vec3 position;
      final Vec3 movement;
      final float yRot;
      final float xRot;
      final float weight;
      public static final StreamCodec<ByteBuf, MinecartStep> STREAM_CODEC;
      public static MinecartStep ZERO;

      public MinecartStep(Vec3 var1, Vec3 var2, float var3, float var4, float var5) {
         super();
         this.position = var1;
         this.movement = var2;
         this.yRot = var3;
         this.xRot = var4;
         this.weight = var5;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, MinecartStep::position, Vec3.STREAM_CODEC, MinecartStep::movement, ByteBufCodecs.ROTATION_BYTE, MinecartStep::yRot, ByteBufCodecs.ROTATION_BYTE, MinecartStep::xRot, ByteBufCodecs.FLOAT, MinecartStep::weight, MinecartStep::new);
         ZERO = new MinecartStep(Vec3.ZERO, Vec3.ZERO, 0.0F, 0.0F, 0.0F);
      }
   }

   static class TrackIteration {
      double movementLeft = 0.0;
      boolean firstIteration = true;
      boolean hasGainedSlopeSpeed = false;
      boolean hasHalted = false;
      boolean hasBoosted = false;

      TrackIteration() {
         super();
      }

      public boolean shouldIterate() {
         return this.firstIteration || this.movementLeft > 9.999999747378752E-6;
      }
   }

   static record StepPartialTicks(float partialTicksInStep, MinecartStep currentStep, MinecartStep previousStep) {
      final float partialTicksInStep;
      final MinecartStep currentStep;
      final MinecartStep previousStep;

      StepPartialTicks(float var1, MinecartStep var2, MinecartStep var3) {
         super();
         this.partialTicksInStep = var1;
         this.currentStep = var2;
         this.previousStep = var3;
      }
   }
}
