package net.minecraft.data.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceKey<LootTable>> requiredTables;
   private final List<LootTableProvider.SubProviderEntry> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public LootTableProvider(
      PackOutput var1, Set<ResourceKey<LootTable>> var2, List<LootTableProvider.SubProviderEntry> var3, CompletableFuture<HolderLookup.Provider> var4
   ) {
      super();
      this.pathProvider = var1.createRegistryElementsPathProvider(Registries.LOOT_TABLE);
      this.subProviders = var3;
      this.requiredTables = var2;
      this.registries = var4;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose(var2 -> this.run(var1, var2));
   }

   private CompletableFuture<?> run(CachedOutput var1, HolderLookup.Provider var2) {
      MappedRegistry var3 = new MappedRegistry<>(Registries.LOOT_TABLE, Lifecycle.experimental());
      Object2ObjectOpenHashMap var4 = new Object2ObjectOpenHashMap();
      this.subProviders.forEach(var3x -> var3x.provider().apply(var2).generate((var3xx, var4x) -> {
            ResourceLocation var5x = sequenceIdForLootTable(var3xx);
            ResourceLocation var6x = var4.put(RandomSequence.seedForKey(var5x), var5x);
            if (var6x != null) {
               Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + var6x + " and " + var3xx.location());
            }

            var4x.setRandomSequence(var5x);
            LootTable var7x = var4x.setParamSet(var3x.paramSet).build();
            var3.register(var3xx, var7x, RegistrationInfo.BUILT_IN);
         }));
      var3.freeze();
      ProblemReporter.Collector var5 = new ProblemReporter.Collector();
      RegistryAccess.Frozen var6 = new RegistryAccess.ImmutableRegistryAccess(List.of(var3)).freeze();
      ValidationContext var7 = new ValidationContext(var5, LootContextParamSets.ALL_PARAMS, var6);

      for (ResourceKey var10 : Sets.difference(this.requiredTables, var3.registryKeySet())) {
         var5.report("Missing built-in table: " + var10.location());
      }

      var3.listElements()
         .forEach(
            var1x -> ((LootTable)var1x.value())
                  .validate(var7.setContextKeySet(((LootTable)var1x.value()).getParamSet()).enterElement("{" + var1x.key().location() + "}", var1x.key()))
         );
      Multimap var11 = var5.get();
      if (!var11.isEmpty()) {
         var11.forEach((var0, var1x) -> LOGGER.warn("Found validation problem in {}: {}", var0, var1x));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf(var3.entrySet().stream().map(var3x -> {
            ResourceKey var4x = (ResourceKey)var3x.getKey();
            LootTable var5x = (LootTable)var3x.getValue();
            Path var6x = this.pathProvider.json(var4x.location());
            return DataProvider.saveStable(var1, var2, LootTable.DIRECT_CODEC, var5x, var6x);
         }).toArray(CompletableFuture[]::new));
      }
   }

   private static ResourceLocation sequenceIdForLootTable(ResourceKey<LootTable> var0) {
      return var0.location();
   }

   @Override
   public final String getName() {
      return "Loot Tables";
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
