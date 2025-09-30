package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle extends Particle {
   protected static final int LIFE_TIME = 3;
   private final Entity target;
   protected int life;
   protected final EntityRenderState itemRenderState;
   protected double targetX;
   protected double targetY;
   protected double targetZ;
   protected double targetXOld;
   protected double targetYOld;
   protected double targetZOld;

   public ItemPickupParticle(ClientLevel var1, EntityRenderState var2, Entity var3, Vec3 var4) {
      super(var1, var2.x, var2.y, var2.z, var4.x, var4.y, var4.z);
      this.target = var3;
      this.itemRenderState = var2;
      this.itemRenderState.outlineColor = 0;
      this.updatePosition();
      this.saveOldPosition();
   }

   public void tick() {
      ++this.life;
      if (this.life == 3) {
         this.remove();
      }

      this.saveOldPosition();
      this.updatePosition();
   }

   public ParticleRenderType getGroup() {
      return ParticleRenderType.ITEM_PICKUP;
   }

   private void updatePosition() {
      this.targetX = this.target.getX();
      this.targetY = (this.target.getY() + this.target.getEyeY()) / 2.0;
      this.targetZ = this.target.getZ();
   }

   private void saveOldPosition() {
      this.targetXOld = this.targetX;
      this.targetYOld = this.targetY;
      this.targetZOld = this.targetZ;
   }
}
