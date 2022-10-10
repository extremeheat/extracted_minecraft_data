package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderTropicalFish;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishA;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishB;
import net.minecraft.entity.passive.EntityTropicalFish;

public class LayerTropicalFishPattern implements LayerRenderer<EntityTropicalFish> {
   private final RenderTropicalFish field_204250_a;
   private final ModelTropicalFishA field_204251_b;
   private final ModelTropicalFishB field_204252_c;

   public LayerTropicalFishPattern(RenderTropicalFish var1) {
      super();
      this.field_204250_a = var1;
      this.field_204251_b = new ModelTropicalFishA(0.008F);
      this.field_204252_c = new ModelTropicalFishB(0.008F);
   }

   public void func_177141_a(EntityTropicalFish var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.func_82150_aj()) {
         Object var9 = var1.func_204217_dE() == 0 ? this.field_204251_b : this.field_204252_c;
         this.field_204250_a.func_110776_a(var1.func_204220_dF());
         float[] var10 = var1.func_204222_dD();
         GlStateManager.func_179124_c(var10[0], var10[1], var10[2]);
         ((ModelBase)var9).func_178686_a(this.field_204250_a.func_177087_b());
         ((ModelBase)var9).func_78086_a(var1, var2, var3, var4);
         ((ModelBase)var9).func_78088_a(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
