package net.minecraft.world.entity.vehicle;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
      if (this.level().isClientSide) {
         if (this.lerpSteps > 0) {
            this.minecart.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            this.lerpSteps--;
         } else {
            this.minecart.reapplyPosition();
            this.setXRot(this.getXRot() % 360.0F);
            this.setYRot(this.getYRot() % 360.0F);
         }
      } else {
         this.minecart.applyGravity();
         BlockPos var1 = this.minecart.getCurrentBlockPosOrRailBelow();
         BlockState var2 = this.level().getBlockState(var1);
         boolean var3 = BaseRailBlock.isRail(var2);
         this.minecart.setOnRails(var3);
         if (var3) {
            this.moveAlongTrack();
            if (var2.is(Blocks.ACTIVATOR_RAIL)) {
               this.minecart.activateMinecart(var1.getX(), var1.getY(), var1.getZ(), var2.getValue(PoweredRailBlock.POWERED));
            }
         } else {
            this.minecart.comeOffTrack();
         }

         this.minecart.applyEffectsFromBlocks();
         this.setXRot(0.0F);
         double var4 = this.minecart.xo - this.getX();
         double var6 = this.minecart.zo - this.getZ();
         if (var4 * var4 + var6 * var6 > 0.001) {
            this.setYRot((float)(Mth.atan2(var6, var4) * 180.0 / 3.141592653589793));
            if (this.minecart.isFlipped()) {
               this.setYRot(this.getYRot() + 180.0F);
            }
         }

         double var8 = (double)Mth.wrapDegrees(this.getYRot() - this.minecart.yRotO);
         if (var8 < -170.0 || var8 >= 170.0) {
            this.setYRot(this.getYRot() + 180.0F);
            this.minecart.setFlipped(!this.minecart.isFlipped());
         }

         this.setXRot(this.getXRot() % 360.0F);
         this.setYRot(this.getYRot() % 360.0F);
         this.pushAndPickupEntities();
      }
   }

   @Override
   public void moveAlongTrack() {
      BlockPos var1 = this.minecart.getCurrentBlockPosOrRailBelow();
      BlockState var2 = this.level().getBlockState(var1);
      this.minecart.resetFallDistance();
      double var3 = this.minecart.getX();
      double var5 = this.minecart.getY();
      double var7 = this.minecart.getZ();
      Vec3 var9 = this.getPos(var3, var5, var7);
      var5 = (double)var1.getY();
      boolean var10 = false;
      boolean var11 = false;
      if (var2.is(Blocks.POWERED_RAIL)) {
         var10 = var2.getValue(PoweredRailBlock.POWERED);
         var11 = !var10;
      }

      double var12 = 0.0078125;
      if (this.minecart.isInWater()) {
         var12 *= 0.2;
      }

      Vec3 var14 = this.getDeltaMovement();
      RailShape var15 = var2.getValue(((BaseRailBlock)var2.getBlock()).getShapeProperty());
      switch (var15) {
         case ASCENDING_EAST:
            this.setDeltaMovement(var14.add(-var12, 0.0, 0.0));
            var5++;
            break;
         case ASCENDING_WEST:
            this.setDeltaMovement(var14.add(var12, 0.0, 0.0));
            var5++;
            break;
         case ASCENDING_NORTH:
            this.setDeltaMovement(var14.add(0.0, 0.0, var12));
            var5++;
            break;
         case ASCENDING_SOUTH:
            this.setDeltaMovement(var14.add(0.0, 0.0, -var12));
            var5++;
      }

      var14 = this.getDeltaMovement();
      Pair var16 = AbstractMinecart.exits(var15);
      Vec3i var17 = (Vec3i)var16.getFirst();
      Vec3i var18 = (Vec3i)var16.getSecond();
      double var19 = (double)(var18.getX() - var17.getX());
      double var21 = (double)(var18.getZ() - var17.getZ());
      double var23 = Math.sqrt(var19 * var19 + var21 * var21);
      double var25 = var14.x * var19 + var14.z * var21;
      if (var25 < 0.0) {
         var19 = -var19;
         var21 = -var21;
      }

      double var27 = Math.min(2.0, var14.horizontalDistance());
      var14 = new Vec3(var27 * var19 / var23, var14.y, var27 * var21 / var23);
      this.setDeltaMovement(var14);
      Entity var29 = this.minecart.getFirstPassenger();
      Vec3 var30;
      if (this.minecart.getFirstPassenger() instanceof ServerPlayer var31) {
         var30 = var31.getLastClientMoveIntent();
      } else {
         var30 = Vec3.ZERO;
      }

      if (var29 instanceof Player && var30.lengthSqr() > 0.0) {
         Vec3 var64 = var30.normalize();
         double var67 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var64.lengthSqr() > 0.0 && var67 < 0.01) {
            this.setDeltaMovement(this.getDeltaMovement().add(var30.x * 0.001, 0.0, var30.z * 0.001));
            var11 = false;
         }
      }

      if (var11) {
         double var65 = this.getDeltaMovement().horizontalDistance();
         if (var65 < 0.03) {
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.0, 0.5));
         }
      }

      double var66 = (double)var1.getX() + 0.5 + (double)var17.getX() * 0.5;
      double var33 = (double)var1.getZ() + 0.5 + (double)var17.getZ() * 0.5;
      double var35 = (double)var1.getX() + 0.5 + (double)var18.getX() * 0.5;
      double var37 = (double)var1.getZ() + 0.5 + (double)var18.getZ() * 0.5;
      var19 = var35 - var66;
      var21 = var37 - var33;
      double var39;
      if (var19 == 0.0) {
         var39 = var7 - (double)var1.getZ();
      } else if (var21 == 0.0) {
         var39 = var3 - (double)var1.getX();
      } else {
         double var41 = var3 - var66;
         double var43 = var7 - var33;
         var39 = (var41 * var19 + var43 * var21) * 2.0;
      }

      var3 = var66 + var19 * var39;
      var7 = var33 + var21 * var39;
      this.setPos(var3, var5, var7);
      double var68 = this.minecart.isVehicle() ? 0.75 : 1.0;
      double var69 = this.minecart.getMaxSpeed();
      var14 = this.getDeltaMovement();
      this.minecart.move(MoverType.SELF, new Vec3(Mth.clamp(var68 * var14.x, -var69, var69), 0.0, Mth.clamp(var68 * var14.z, -var69, var69)));
      if (var17.getY() != 0 && Mth.floor(this.minecart.getX()) - var1.getX() == var17.getX() && Mth.floor(this.minecart.getZ()) - var1.getZ() == var17.getZ()) {
         this.setPos(this.minecart.getX(), this.minecart.getY() + (double)var17.getY(), this.minecart.getZ());
      } else if (var18.getY() != 0
         && Mth.floor(this.minecart.getX()) - var1.getX() == var18.getX()
         && Mth.floor(this.minecart.getZ()) - var1.getZ() == var18.getZ()) {
         this.setPos(this.minecart.getX(), this.minecart.getY() + (double)var18.getY(), this.minecart.getZ());
      }

      this.setDeltaMovement(this.minecart.applyNaturalSlowdown(this.getDeltaMovement()));
      Vec3 var45 = this.getPos(this.minecart.getX(), this.minecart.getY(), this.minecart.getZ());
      if (var45 != null && var9 != null) {
         double var46 = (var9.y - var45.y) * 0.05;
         Vec3 var48 = this.getDeltaMovement();
         double var49 = var48.horizontalDistance();
         if (var49 > 0.0) {
            this.setDeltaMovement(var48.multiply((var49 + var46) / var49, 1.0, (var49 + var46) / var49));
         }

         this.setPos(this.minecart.getX(), var45.y, this.minecart.getZ());
      }

      int var70 = Mth.floor(this.minecart.getX());
      int var47 = Mth.floor(this.minecart.getZ());
      if (var70 != var1.getX() || var47 != var1.getZ()) {
         Vec3 var71 = this.getDeltaMovement();
         double var73 = var71.horizontalDistance();
         this.setDeltaMovement(var73 * (double)(var70 - var1.getX()), var71.y, var73 * (double)(var47 - var1.getZ()));
      }

      if (var10) {
         Vec3 var72 = this.getDeltaMovement();
         double var74 = var72.horizontalDistance();
         if (var74 > 0.01) {
            double var51 = 0.06;
            this.setDeltaMovement(var72.add(var72.x / var74 * 0.06, 0.0, var72.z / var74 * 0.06));
         } else {
            Vec3 var75 = this.getDeltaMovement();
            double var52 = var75.x;
            double var54 = var75.z;
            if (var15 == RailShape.EAST_WEST) {
               if (this.minecart.isRedstoneConductor(var1.west())) {
                  var52 = 0.02;
               } else if (this.minecart.isRedstoneConductor(var1.east())) {
                  var52 = -0.02;
               }
            } else {
               if (var15 != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.minecart.isRedstoneConductor(var1.north())) {
                  var54 = 0.02;
               } else if (this.minecart.isRedstoneConductor(var1.south())) {
                  var54 = -0.02;
               }
            }

            this.setDeltaMovement(var52, var75.y, var54);
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
      if (this.minecart.getMinecartType() == AbstractMinecart.Type.RIDEABLE && this.getDeltaMovement().horizontalDistanceSqr() >= 0.01) {
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
      double var2 = this.minecart.getMaxSpeed();
      return new Vec3(Mth.clamp(var1.x, -var2, var2), var1.y, Mth.clamp(var1.z, -var2, var2));
   }

   @Override
   public double getMaxSpeed() {
      return (this.minecart.isInWater() ? 4.0 : 8.0) / 20.0;
   }

   @Override
   public double getSlowdownFactor() {
      return this.minecart.isVehicle() ? 0.997 : 0.96;
   }
}
