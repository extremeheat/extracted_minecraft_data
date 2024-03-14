package net.minecraft.data.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataResolver;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceLocation> requiredTables;
   private final List<LootTableProvider.SubProviderEntry> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public LootTableProvider(
      PackOutput var1, Set<ResourceLocation> var2, List<LootTableProvider.SubProviderEntry> var3, CompletableFuture<HolderLookup.Provider> var4
   ) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
      this.subProviders = var3;
      this.requiredTables = var2;
      this.registries = var4;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose(var2 -> this.run(var1, var2));
   }

   private CompletableFuture<?> run(CachedOutput var1, HolderLookup.Provider var2) {
      final HashMap var3 = Maps.newHashMap();
      Object2ObjectOpenHashMap var4 = new Object2ObjectOpenHashMap();
      this.subProviders.forEach(var3x -> var3x.provider().get().generate(var2, (var3xx, var4x) -> {
            ResourceLocation var5xx = var4.put(RandomSequence.seedForKey(var3xx), var3xx);
            if (var5xx != null) {
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + var5xx + " and " + var3xx);
            }

            var4x.setRandomSequence(var3xx);
            if (var3.put(var3xx, var4x.setParamSet(var3x.paramSet).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + var3xx);
            }
         }));
      ProblemReporter.Collector var5 = new ProblemReporter.Collector();
      ValidationContext var6 = new ValidationContext(var5, LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
         @Nullable
         @Override
         public <T> T getElement(LootDataId<T> var1) {
            return (T)(var1.type() == LootDataType.TABLE ? var3.get(var1.location()) : null);
         }
      });

      for(ResourceLocation var9 : Sets.difference(this.requiredTables, var3.keySet())) {
         var5.report("Missing built-in table: " + var9);
      }

      var3.forEach(
         (var1x, var2x) -> var2x.validate(var6.setParams(var2x.getParamSet()).enterElement("{" + var1x + "}", new LootDataId<>(LootDataType.TABLE, var1x)))
      );
      Multimap var10 = var5.get();
      if (!var10.isEmpty()) {
         var10.forEach((var0, var1x) -> LOGGER.warn("Found validation problem in {}: {}", var0, var1x));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf(var3.entrySet().stream().map(var3x -> {
            ResourceLocation var4xx = (ResourceLocation)var3x.getKey();
            LootTable var5xx = (LootTable)var3x.getValue();
            Path var6xx = this.pathProvider.json(var4xx);
            return DataProvider.saveStable(var1, var2, LootTable.CODEC, var5xx, var6xx);
         }).toArray(var0 -> new CompletableFuture[var0]));
      }
   }

   @Override
   public final String getName() {
      return "Loot Tables";
   }

   public static record SubProviderEntry(Supplier<LootTableSubProvider> a, LootContextParamSet b) {
      private final Supplier<LootTableSubProvider> provider;
      final LootContextParamSet paramSet;

      public SubProviderEntry(Supplier<LootTableSubProvider> var1, LootContextParamSet var2) {
         super();
         this.provider = var1;
         this.paramSet = var2;
      }
   }
}
