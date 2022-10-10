package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderTNTPrimed extends Render<EntityTNTPrimed> {
   public RenderTNTPrimed(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityTNTPrimed var1, double var2, double var4, double var6, float var8, float var9) {
      BlockRendererDispatcher var10 = Minecraft.func_71410_x().func_175602_ab();
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4 + 0.5F, (float)var6);
      float var11;
      if ((float)var1.func_184536_l() - var9 + 1.0F < 10.0F) {
         var11 = 1.0F - ((float)var1.func_184536_l() - var9 + 1.0F) / 10.0F;
         var11 = MathHelper.func_76131_a(var11, 0.0F, 1.0F);
         var11 *= var11;
         var11 *= var11;
         float var12 = 1.0F + var11 * 0.3F;
         GlStateManager.func_179152_a(var12, var12, var12);
      }

      var11 = (1.0F - ((float)var1.func_184536_l() - var9 + 1.0F) / 100.0F) * 0.8F;
      this.func_180548_c(var1);
      GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(-0.5F, -0.5F, 0.5F);
      var10.func_175016_a(Blocks.field_150335_W.func_176223_P(), var1.func_70013_c());
      GlStateManager.func_179109_b(0.0F, 0.0F, 1.0F);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
         var10.func_175016_a(Blocks.field_150335_W.func_176223_P(), 1.0F);
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      } else if (var1.func_184536_l() / 5 % 2 == 0) {
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         GlStateManager.func_179147_l();
         GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var11);
         GlStateManager.func_179136_a(-3.0F, -3.0F);
         GlStateManager.func_179088_q();
         var10.func_175016_a(Blocks.field_150335_W.func_176223_P(), 1.0F);
         GlStateManager.func_179136_a(0.0F, 0.0F);
         GlStateManager.func_179113_r();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179084_k();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
      }

      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityTNTPrimed var1) {
      return TextureMap.field_110575_b;
   }
}
