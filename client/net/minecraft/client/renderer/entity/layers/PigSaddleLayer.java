package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

public class PigSaddleLayer extends RenderLayer<Pig, PigModel<Pig>> {
   private static final ResourceLocation SADDLE_LOCATION = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private final PigModel<Pig> model = new PigModel(0.5F);

   public PigSaddleLayer(RenderLayerParent<Pig, PigModel<Pig>> var1) {
      super(var1);
   }

   public void render(Pig var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.hasSaddle()) {
         this.bindTexture(SADDLE_LOCATION);
         ((PigModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
