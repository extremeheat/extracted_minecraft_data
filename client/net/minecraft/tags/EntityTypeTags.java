package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTags {
   private static TagCollection<EntityType<?>> source = new TagCollection((var0) -> {
      return Optional.empty();
   }, "", false, "");
   private static int resetCount;
   public static final Tag<EntityType<?>> SKELETONS = bind("skeletons");
   public static final Tag<EntityType<?>> RAIDERS = bind("raiders");

   public static void reset(TagCollection<EntityType<?>> var0) {
      source = var0;
      ++resetCount;
   }

   public static TagCollection<EntityType<?>> getAllTags() {
      return source;
   }

   private static Tag<EntityType<?>> bind(String var0) {
      return new EntityTypeTags.Wrapper(new ResourceLocation(var0));
   }

   public static class Wrapper extends Tag<EntityType<?>> {
      private int check = -1;
      private Tag<EntityType<?>> actual;

      public Wrapper(ResourceLocation var1) {
         super(var1);
      }

      public boolean contains(EntityType<?> var1) {
         if (this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.contains(var1);
      }

      public Collection<EntityType<?>> getValues() {
         if (this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection<Tag.Entry<EntityType<?>>> getSource() {
         if (this.check != EntityTypeTags.resetCount) {
            this.actual = EntityTypeTags.source.getTagOrEmpty(this.getId());
            this.check = EntityTypeTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
