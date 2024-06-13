package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Mob;

public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
   public MobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1, (M)var2, var3);
   }

   protected boolean shouldShowName(T var1) {
      return super.shouldShowName((T)var1) && (var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity);
   }

   protected float getShadowRadius(T var1) {
      return super.getShadowRadius((T)var1) * var1.getAgeScale();
   }
}
