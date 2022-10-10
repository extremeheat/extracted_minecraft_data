package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class DebugRendererChunkBorder implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_190072_a;

   public DebugRendererChunkBorder(Minecraft var1) {
      super();
      this.field_190072_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      EntityPlayerSP var4 = this.field_190072_a.field_71439_g;
      Tessellator var5 = Tessellator.func_178181_a();
      BufferBuilder var6 = var5.func_178180_c();
      double var7 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var9 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var11 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      double var13 = 0.0D - var9;
      double var15 = 256.0D - var9;
      GlStateManager.func_179090_x();
      GlStateManager.func_179084_k();
      double var17 = (double)(var4.field_70176_ah << 4) - var7;
      double var19 = (double)(var4.field_70164_aj << 4) - var11;
      GlStateManager.func_187441_d(1.0F);
      var6.func_181668_a(3, DefaultVertexFormats.field_181706_f);

      int var21;
      int var22;
      for(var21 = -16; var21 <= 32; var21 += 16) {
         for(var22 = -16; var22 <= 32; var22 += 16) {
            var6.func_181662_b(var17 + (double)var21, var13, var19 + (double)var22).func_181666_a(1.0F, 0.0F, 0.0F, 0.0F).func_181675_d();
            var6.func_181662_b(var17 + (double)var21, var13, var19 + (double)var22).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
            var6.func_181662_b(var17 + (double)var21, var15, var19 + (double)var22).func_181666_a(1.0F, 0.0F, 0.0F, 0.5F).func_181675_d();
            var6.func_181662_b(var17 + (double)var21, var15, var19 + (double)var22).func_181666_a(1.0F, 0.0F, 0.0F, 0.0F).func_181675_d();
         }
      }

      for(var21 = 2; var21 < 16; var21 += 2) {
         var6.func_181662_b(var17 + (double)var21, var13, var19).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var13, var19).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var15, var19).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var15, var19).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var13, var19 + 16.0D).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var13, var19 + 16.0D).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var15, var19 + 16.0D).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + (double)var21, var15, var19 + 16.0D).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
      }

      for(var21 = 2; var21 < 16; var21 += 2) {
         var6.func_181662_b(var17, var13, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17, var13, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var15, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var15, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var13, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var13, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var15, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var15, var19 + (double)var21).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
      }

      double var24;
      for(var21 = 0; var21 <= 256; var21 += 2) {
         var24 = (double)var21 - var9;
         var6.func_181662_b(var17, var24, var19).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19 + 16.0D).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var24, var19 + 16.0D).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var24, var19).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19).func_181666_a(1.0F, 1.0F, 0.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19).func_181666_a(1.0F, 1.0F, 0.0F, 0.0F).func_181675_d();
      }

      var5.func_78381_a();
      GlStateManager.func_187441_d(2.0F);
      var6.func_181668_a(3, DefaultVertexFormats.field_181706_f);

      for(var21 = 0; var21 <= 16; var21 += 16) {
         for(var22 = 0; var22 <= 16; var22 += 16) {
            var6.func_181662_b(var17 + (double)var21, var13, var19 + (double)var22).func_181666_a(0.25F, 0.25F, 1.0F, 0.0F).func_181675_d();
            var6.func_181662_b(var17 + (double)var21, var13, var19 + (double)var22).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
            var6.func_181662_b(var17 + (double)var21, var15, var19 + (double)var22).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
            var6.func_181662_b(var17 + (double)var21, var15, var19 + (double)var22).func_181666_a(0.25F, 0.25F, 1.0F, 0.0F).func_181675_d();
         }
      }

      for(var21 = 0; var21 <= 256; var21 += 16) {
         var24 = (double)var21 - var9;
         var6.func_181662_b(var17, var24, var19).func_181666_a(0.25F, 0.25F, 1.0F, 0.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19 + 16.0D).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var24, var19 + 16.0D).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17 + 16.0D, var24, var19).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19).func_181666_a(0.25F, 0.25F, 1.0F, 1.0F).func_181675_d();
         var6.func_181662_b(var17, var24, var19).func_181666_a(0.25F, 0.25F, 1.0F, 0.0F).func_181675_d();
      }

      var5.func_78381_a();
      GlStateManager.func_187441_d(1.0F);
      GlStateManager.func_179147_l();
      GlStateManager.func_179098_w();
   }
}
