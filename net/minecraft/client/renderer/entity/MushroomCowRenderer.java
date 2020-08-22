package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.entity.layers.MushroomCowMushroomLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.MushroomCow;

public class MushroomCowRenderer extends MobRenderer {
   private static final Map TEXTURES = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put(MushroomCow.MushroomType.BROWN, new ResourceLocation("textures/entity/cow/brown_mooshroom.png"));
      var0.put(MushroomCow.MushroomType.RED, new ResourceLocation("textures/entity/cow/red_mooshroom.png"));
   });

   public MushroomCowRenderer(EntityRenderDispatcher var1) {
      super(var1, new CowModel(), 0.7F);
      this.addLayer(new MushroomCowMushroomLayer(this));
   }

   public ResourceLocation getTextureLocation(MushroomCow var1) {
      return (ResourceLocation)TEXTURES.get(var1.getMushroomType());
   }
}
