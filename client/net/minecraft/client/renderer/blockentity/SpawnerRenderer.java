package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer extends BlockEntityRenderer<SpawnerBlockEntity> {
   public SpawnerRenderer() {
      super();
   }

   public void render(SpawnerBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2 + 0.5F, (float)var4, (float)var6 + 0.5F);
      render(var1.getSpawner(), var2, var4, var6, var8);
      GlStateManager.popMatrix();
   }

   public static void render(BaseSpawner var0, double var1, double var3, double var5, float var7) {
      Entity var8 = var0.getOrCreateDisplayEntity();
      if (var8 != null) {
         float var9 = 0.53125F;
         float var10 = Math.max(var8.getBbWidth(), var8.getBbHeight());
         if ((double)var10 > 1.0D) {
            var9 /= var10;
         }

         GlStateManager.translatef(0.0F, 0.4F, 0.0F);
         GlStateManager.rotatef((float)Mth.lerp((double)var7, var0.getoSpin(), var0.getSpin()) * 10.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.2F, 0.0F);
         GlStateManager.rotatef(-30.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.scalef(var9, var9, var9);
         var8.moveTo(var1, var3, var5, 0.0F, 0.0F);
         Minecraft.getInstance().getEntityRenderDispatcher().render(var8, 0.0D, 0.0D, 0.0D, 0.0F, var7, false);
      }

   }
}
