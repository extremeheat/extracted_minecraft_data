package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTags {
   private static TagCollection source = new TagCollection((var0) -> {
      return Optional.empty();
   }, "", false, "");
   private static int resetCount;
   public static final Tag SKELETONS = bind("skeletons");
   public static final Tag RAIDERS = bind("raiders");
   public static final Tag BEEHIVE_INHABITORS = bind("beehive_inhabitors");
   public static final Tag ARROWS = bind("arrows");

   public static void reset(TagCollection var0) {
      source = var0;
      ++resetCount;
   }

   public static TagCollection getAllTags() {
      return source;
   }

   private static Tag bind(String var0) {
      return new EntityTypeTags.Wrapper(new ResourceLocation(var0));
   }

   public static class Wrapper extends Tag {
      private int check = -1;
      private Tag actual;

      public Wrapper(ResourceLocation var1) {
         super(var1);
      }

      public boolean contains(EntityType var1) {
         if (this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.contains(var1);
      }

      public Collection getValues() {
         if (this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection getSource() {
         if (this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
