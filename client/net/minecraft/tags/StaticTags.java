package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class StaticTags {
   private static final Set<ResourceKey<?>> HELPERS_IDS = Sets.newHashSet();
   private static final List<StaticTagHelper<?>> HELPERS = Lists.newArrayList();

   public StaticTags() {
      super();
   }

   public static <T> StaticTagHelper<T> create(ResourceKey<? extends Registry<T>> var0, String var1) {
      if (!HELPERS_IDS.add(var0)) {
         throw new IllegalStateException("Duplicate entry for static tag collection: " + var0);
      } else {
         StaticTagHelper var2 = new StaticTagHelper(var0, var1);
         HELPERS.add(var2);
         return var2;
      }
   }

   public static void resetAll(TagContainer var0) {
      HELPERS.forEach((var1) -> {
         var1.reset(var0);
      });
   }

   public static void resetAllToEmpty() {
      HELPERS.forEach(StaticTagHelper::resetToEmpty);
   }

   public static Multimap<ResourceKey<? extends Registry<?>>, ResourceLocation> getAllMissingTags(TagContainer var0) {
      HashMultimap var1 = HashMultimap.create();
      HELPERS.forEach((var2) -> {
         var1.putAll(var2.getKey(), var2.getMissingTags(var0));
      });
      return var1;
   }

   public static void bootStrap() {
      makeSureAllKnownHelpersAreLoaded();
   }

   private static Set<StaticTagHelper<?>> getAllKnownHelpers() {
      return ImmutableSet.of(BlockTags.HELPER, ItemTags.HELPER, FluidTags.HELPER, EntityTypeTags.HELPER, GameEventTags.HELPER);
   }

   private static void makeSureAllKnownHelpersAreLoaded() {
      Set var0 = (Set)getAllKnownHelpers().stream().map(StaticTagHelper::getKey).collect(Collectors.toSet());
      if (!Sets.difference(HELPERS_IDS, var0).isEmpty()) {
         throw new IllegalStateException("Missing helper registrations");
      }
   }

   public static void visitHelpers(Consumer<StaticTagHelper<?>> var0) {
      HELPERS.forEach(var0);
   }

   public static TagContainer createCollection() {
      TagContainer.Builder var0 = new TagContainer.Builder();
      makeSureAllKnownHelpersAreLoaded();
      HELPERS.forEach((var1) -> {
         var1.addToCollection(var0);
      });
      return var0.build();
   }
}
