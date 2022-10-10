package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderSkeleton extends RenderBiped<AbstractSkeleton> {
   private static final ResourceLocation field_110862_k = new ResourceLocation("textures/entity/skeleton/skeleton.png");

   public RenderSkeleton(RenderManager var1) {
      super(var1, new ModelSkeleton(), 0.5F);
      this.func_177094_a(new LayerHeldItem(this));
      this.func_177094_a(new LayerBipedArmor(this) {
         protected void func_177177_a() {
            this.field_177189_c = new ModelSkeleton(0.5F, true);
            this.field_177186_d = new ModelSkeleton(1.0F, true);
         }
      });
   }

   protected ResourceLocation func_110775_a(AbstractSkeleton var1) {
      return field_110862_k;
   }
}
