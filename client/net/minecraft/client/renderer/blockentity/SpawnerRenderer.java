package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer implements BlockEntityRenderer<SpawnerBlockEntity> {
   private final EntityRenderDispatcher entityRenderer;

   public SpawnerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.entityRenderer = var1.getEntityRenderer();
   }

   public void render(SpawnerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      Level var7 = var1.getLevel();
      if (var7 != null) {
         BaseSpawner var8 = var1.getSpawner();
         Entity var9 = var8.getOrCreateDisplayEntity(var7, var1.getBlockPos());
         if (var9 != null) {
            renderEntityInSpawner(var2, var3, var4, var5, var9, this.entityRenderer, var8.getoSpin(), var8.getSpin());
         }

      }
   }

   public static void renderEntityInSpawner(float var0, PoseStack var1, MultiBufferSource var2, int var3, Entity var4, EntityRenderDispatcher var5, double var6, double var8) {
      var1.pushPose();
      var1.translate(0.5F, 0.0F, 0.5F);
      float var10 = 0.53125F;
      float var11 = Math.max(var4.getBbWidth(), var4.getBbHeight());
      if ((double)var11 > 1.0) {
         var10 /= var11;
      }

      var1.translate(0.0F, 0.4F, 0.0F);
      var1.mulPose(Axis.YP.rotationDegrees((float)Mth.lerp((double)var0, var6, var8) * 10.0F));
      var1.translate(0.0F, -0.2F, 0.0F);
      var1.mulPose(Axis.XP.rotationDegrees(-30.0F));
      var1.scale(var10, var10, var10);
      var5.render(var4, 0.0, 0.0, 0.0, var0, var1, var2, var3);
      var1.popPose();
   }
}
