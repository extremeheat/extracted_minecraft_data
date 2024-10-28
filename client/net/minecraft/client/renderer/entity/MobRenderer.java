package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
   public MobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1, var2, var3);
   }

   protected boolean shouldShowName(T var1) {
      return super.shouldShowName((LivingEntity)var1) && (var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity);
   }

   protected float getShadowRadius(T var1) {
      return super.getShadowRadius((LivingEntity)var1) * var1.getAgeScale();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final LivingEntity var1) {
      return this.getShadowRadius((Mob)var1);
   }

   // $FF: synthetic method
   protected boolean shouldShowName(final LivingEntity var1) {
      return this.shouldShowName((Mob)var1);
   }

   // $FF: synthetic method
   protected float getShadowRadius(final Entity var1) {
      return this.getShadowRadius((Mob)var1);
   }

   // $FF: synthetic method
   protected boolean shouldShowName(final Entity var1) {
      return this.shouldShowName((Mob)var1);
   }
}
