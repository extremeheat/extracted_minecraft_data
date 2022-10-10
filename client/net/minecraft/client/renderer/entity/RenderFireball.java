package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball extends Render<EntityFireball> {
   private final float field_77002_a;

   public RenderFireball(RenderManager var1, float var2) {
      super(var1);
      this.field_77002_a = var2;
   }

   public void func_76986_a(EntityFireball var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      this.func_180548_c(var1);
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(this.field_77002_a, this.field_77002_a, this.field_77002_a);
      TextureAtlasSprite var10 = Minecraft.func_71410_x().func_175599_af().func_175037_a().func_199934_a(Items.field_151059_bz);
      Tessellator var11 = Tessellator.func_178181_a();
      BufferBuilder var12 = var11.func_178180_c();
      float var13 = var10.func_94209_e();
      float var14 = var10.func_94212_f();
      float var15 = var10.func_94206_g();
      float var16 = var10.func_94210_h();
      float var17 = 1.0F;
      float var18 = 0.5F;
      float var19 = 0.25F;
      GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b((float)(this.field_76990_c.field_78733_k.field_74320_O == 2 ? -1 : 1) * -this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      var12.func_181668_a(7, DefaultVertexFormats.field_181710_j);
      var12.func_181662_b(-0.5D, -0.25D, 0.0D).func_187315_a((double)var13, (double)var16).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var12.func_181662_b(0.5D, -0.25D, 0.0D).func_187315_a((double)var14, (double)var16).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var12.func_181662_b(0.5D, 0.75D, 0.0D).func_187315_a((double)var14, (double)var15).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var12.func_181662_b(-0.5D, 0.75D, 0.0D).func_187315_a((double)var13, (double)var15).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_78381_a();
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityFireball var1) {
      return TextureMap.field_110575_b;
   }
}
