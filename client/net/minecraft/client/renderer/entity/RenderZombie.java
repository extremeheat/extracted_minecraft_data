package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped<EntityZombie> {
   private static final ResourceLocation field_110865_p = new ResourceLocation("textures/entity/zombie/zombie.png");

   public RenderZombie(RenderManager var1, ModelBiped var2) {
      super(var1, var2, 0.5F);
      this.func_177094_a(this.func_209265_c());
   }

   public RenderZombie(RenderManager var1) {
      this(var1, new ModelZombie());
   }

   protected LayerBipedArmor func_209265_c() {
      return new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelZombie(0.5F, true);
            this.field_177186_d = new ModelZombie(1.0F, true);
         }
      };
   }

   protected ResourceLocation func_110775_a(EntityZombie var1) {
      return field_110865_p;
   }

   protected void func_77043_a(EntityZombie var1, float var2, float var3, float var4) {
      if (var1.func_204706_dD()) {
         var3 += (float)(Math.cos((double)var1.field_70173_aa * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.func_77043_a(var1, var2, var3, var4);
   }
}
