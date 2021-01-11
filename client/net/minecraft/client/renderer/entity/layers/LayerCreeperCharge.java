package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class LayerCreeperCharge implements LayerRenderer<EntityCreeper> {
   private static final ResourceLocation field_177172_a = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final RenderCreeper field_177170_b;
   private final ModelCreeper field_177171_c = new ModelCreeper(2.0F);

   public LayerCreeperCharge(RenderCreeper var1) {
      super();
      this.field_177170_b = var1;
   }

   public void func_177141_a(EntityCreeper var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_70830_n()) {
         boolean var9 = var1.func_82150_aj();
         GlStateManager.func_179132_a(!var9);
         this.field_177170_b.func_110776_a(field_177172_a);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         float var10 = (float)var1.field_70173_aa + var4;
         GlStateManager.func_179109_b(var10 * 0.01F, var10 * 0.01F, 0.0F);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179147_l();
         float var11 = 0.5F;
         GlStateManager.func_179131_c(var11, var11, var11, 1.0F);
         GlStateManager.func_179140_f();
         GlStateManager.func_179112_b(1, 1);
         this.field_177171_c.func_178686_a(this.field_177170_b.func_177087_b());
         this.field_177171_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179096_D();
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
         GlStateManager.func_179132_a(var9);
      }
   }

   public boolean func_177142_b() {
      return false;
   }
}
