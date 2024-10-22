package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;

public class DrownedOuterLayer extends RenderLayer<ZombieRenderState, DrownedModel> {
   private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned_outer_layer.png");
   private final DrownedModel model;
   private final DrownedModel babyModel;

   public DrownedOuterLayer(RenderLayerParent<ZombieRenderState, DrownedModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new DrownedModel(var2.bakeLayer(ModelLayers.DROWNED_OUTER_LAYER));
      this.babyModel = new DrownedModel(var2.bakeLayer(ModelLayers.DROWNED_BABY_OUTER_LAYER));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, ZombieRenderState var4, float var5, float var6) {
      DrownedModel var7 = var4.isBaby ? this.babyModel : this.model;
      coloredCutoutModelCopyLayerRender(var7, DROWNED_OUTER_LAYER_LOCATION, var1, var2, var3, var4, -1);
   }
}
