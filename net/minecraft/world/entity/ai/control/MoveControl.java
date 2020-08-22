package net.minecraft.world.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoveControl {
   protected final Mob mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected double speedModifier;
   protected float strafeForwards;
   protected float strafeRight;
   protected MoveControl.Operation operation;

   public MoveControl(Mob var1) {
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
      this.speedModifier = 0.25D;
   }

   public void tick() {
      float var9;
      if (this.operation == MoveControl.Operation.STRAFE) {
         float var1 = (float)this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
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
         float var6 = Mth.sin(this.mob.yRot * 0.017453292F);
         float var7 = Mth.cos(this.mob.yRot * 0.017453292F);
         float var8 = var3 * var7 - var4 * var6;
         var9 = var4 * var7 + var3 * var6;
         PathNavigation var10 = this.mob.getNavigation();
         if (var10 != null) {
            NodeEvaluator var11 = var10.getNodeEvaluator();
            if (var11 != null && var11.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double)var8), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + (double)var9)) != BlockPathTypes.WALKABLE) {
               this.strafeForwards = 1.0F;
               this.strafeRight = 0.0F;
               var2 = var1;
            }
         }

         this.mob.setSpeed(var2);
         this.mob.setZza(this.strafeForwards);
         this.mob.setXxa(this.strafeRight);
         this.operation = MoveControl.Operation.WAIT;
      } else if (this.operation == MoveControl.Operation.MOVE_TO) {
         this.operation = MoveControl.Operation.WAIT;
         double var14 = this.wantedX - this.mob.getX();
         double var15 = this.wantedZ - this.mob.getZ();
         double var16 = this.wantedY - this.mob.getY();
         double var17 = var14 * var14 + var16 * var16 + var15 * var15;
         if (var17 < 2.500000277905201E-7D) {
            this.mob.setZza(0.0F);
            return;
         }

         var9 = (float)(Mth.atan2(var15, var14) * 57.2957763671875D) - 90.0F;
         this.mob.yRot = this.rotlerp(this.mob.yRot, var9, 90.0F);
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
         BlockPos var18 = new BlockPos(this.mob);
         BlockState var19 = this.mob.level.getBlockState(var18);
         Block var12 = var19.getBlock();
         VoxelShape var13 = var19.getCollisionShape(this.mob.level, var18);
         if (var16 > (double)this.mob.maxUpStep && var14 * var14 + var15 * var15 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !var13.isEmpty() && this.mob.getY() < var13.max(Direction.Axis.Y) + (double)var18.getY() && !var12.is(BlockTags.DOORS) && !var12.is(BlockTags.FENCES)) {
            this.mob.getJumpControl().jump();
            this.operation = MoveControl.Operation.JUMPING;
         }
      } else if (this.operation == MoveControl.Operation.JUMPING) {
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
         if (this.mob.onGround) {
            this.operation = MoveControl.Operation.WAIT;
         }
      } else {
         this.mob.setZza(0.0F);
      }

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

   public static enum Operation {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;
   }
}
