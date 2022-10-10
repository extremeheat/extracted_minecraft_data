package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelPufferFishBig;
import net.minecraft.client.renderer.entity.model.ModelPufferFishMedium;
import net.minecraft.client.renderer.entity.model.ModelPufferFishSmall;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityPufferFish;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderPufferFish extends RenderLiving<EntityPufferFish> {
   private static final ResourceLocation field_203771_a = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int field_203772_j = 3;
   private final ModelPufferFishSmall field_203773_k = new ModelPufferFishSmall();
   private final ModelPufferFishMedium field_203774_l = new ModelPufferFishMedium();
   private final ModelPufferFishBig field_203775_m = new ModelPufferFishBig();

   public RenderPufferFish(RenderManager var1) {
      super(var1, new ModelPufferFishBig(), 0.1F);
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityPufferFish var1) {
      return field_203771_a;
   }

   public void func_76986_a(EntityPufferFish var1, double var2, double var4, double var6, float var8, float var9) {
      int var10 = var1.func_203715_dA();
      if (var10 != this.field_203772_j) {
         if (var10 == 0) {
            this.field_77045_g = this.field_203773_k;
         } else if (var10 == 1) {
            this.field_77045_g = this.field_203774_l;
         } else {
            this.field_77045_g = this.field_203775_m;
         }
      }

      this.field_203772_j = var10;
      this.field_76989_e = 0.1F + 0.1F * (float)var10;
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected void func_77043_a(EntityPufferFish var1, float var2, float var3, float var4) {
      GlStateManager.func_179109_b(0.0F, MathHelper.func_76134_b(var2 * 0.05F) * 0.08F, 0.0F);
      super.func_77043_a(var1, var2, var3, var4);
   }
}
