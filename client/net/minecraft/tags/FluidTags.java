package net.minecraft.tags;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;

public final class FluidTags {
   protected static final StaticTagHelper<Fluid> HELPER;
   private static final List<Tag<Fluid>> KNOWN_TAGS;
   public static final Tag.Named<Fluid> WATER;
   public static final Tag.Named<Fluid> LAVA;

   private FluidTags() {
      super();
   }

   private static Tag.Named<Fluid> bind(String var0) {
      Tag.Named var1 = HELPER.bind(var0);
      KNOWN_TAGS.add(var1);
      return var1;
   }

   public static TagCollection<Fluid> getAllTags() {
      return HELPER.getAllTags();
   }

   /** @deprecated */
   @Deprecated
   public static List<Tag<Fluid>> getStaticTags() {
      return KNOWN_TAGS;
   }

   static {
      HELPER = StaticTags.create(Registry.FLUID_REGISTRY, "tags/fluids");
      KNOWN_TAGS = Lists.newArrayList();
      WATER = bind("water");
      LAVA = bind("lava");
   }
}
