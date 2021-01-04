package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedOuterLayer<T extends Zombie> extends RenderLayer<T, DrownedModel<T>> {
   private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
   private final DrownedModel<T> model = new DrownedModel(0.25F, 0.0F, 64, 64);

   public DrownedOuterLayer(RenderLayerParent<T, DrownedModel<T>> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.isInvisible()) {
         ((DrownedModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel(var1, var2, var3, var4);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(DROWNED_OUTER_LAYER_LOCATION);
         this.model.render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
