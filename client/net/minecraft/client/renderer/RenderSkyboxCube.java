package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderSkyboxCube {
   private final ResourceLocation[] field_209143_a = new ResourceLocation[6];

   public RenderSkyboxCube(ResourceLocation var1) {
      super();

      for(int var2 = 0; var2 < 6; ++var2) {
         this.field_209143_a[var2] = new ResourceLocation(var1.func_110624_b(), var1.func_110623_a() + '_' + var2 + ".png");
      }

   }

   public void func_209142_a(Minecraft var1, float var2, float var3) {
      Tessellator var4 = Tessellator.func_178181_a();
      BufferBuilder var5 = var4.func_178180_c();
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179094_E();
      GlStateManager.func_179096_D();
      GlStateManager.func_199294_a(Matrix4f.func_195876_a(85.0D, (float)var1.field_195558_d.func_198109_k() / (float)var1.field_195558_d.func_198091_l(), 0.05F, 10.0F));
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179094_E();
      GlStateManager.func_179096_D();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_179129_p();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      boolean var6 = true;

      for(int var7 = 0; var7 < 4; ++var7) {
         GlStateManager.func_179094_E();
         float var8 = ((float)(var7 % 2) / 2.0F - 0.5F) / 256.0F;
         float var9 = ((float)(var7 / 2) / 2.0F - 0.5F) / 256.0F;
         float var10 = 0.0F;
         GlStateManager.func_179109_b(var8, var9, 0.0F);
         GlStateManager.func_179114_b(var2, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(var3, 0.0F, 1.0F, 0.0F);

         for(int var11 = 0; var11 < 6; ++var11) {
            var1.func_110434_K().func_110577_a(this.field_209143_a[var11]);
            var5.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            int var12 = 255 / (var7 + 1);
            if (var11 == 0) {
               var5.func_181662_b(-1.0D, -1.0D, 1.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, 1.0D, 1.0D).func_187315_a(0.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, 1.0D, 1.0D).func_187315_a(1.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, -1.0D, 1.0D).func_187315_a(1.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
            }

            if (var11 == 1) {
               var5.func_181662_b(1.0D, -1.0D, 1.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, 1.0D, 1.0D).func_187315_a(0.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, 1.0D, -1.0D).func_187315_a(1.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, -1.0D, -1.0D).func_187315_a(1.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
            }

            if (var11 == 2) {
               var5.func_181662_b(1.0D, -1.0D, -1.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, 1.0D, -1.0D).func_187315_a(0.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, 1.0D, -1.0D).func_187315_a(1.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, -1.0D, -1.0D).func_187315_a(1.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
            }

            if (var11 == 3) {
               var5.func_181662_b(-1.0D, -1.0D, -1.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, 1.0D, -1.0D).func_187315_a(0.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, 1.0D, 1.0D).func_187315_a(1.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, -1.0D, 1.0D).func_187315_a(1.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
            }

            if (var11 == 4) {
               var5.func_181662_b(-1.0D, -1.0D, -1.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, -1.0D, 1.0D).func_187315_a(0.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, -1.0D, 1.0D).func_187315_a(1.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, -1.0D, -1.0D).func_187315_a(1.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
            }

            if (var11 == 5) {
               var5.func_181662_b(-1.0D, 1.0D, 1.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(-1.0D, 1.0D, -1.0D).func_187315_a(0.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, 1.0D, -1.0D).func_187315_a(1.0D, 1.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
               var5.func_181662_b(1.0D, 1.0D, 1.0D).func_187315_a(1.0D, 0.0D).func_181669_b(255, 255, 255, var12).func_181675_d();
            }

            var4.func_78381_a();
         }

         GlStateManager.func_179121_F();
         GlStateManager.func_179135_a(true, true, true, false);
      }

      var5.func_178969_c(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179135_a(true, true, true, true);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179121_F();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179121_F();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179089_o();
      GlStateManager.func_179126_j();
   }
}
