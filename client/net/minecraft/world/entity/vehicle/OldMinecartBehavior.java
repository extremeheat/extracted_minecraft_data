package net.minecraft.world.entity.vehicle;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class OldMinecartBehavior extends MinecartBehavior {
   private static final double MINECART_RIDABLE_THRESHOLD = 0.01;
   private static final double MAX_SPEED_IN_WATER = 0.2;
   private static final double MAX_SPEED_ON_LAND = 0.4;
   private static final double ABSOLUTE_MAX_SPEED = 0.4;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;
   private Vec3 targetDeltaMovement = Vec3.ZERO;

   public OldMinecartBehavior(AbstractMinecart var1) {
      super(var1);
   }

   @Override
   public void cancelLerp() {
      this.lerpSteps = 0;
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.lerpX = var1;
      this.lerpY = var3;
      this.lerpZ = var5;
      this.lerpYRot = (double)var7;
      this.lerpXRot = (double)var8;
      this.lerpSteps = var9 + 2;
      this.setDeltaMovement(this.targetDeltaMovement);
   }

   @Override
   public double lerpTargetX() {
      return this.lerpSteps > 0 ? this.lerpX : this.minecart.getX();
   }

   @Override
   public double lerpTargetY() {
      return this.lerpSteps > 0 ? this.lerpY : this.minecart.getY();
   }

   @Override
   public double lerpTargetZ() {
      return this.lerpSteps > 0 ? this.lerpZ : this.minecart.getZ();
   }

   @Override
   public float lerpTargetXRot() {
      return this.lerpSteps > 0 ? (float)this.lerpXRot : this.getXRot();
   }

   @Override
   public float lerpTargetYRot() {
      return this.lerpSteps > 0 ? (float)this.lerpYRot : this.getYRot();
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      this.targetDeltaMovement = new Vec3(var1, var3, var5);
      this.setDeltaMovement(this.targetDeltaMovement);
   }

   @Override
   public void tick() {
      if (this.level() instanceof ServerLevel var1) {
         this.minecart.applyGravity();
         BlockPos var11 = this.minecart.getCurrentBlockPosOrRailBelow();
         BlockState var3 = this.level().getBlockState(var11);
         boolean var4 = BaseRailBlock.isRail(var3);
         this.minecart.setOnRails(var4);
         if (var4) {
            this.moveAlongTrack(var1);
            if (var3.is(Blocks.ACTIVATOR_RAIL)) {
               this.minecart.activateMinecart(var11.getX(), var11.getY(), var11.getZ(), var3.getValue(PoweredRailBlock.POWERED));
            }
         } else {
            this.minecart.comeOffTrack(var1);
         }

         this.minecart.applyEffectsFromBlocks();
         this.minecart.applyEffectsFromBlocks();
         this.setXRot(0.0F);
         double var5 = this.minecart.xo - this.getX();
         double var7 = this.minecart.zo - this.getZ();
         if (var5 * var5 + var7 * var7 > 0.001) {
            this.setYRot((float)(Mth.atan2(var7, var5) * 180.0 / 3.141592653589793));
            if (this.minecart.isFlipped()) {
               this.setYRot(this.getYRot() + 180.0F);
            }
         }

         double var9 = (double)Mth.wrapDegrees(this.getYRot() - this.minecart.yRotO);
         if (var9 < -170.0 || var9 >= 170.0) {
            this.setYRot(this.getYRot() + 180.0F);
            this.minecart.setFlipped(!this.minecart.isFlipped());
         }

         this.setXRot(this.getXRot() % 360.0F);
         this.setYRot(this.getYRot() % 360.0F);
         this.pushAndPickupEntities();
      } else {
         if (this.lerpSteps > 0) {
            this.minecart.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            this.lerpSteps--;
         } else {
            this.minecart.reapplyPosition();
            this.setXRot(this.getXRot() % 360.0F);
            this.setYRot(this.getYRot() % 360.0F);
         }
      }
   }

   @Override
   public void moveAlongTrack(ServerLevel var1) {
      BlockPos var2 = this.minecart.getCurrentBlockPosOrRailBelow();
      BlockState var3 = this.level().getBlockState(var2);
      this.minecart.resetFallDistance();
      double var4 = this.minecart.getX();
      double var6 = this.minecart.getY();
      double var8 = this.minecart.getZ();
      Vec3 var10 = this.getPos(var4, var6, var8);
      var6 = (double)var2.getY();
      boolean var11 = false;
      boolean var12 = false;
      if (var3.is(Blocks.POWERED_RAIL)) {
         var11 = var3.getValue(PoweredRailBlock.POWERED);
         var12 = !var11;
      }

      double var13 = 0.0078125;
      if (this.minecart.isInWater()) {
         var13 *= 0.2;
      }

      Vec3 var15 = this.getDeltaMovement();
      RailShape var16 = var3.getValue(((BaseRailBlock)var3.getBlock()).getShapeProperty());
      switch (var16) {
         case ASCENDING_EAST:
            this.setDeltaMovement(var15.add(-var13, 0.0, 0.0));
            var6++;
            break;
         case ASCENDING_WEST:
            this.setDeltaMovement(var15.add(var13, 0.0, 0.0));
            var6++;
            break;
         case ASCENDING_NORTH:
            this.setDeltaMovement(var15.add(0.0, 0.0, var13));
            var6++;
            break;
         case ASCENDING_SOUTH:
            this.setDeltaMovement(var15.add(0.0, 0.0, -var13));
            var6++;
      }

      var15 = this.getDeltaMovement();
      Pair var17 = AbstractMinecart.exits(var16);
      Vec3i var18 = (Vec3i)var17.getFirst();
      Vec3i var19 = (Vec3i)var17.getSecond();
      double var20 = (double)(var19.getX() - var18.getX());
      double var22 = (double)(var19.getZ() - var18.getZ());
      double var24 = Math.sqrt(var20 * var20 + var22 * var22);
      double var26 = var15.x * var20 + var15.z * var22;
      if (var26 < 0.0) {
         var20 = -var20;
         var22 = -var22;
      }

      double var28 = Math.min(2.0, var15.horizontalDistance());
      var15 = new Vec3(var28 * var20 / var24, var15.y, var28 * var22 / var24);
      this.setDeltaMovement(var15);
      Entity var30 = this.minecart.getFirstPassenger();
      Vec3 var31;
      if (this.minecart.getFirstPassenger() instanceof ServerPlayer var32) {
         var31 = var32.getLastClientMoveIntent();
      } else {
         var31 = Vec3.ZERO;
      }

      if (var30 instanceof Player && var31.lengthSqr() > 0.0) {
         Vec3 var65 = var31.normalize();
         double var68 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var65.lengthSqr() > 0.0 && var68 < 0.01) {
            this.setDeltaMovement(this.getDeltaMovement().add(var31.x * 0.001, 0.0, var31.z * 0.001));
            var12 = false;
         }
      }

      if (var12) {
         double var66 = this.getDeltaMovement().horizontalDistance();
         if (var66 < 0.03) {
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
         }
      }

      double var67 = (double)var2.getX() + 0.5 + (double)var18.getX() * 0.5;
      double var34 = (double)var2.getZ() + 0.5 + (double)var18.getZ() * 0.5;
      double var36 = (double)var2.getX() + 0.5 + (double)var19.getX() * 0.5;
      double var38 = (double)var2.getZ() + 0.5 + (double)var19.getZ() * 0.5;
      var20 = var36 - var67;
      var22 = var38 - var34;
      double var40;
      if (var20 == 0.0) {
         var40 = var8 - (double)var2.getZ();
      } else if (var22 == 0.0) {
         var40 = var4 - (double)var2.getX();
      } else {
         double var42 = var4 - var67;
         double var44 = var8 - var34;
         var40 = (var42 * var20 + var44 * var22) * 2.0;
      }

      var4 = var67 + var20 * var40;
      var8 = var34 + var22 * var40;
      this.setPos(var4, var6, var8);
      double var69 = this.minecart.isVehicle() ? 0.75 : 1.0;
      double var70 = this.minecart.getMaxSpeed(var1);
      var15 = this.getDeltaMovement();
      this.minecart.move(MoverType.SELF, new Vec3(Mth.clamp(var69 * var15.x, -var70, var70), 0.0, Mth.clamp(var69 * var15.z, -var70, var70)));
      if (var18.getY() != 0 && Mth.floor(this.minecart.getX()) - var2.getX() == var18.getX() && Mth.floor(this.minecart.getZ()) - var2.getZ() == var18.getZ()) {
         this.setPos(this.minecart.getX(), this.minecart.getY() + (double)var18.getY(), this.minecart.getZ());
      } else if (var19.getY() != 0
         && Mth.floor(this.minecart.getX()) - var2.getX() == var19.getX()
         && Mth.floor(this.minecart.getZ()) - var2.getZ() == var19.getZ()) {
         this.setPos(this.minecart.getX(), this.minecart.getY() + (double)var19.getY(), this.minecart.getZ());
      }

      this.setDeltaMovement(this.minecart.applyNaturalSlowdown(this.getDeltaMovement()));
      Vec3 var46 = this.getPos(this.minecart.getX(), this.minecart.getY(), this.minecart.getZ());
      if (var46 != null && var10 != null) {
         double var47 = (var10.y - var46.y) * 0.05;
         Vec3 var49 = this.getDeltaMovement();
         double var50 = var49.horizontalDistance();
         if (var50 > 0.0) {
            this.setDeltaMovement(var49.multiply((var50 + var47) / var50, 1.0, (var50 + var47) / var50));
         }

         this.setPos(this.minecart.getX(), var46.y, this.minecart.getZ());
      }

      int var71 = Mth.floor(this.minecart.getX());
      int var48 = Mth.floor(this.minecart.getZ());
      if (var71 != var2.getX() || var48 != var2.getZ()) {
         Vec3 var72 = this.getDeltaMovement();
         double var74 = var72.horizontalDistance();
         this.setDeltaMovement(var74 * (double)(var71 - var2.getX()), var72.y, var74 * (double)(var48 - var2.getZ()));
      }

      if (var11) {
         Vec3 var73 = this.getDeltaMovement();
         double var75 = var73.horizontalDistance();
         if (var75 > 0.01) {
            double var52 = 0.06;
            this.setDeltaMovement(var73.add(var73.x / var75 * 0.06, 0.0, var73.z / var75 * 0.06));
         } else {
            Vec3 var76 = this.getDeltaMovement();
            double var53 = var76.x;
            double var55 = var76.z;
            if (var16 == RailShape.EAST_WEST) {
               if (this.minecart.isRedstoneConductor(var2.west())) {
                  var53 = 0.02;
               } else if (this.minecart.isRedstoneConductor(var2.east())) {
                  var53 = -0.02;
               }
            } else {
               if (var16 != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.minecart.isRedstoneConductor(var2.north())) {
                  var55 = 0.02;
               } else if (this.minecart.isRedstoneConductor(var2.south())) {
                  var55 = -0.02;
               }
            }

            this.setDeltaMovement(var53, var76.y, var55);
         }
      }
   }

   @Nullable
   public Vec3 getPosOffs(double var1, double var3, double var5, double var7) {
      int var9 = Mth.floor(var1);
      int var10 = Mth.floor(var3);
      int var11 = Mth.floor(var5);
      if (this.level().getBlockState(new BlockPos(var9, var10 - 1, var11)).is(BlockTags.RAILS)) {
         var10--;
      }

      BlockState var12 = this.level().getBlockState(new BlockPos(var9, var10, var11));
      if (BaseRailBlock.isRail(var12)) {
         RailShape var13 = var12.getValue(((BaseRailBlock)var12.getBlock()).getShapeProperty());
         var3 = (double)var10;
         if (var13.isSlope()) {
            var3 = (double)(var10 + 1);
         }

         Pair var14 = AbstractMinecart.exits(var13);
         Vec3i var15 = (Vec3i)var14.getFirst();
         Vec3i var16 = (Vec3i)var14.getSecond();
         double var17 = (double)(var16.getX() - var15.getX());
         double var19 = (double)(var16.getZ() - var15.getZ());
         double var21 = Math.sqrt(var17 * var17 + var19 * var19);
         var17 /= var21;
         var19 /= var21;
         var1 += var17 * var7;
         var5 += var19 * var7;
         if (var15.getY() != 0 && Mth.floor(var1) - var9 == var15.getX() && Mth.floor(var5) - var11 == var15.getZ()) {
            var3 += (double)var15.getY();
         } else if (var16.getY() != 0 && Mth.floor(var1) - var9 == var16.getX() && Mth.floor(var5) - var11 == var16.getZ()) {
            var3 += (double)var16.getY();
         }

         return this.getPos(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3 getPos(double var1, double var3, double var5) {
      int var7 = Mth.floor(var1);
      int var8 = Mth.floor(var3);
      int var9 = Mth.floor(var5);
      if (this.level().getBlockState(new BlockPos(var7, var8 - 1, var9)).is(BlockTags.RAILS)) {
         var8--;
      }

      BlockState var10 = this.level().getBlockState(new BlockPos(var7, var8, var9));
      if (BaseRailBlock.isRail(var10)) {
         RailShape var11 = var10.getValue(((BaseRailBlock)var10.getBlock()).getShapeProperty());
         Pair var12 = AbstractMinecart.exits(var11);
         Vec3i var13 = (Vec3i)var12.getFirst();
         Vec3i var14 = (Vec3i)var12.getSecond();
         double var15 = (double)var7 + 0.5 + (double)var13.getX() * 0.5;
         double var17 = (double)var8 + 0.0625 + (double)var13.getY() * 0.5;
         double var19 = (double)var9 + 0.5 + (double)var13.getZ() * 0.5;
         double var21 = (double)var7 + 0.5 + (double)var14.getX() * 0.5;
         double var23 = (double)var8 + 0.0625 + (double)var14.getY() * 0.5;
         double var25 = (double)var9 + 0.5 + (double)var14.getZ() * 0.5;
         double var27 = var21 - var15;
         double var29 = (var23 - var17) * 2.0;
         double var31 = var25 - var19;
         double var33;
         if (var27 == 0.0) {
            var33 = var5 - (double)var9;
         } else if (var31 == 0.0) {
            var33 = var1 - (double)var7;
         } else {
            double var35 = var1 - var15;
            double var37 = var5 - var19;
            var33 = (var35 * var27 + var37 * var31) * 2.0;
         }

         var1 = var15 + var27 * var33;
         var3 = var17 + var29 * var33;
         var5 = var19 + var31 * var33;
         if (var29 < 0.0) {
            var3++;
         } else if (var29 > 0.0) {
            var3 += 0.5;
         }

         return new Vec3(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Override
   public double stepAlongTrack(BlockPos var1, RailShape var2, double var3) {
      return 0.0;
   }

   @Override
   public boolean pushAndPickupEntities() {
      AABB var1 = this.minecart.getBoundingBox().inflate(0.20000000298023224, 0.0, 0.20000000298023224);
      if (this.minecart.isRideable() && this.getDeltaMovement().horizontalDistanceSqr() >= 0.01) {
         List var5 = this.level().getEntities(this.minecart, var1, EntitySelector.pushableBy(this.minecart));
         if (!var5.isEmpty()) {
            for (Entity var4 : var5) {
               if (!(var4 instanceof Player)
                  && !(var4 instanceof IronGolem)
                  && !(var4 instanceof AbstractMinecart)
                  && !this.minecart.isVehicle()
                  && !var4.isPassenger()) {
                  var4.startRiding(this.minecart);
               } else {
                  var4.push(this.minecart);
               }
            }
         }
      } else {
         for (Entity var3 : this.level().getEntities(this.minecart, var1)) {
            if (!this.minecart.hasPassenger(var3) && var3.isPushable() && var3 instanceof AbstractMinecart) {
               var3.push(this.minecart);
            }
         }
      }

      return false;
   }

   @Override
   public Direction getMotionDirection() {
      return this.minecart.isFlipped() ? this.minecart.getDirection().getOpposite().getClockWise() : this.minecart.getDirection().getClockWise();
   }

   @Override
   public Vec3 getKnownMovement(Vec3 var1) {
      return new Vec3(Mth.clamp(var1.x, -0.4, 0.4), var1.y, Mth.clamp(var1.z, -0.4, 0.4));
   }

   @Override
   public double getMaxSpeed(ServerLevel var1) {
      return this.minecart.isInWater() ? 0.2 : 0.4;
   }

   @Override
   public double getSlowdownFactor() {
      return this.minecart.isVehicle() ? 0.997 : 0.96;
   }
}
