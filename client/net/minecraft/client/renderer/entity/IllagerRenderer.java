package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.monster.AbstractIllager;

public abstract class IllagerRenderer<T extends AbstractIllager> extends MobRenderer<T, IllagerModel<T>> {
   protected IllagerRenderer(EntityRendererProvider.Context var1, IllagerModel<T> var2, float var3) {
      super(var1, var2, var3);
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getItemInHandRenderer()));
   }

   protected void scale(T var1, PoseStack var2, float var3) {
      float var4 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }
}
