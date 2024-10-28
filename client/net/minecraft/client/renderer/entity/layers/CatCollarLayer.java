package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;

public class CatCollarLayer extends RenderLayer<Cat, CatModel<Cat>> {
   private static final ResourceLocation CAT_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cat/cat_collar.png");
   private final CatModel<Cat> catModel;

   public CatCollarLayer(RenderLayerParent<Cat, CatModel<Cat>> var1, EntityModelSet var2) {
      super(var1);
      this.catModel = new CatModel(var2.bakeLayer(ModelLayers.CAT_COLLAR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Cat var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.isTame()) {
         int var11 = var4.getCollarColor().getTextureDiffuseColor();
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.catModel, CAT_COLLAR_LOCATION, var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, var11);
      }
   }
}
