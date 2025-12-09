package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.sheep.SheepFurModel;
import net.minecraft.client.model.animal.sheep.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public class SheepWoolLayer extends RenderLayer<SheepRenderState, SheepModel> {
   private static final Identifier SHEEP_WOOL_LOCATION = Identifier.withDefaultNamespace("textures/entity/sheep/sheep_wool.png");
   private final EntityModel<SheepRenderState> adultModel;
   private final EntityModel<SheepRenderState> babyModel;

   public SheepWoolLayer(RenderLayerParent<SheepRenderState, SheepModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_WOOL));
      this.babyModel = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_BABY_WOOL));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, SheepRenderState var4, float var5, float var6) {
      if (!var4.isSheared) {
         EntityModel var7 = var4.isBaby ? this.babyModel : this.adultModel;
         if (var4.isInvisible) {
            if (var4.appearsGlowing()) {
               var2.submitModel(var7, var4, var1, RenderTypes.outline(SHEEP_WOOL_LOCATION), var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), -16777216, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
            }

         } else {
            coloredCutoutModelCopyLayerRender(var7, SHEEP_WOOL_LOCATION, var1, var2, var3, var4, var4.getWoolColor(), 0);
         }
      }
   }
}
