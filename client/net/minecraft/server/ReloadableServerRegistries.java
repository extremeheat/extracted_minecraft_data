package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class ReloadableServerRegistries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().create();
   private static final RegistrationInfo DEFAULT_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());

   public ReloadableServerRegistries() {
      super();
   }

   public static CompletableFuture<LayeredRegistryAccess<RegistryLayer>> reload(LayeredRegistryAccess<RegistryLayer> var0, ResourceManager var1, Executor var2) {
      RegistryAccess.Frozen var3 = var0.getAccessForLoading(RegistryLayer.RELOADABLE);
      RegistryOps var4 = new ReloadableServerRegistries.EmptyTagLookupWrapper(var3).createSerializationContext(JsonOps.INSTANCE);
      List var5 = LootDataType.values().map(var3x -> scheduleElementParse((LootDataType<?>)var3x, var4, var1, var2)).toList();
      CompletableFuture var6 = Util.sequence(var5);
      return var6.thenApplyAsync(var1x -> apply(var0, (List<WritableRegistry<?>>)var1x), var2);
   }

   private static <T> CompletableFuture<WritableRegistry<?>> scheduleElementParse(
      LootDataType<T> var0, RegistryOps<JsonElement> var1, ResourceManager var2, Executor var3
   ) {
      return CompletableFuture.supplyAsync(
         () -> {
            MappedRegistry var3x = new MappedRegistry(var0.registryKey(), Lifecycle.experimental());
            HashMap var4 = new HashMap();
            String var5 = Registries.elementsDirPath(var0.registryKey());
            SimpleJsonResourceReloadListener.scanDirectory(var2, var5, GSON, var4);
            var4.forEach(
               (var3xx, var4x) -> var0.deserialize(var3xx, var1, var4x)
                     .ifPresent(var3xxx -> var3x.register(ResourceKey.create(var0.registryKey(), var3xx), var3xxx, DEFAULT_REGISTRATION_INFO))
            );
            return var3x;
         },
         var3
      );
   }

   private static LayeredRegistryAccess<RegistryLayer> apply(LayeredRegistryAccess<RegistryLayer> var0, List<WritableRegistry<?>> var1) {
      LayeredRegistryAccess var2 = createUpdatedRegistries(var0, var1);
      ProblemReporter.Collector var3 = new ProblemReporter.Collector();
      RegistryAccess.Frozen var4 = var2.compositeAccess();
      ValidationContext var5 = new ValidationContext(var3, LootContextParamSets.ALL_PARAMS, var4.asGetterLookup());
      LootDataType.values().forEach(var2x -> validateRegistry(var5, (LootDataType<?>)var2x, var4));
      var3.get().forEach((var0x, var1x) -> LOGGER.warn("Found loot table element validation problem in {}: {}", var0x, var1x));
      return var2;
   }

   private static LayeredRegistryAccess<RegistryLayer> createUpdatedRegistries(LayeredRegistryAccess<RegistryLayer> var0, List<WritableRegistry<?>> var1) {
      RegistryAccess.ImmutableRegistryAccess var2 = new RegistryAccess.ImmutableRegistryAccess(var1);
      ((WritableRegistry)var2.<LootTable>registryOrThrow(Registries.LOOT_TABLE)).register(BuiltInLootTables.EMPTY, LootTable.EMPTY, DEFAULT_REGISTRATION_INFO);
      return var0.replaceFrom(RegistryLayer.RELOADABLE, var2.freeze());
   }

   private static <T> void validateRegistry(ValidationContext var0, LootDataType<T> var1, RegistryAccess var2) {
      Registry var3 = var2.registryOrThrow(var1.registryKey());
      var3.holders().forEach(var2x -> var1.runValidation(var0, var2x.key(), var2x.value()));
   }

   static class EmptyTagLookupWrapper implements HolderLookup.Provider {
      private final RegistryAccess registryAccess;

      EmptyTagLookupWrapper(RegistryAccess var1) {
         super();
         this.registryAccess = var1;
      }

      @Override
      public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
         return this.registryAccess.listRegistries();
      }

      @Override
      public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
         return this.registryAccess.registry(var1).map(Registry::asTagAddingLookup);
      }
   }

   public static class Holder {
      private final RegistryAccess.Frozen registries;

      public Holder(RegistryAccess.Frozen var1) {
         super();
         this.registries = var1;
      }

      public RegistryAccess.Frozen get() {
         return this.registries;
      }

      public HolderGetter.Provider lookup() {
         return this.registries.asGetterLookup();
      }

      public Collection<ResourceLocation> getKeys(ResourceKey<? extends Registry<?>> var1) {
         return this.registries.registry(var1).stream().flatMap(var0 -> var0.holders().map(var0x -> var0x.key().location())).toList();
      }

      public LootTable getLootTable(ResourceKey<LootTable> var1) {
         return this.registries.lookup(Registries.LOOT_TABLE).flatMap(var1x -> var1x.get(var1)).map(net.minecraft.core.Holder::value).orElse(LootTable.EMPTY);
      }
   }
}
