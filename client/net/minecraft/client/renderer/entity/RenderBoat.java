package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderBoat extends Render<EntityBoat> {
   private static final ResourceLocation field_110782_f = new ResourceLocation("textures/entity/boat.png");
   protected ModelBase field_76998_a = new ModelBoat();

   public RenderBoat(RenderManager var1) {
      super(var1);
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityBoat var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4 + 0.25F, (float)var6);
      GlStateManager.func_179114_b(180.0F - var8, 0.0F, 1.0F, 0.0F);
      float var10 = (float)var1.func_70268_h() - var9;
      float var11 = var1.func_70271_g() - var9;
      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 0.0F) {
         GlStateManager.func_179114_b(MathHelper.func_76126_a(var10) * var10 * var11 / 10.0F * (float)var1.func_70267_i(), 1.0F, 0.0F, 0.0F);
      }

      float var12 = 0.75F;
      GlStateManager.func_179152_a(var12, var12, var12);
      GlStateManager.func_179152_a(1.0F / var12, 1.0F / var12, 1.0F / var12);
      this.func_180548_c(var1);
      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      this.field_76998_a.func_78088_a(var1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityBoat var1) {
      return field_110782_f;
   }
}
