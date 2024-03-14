package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootDataManager implements PreparableReloadListener, LootDataResolver {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().create();
   public static final LootDataId<LootTable> EMPTY_LOOT_TABLE_KEY = new LootDataId<>(LootDataType.TABLE, BuiltInLootTables.EMPTY);
   private final HolderLookup.Provider registries;
   private Map<LootDataId<?>, ?> elements = Map.of();
   private Multimap<LootDataType<?>, ResourceLocation> typeKeys = ImmutableMultimap.of();

   public LootDataManager(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
   }

   @Override
   public final CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      HashMap var7 = new HashMap();
      CompletableFuture[] var8 = LootDataType.values()
         .map(var4x -> scheduleElementParse(var4x, this.registries, var2, var5, var7))
         .toArray(var0 -> new CompletableFuture[var0]);
      return CompletableFuture.allOf(var8).thenCompose(var1::wait).thenAcceptAsync(var2x -> this.apply(var7), var6);
   }

   private static <T> CompletableFuture<?> scheduleElementParse(
      LootDataType<T> var0, HolderLookup.Provider var1, ResourceManager var2, Executor var3, Map<LootDataType<?>, Map<ResourceLocation, ?>> var4
   ) {
      RegistryOps var5 = var1.createSerializationContext(JsonOps.INSTANCE);
      HashMap var6 = new HashMap();
      var4.put(var0, var6);
      return CompletableFuture.runAsync(() -> {
         HashMap var4xx = new HashMap();
         SimpleJsonResourceReloadListener.scanDirectory(var2, var0.directory(), GSON, var4xx);
         var4xx.forEach((var3xx, var4xx) -> var0.deserialize(var3xx, var5, var4xx).ifPresent(var2xxx -> var6.put(var3xx, var2xxx)));
      }, var3);
   }

   private void apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> var1) {
      Object var2 = ((Map)var1.get(LootDataType.TABLE)).remove(BuiltInLootTables.EMPTY);
      if (var2 != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", BuiltInLootTables.EMPTY);
      }

      Builder var3 = ImmutableMap.builder();
      com.google.common.collect.ImmutableMultimap.Builder var4 = ImmutableMultimap.builder();
      var1.forEach((var2x, var3x) -> var3x.forEach((var3xx, var4x) -> {
            var3.put(new LootDataId(var2x, var3xx), var4x);
            var4.put(var2x, var3xx);
         }));
      var3.put(EMPTY_LOOT_TABLE_KEY, LootTable.EMPTY);
      ProblemReporter.Collector var5 = new ProblemReporter.Collector();
      final ImmutableMap var6 = var3.build();
      ValidationContext var7 = new ValidationContext(var5, LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
         @Nullable
         @Override
         public <T> T getElement(LootDataId<T> var1) {
            return (T)var6.get(var1);
         }
      });
      var6.forEach((var1x, var2x) -> castAndValidate(var7, var1x, var2x));
      var5.get().forEach((var0, var1x) -> LOGGER.warn("Found loot table element validation problem in {}: {}", var0, var1x));
      this.elements = var6;
      this.typeKeys = var4.build();
   }

   private static <T> void castAndValidate(ValidationContext var0, LootDataId<T> var1, Object var2) {
      var1.type().runValidation(var0, var1, var2);
   }

   @Nullable
   @Override
   public <T> T getElement(LootDataId<T> var1) {
      return (T)this.elements.get(var1);
   }

   public Collection<ResourceLocation> getKeys(LootDataType<?> var1) {
      return this.typeKeys.get(var1);
   }
}
