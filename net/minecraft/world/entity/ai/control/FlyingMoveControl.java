package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;

public class FlyingMoveControl extends MoveControl {
   private final int maxTurn;
   private final boolean hoversInPlace;

   public FlyingMoveControl(Mob var1, int var2, boolean var3) {
      super(var1);
      this.maxTurn = var2;
      this.hoversInPlace = var3;
   }

   public void tick() {
      if (this.operation == MoveControl.Operation.MOVE_TO) {
         this.operation = MoveControl.Operation.WAIT;
         this.mob.setNoGravity(true);
         double var1 = this.wantedX - this.mob.getX();
         double var3 = this.wantedY - this.mob.getY();
         double var5 = this.wantedZ - this.mob.getZ();
         double var7 = var1 * var1 + var3 * var3 + var5 * var5;
         if (var7 < 2.500000277905201E-7D) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
         }

         float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875D) - 90.0F;
         this.mob.yRot = this.rotlerp(this.mob.yRot, var9, 90.0F);
         float var10;
         if (this.mob.onGround) {
            var10 = (float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
         } else {
            var10 = (float)(this.speedModifier * this.mob.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getValue());
         }

         this.mob.setSpeed(var10);
         double var11 = (double)Mth.sqrt(var1 * var1 + var5 * var5);
         float var13 = (float)(-(Mth.atan2(var3, var11) * 57.2957763671875D));
         this.mob.xRot = this.rotlerp(this.mob.xRot, var13, (float)this.maxTurn);
         this.mob.setYya(var3 > 0.0D ? var10 : -var10);
      } else {
         if (!this.hoversInPlace) {
            this.mob.setNoGravity(false);
         }

         this.mob.setYya(0.0F);
         this.mob.setZza(0.0F);
      }

   }
}
