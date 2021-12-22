package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;

public class RemotePlayer extends AbstractClientPlayer {
   public RemotePlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
      this.maxUpStep = 1.0F;
      this.noPhysics = true;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 *= 64.0D * getViewScale();
      return var1 < var3 * var3;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return true;
   }

   public void tick() {
      super.tick();
      this.calculateEntityAnimation(this, false);
   }

   public void aiStep() {
      if (this.lerpSteps > 0) {
         double var1 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
         double var3 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
         double var5 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
         this.setYRot(this.getYRot() + (float)Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot()) / (float)this.lerpSteps);
         this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
         --this.lerpSteps;
         this.setPos(var1, var3, var5);
         this.setRot(this.getYRot(), this.getXRot());
      }

      if (this.lerpHeadSteps > 0) {
         this.yHeadRot = (float)((double)this.yHeadRot + Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
         --this.lerpHeadSteps;
      }

      this.oBob = this.bob;
      this.updateSwingTime();
      float var7;
      if (this.onGround && !this.isDeadOrDying()) {
         var7 = (float)Math.min(0.1D, this.getDeltaMovement().horizontalDistance());
      } else {
         var7 = 0.0F;
      }

      this.bob += (var7 - this.bob) * 0.4F;
      this.level.getProfiler().push("push");
      this.pushEntities();
      this.level.getProfiler().pop();
   }

   protected void updatePlayerPose() {
   }

   public void sendMessage(Component var1, UUID var2) {
      Minecraft var3 = Minecraft.getInstance();
      if (!var3.isBlocked(var2)) {
         var3.gui.getChat().addMessage(var1);
      }

   }
}
