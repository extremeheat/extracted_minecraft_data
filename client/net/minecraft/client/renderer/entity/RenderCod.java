package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelCod;
import net.minecraft.entity.passive.EntityCod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderCod extends RenderLiving<EntityCod> {
   private static final ResourceLocation field_203769_a = new ResourceLocation("textures/entity/fish/cod.png");

   public RenderCod(RenderManager var1) {
      super(var1, new ModelCod(), 0.2F);
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityCod var1) {
      return field_203769_a;
   }

   protected void func_77043_a(EntityCod var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
      float var5 = 4.3F * MathHelper.func_76126_a(0.6F * var2);
      GlStateManager.func_179114_b(var5, 0.0F, 1.0F, 0.0F);
      if (!var1.func_70090_H()) {
         GlStateManager.func_179109_b(0.1F, 0.1F, -0.1F);
         GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
