package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderXPOrb extends Render<EntityXPOrb> {
   private static final ResourceLocation field_110785_a = new ResourceLocation("textures/entity/experience_orb.png");

   public RenderXPOrb(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.15F;
      this.field_76987_f = 0.75F;
   }

   public void func_76986_a(EntityXPOrb var1, double var2, double var4, double var6, float var8, float var9) {
      if (!this.field_188301_f) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
         this.func_180548_c(var1);
         RenderHelper.func_74519_b();
         int var10 = var1.func_70528_g();
         float var11 = (float)(var10 % 4 * 16 + 0) / 64.0F;
         float var12 = (float)(var10 % 4 * 16 + 16) / 64.0F;
         float var13 = (float)(var10 / 4 * 16 + 0) / 64.0F;
         float var14 = (float)(var10 / 4 * 16 + 16) / 64.0F;
         float var15 = 1.0F;
         float var16 = 0.5F;
         float var17 = 0.25F;
         int var18 = var1.func_70070_b();
         int var19 = var18 % 65536;
         int var20 = var18 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var19, (float)var20);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         float var21 = 255.0F;
         float var22 = ((float)var1.field_70533_a + var9) / 2.0F;
         int var23 = (int)((MathHelper.func_76126_a(var22 + 0.0F) + 1.0F) * 0.5F * 255.0F);
         boolean var24 = true;
         int var25 = (int)((MathHelper.func_76126_a(var22 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
         GlStateManager.func_179109_b(0.0F, 0.1F, 0.0F);
         GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b((float)(this.field_76990_c.field_78733_k.field_74320_O == 2 ? -1 : 1) * -this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
         float var26 = 0.3F;
         GlStateManager.func_179152_a(0.3F, 0.3F, 0.3F);
         Tessellator var27 = Tessellator.func_178181_a();
         BufferBuilder var28 = var27.func_178180_c();
         var28.func_181668_a(7, DefaultVertexFormats.field_181712_l);
         var28.func_181662_b(-0.5D, -0.25D, 0.0D).func_187315_a((double)var11, (double)var14).func_181669_b(var23, 255, var25, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var28.func_181662_b(0.5D, -0.25D, 0.0D).func_187315_a((double)var12, (double)var14).func_181669_b(var23, 255, var25, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var28.func_181662_b(0.5D, 0.75D, 0.0D).func_187315_a((double)var12, (double)var13).func_181669_b(var23, 255, var25, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var28.func_181662_b(-0.5D, 0.75D, 0.0D).func_187315_a((double)var11, (double)var13).func_181669_b(var23, 255, var25, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var27.func_78381_a();
         GlStateManager.func_179084_k();
         GlStateManager.func_179101_C();
         GlStateManager.func_179121_F();
         super.func_76986_a(var1, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation func_110775_a(EntityXPOrb var1) {
      return field_110785_a;
   }
}
