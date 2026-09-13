package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import org.lwjgl.opengl.GL11;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer {
   public TileEntityMobSpawnerRenderer() {
      super();
   }

   public void func_147500_a(TileEntityMobSpawner var1, double var2, double var4, double var6, float var8) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2 + 0.5F, (float)var4, (float)var6 + 0.5F);
      func_147517_a(var1.func_145881_a(), var2, var4, var6, var8);
      GL11.glPopMatrix();
   }

   public static void func_147517_a(MobSpawnerBaseLogic var0, double var1, double var3, double var5, float var7) {
      Entity var8 = var0.func_98281_h();
      if (var8 != null) {
         var8.func_70029_a(var0.func_98271_a());
         float var9 = 0.4375F;
         GL11.glTranslatef(0.0F, 0.4F, 0.0F);
         GL11.glRotatef((float)(var0.field_98284_d + (var0.field_98287_c - var0.field_98284_d) * (double)var7) * 10.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslatef(0.0F, -0.4F, 0.0F);
         GL11.glScalef(var9, var9, var9);
         var8.func_70012_b(var1, var3, var5, 0.0F, 0.0F);
         RenderManager.field_78727_a.func_147940_a(var8, 0.0, 0.0, 0.0, 0.0F, var7);
      }
   }
}
