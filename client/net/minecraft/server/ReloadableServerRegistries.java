package net.minecraft.server;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class ReloadableServerRegistries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RegistrationInfo DEFAULT_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());

   public ReloadableServerRegistries() {
      super();
   }

   public static CompletableFuture<LoadResult> reload(LayeredRegistryAccess<RegistryLayer> var0, List<Registry.PendingTags<?>> var1, ResourceManager var2, Executor var3) {
      List var4 = TagLoader.buildUpdatedLookups(var0.getAccessForLoading(RegistryLayer.RELOADABLE), var1);
      HolderLookup.Provider var5 = HolderLookup.Provider.create(var4.stream());
      RegistryOps var6 = var5.createSerializationContext(JsonOps.INSTANCE);
      List var7 = LootDataType.values().map((var3x) -> scheduleRegistryLoad(var3x, var6, var2, var3)).toList();
      CompletableFuture var8 = Util.sequence(var7);
      return var8.thenApplyAsync((var2x) -> createAndValidateFullContext(var0, var5, var2x), var3);
   }

   private static <T> CompletableFuture<WritableRegistry<?>> scheduleRegistryLoad(LootDataType<T> var0, RegistryOps<JsonElement> var1, ResourceManager var2, Executor var3) {
      return CompletableFuture.supplyAsync(() -> {
         MappedRegistry var3 = new MappedRegistry(var0.registryKey(), Lifecycle.experimental());
         HashMap var4 = new HashMap();
         SimpleJsonResourceReloadListener.scanDirectory(var2, var0.registryKey(), var1, var0.codec(), var4);
         var4.forEach((var2x, var3x) -> var3.register(ResourceKey.create(var0.registryKey(), var2x), var3x, DEFAULT_REGISTRATION_INFO));
         TagLoader.loadTagsForRegistry(var2, var3);
         return var3;
      }, var3);
   }

   private static LoadResult createAndValidateFullContext(LayeredRegistryAccess<RegistryLayer> var0, HolderLookup.Provider var1, List<WritableRegistry<?>> var2) {
      LayeredRegistryAccess var3 = createUpdatedRegistries(var0, var2);
      HolderLookup.Provider var4 = concatenateLookups(var1, var3.getLayer(RegistryLayer.RELOADABLE));
      validateLootRegistries(var4);
      return new LoadResult(var3, var4);
   }

   private static HolderLookup.Provider concatenateLookups(HolderLookup.Provider var0, HolderLookup.Provider var1) {
      return HolderLookup.Provider.create(Stream.concat(var0.listRegistries(), var1.listRegistries()));
   }

   private static void validateLootRegistries(HolderLookup.Provider var0) {
      ProblemReporter.Collector var1 = new ProblemReporter.Collector();
      ValidationContext var2 = new ValidationContext(var1, LootContextParamSets.ALL_PARAMS, var0);
      LootDataType.values().forEach((var2x) -> validateRegistry(var2, var2x, var0));
      var1.get().forEach((var0x, var1x) -> LOGGER.warn("Found loot table element validation problem in {}: {}", var0x, var1x));
   }

   private static LayeredRegistryAccess<RegistryLayer> createUpdatedRegistries(LayeredRegistryAccess<RegistryLayer> var0, List<WritableRegistry<?>> var1) {
      return var0.replaceFrom(RegistryLayer.RELOADABLE, (new RegistryAccess.ImmutableRegistryAccess(var1)).freeze());
   }

   private static <T> void validateRegistry(ValidationContext var0, LootDataType<T> var1, HolderLookup.Provider var2) {
      HolderLookup.RegistryLookup var3 = var2.lookupOrThrow(var1.registryKey());
      var3.listElements().forEach((var2x) -> var1.runValidation(var0, var2x.key(), var2x.value()));
   }

   public static record LoadResult(LayeredRegistryAccess<RegistryLayer> layers, HolderLookup.Provider lookupWithUpdatedTags) {
      public LoadResult(LayeredRegistryAccess<RegistryLayer> var1, HolderLookup.Provider var2) {
         super();
         this.layers = var1;
         this.lookupWithUpdatedTags = var2;
      }
   }

   public static class Holder {
      private final HolderLookup.Provider registries;

      public Holder(HolderLookup.Provider var1) {
         super();
         this.registries = var1;
      }

      public HolderGetter.Provider lookup() {
         return this.registries;
      }

      public Collection<ResourceLocation> getKeys(ResourceKey<? extends Registry<?>> var1) {
         return this.registries.lookupOrThrow(var1).listElementIds().map(ResourceKey::location).toList();
      }

      public LootTable getLootTable(ResourceKey<LootTable> var1) {
         return (LootTable)this.registries.lookup(Registries.LOOT_TABLE).flatMap((var1x) -> var1x.get(var1)).map(net.minecraft.core.Holder::value).orElse(LootTable.EMPTY);
      }
   }
}
