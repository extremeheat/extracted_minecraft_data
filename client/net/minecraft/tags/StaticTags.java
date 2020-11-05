package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public class StaticTags {
   private static final Map<ResourceLocation, StaticTagHelper<?>> HELPERS = Maps.newHashMap();

   public static <T> StaticTagHelper<T> create(ResourceLocation var0, Function<TagContainer, TagCollection<T>> var1) {
      StaticTagHelper var2 = new StaticTagHelper(var1);
      StaticTagHelper var3 = (StaticTagHelper)HELPERS.putIfAbsent(var0, var2);
      if (var3 != null) {
         throw new IllegalStateException("Duplicate entry for static tag collection: " + var0);
      } else {
         return var2;
      }
   }

   public static void resetAll(TagContainer var0) {
      HELPERS.values().forEach((var1) -> {
         var1.reset(var0);
      });
   }

   public static void resetAllToEmpty() {
      HELPERS.values().forEach(StaticTagHelper::resetToEmpty);
   }

   public static Multimap<ResourceLocation, ResourceLocation> getAllMissingTags(TagContainer var0) {
      HashMultimap var1 = HashMultimap.create();
      HELPERS.forEach((var2, var3) -> {
         var1.putAll(var2, var3.getMissingTags(var0));
      });
      return var1;
   }

   public static void bootStrap() {
      StaticTagHelper[] var0 = new StaticTagHelper[]{BlockTags.HELPER, ItemTags.HELPER, FluidTags.HELPER, EntityTypeTags.HELPER};
      boolean var1 = Stream.of(var0).anyMatch((var0x) -> {
         return !HELPERS.containsValue(var0x);
      });
      if (var1) {
         throw new IllegalStateException("Missing helper registrations");
      }
   }
}
