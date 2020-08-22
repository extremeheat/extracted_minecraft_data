package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

public class ChestedHorseRenderer extends AbstractHorseRenderer {
   private static final Map MAP;

   public ChestedHorseRenderer(EntityRenderDispatcher var1, float var2) {
      super(var1, new ChestedHorseModel(0.0F), var2);
   }

   public ResourceLocation getTextureLocation(AbstractChestedHorse var1) {
      return (ResourceLocation)MAP.get(var1.getType());
   }

   static {
      MAP = Maps.newHashMap(ImmutableMap.of(EntityType.DONKEY, new ResourceLocation("textures/entity/horse/donkey.png"), EntityType.MULE, new ResourceLocation("textures/entity/horse/mule.png")));
   }
}
