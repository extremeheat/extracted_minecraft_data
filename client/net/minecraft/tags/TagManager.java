package net.minecraft.tags;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class TagManager implements PreparableReloadListener {
   private static final Map<ResourceKey<? extends Registry<?>>, String> CUSTOM_REGISTRY_DIRECTORIES = Map.of(
      Registries.BLOCK,
      "tags/blocks",
      Registries.ENTITY_TYPE,
      "tags/entity_types",
      Registries.FLUID,
      "tags/fluids",
      Registries.GAME_EVENT,
      "tags/game_events",
      Registries.ITEM,
      "tags/items"
   );
   private final RegistryAccess registryAccess;
   private List<TagManager.LoadResult<?>> results = List.of();

   public TagManager(RegistryAccess var1) {
      super();
      this.registryAccess = var1;
   }

   public List<TagManager.LoadResult<?>> getResult() {
      return this.results;
   }

   public static String getTagDir(ResourceKey<? extends Registry<?>> var0) {
      String var1 = CUSTOM_REGISTRY_DIRECTORIES.get(var0);
      return var1 != null ? var1 : "tags/" + var0.location().getPath();
   }

   @Override
   public CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      List var7 = this.registryAccess.registries().map(var3x -> this.createLoader(var2, var5, var3x)).toList();
      return CompletableFuture.allOf(var7.toArray(var0 -> new CompletableFuture[var0]))
         .thenCompose(var1::wait)
         .thenAcceptAsync(var2x -> this.results = var7.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList()), var6);
   }

   private <T> CompletableFuture<TagManager.LoadResult<T>> createLoader(ResourceManager var1, Executor var2, RegistryAccess.RegistryEntry<T> var3) {
      ResourceKey var4 = var3.key();
      Registry var5 = var3.value();
      TagLoader var6 = new TagLoader<>(var5::getHolder, getTagDir(var4));
      return CompletableFuture.supplyAsync(() -> new TagManager.LoadResult(var4, var6.loadAndBuild(var1)), var2);
   }

   public static record LoadResult<T>(ResourceKey<? extends Registry<T>> a, Map<ResourceLocation, Collection<Holder<T>>> b) {
      private final ResourceKey<? extends Registry<T>> key;
      private final Map<ResourceLocation, Collection<Holder<T>>> tags;

      public LoadResult(ResourceKey<? extends Registry<T>> var1, Map<ResourceLocation, Collection<Holder<T>>> var2) {
         super();
         this.key = var1;
         this.tags = var2;
      }
   }
}
