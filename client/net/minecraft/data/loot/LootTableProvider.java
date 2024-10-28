package net.minecraft.data.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceKey<LootTable>> requiredTables;
   private final List<SubProviderEntry> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public LootTableProvider(PackOutput var1, Set<ResourceKey<LootTable>> var2, List<SubProviderEntry> var3, CompletableFuture<HolderLookup.Provider> var4) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
      this.subProviders = var3;
      this.requiredTables = var2;
      this.registries = var4;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose((var2) -> {
         return this.run(var1, var2);
      });
   }

   private CompletableFuture<?> run(CachedOutput var1, HolderLookup.Provider var2) {
      MappedRegistry var3 = new MappedRegistry(Registries.LOOT_TABLE, Lifecycle.experimental());
      Object2ObjectOpenHashMap var4 = new Object2ObjectOpenHashMap();
      this.subProviders.forEach((var3x) -> {
         ((LootTableSubProvider)var3x.provider().get()).generate(var2, (var3xx, var4x) -> {
            ResourceLocation var5 = sequenceIdForLootTable(var3xx);
            ResourceLocation var6 = (ResourceLocation)var4.put(RandomSequence.seedForKey(var5), var5);
            if (var6 != null) {
               String var10000 = String.valueOf(var6);
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + var10000 + " and " + String.valueOf(var3xx.location()));
            }

            var4x.setRandomSequence(var5);
            LootTable var7 = var4x.setParamSet(var3x.paramSet).build();
            var3.register(var3xx, var7, RegistrationInfo.BUILT_IN);
         });
      });
      var3.freeze();
      ProblemReporter.Collector var5 = new ProblemReporter.Collector();
      HolderGetter.Provider var6 = (new RegistryAccess.ImmutableRegistryAccess(List.of(var3))).freeze().asGetterLookup();
      ValidationContext var7 = new ValidationContext(var5, LootContextParamSets.ALL_PARAMS, var6);
      Sets.SetView var8 = Sets.difference(this.requiredTables, var3.registryKeySet());
      Iterator var9 = var8.iterator();

      while(var9.hasNext()) {
         ResourceKey var10 = (ResourceKey)var9.next();
         var5.report("Missing built-in table: " + String.valueOf(var10.location()));
      }

      var3.holders().forEach((var1x) -> {
         ((LootTable)var1x.value()).validate(var7.setParams(((LootTable)var1x.value()).getParamSet()).enterElement("{" + String.valueOf(var1x.key().location()) + "}", var1x.key()));
      });
      Multimap var11 = var5.get();
      if (!var11.isEmpty()) {
         var11.forEach((var0, var1x) -> {
            LOGGER.warn("Found validation problem in {}: {}", var0, var1x);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf((CompletableFuture[])var3.entrySet().stream().map((var3x) -> {
            ResourceKey var4 = (ResourceKey)var3x.getKey();
            LootTable var5 = (LootTable)var3x.getValue();
            Path var6 = this.pathProvider.json(var4.location());
            return DataProvider.saveStable(var1, var2, LootTable.DIRECT_CODEC, var5, var6);
         }).toArray((var0) -> {
            return new CompletableFuture[var0];
         }));
      }
   }

   private static ResourceLocation sequenceIdForLootTable(ResourceKey<LootTable> var0) {
      return var0.location();
   }

   public final String getName() {
      return "Loot Tables";
   }

   public static record SubProviderEntry(Supplier<LootTableSubProvider> provider, LootContextParamSet paramSet) {
      final LootContextParamSet paramSet;

      public SubProviderEntry(Supplier<LootTableSubProvider> var1, LootContextParamSet var2) {
         super();
         this.provider = var1;
         this.paramSet = var2;
      }

      public Supplier<LootTableSubProvider> provider() {
         return this.provider;
      }

      public LootContextParamSet paramSet() {
         return this.paramSet;
      }
   }
}
