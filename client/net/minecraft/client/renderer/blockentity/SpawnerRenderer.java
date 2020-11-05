package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer extends BlockEntityRenderer<SpawnerBlockEntity> {
   public SpawnerRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(SpawnerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      var3.pushPose();
      var3.translate(0.5D, 0.0D, 0.5D);
      BaseSpawner var7 = var1.getSpawner();
      Entity var8 = var7.getOrCreateDisplayEntity();
      if (var8 != null) {
         float var9 = 0.53125F;
         float var10 = Math.max(var8.getBbWidth(), var8.getBbHeight());
         if ((double)var10 > 1.0D) {
            var9 /= var10;
         }

         var3.translate(0.0D, 0.4000000059604645D, 0.0D);
         var3.mulPose(Vector3f.YP.rotationDegrees((float)Mth.lerp((double)var2, var7.getoSpin(), var7.getSpin()) * 10.0F));
         var3.translate(0.0D, -0.20000000298023224D, 0.0D);
         var3.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
         var3.scale(var9, var9, var9);
         Minecraft.getInstance().getEntityRenderDispatcher().render(var8, 0.0D, 0.0D, 0.0D, 0.0F, var2, var3, var4, var5);
      }

      var3.popPose();
   }
}
