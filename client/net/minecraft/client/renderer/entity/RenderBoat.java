package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.IMultipassModel;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelBoat;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderBoat extends Render<EntityBoat> {
   private static final ResourceLocation[] field_110782_f = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
   protected ModelBase field_76998_a = new ModelBoat();

   public RenderBoat(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityBoat var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      this.func_188309_a(var2, var4, var6);
      this.func_188311_a(var1, var8, var9);
      this.func_180548_c(var1);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      this.field_76998_a.func_78088_a(var1, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   public void func_188311_a(EntityBoat var1, float var2, float var3) {
      GlStateManager.func_179114_b(180.0F - var2, 0.0F, 1.0F, 0.0F);
      float var4 = (float)var1.func_70268_h() - var3;
      float var5 = var1.func_70271_g() - var3;
      if (var5 < 0.0F) {
         var5 = 0.0F;
      }

      if (var4 > 0.0F) {
         GlStateManager.func_179114_b(MathHelper.func_76126_a(var4) * var4 * var5 / 10.0F * (float)var1.func_70267_i(), 1.0F, 0.0F, 0.0F);
      }

      float var6 = var1.func_203056_b(var3);
      if (!MathHelper.func_180185_a(var6, 0.0F)) {
         GlStateManager.func_179114_b(var1.func_203056_b(var3), 1.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
   }

   public void func_188309_a(double var1, double var3, double var5) {
      GlStateManager.func_179109_b((float)var1, (float)var3 + 0.375F, (float)var5);
   }

   protected ResourceLocation func_110775_a(EntityBoat var1) {
      return field_110782_f[var1.func_184453_r().ordinal()];
   }

   public boolean func_188295_H_() {
      return true;
   }

   public void func_188300_b(EntityBoat var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      this.func_188309_a(var2, var4, var6);
      this.func_188311_a(var1, var8, var9);
      this.func_180548_c(var1);
      ((IMultipassModel)this.field_76998_a).func_187054_b(var1, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.func_179121_F();
   }
}
