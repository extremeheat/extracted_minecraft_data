package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.Markings;

public class HorseMarkingLayer extends RenderLayer<HorseRenderState, HorseModel> {
   private static final Identifier INVISIBLE_TEXTURE = Identifier.withDefaultNamespace("invisible");
   private static final Map<Markings, Identifier> TEXTURE_BY_MARKINGS;

   public HorseMarkingLayer(RenderLayerParent<HorseRenderState, HorseModel> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, HorseRenderState var4, float var5, float var6) {
      Identifier var7 = (Identifier)TEXTURE_BY_MARKINGS.get(var4.markings);
      if (var7 != INVISIBLE_TEXTURE && !var4.isInvisible) {
         var2.order(1).submitModel(this.getParentModel(), var4, var1, RenderTypes.entityTranslucent(var7), var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), -1, (TextureAtlasSprite)null, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }
   }

   static {
      TEXTURE_BY_MARKINGS = Maps.newEnumMap(Map.of(Markings.NONE, INVISIBLE_TEXTURE, Markings.WHITE, Identifier.withDefaultNamespace("textures/entity/horse/horse_markings_white.png"), Markings.WHITE_FIELD, Identifier.withDefaultNamespace("textures/entity/horse/horse_markings_whitefield.png"), Markings.WHITE_DOTS, Identifier.withDefaultNamespace("textures/entity/horse/horse_markings_whitedots.png"), Markings.BLACK_DOTS, Identifier.withDefaultNamespace("textures/entity/horse/horse_markings_blackdots.png")));
   }
}
