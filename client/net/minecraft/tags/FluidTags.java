package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class FluidTags {
   private static TagCollection<Fluid> source = new TagCollection((var0) -> {
      return Optional.empty();
   }, "", false, "");
   private static int resetCount;
   public static final Tag<Fluid> WATER = bind("water");
   public static final Tag<Fluid> LAVA = bind("lava");

   public static void reset(TagCollection<Fluid> var0) {
      source = var0;
      ++resetCount;
   }

   private static Tag<Fluid> bind(String var0) {
      return new FluidTags.Wrapper(new ResourceLocation(var0));
   }

   public static class Wrapper extends Tag<Fluid> {
      private int check = -1;
      private Tag<Fluid> actual;

      public Wrapper(ResourceLocation var1) {
         super(var1);
      }

      public boolean contains(Fluid var1) {
         if (this.check != FluidTags.resetCount) {
            this.actual = FluidTags.source.getTagOrEmpty(this.getId());
            this.check = FluidTags.resetCount;
         }

         return this.actual.contains(var1);
      }

      public Collection<Fluid> getValues() {
         if (this.check != FluidTags.resetCount) {
            this.actual = FluidTags.source.getTagOrEmpty(this.getId());
            this.check = FluidTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection<Tag.Entry<Fluid>> getSource() {
         if (this.check != FluidTags.resetCount) {
            this.actual = FluidTags.source.getTagOrEmpty(this.getId());
            this.check = FluidTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
