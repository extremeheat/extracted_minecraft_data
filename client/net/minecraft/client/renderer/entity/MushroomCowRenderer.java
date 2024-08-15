package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.client.renderer.entity.state.MushroomCowRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;

public class MushroomCowRenderer extends MobRenderer<MushroomCow, MushroomCowRenderState, CowModel> {
   private static final Map<MushroomCow.Variant, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), var0 -> {
      var0.put(MushroomCow.Variant.BROWN, ResourceLocation.withDefaultNamespace("textures/entity/cow/brown_mooshroom.png"));
      var0.put(MushroomCow.Variant.RED, ResourceLocation.withDefaultNamespace("textures/entity/cow/red_mooshroom.png"));
   });

   public MushroomCowRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CowModel(var1.bakeLayer(ModelLayers.MOOSHROOM)), 0.7F);
      this.addLayer(new MushroomCowMushroomLayer(this, var1.getBlockRenderDispatcher()));
   }

   public ResourceLocation getTextureLocation(MushroomCowRenderState var1) {
      return TEXTURES.get(var1.variant);
   }

   public MushroomCowRenderState createRenderState() {
      return new MushroomCowRenderState();
   }

   public void extractRenderState(MushroomCow var1, MushroomCowRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.variant = var1.getVariant();
   }
}
