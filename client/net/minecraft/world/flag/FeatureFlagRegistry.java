package net.minecraft.world.flag;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class FeatureFlagRegistry {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final FeatureFlagUniverse universe;
   private final Map<ResourceLocation, FeatureFlag> names;
   private final FeatureFlagSet allFlags;

   FeatureFlagRegistry(FeatureFlagUniverse var1, FeatureFlagSet var2, Map<ResourceLocation, FeatureFlag> var3) {
      super();
      this.universe = var1;
      this.names = var3;
      this.allFlags = var2;
   }

   public boolean isSubset(FeatureFlagSet var1) {
      return var1.isSubsetOf(this.allFlags);
   }

   public FeatureFlagSet allFlags() {
      return this.allFlags;
   }

   public FeatureFlagSet fromNames(Iterable<ResourceLocation> var1) {
      return this.fromNames(var1, (var0) -> {
         LOGGER.warn("Unknown feature flag: {}", var0);
      });
   }

   public FeatureFlagSet subset(FeatureFlag... var1) {
      return FeatureFlagSet.create(this.universe, Arrays.asList(var1));
   }

   public FeatureFlagSet fromNames(Iterable<ResourceLocation> var1, Consumer<ResourceLocation> var2) {
      Set var3 = Sets.newIdentityHashSet();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var4.next();
         FeatureFlag var6 = (FeatureFlag)this.names.get(var5);
         if (var6 == null) {
            var2.accept(var5);
         } else {
            var3.add(var6);
         }
      }

      return FeatureFlagSet.create(this.universe, var3);
   }

   public Set<ResourceLocation> toNames(FeatureFlagSet var1) {
      HashSet var2 = new HashSet();
      this.names.forEach((var2x, var3) -> {
         if (var1.contains(var3)) {
            var2.add(var2x);
         }

      });
      return var2;
   }

   public Codec<FeatureFlagSet> codec() {
      return ResourceLocation.CODEC.listOf().comapFlatMap((var1) -> {
         HashSet var2 = new HashSet();
         Objects.requireNonNull(var2);
         FeatureFlagSet var3 = this.fromNames(var1, var2::add);
         return !var2.isEmpty() ? DataResult.error(() -> {
            return "Unknown feature ids: " + String.valueOf(var2);
         }, var3) : DataResult.success(var3);
      }, (var1) -> {
         return List.copyOf(this.toNames(var1));
      });
   }

   public static class Builder {
      private final FeatureFlagUniverse universe;
      private int id;
      private final Map<ResourceLocation, FeatureFlag> flags = new LinkedHashMap();

      public Builder(String var1) {
         super();
         this.universe = new FeatureFlagUniverse(var1);
      }

      public FeatureFlag createVanilla(String var1) {
         return this.create(ResourceLocation.withDefaultNamespace(var1));
      }

      public FeatureFlag create(ResourceLocation var1) {
         if (this.id >= 64) {
            throw new IllegalStateException("Too many feature flags");
         } else {
            FeatureFlag var2 = new FeatureFlag(this.universe, this.id++);
            FeatureFlag var3 = (FeatureFlag)this.flags.put(var1, var2);
            if (var3 != null) {
               throw new IllegalStateException("Duplicate feature flag " + String.valueOf(var1));
            } else {
               return var2;
            }
         }
      }

      public FeatureFlagRegistry build() {
         FeatureFlagSet var1 = FeatureFlagSet.create(this.universe, this.flags.values());
         return new FeatureFlagRegistry(this.universe, var1, Map.copyOf(this.flags));
      }
   }
}
