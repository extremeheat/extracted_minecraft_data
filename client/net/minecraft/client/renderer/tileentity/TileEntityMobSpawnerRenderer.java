package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class TileEntityMobSpawnerRenderer extends TileEntityRenderer<TileEntityMobSpawner> {
   public TileEntityMobSpawnerRenderer() {
      super();
   }

   public void func_199341_a(TileEntityMobSpawner var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4, (float)var6 + 0.5F);
      func_147517_a(var1.func_145881_a(), var2, var4, var6, var8);
      GlStateManager.func_179121_F();
   }

   public static void func_147517_a(MobSpawnerBaseLogic var0, double var1, double var3, double var5, float var7) {
      Entity var8 = var0.func_184994_d();
      if (var8 != null) {
         float var9 = 0.53125F;
         float var10 = Math.max(var8.field_70130_N, var8.field_70131_O);
         if ((double)var10 > 1.0D) {
            var9 /= var10;
         }

         GlStateManager.func_179109_b(0.0F, 0.4F, 0.0F);
         GlStateManager.func_179114_b((float)(var0.func_177223_e() + (var0.func_177222_d() - var0.func_177223_e()) * (double)var7) * 10.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(0.0F, -0.2F, 0.0F);
         GlStateManager.func_179114_b(-30.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179152_a(var9, var9, var9);
         var8.func_70012_b(var1, var3, var5, 0.0F, 0.0F);
         Minecraft.func_71410_x().func_175598_ae().func_188391_a(var8, 0.0D, 0.0D, 0.0D, 0.0F, var7, false);
      }

   }
}
