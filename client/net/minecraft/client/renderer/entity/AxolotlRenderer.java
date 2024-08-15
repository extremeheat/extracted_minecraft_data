package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.AxolotlRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderer extends AgeableMobRenderer<Axolotl, AxolotlRenderState, AxolotlModel> {
   private static final Map<Axolotl.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), var0 -> {
      for (Axolotl.Variant var4 : Axolotl.Variant.values()) {
         var0.put(var4, ResourceLocation.withDefaultNamespace(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", var4.getName())));
      }
   });

   public AxolotlRenderer(EntityRendererProvider.Context var1) {
      super(var1, new AxolotlModel(var1.bakeLayer(ModelLayers.AXOLOTL)), new AxolotlModel(var1.bakeLayer(ModelLayers.AXOLOTL_BABY)), 0.5F);
   }

   public ResourceLocation getTextureLocation(AxolotlRenderState var1) {
      return TEXTURE_BY_TYPE.get(var1.variant);
   }

   public AxolotlRenderState createRenderState() {
      return new AxolotlRenderState();
   }

   public void extractRenderState(Axolotl var1, AxolotlRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
      var2.playingDeadFactor = var1.playingDeadAnimator.getFactor(var3);
      var2.inWaterFactor = var1.inWaterAnimator.getFactor(var3);
      var2.onGroundFactor = var1.onGroundAnimator.getFactor(var3);
      var2.movingFactor = var1.movingAnimator.getFactor(var3);
   }
}
