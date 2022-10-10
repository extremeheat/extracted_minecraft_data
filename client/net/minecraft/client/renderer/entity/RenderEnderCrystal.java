package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class RenderEnderCrystal extends Render<EntityEnderCrystal> {
   private static final ResourceLocation field_110787_a = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private final ModelBase field_76995_b = new ModelEnderCrystal(0.0F, true);
   private final ModelBase field_188316_g = new ModelEnderCrystal(0.0F, false);

   public RenderEnderCrystal(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityEnderCrystal var1, double var2, double var4, double var6, float var8, float var9) {
      float var10 = (float)var1.field_70261_a + var9;
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      this.func_110776_a(field_110787_a);
      float var11 = MathHelper.func_76126_a(var10 * 0.2F) / 2.0F + 0.5F;
      var11 += var11 * var11;
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      if (var1.func_184520_k()) {
         this.field_76995_b.func_78088_a(var1, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
      } else {
         this.field_188316_g.func_78088_a(var1, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
      }

      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      BlockPos var12 = var1.func_184518_j();
      if (var12 != null) {
         this.func_110776_a(RenderDragon.field_110843_g);
         float var13 = (float)var12.func_177958_n() + 0.5F;
         float var14 = (float)var12.func_177956_o() + 0.5F;
         float var15 = (float)var12.func_177952_p() + 0.5F;
         double var16 = (double)var13 - var1.field_70165_t;
         double var18 = (double)var14 - var1.field_70163_u;
         double var20 = (double)var15 - var1.field_70161_v;
         RenderDragon.func_188325_a(var2 + var16, var4 - 0.3D + (double)(var11 * 0.4F) + var18, var6 + var20, var9, (double)var13, (double)var14, (double)var15, var1.field_70261_a, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v);
      }

      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityEnderCrystal var1) {
      return field_110787_a;
   }

   public boolean func_177071_a(EntityEnderCrystal var1, ICamera var2, double var3, double var5, double var7) {
      return super.func_177071_a(var1, var2, var3, var5, var7) || var1.func_184518_j() != null;
   }
}
