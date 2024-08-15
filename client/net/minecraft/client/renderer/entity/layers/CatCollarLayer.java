package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class CatCollarLayer extends RenderLayer<CatRenderState, CatModel> {
   private static final ResourceLocation CAT_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cat/cat_collar.png");
   private final CatModel adultModel;
   private final CatModel babyModel;

   public CatCollarLayer(RenderLayerParent<CatRenderState, CatModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new CatModel(var2.bakeLayer(ModelLayers.CAT_COLLAR));
      this.babyModel = new CatModel(var2.bakeLayer(ModelLayers.CAT_BABY_COLLAR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, CatRenderState var4, float var5, float var6) {
      DyeColor var7 = var4.collarColor;
      if (var7 != null) {
         int var8 = var7.getTextureDiffuseColor();
         CatModel var9 = var4.isBaby ? this.babyModel : this.adultModel;
         coloredCutoutModelCopyLayerRender(var9, CAT_COLLAR_LOCATION, var1, var2, var3, var4, var8);
      }
   }
}
