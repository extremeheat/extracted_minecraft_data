package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Markings;

public class HorseMarkingLayer extends RenderLayer<HorseRenderState, HorseModel> {
   private static final ResourceLocation INVISIBLE_TEXTURE = ResourceLocation.withDefaultNamespace("invisible");
   private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS;

   public HorseMarkingLayer(RenderLayerParent<HorseRenderState, HorseModel> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, HorseRenderState var4, float var5, float var6) {
      ResourceLocation var7 = (ResourceLocation)LOCATION_BY_MARKINGS.get(var4.markings);
      if (var7 != INVISIBLE_TEXTURE && !var4.isInvisible) {
         var2.order(1).submitModel(this.getParentModel(), var4, var1, RenderType.entityTranslucent(var7), var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), -1, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }

   static {
      LOCATION_BY_MARKINGS = Maps.newEnumMap(Map.of(Markings.NONE, INVISIBLE_TEXTURE, Markings.WHITE, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_white.png"), Markings.WHITE_FIELD, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitefield.png"), Markings.WHITE_DOTS, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitedots.png"), Markings.BLACK_DOTS, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_blackdots.png")));
   }
}
