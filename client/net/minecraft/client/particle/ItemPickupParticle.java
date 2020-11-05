package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle extends Particle {
   private final RenderBuffers renderBuffers;
   private final Entity itemEntity;
   private final Entity target;
   private int life;
   private final EntityRenderDispatcher entityRenderDispatcher;

   public ItemPickupParticle(EntityRenderDispatcher var1, RenderBuffers var2, ClientLevel var3, Entity var4, Entity var5) {
      this(var1, var2, var3, var4, var5, var4.getDeltaMovement());
   }

   private ItemPickupParticle(EntityRenderDispatcher var1, RenderBuffers var2, ClientLevel var3, Entity var4, Entity var5, Vec3 var6) {
      super(var3, var4.getX(), var4.getY(), var4.getZ(), var6.x, var6.y, var6.z);
      this.renderBuffers = var2;
      this.itemEntity = this.getSafeCopy(var4);
      this.target = var5;
      this.entityRenderDispatcher = var1;
   }

   private Entity getSafeCopy(Entity var1) {
      return (Entity)(!(var1 instanceof ItemEntity) ? var1 : ((ItemEntity)var1).copy());
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void render(VertexConsumer var1, Camera var2, float var3) {
      float var4 = ((float)this.life + var3) / 3.0F;
      var4 *= var4;
      double var5 = Mth.lerp((double)var3, this.target.xOld, this.target.getX());
      double var7 = Mth.lerp((double)var3, this.target.yOld, this.target.getY()) + 0.5D;
      double var9 = Mth.lerp((double)var3, this.target.zOld, this.target.getZ());
      double var11 = Mth.lerp((double)var4, this.itemEntity.getX(), var5);
      double var13 = Mth.lerp((double)var4, this.itemEntity.getY(), var7);
      double var15 = Mth.lerp((double)var4, this.itemEntity.getZ(), var9);
      MultiBufferSource.BufferSource var17 = this.renderBuffers.bufferSource();
      Vec3 var18 = var2.getPosition();
      this.entityRenderDispatcher.render(this.itemEntity, var11 - var18.x(), var13 - var18.y(), var15 - var18.z(), this.itemEntity.yRot, var3, new PoseStack(), var17, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, var3));
      var17.endBatch();
   }

   public void tick() {
      ++this.life;
      if (this.life == 3) {
         this.remove();
      }

   }
}
