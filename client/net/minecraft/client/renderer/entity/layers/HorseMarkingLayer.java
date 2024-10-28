package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Markings;

public class HorseMarkingLayer extends RenderLayer<HorseRenderState, HorseModel> {
   private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = (Map)Util.make(Maps.newEnumMap(Markings.class), (var0) -> {
      var0.put(Markings.NONE, (Object)null);
      var0.put(Markings.WHITE, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_white.png"));
      var0.put(Markings.WHITE_FIELD, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitefield.png"));
      var0.put(Markings.WHITE_DOTS, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitedots.png"));
      var0.put(Markings.BLACK_DOTS, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_blackdots.png"));
   });

   public HorseMarkingLayer(RenderLayerParent<HorseRenderState, HorseModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, HorseRenderState var4, float var5, float var6) {
      ResourceLocation var7 = (ResourceLocation)LOCATION_BY_MARKINGS.get(var4.markings);
      if (var7 != null && !var4.isInvisible) {
         VertexConsumer var8 = var2.getBuffer(RenderType.entityTranslucent(var7));
         ((HorseModel)this.getParentModel()).renderToBuffer(var1, var8, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
      }
   }
}
