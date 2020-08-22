package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.monster.AbstractIllager;

public abstract class IllagerRenderer extends MobRenderer {
   protected IllagerRenderer(EntityRenderDispatcher var1, IllagerModel var2, float var3) {
      super(var1, var2, var3);
      this.addLayer(new CustomHeadLayer(this));
   }

   protected void scale(AbstractIllager var1, PoseStack var2, float var3) {
      float var4 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }
}
