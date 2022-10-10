package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelSkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.util.ResourceLocation;

public class LayerStrayClothing implements LayerRenderer<EntityStray> {
   private static final ResourceLocation field_190092_a = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final RenderLivingBase<?> field_190093_b;
   private final ModelSkeleton field_190094_c = new ModelSkeleton(0.25F, true);

   public LayerStrayClothing(RenderLivingBase<?> var1) {
      super();
      this.field_190093_b = var1;
   }

   public void func_177141_a(EntityStray var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.field_190094_c.func_178686_a(this.field_190093_b.func_177087_b());
      this.field_190094_c.func_78086_a(var1, var2, var3, var4);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_190093_b.func_110776_a(field_190092_a);
      this.field_190094_c.func_78088_a(var1, var2, var3, var5, var6, var7, var8);
   }

   public boolean func_177142_b() {
      return true;
   }
}
