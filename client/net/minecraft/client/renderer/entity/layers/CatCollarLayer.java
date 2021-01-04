package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;

public class CatCollarLayer extends RenderLayer<Cat, CatModel<Cat>> {
   private static final ResourceLocation CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
   private final CatModel<Cat> catModel = new CatModel(0.01F);

   public CatCollarLayer(RenderLayerParent<Cat, CatModel<Cat>> var1) {
      super(var1);
   }

   public void render(Cat var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.isTame() && !var1.isInvisible()) {
         this.bindTexture(CAT_COLLAR_LOCATION);
         float[] var9 = var1.getCollarColor().getTextureDiffuseColors();
         GlStateManager.color3f(var9[0], var9[1], var9[2]);
         ((CatModel)this.getParentModel()).copyPropertiesTo(this.catModel);
         this.catModel.prepareMobModel(var1, var2, var3, var4);
         this.catModel.render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   public boolean colorsOnDamage() {
      return true;
   }
}
