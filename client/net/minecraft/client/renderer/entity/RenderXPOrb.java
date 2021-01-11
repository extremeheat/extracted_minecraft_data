package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderXPOrb extends Render<EntityXPOrb> {
   private static final ResourceLocation field_110785_a = new ResourceLocation("textures/entity/experience_orb.png");

   public RenderXPOrb(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.15F;
      this.field_76987_f = 0.75F;
   }

   public void func_76986_a(EntityXPOrb var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      this.func_180548_c(var1);
      int var10 = var1.func_70528_g();
      float var11 = (float)(var10 % 4 * 16 + 0) / 64.0F;
      float var12 = (float)(var10 % 4 * 16 + 16) / 64.0F;
      float var13 = (float)(var10 / 4 * 16 + 0) / 64.0F;
      float var14 = (float)(var10 / 4 * 16 + 16) / 64.0F;
      float var15 = 1.0F;
      float var16 = 0.5F;
      float var17 = 0.25F;
      int var18 = var1.func_70070_b(var9);
      int var19 = var18 % 65536;
      int var20 = var18 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var19 / 1.0F, (float)var20 / 1.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float var26 = 255.0F;
      float var27 = ((float)var1.field_70533_a + var9) / 2.0F;
      var20 = (int)((MathHelper.func_76126_a(var27 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      boolean var21 = true;
      int var22 = (int)((MathHelper.func_76126_a(var27 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      float var23 = 0.3F;
      GlStateManager.func_179152_a(0.3F, 0.3F, 0.3F);
      Tessellator var24 = Tessellator.func_178181_a();
      WorldRenderer var25 = var24.func_178180_c();
      var25.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      var25.func_181662_b((double)(0.0F - var16), (double)(0.0F - var17), 0.0D).func_181673_a((double)var11, (double)var14).func_181669_b(var20, 255, var22, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var25.func_181662_b((double)(var15 - var16), (double)(0.0F - var17), 0.0D).func_181673_a((double)var12, (double)var14).func_181669_b(var20, 255, var22, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var25.func_181662_b((double)(var15 - var16), (double)(1.0F - var17), 0.0D).func_181673_a((double)var12, (double)var13).func_181669_b(var20, 255, var22, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var25.func_181662_b((double)(0.0F - var16), (double)(1.0F - var17), 0.0D).func_181673_a((double)var11, (double)var13).func_181669_b(var20, 255, var22, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var24.func_78381_a();
      GlStateManager.func_179084_k();
      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityXPOrb var1) {
      return field_110785_a;
   }
}
