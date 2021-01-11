package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerWitherAura implements LayerRenderer<EntityWither> {
   private static final ResourceLocation field_177217_a = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final RenderWither field_177215_b;
   private final ModelWither field_177216_c = new ModelWither(0.5F);

   public LayerWitherAura(RenderWither var1) {
      super();
      this.field_177215_b = var1;
   }

   public void func_177141_a(EntityWither var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_82205_o()) {
         GlStateManager.func_179132_a(!var1.func_82150_aj());
         this.field_177215_b.func_110776_a(field_177217_a);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         float var9 = (float)var1.field_70173_aa + var4;
         float var10 = MathHelper.func_76134_b(var9 * 0.02F) * 3.0F;
         float var11 = var9 * 0.01F;
         GlStateManager.func_179109_b(var10, var11, 0.0F);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179147_l();
         float var12 = 0.5F;
         GlStateManager.func_179131_c(var12, var12, var12, 1.0F);
         GlStateManager.func_179140_f();
         GlStateManager.func_179112_b(1, 1);
         this.field_177216_c.func_78086_a(var1, var2, var3, var4);
         this.field_177216_c.func_178686_a(this.field_177215_b.func_177087_b());
         this.field_177216_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
