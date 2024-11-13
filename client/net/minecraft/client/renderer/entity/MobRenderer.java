package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Mob;

public abstract class MobRenderer<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends LivingEntityRenderer<T, S, M> {
   public MobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1, var2, var3);
   }

   protected boolean shouldShowName(T var1, double var2) {
      return super.shouldShowName(var1, var2) && (var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity);
   }

   protected float getShadowRadius(S var1) {
      return super.getShadowRadius(var1) * var1.ageScale;
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState var1) {
      return this.getShadowRadius((LivingEntityRenderState)var1);
   }
}
