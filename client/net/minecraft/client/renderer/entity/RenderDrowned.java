package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerDrownedOuter;
import net.minecraft.client.renderer.entity.model.ModelDrowned;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderDrowned extends RenderZombie {
   private static final ResourceLocation field_204720_a = new ResourceLocation("textures/entity/zombie/drowned.png");
   private float field_208407_j;

   public RenderDrowned(RenderManager var1) {
      super(var1, new ModelDrowned(0.0F, 0.0F, 64, 64));
      this.func_177094_a(new LayerDrownedOuter(this));
   }

   protected LayerBipedArmor func_209265_c() {
      return new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelDrowned(0.5F, true);
            this.field_177186_d = new ModelDrowned(1.0F, true);
         }
      };
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityZombie var1) {
      return field_204720_a;
   }

   protected void func_77043_a(EntityZombie var1, float var2, float var3, float var4) {
      float var5 = var1.func_205015_b(var4);
      super.func_77043_a(var1, var2, var3, var4);
      if (var5 > 0.0F) {
         float var6 = this.func_208406_b(var1.field_70125_A, -10.0F - var1.field_70125_A, var5);
         if (!var1.func_203007_ba()) {
            var6 = this.func_77034_a(this.field_208407_j, 0.0F, 1.0F - var5);
         }

         GlStateManager.func_179114_b(var6, 1.0F, 0.0F, 0.0F);
         if (var1.func_203007_ba()) {
            this.field_208407_j = var6;
         }
      }

   }

   private float func_208406_b(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * var3;
   }
}
