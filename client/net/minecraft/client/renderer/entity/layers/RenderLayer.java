package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class RenderLayer<T extends Entity, M extends EntityModel<T>> {
   private final RenderLayerParent<T, M> renderer;

   public RenderLayer(RenderLayerParent<T, M> var1) {
      super();
      this.renderer = var1;
   }

   public M getParentModel() {
      return this.renderer.getModel();
   }

   public void bindTexture(ResourceLocation var1) {
      this.renderer.bindTexture(var1);
   }

   public void setLightColor(T var1) {
      this.renderer.setLightColor(var1);
   }

   public abstract void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

   public abstract boolean colorsOnDamage();
}
