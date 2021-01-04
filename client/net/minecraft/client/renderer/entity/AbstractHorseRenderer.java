package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.HorseModel;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, M extends HorseModel<T>> extends MobRenderer<T, M> {
   private final float scale;

   public AbstractHorseRenderer(EntityRenderDispatcher var1, M var2, float var3) {
      super(var1, var2, 0.75F);
      this.scale = var3;
   }

   protected void scale(T var1, float var2) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
      super.scale(var1, var2);
   }
}
