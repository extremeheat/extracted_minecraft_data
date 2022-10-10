package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorChests;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.util.ResourceLocation;

public class RenderHorseChest extends RenderAbstractHorse<EntityHorse> {
   private static final Map<Class<?>, ResourceLocation> field_195635_a = Maps.newHashMap(ImmutableMap.of(EntityDonkey.class, new ResourceLocation("textures/entity/horse/donkey.png"), EntityMule.class, new ResourceLocation("textures/entity/horse/mule.png")));

   public RenderHorseChest(RenderManager var1, float var2) {
      super(var1, new ModelHorseArmorChests(), var2);
   }

   protected ResourceLocation func_110775_a(AbstractHorse var1) {
      return (ResourceLocation)field_195635_a.get(var1.getClass());
   }
}
