package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.util.ResourceLocation;

public class RenderDragonFireball extends Render<EntityDragonFireball> {
   private static final ResourceLocation field_188314_a = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");

   public RenderDragonFireball(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityDragonFireball var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      this.func_180548_c(var1);
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      Tessellator var10 = Tessellator.func_178181_a();
      BufferBuilder var11 = var10.func_178180_c();
      float var12 = 1.0F;
      float var13 = 0.5F;
      float var14 = 0.25F;
      GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b((float)(this.field_76990_c.field_78733_k.field_74320_O == 2 ? -1 : 1) * -this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      var11.func_181668_a(7, DefaultVertexFormats.field_181710_j);
      var11.func_181662_b(-0.5D, -0.25D, 0.0D).func_187315_a(0.0D, 1.0D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_181662_b(0.5D, -0.25D, 0.0D).func_187315_a(1.0D, 1.0D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_181662_b(0.5D, 0.75D, 0.0D).func_187315_a(1.0D, 0.0D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var11.func_181662_b(-0.5D, 0.75D, 0.0D).func_187315_a(0.0D, 0.0D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      var10.func_78381_a();
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityDragonFireball var1) {
      return field_188314_a;
   }
}
