package net.minecraft.data.loot;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
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
      HashMap var2 = Maps.newHashMap();
      this.subProviders.forEach(var1x -> var1x.provider().get().generate((var2x, var3x) -> {
            if (var2.put(var2x, var3x.setParamSet(var1x.paramSet).build()) != null) {
               throw new IllegalStateException("Duplicate loot table " + var2x);
            }
         }));
      ValidationContext var3 = new ValidationContext(LootContextParamSets.ALL_PARAMS, var0 -> null, var2::get);

      for(ResourceLocation var6 : Sets.difference(this.requiredTables, var2.keySet())) {
         var3.reportProblem("Missing built-in table: " + var6);
      }

      var2.forEach((var1x, var2x) -> LootTables.validate(var3, var1x, var2x));
      Multimap var7 = var3.getProblems();
      if (!var7.isEmpty()) {
         var7.forEach((var0, var1x) -> LOGGER.warn("Found validation problem in {}: {}", var0, var1x));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf(var2.entrySet().stream().map(var2x -> {
            ResourceLocation var3x = (ResourceLocation)var2x.getKey();
            LootTable var4 = (LootTable)var2x.getValue();
            Path var5 = this.pathProvider.json(var3x);
            return DataProvider.saveStable(var1, LootTables.serialize(var4), var5);
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
