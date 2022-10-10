package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelEvokerFangs;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.util.ResourceLocation;

public class RenderEvokerFangs extends Render<EntityEvokerFangs> {
   private static final ResourceLocation field_191329_a = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final ModelEvokerFangs field_191330_f = new ModelEvokerFangs();

   public RenderEvokerFangs(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityEvokerFangs var1, double var2, double var4, double var6, float var8, float var9) {
      float var10 = var1.func_190550_a(var9);
      if (var10 != 0.0F) {
         float var11 = 2.0F;
         if (var10 > 0.9F) {
            var11 = (float)((double)var11 * ((1.0D - (double)var10) / 0.10000000149011612D));
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179129_p();
         GlStateManager.func_179141_d();
         this.func_180548_c(var1);
         GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
         GlStateManager.func_179114_b(90.0F - var1.field_70177_z, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179152_a(-var11, -var11, var11);
         float var12 = 0.03125F;
         GlStateManager.func_179109_b(0.0F, -0.626F, 0.0F);
         this.field_191330_f.func_78088_a(var1, var10, 0.0F, 0.0F, var1.field_70177_z, var1.field_70125_A, 0.03125F);
         GlStateManager.func_179121_F();
         GlStateManager.func_179089_o();
         super.func_76986_a(var1, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation func_110775_a(EntityEvokerFangs var1) {
      return field_191329_a;
   }
}
