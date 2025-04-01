package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class PackStuff {
   private static final Logger LOGGER = LogUtils.getLogger();

   public PackStuff() {
      super();
   }

   public static WorldDataConfiguration configurePackRepository(PackRepository var0, WorldDataConfiguration var1, boolean var2, boolean var3) {
      DataPackConfig var4 = var1.dataPacks();
      FeatureFlagSet var5 = var2 ? FeatureFlagSet.of() : var1.enabledFeatures();
      FeatureFlagSet var6 = var2 ? FeatureFlags.REGISTRY.allFlags() : var1.enabledFeatures();
      var0.reload();
      if (var3) {
         return configureRepositoryWithSelection(var0, List.of("vanilla"), var5, false);
      } else {
         LinkedHashSet var7 = Sets.newLinkedHashSet();

         for(String var9 : var4.getEnabled()) {
            if (var0.isAvailable(var9)) {
               var7.add(var9);
            } else {
               LOGGER.warn("Missing data pack {}", var9);
            }
         }

         for(Pack var14 : var0.getAvailablePacks()) {
            String var10 = var14.getId();
            if (!var4.getDisabled().contains(var10)) {
               FeatureFlagSet var11 = var14.getRequestedFeatures();
               boolean var12 = var7.contains(var10);
               if (!var12 && var14.getPackSource().shouldAddAutomatically()) {
                  if (var11.isSubsetOf(var6)) {
                     LOGGER.info("Found new data pack {}, loading it automatically", var10);
                     var7.add(var10);
                  } else {
                     LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", var10, FeatureFlags.printMissingFlags(var6, var11));
                  }
               }

               if (var12 && !var11.isSubsetOf(var6)) {
                  LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", var10, FeatureFlags.printMissingFlags(var6, var11));
                  var7.remove(var10);
               }
            }
         }

         if (var7.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            var7.add("vanilla");
         }

         return configureRepositoryWithSelection(var0, var7, var5, true);
      }
   }

   private static WorldDataConfiguration configureRepositoryWithSelection(PackRepository var0, Collection<String> var1, FeatureFlagSet var2, boolean var3) {
      var0.setSelected(var1);
      enableForcedFeaturePacks(var0, var2);
      DataPackConfig var4 = getSelectedPacks(var0, var3);
      FeatureFlagSet var5 = var0.getRequestedFeatureFlags().join(var2);
      return new WorldDataConfiguration(var4, var5);
   }

   private static void enableForcedFeaturePacks(PackRepository var0, FeatureFlagSet var1) {
      FeatureFlagSet var2 = var0.getRequestedFeatureFlags();
      FeatureFlagSet var3 = var1.subtract(var2);
      if (!var3.isEmpty()) {
         ObjectArraySet var4 = new ObjectArraySet(var0.getSelectedIds());

         for(Pack var6 : var0.getAvailablePacks()) {
            if (var3.isEmpty()) {
               break;
            }

            if (var6.getPackSource() == PackSource.FEATURE) {
               String var7 = var6.getId();
               FeatureFlagSet var8 = var6.getRequestedFeatures();
               if (!var8.isEmpty() && var8.intersects(var3) && var8.isSubsetOf(var1)) {
                  if (!var4.add(var7)) {
                     throw new IllegalStateException("Tried to force '" + var7 + "', but it was already enabled");
                  }

                  LOGGER.info("Found feature pack ('{}') for requested feature, forcing to enabled", var7);
                  var3 = var3.subtract(var8);
               }
            }
         }

         var0.setSelected(var4);
      }
   }

   public static DataPackConfig getSelectedPacks(PackRepository var0, boolean var1) {
      Collection var2 = var0.getSelectedIds();
      ImmutableList var3 = ImmutableList.copyOf(var2);
      List var4 = var1 ? var0.getAvailableIds().stream().filter((var1x) -> !var2.contains(var1x)).toList() : List.of();
      return new DataPackConfig(var3, var4);
   }
}
