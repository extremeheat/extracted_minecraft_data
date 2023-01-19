package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer implements BlockEntityRenderer<SpawnerBlockEntity> {
   private final EntityRenderDispatcher entityRenderer;

   public SpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.getEntityRenderer();
   }

   public void render(SpawnerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      var3.pushPose();
      var3.translate(0.5F, 0.0F, 0.5F);
      BaseSpawner var7 = var1.getSpawner();
      Entity var8 = var7.getOrCreateDisplayEntity(var1.getLevel(), var1.getLevel().getRandom(), var1.getBlockPos());
      if (var8 != null) {
         float var9 = 0.53125F;
         float var10 = Math.max(var8.getBbWidth(), var8.getBbHeight());
         if ((double)var10 > 1.0) {
            var9 /= var10;
         }

         var3.translate(0.0F, 0.4F, 0.0F);
         var3.mulPose(Axis.YP.rotationDegrees((float)Mth.lerp((double)var2, var7.getoSpin(), var7.getSpin()) * 10.0F));
         var3.translate(0.0F, -0.2F, 0.0F);
         var3.mulPose(Axis.XP.rotationDegrees(-30.0F));
         var3.scale(var9, var9, var9);
         this.entityRenderer.render(var8, 0.0, 0.0, 0.0, 0.0F, var2, var3, var4, var5);
      }

      var3.popPose();
   }
}
