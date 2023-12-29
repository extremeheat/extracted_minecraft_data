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

   public LootTableProvider(PackOutput var1, Set<ResourceLocation> var2, List<LootTableProvider.SubProviderEntry> var3) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
      this.subProviders = var3;
      this.requiredTables = var2;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      final HashMap var2 = Maps.newHashMap();
      Object2ObjectOpenHashMap var3 = new Object2ObjectOpenHashMap();
      this.subProviders.forEach(var2x -> var2x.provider().get().generate((var3x, var4x) -> {
            ResourceLocation var5xx = var3.put(RandomSequence.seedForKey(var3x), var3x);
            if (var5xx != null) {
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + var5xx + " and " + var3x);
            }

            var4x.setRandomSequence(var3x);
            if (var2.put(var3x, var4x.setParamSet(var2x.paramSet).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + var3x);
            }
         }));
      ProblemReporter.Collector var4 = new ProblemReporter.Collector();
      ValidationContext var5 = new ValidationContext(var4, LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
         @Nullable
         @Override
         public <T> T getElement(LootDataId<T> var1) {
            return (T)(var1.type() == LootDataType.TABLE ? var2.get(var1.location()) : null);
         }
      });

      for(ResourceLocation var8 : Sets.difference(this.requiredTables, var2.keySet())) {
         var4.report("Missing built-in table: " + var8);
      }

      var2.forEach(
         (var1x, var2x) -> var2x.validate(var5.setParams(var2x.getParamSet()).enterElement("{" + var1x + "}", new LootDataId<>(LootDataType.TABLE, var1x)))
      );
      Multimap var9 = var4.get();
      if (!var9.isEmpty()) {
         var9.forEach((var0, var1x) -> LOGGER.warn("Found validation problem in {}: {}", var0, var1x));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf(var2.entrySet().stream().map(var2x -> {
            ResourceLocation var3xx = (ResourceLocation)var2x.getKey();
            LootTable var4xx = (LootTable)var2x.getValue();
            Path var5xx = this.pathProvider.json(var3xx);
            return DataProvider.saveStable(var1, LootTable.CODEC, var4xx, var5xx);
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
