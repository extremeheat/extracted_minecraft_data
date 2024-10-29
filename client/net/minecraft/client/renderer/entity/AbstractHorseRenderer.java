package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, S extends EquineRenderState, M extends EntityModel<? super S>> extends AgeableMobRenderer<T, S, M> {
   private final float scale;

   public AbstractHorseRenderer(EntityRendererProvider.Context var1, M var2, M var3, float var4) {
      super(var1, var2, var3, 0.75F);
      this.scale = var4;
   }

   protected void scale(S var1, PoseStack var2) {
      var2.scale(this.scale, this.scale, this.scale);
      super.scale(var1, var2);
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSaddled = var1.isSaddled();
      var2.isRidden = var1.isVehicle();
      var2.eatAnimation = var1.getEatAnim(var3);
      var2.standAnimation = var1.getStandAnim(var3);
      var2.feedingAnimation = var1.getMouthAnim(var3);
      var2.animateTail = var1.tailCounter > 0;
   }
}
