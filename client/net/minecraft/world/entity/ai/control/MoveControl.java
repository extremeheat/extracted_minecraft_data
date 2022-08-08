package net.minecraft.world.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoveControl implements Control {
   public static final float MIN_SPEED = 5.0E-4F;
   public static final float MIN_SPEED_SQR = 2.5000003E-7F;
   protected static final int MAX_TURN = 90;
   protected final Mob mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected double speedModifier;
   protected float strafeForwards;
   protected float strafeRight;
   protected Operation operation;

   public MoveControl(Mob var1) {
      super();
      this.operation = MoveControl.Operation.WAIT;
      this.mob = var1;
   }

   public boolean hasWanted() {
      return this.operation == MoveControl.Operation.MOVE_TO;
   }

   public double getSpeedModifier() {
      return this.speedModifier;
   }

   public void setWantedPosition(double var1, double var3, double var5, double var7) {
      this.wantedX = var1;
      this.wantedY = var3;
      this.wantedZ = var5;
      this.speedModifier = var7;
      if (this.operation != MoveControl.Operation.JUMPING) {
         this.operation = MoveControl.Operation.MOVE_TO;
      }

   }

   public void strafe(float var1, float var2) {
      this.operation = MoveControl.Operation.STRAFE;
      this.strafeForwards = var1;
      this.strafeRight = var2;
      this.speedModifier = 0.25;
   }

   public void tick() {
      float var9;
      if (this.operation == MoveControl.Operation.STRAFE) {
         float var1 = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
         float var2 = (float)this.speedModifier * var1;
         float var3 = this.strafeForwards;
         float var4 = this.strafeRight;
         float var5 = Mth.sqrt(var3 * var3 + var4 * var4);
         if (var5 < 1.0F) {
            var5 = 1.0F;
         }

         var5 = var2 / var5;
         var3 *= var5;
         var4 *= var5;
         float var6 = Mth.sin(this.mob.getYRot() * 0.017453292F);
         float var7 = Mth.cos(this.mob.getYRot() * 0.017453292F);
         float var8 = var3 * var7 - var4 * var6;
         var9 = var4 * var7 + var3 * var6;
         if (!this.isWalkable(var8, var9)) {
            this.strafeForwards = 1.0F;
            this.strafeRight = 0.0F;
         }

         this.mob.setSpeed(var2);
         this.mob.setZza(this.strafeForwards);
         this.mob.setXxa(this.strafeRight);
         this.operation = MoveControl.Operation.WAIT;
      } else if (this.operation == MoveControl.Operation.MOVE_TO) {
         this.operation = MoveControl.Operation.WAIT;
         double var13 = this.wantedX - this.mob.getX();
         double var14 = this.wantedZ - this.mob.getZ();
         double var15 = this.wantedY - this.mob.getY();
         double var16 = var13 * var13 + var15 * var15 + var14 * var14;
         if (var16 < 2.500000277905201E-7) {
            this.mob.setZza(0.0F);
            return;
         }

         var9 = (float)(Mth.atan2(var14, var13) * 57.2957763671875) - 90.0F;
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), var9, 90.0F));
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         BlockPos var10 = this.mob.blockPosition();
         BlockState var11 = this.mob.level.getBlockState(var10);
         VoxelShape var12 = var11.getCollisionShape(this.mob.level, var10);
         if (var15 > (double)this.mob.maxUpStep && var13 * var13 + var14 * var14 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !var12.isEmpty() && this.mob.getY() < var12.max(Direction.Axis.Y) + (double)var10.getY() && !var11.is(BlockTags.DOORS) && !var11.is(BlockTags.FENCES)) {
            this.mob.getJumpControl().jump();
            this.operation = MoveControl.Operation.JUMPING;
         }
      } else if (this.operation == MoveControl.Operation.JUMPING) {
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         if (this.mob.isOnGround()) {
            this.operation = MoveControl.Operation.WAIT;
         }
      } else {
         this.mob.setZza(0.0F);
      }

   }

   private boolean isWalkable(float var1, float var2) {
      PathNavigation var3 = this.mob.getNavigation();
      if (var3 != null) {
         NodeEvaluator var4 = var3.getNodeEvaluator();
         if (var4 != null && var4.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double)var1), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double)var2)) != BlockPathTypes.WALKABLE) {
            return false;
         }
      }

      return true;
   }

   protected float rotlerp(float var1, float var2, float var3) {
      float var4 = Mth.wrapDegrees(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      float var5 = var1 + var4;
      if (var5 < 0.0F) {
         var5 += 360.0F;
      } else if (var5 > 360.0F) {
         var5 -= 360.0F;
      }

      return var5;
   }

   public double getWantedX() {
      return this.wantedX;
   }

   public double getWantedY() {
      return this.wantedY;
   }

   public double getWantedZ() {
      return this.wantedZ;
   }

   protected static enum Operation {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;

      private Operation() {
      }

      // $FF: synthetic method
      private static Operation[] $values() {
         return new Operation[]{WAIT, MOVE_TO, STRAFE, JUMPING};
      }
   }
}
