package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

public class LayerWolfCollar implements LayerRenderer<EntityWolf> {
   private static final ResourceLocation field_177147_a = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
   private final RenderWolf field_177146_b;

   public LayerWolfCollar(RenderWolf var1) {
      super();
      this.field_177146_b = var1;
   }

   public void func_177141_a(EntityWolf var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.func_70909_n() && !var1.func_82150_aj()) {
         this.field_177146_b.func_110776_a(field_177147_a);
         float[] var9 = var1.func_175546_cu().func_193349_f();
         GlStateManager.func_179124_c(var9[0], var9[1], var9[2]);
         this.field_177146_b.func_177087_b().func_78088_a(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
