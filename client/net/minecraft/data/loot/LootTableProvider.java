package net.minecraft.data.loot;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceKey<LootTable>> requiredTables;
   private final List<SubProviderEntry> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public LootTableProvider(final PackOutput output, final Set<ResourceKey<LootTable>> requiredTables, final List<SubProviderEntry> subProviders, final CompletableFuture<HolderLookup.Provider> registries) {
      super();
      this.pathProvider = output.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
      this.subProviders = subProviders;
      this.requiredTables = requiredTables;
      this.registries = registries;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      return this.registries.thenCompose((registries) -> this.run(cache, registries));
   }

   private CompletableFuture<?> run(final CachedOutput cache, final HolderLookup.Provider registries) {
      WritableRegistry<LootTable> tables = new MappedRegistry<LootTable>(Registries.LOOT_TABLE, Lifecycle.experimental());
      Map<RandomSupport.Seed128bit, Identifier> randomSequenceSeeds = new Object2ObjectOpenHashMap();
      this.subProviders.forEach((subProvider) -> ((LootTableSubProvider)subProvider.provider().apply(registries)).generate((id, lootTable) -> {
            Identifier sequenceId = sequenceIdForLootTable(id);
            Identifier previous = (Identifier)randomSequenceSeeds.put(RandomSequence.seedForKey(sequenceId), sequenceId);
            if (previous != null) {
               String var10000 = String.valueOf(previous);
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + var10000 + " and " + String.valueOf(id.identifier()));
            }

            lootTable.setRandomSequence(sequenceId);
            LootTable table = lootTable.setParamSet(subProvider.paramSet).build();
            tables.register(id, table, RegistrationInfo.BUILT_IN);
         }));
      tables.freeze();
      ProblemReporter.Collector problems = new ProblemReporter.Collector();
      HolderGetter.Provider validationProvider = (new RegistryAccess.ImmutableRegistryAccess(List.of(tables))).freeze();
      ValidationContextSource validationContext = new ValidationContextSource(problems, validationProvider);

      for(ResourceKey<LootTable> missingTable : Sets.difference(this.requiredTables, tables.registryKeySet())) {
         problems.report(new MissingTableProblem(missingTable));
      }

      LootDataType.TABLE.runValidation(validationContext, tables);
      if (!problems.isEmpty()) {
         problems.forEach((id, problem) -> LOGGER.warn("Found validation problem in {}: {}", id, problem.description()));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf((CompletableFuture[])tables.entrySet().stream().map((entry) -> {
            ResourceKey<LootTable> id = (ResourceKey)entry.getKey();
            LootTable table = (LootTable)entry.getValue();
            Path path = this.pathProvider.json(id.identifier());
            return DataProvider.saveStable(cache, registries, LootTable.DIRECT_CODEC, table, path);
         }).toArray((x$0) -> new CompletableFuture[x$0]));
      }
   }

   private static Identifier sequenceIdForLootTable(final ResourceKey<LootTable> id) {
      return id.identifier();
   }

   public final String getName() {
      return "Loot Tables";
   }

   public static record SubProviderEntry(Function<HolderLookup.Provider, LootTableSubProvider> provider, ContextKeySet paramSet) {
      public SubProviderEntry {
         super();
      }
   }

   public static record MissingTableProblem(ResourceKey<LootTable> id) implements ProblemReporter.Problem {
      public MissingTableProblem {
         super();
      }

      public String description() {
         return "Missing built-in table: " + String.valueOf(this.id.identifier());
      }
   }
}
