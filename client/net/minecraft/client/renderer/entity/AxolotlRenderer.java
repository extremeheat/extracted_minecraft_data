package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderer extends MobRenderer<Axolotl, AxolotlModel<Axolotl>> {
   private static final Map<Axolotl.Variant, ResourceLocation> TEXTURE_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      Axolotl.Variant[] var1 = Axolotl.Variant.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Axolotl.Variant var4 = var1[var3];
         var0.put(var4, ResourceLocation.withDefaultNamespace(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", var4.getName())));
      }

   });

   public AxolotlRenderer(EntityRendererProvider.Context var1) {
      super(var1, new AxolotlModel(var1.bakeLayer(ModelLayers.AXOLOTL)), 0.5F);
   }

   public ResourceLocation getTextureLocation(Axolotl var1) {
      return (ResourceLocation)TEXTURE_BY_TYPE.get(var1.getVariant());
   }
}
