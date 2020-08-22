package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;

public final class HorseRenderer extends AbstractHorseRenderer {
   private static final Map LAYERED_LOCATION_CACHE = Maps.newHashMap();

   public HorseRenderer(EntityRenderDispatcher var1) {
      super(var1, new HorseModel(0.0F), 1.1F);
      this.addLayer(new HorseArmorLayer(this));
   }

   public ResourceLocation getTextureLocation(Horse var1) {
      String var2 = var1.getLayeredTextureHashName();
      ResourceLocation var3 = (ResourceLocation)LAYERED_LOCATION_CACHE.get(var2);
      if (var3 == null) {
         var3 = new ResourceLocation(var2);
         Minecraft.getInstance().getTextureManager().register((ResourceLocation)var3, (AbstractTexture)(new LayeredTexture(var1.getLayeredTextureLayers())));
         LAYERED_LOCATION_CACHE.put(var2, var3);
      }

      return var3;
   }
}
