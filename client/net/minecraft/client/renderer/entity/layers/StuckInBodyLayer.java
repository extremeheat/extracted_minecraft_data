package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class StuckInBodyLayer<T extends LivingEntity, M extends PlayerModel<T>> extends RenderLayer<T, M> {
   public StuckInBodyLayer(LivingEntityRenderer<T, M> var1) {
      super(var1);
   }

   protected abstract int numStuck(T var1);

   protected abstract void renderStuckItem(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8);

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      int var11 = this.numStuck(var4);
      RandomSource var12 = RandomSource.create((long)var4.getId());
      if (var11 > 0) {
         for(int var13 = 0; var13 < var11; ++var13) {
            var1.pushPose();
            ModelPart var14 = ((PlayerModel)this.getParentModel()).getRandomModelPart(var12);
            ModelPart.Cube var15 = var14.getRandomCube(var12);
            var14.translateAndRotate(var1);
            float var16 = var12.nextFloat();
            float var17 = var12.nextFloat();
            float var18 = var12.nextFloat();
            float var19 = Mth.lerp(var16, var15.minX, var15.maxX) / 16.0F;
            float var20 = Mth.lerp(var17, var15.minY, var15.maxY) / 16.0F;
            float var21 = Mth.lerp(var18, var15.minZ, var15.maxZ) / 16.0F;
            var1.translate(var19, var20, var21);
            var16 = -1.0F * (var16 * 2.0F - 1.0F);
            var17 = -1.0F * (var17 * 2.0F - 1.0F);
            var18 = -1.0F * (var18 * 2.0F - 1.0F);
            this.renderStuckItem(var1, var2, var3, var4, var16, var17, var18, var7);
            var1.popPose();
         }

      }
   }
}
