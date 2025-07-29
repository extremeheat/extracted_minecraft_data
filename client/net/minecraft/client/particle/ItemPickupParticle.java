package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ItemPickupParticle extends Particle {
   private static final int LIFE_TIME = 3;
   private final Entity target;
   private int life;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final EntityRenderState itemRenderState;
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
      this.target = var4;
      this.entityRenderDispatcher = var1;
      this.itemRenderState = var1.extractEntity(var3, 1.0F);
      this.updatePosition();
      this.saveOldPosition();
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
      double var12 = Mth.lerp((double)var5, this.itemRenderState.x, var6);
      double var14 = Mth.lerp((double)var5, this.itemRenderState.y, var8);
      double var16 = Mth.lerp((double)var5, this.itemRenderState.z, var10);
      Vec3 var18 = var3.getPosition();
      this.entityRenderDispatcher.submit(this.itemRenderState, var12 - var18.x(), var14 - var18.y(), var16 - var18.z(), new PoseStack(), Minecraft.getInstance().gameRenderer.getSubmitNodeStorage());
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
