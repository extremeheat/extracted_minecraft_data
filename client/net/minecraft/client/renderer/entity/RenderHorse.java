package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.ModelHorseArmorBase;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse extends RenderAbstractHorse<EntityHorse> {
   private static final Map<String, ResourceLocation> field_110852_a = Maps.newHashMap();

   public RenderHorse(RenderManager var1) {
      super(var1, new ModelHorseArmorBase(), 1.1F);
   }

   protected ResourceLocation func_110775_a(AbstractHorse var1) {
      EntityHorse var2 = (EntityHorse)var1;
      String var3 = var2.func_110264_co();
      ResourceLocation var4 = (ResourceLocation)field_110852_a.get(var3);
      if (var4 == null) {
         var4 = new ResourceLocation(var3);
         Minecraft.func_71410_x().func_110434_K().func_110579_a(var4, new LayeredTexture(var2.func_110212_cp()));
         field_110852_a.put(var3, var4);
      }

      return var4;
   }
}
