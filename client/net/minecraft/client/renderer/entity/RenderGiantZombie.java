package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;

public class RenderGiantZombie extends RenderLiving<EntityGiantZombie> {
   private static final ResourceLocation field_110871_a = new ResourceLocation("textures/entity/zombie/zombie.png");
   private final float field_77073_a;

   public RenderGiantZombie(RenderManager var1, float var2) {
      super(var1, new ModelZombie(), 0.5F * var2);
      this.field_77073_a = var2;
      this.func_177094_a(new LayerHeldItem(this));
      this.func_177094_a(new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelZombie(0.5F, true);
            this.field_177186_d = new ModelZombie(1.0F, true);
         }
      });
   }

   protected void func_77041_b(EntityGiantZombie var1, float var2) {
      GlStateManager.func_179152_a(this.field_77073_a, this.field_77073_a, this.field_77073_a);
   }

   protected ResourceLocation func_110775_a(EntityGiantZombie var1) {
      return field_110871_a;
   }
}
