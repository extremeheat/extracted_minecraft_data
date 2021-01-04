package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Mule;

public class ChestedHorseRenderer<T extends AbstractChestedHorse> extends AbstractHorseRenderer<T, ChestedHorseModel<T>> {
   private static final Map<Class<?>, ResourceLocation> MAP = Maps.newHashMap(ImmutableMap.of(Donkey.class, new ResourceLocation("textures/entity/horse/donkey.png"), Mule.class, new ResourceLocation("textures/entity/horse/mule.png")));

   public ChestedHorseRenderer(EntityRenderDispatcher var1, float var2) {
      super(var1, new ChestedHorseModel(0.0F), var2);
   }

   protected ResourceLocation getTextureLocation(T var1) {
      return (ResourceLocation)MAP.get(var1.getClass());
   }
}
