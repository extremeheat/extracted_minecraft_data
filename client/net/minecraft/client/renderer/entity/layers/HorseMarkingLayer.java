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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;

public class HorseMarkingLayer extends RenderLayer<Horse, HorseModel<Horse>> {
   private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = (Map)Util.make(Maps.newEnumMap(Markings.class), (var0) -> {
      var0.put(Markings.NONE, (Object)null);
      var0.put(Markings.WHITE, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_white.png"));
      var0.put(Markings.WHITE_FIELD, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitefield.png"));
      var0.put(Markings.WHITE_DOTS, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_whitedots.png"));
      var0.put(Markings.BLACK_DOTS, ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_markings_blackdots.png"));
   });

   public HorseMarkingLayer(RenderLayerParent<Horse, HorseModel<Horse>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Horse var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ResourceLocation var11 = (ResourceLocation)LOCATION_BY_MARKINGS.get(var4.getMarkings());
      if (var11 != null && !var4.isInvisible()) {
         VertexConsumer var12 = var2.getBuffer(RenderType.entityTranslucent(var11));
         ((HorseModel)this.getParentModel()).renderToBuffer(var1, var12, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
      }
   }
}
