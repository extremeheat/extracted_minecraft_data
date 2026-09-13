package net.minecraft.client.gui;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData$MapCoord;
import org.lwjgl.opengl.GL11;

class MapItemRenderer$Instance {
   private final MapData field_148242_b;
   private final DynamicTexture field_148243_c;
   private final ResourceLocation field_148240_d;
   private final int[] field_148241_e;

   private MapItemRenderer$Instance(MapItemRenderer var1, MapData var2) {
      super();
      this.field_148244_a = var1;
      this.field_148242_b = var2;
      this.field_148243_c = new DynamicTexture(128, 128);
      this.field_148241_e = this.field_148243_c.func_110565_c();
      this.field_148240_d = MapItemRenderer.access$400(var1).func_110578_a("map/" + var2.field_76190_i, this.field_148243_c);

      for(int var3 = 0; var3 < this.field_148241_e.length; ++var3) {
         this.field_148241_e[var3] = 0;
      }
   }

   private void func_148236_a() {
      for(int var1 = 0; var1 < 16384; ++var1) {
         int var2 = this.field_148242_b.field_76198_e[var1] & 255;
         if (var2 / 4 == 0) {
            this.field_148241_e[var1] = (var1 + var1 / 128 & 1) * 8 + 16 << 24;
         } else {
            this.field_148241_e[var1] = MapColor.field_76281_a[var2 / 4].func_151643_b(var2 & 3);
         }
      }

      this.field_148243_c.func_110564_a();
   }

   private void func_148237_a(boolean var1) {
      byte var2 = 0;
      byte var3 = 0;
      Tessellator var4 = Tessellator.field_78398_a;
      float var5 = 0.0F;
      MapItemRenderer.access$400(this.field_148244_a).func_110577_a(this.field_148240_d);
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(1, 771, 0, 1);
      GL11.glDisable(3008);
      var4.func_78382_b();
      var4.func_78374_a((double)((float)(var2 + 0) + var5), (double)((float)(var3 + 128) - var5), -0.009999999776482582, 0.0, 1.0);
      var4.func_78374_a((double)((float)(var2 + 128) - var5), (double)((float)(var3 + 128) - var5), -0.009999999776482582, 1.0, 1.0);
      var4.func_78374_a((double)((float)(var2 + 128) - var5), (double)((float)(var3 + 0) + var5), -0.009999999776482582, 1.0, 0.0);
      var4.func_78374_a((double)((float)(var2 + 0) + var5), (double)((float)(var3 + 0) + var5), -0.009999999776482582, 0.0, 0.0);
      var4.func_78381_a();
      GL11.glEnable(3008);
      GL11.glDisable(3042);
      MapItemRenderer.access$400(this.field_148244_a).func_110577_a(MapItemRenderer.access$500());
      int var6 = 0;

      for(MapData$MapCoord var8 : this.field_148242_b.field_76203_h.values()) {
         if (!var1 || var8.field_76216_a == 1) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)var2 + (float)var8.field_76214_b / 2.0F + 64.0F, (float)var3 + (float)var8.field_76215_c / 2.0F + 64.0F, -0.02F);
            GL11.glRotatef((float)(var8.field_76212_d * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(4.0F, 4.0F, 3.0F);
            GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
            float var9 = (float)(var8.field_76216_a % 4 + 0) / 4.0F;
            float var10 = (float)(var8.field_76216_a / 4 + 0) / 4.0F;
            float var11 = (float)(var8.field_76216_a % 4 + 1) / 4.0F;
            float var12 = (float)(var8.field_76216_a / 4 + 1) / 4.0F;
            var4.func_78382_b();
            var4.func_78374_a(-1.0, 1.0, (double)((float)var6 * 0.001F), (double)var9, (double)var10);
            var4.func_78374_a(1.0, 1.0, (double)((float)var6 * 0.001F), (double)var11, (double)var10);
            var4.func_78374_a(1.0, -1.0, (double)((float)var6 * 0.001F), (double)var11, (double)var12);
            var4.func_78374_a(-1.0, -1.0, (double)((float)var6 * 0.001F), (double)var9, (double)var12);
            var4.func_78381_a();
            GL11.glPopMatrix();
            ++var6;
         }
      }

      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.0F, -0.04F);
      GL11.glScalef(1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }
}
