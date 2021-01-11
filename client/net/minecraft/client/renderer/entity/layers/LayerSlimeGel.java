package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;

public class LayerSlimeGel implements LayerRenderer<EntitySlime> {
   private final RenderSlime field_177161_a;
   private final ModelBase field_177160_b = new ModelSlime(0);

   public LayerSlimeGel(RenderSlime var1) {
      super();
      this.field_177161_a = var1;
   }

   public void func_177141_a(EntitySlime var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.func_82150_aj()) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179108_z();
         GlStateManager.func_179147_l();
         GlStateManager.func_179112_b(770, 771);
         this.field_177160_b.func_178686_a(this.field_177161_a.func_177087_b());
         this.field_177160_b.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
         GlStateManager.func_179084_k();
         GlStateManager.func_179133_A();
      }
   }

   public boolean func_177142_b() {
      return true;
   }
}
