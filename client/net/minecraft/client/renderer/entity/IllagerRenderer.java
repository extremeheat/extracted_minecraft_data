package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.monster.AbstractIllager;

public abstract class IllagerRenderer<T extends AbstractIllager> extends MobRenderer<T, IllagerModel<T>> {
   protected IllagerRenderer(EntityRenderDispatcher var1, IllagerModel<T> var2, float var3) {
      super(var1, var2, var3);
      this.addLayer(new CustomHeadLayer(this));
   }

   public IllagerRenderer(EntityRenderDispatcher var1) {
      super(var1, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new CustomHeadLayer(this));
   }

   protected void scale(T var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}
