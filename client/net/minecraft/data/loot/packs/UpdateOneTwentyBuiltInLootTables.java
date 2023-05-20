package net.minecraft.data.loot.packs;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class UpdateOneTwentyBuiltInLootTables {
   private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
   private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
   public static final ResourceLocation DESERT_WELL_ARCHAEOLOGY = register("archaeology/desert_well");
   public static final ResourceLocation DESERT_PYRAMID_ARCHAEOLOGY = register("archaeology/desert_pyramid");

   public UpdateOneTwentyBuiltInLootTables() {
      super();
   }

   private static ResourceLocation register(String var0) {
      return register(new ResourceLocation(var0));
   }

   private static ResourceLocation register(ResourceLocation var0) {
      if (LOCATIONS.add(var0)) {
         return var0;
      } else {
         throw new IllegalArgumentException(var0 + " is already a registered built-in loot table");
      }
   }

   public static Set<ResourceLocation> all() {
      return IMMUTABLE_LOCATIONS;
   }
}
