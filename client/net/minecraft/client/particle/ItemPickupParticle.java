package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle extends Particle {
   private static final int LIFE_TIME = 3;
   private final Entity itemEntity;
   private final Entity target;
   private int life;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double targetXOld;
   private double targetYOld;
   private double targetZOld;

   public ItemPickupParticle(EntityRenderDispatcher var1, ClientLevel var2, Entity var3, Entity var4) {
      this(var1, var2, var3, var4, var3.getDeltaMovement());
   }

   private ItemPickupParticle(EntityRenderDispatcher var1, ClientLevel var2, Entity var3, Entity var4, Vec3 var5) {
      super(var2, var3.getX(), var3.getY(), var3.getZ(), var5.x, var5.y, var5.z);
      this.itemEntity = this.getSafeCopy(var3);
      this.target = var4;
      this.entityRenderDispatcher = var1;
      this.updatePosition();
      this.saveOldPosition();
   }

   private Entity getSafeCopy(Entity var1) {
      return (Entity)(!(var1 instanceof ItemEntity) ? var1 : ((ItemEntity)var1).copy());
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void renderCustom(PoseStack var1, MultiBufferSource var2, Camera var3, float var4) {
      float var5 = ((float)this.life + var4) / 3.0F;
      var5 *= var5;
      double var6 = Mth.lerp((double)var4, this.targetXOld, this.targetX);
      double var8 = Mth.lerp((double)var4, this.targetYOld, this.targetY);
      double var10 = Mth.lerp((double)var4, this.targetZOld, this.targetZ);
      double var12 = Mth.lerp((double)var5, this.itemEntity.getX(), var6);
      double var14 = Mth.lerp((double)var5, this.itemEntity.getY(), var8);
      double var16 = Mth.lerp((double)var5, this.itemEntity.getZ(), var10);
      Vec3 var18 = var3.getPosition();
      this.entityRenderDispatcher.render(this.itemEntity, var12 - var18.x(), var14 - var18.y(), var16 - var18.z(), var4, new PoseStack(), var2, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, var4));
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
   }

   public void tick() {
      ++this.life;
      if (this.life == 3) {
         this.remove();
      }

      this.saveOldPosition();
      this.updatePosition();
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
