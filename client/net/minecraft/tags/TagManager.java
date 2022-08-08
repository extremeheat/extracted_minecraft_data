package net.minecraft.tags;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class TagManager implements PreparableReloadListener {
   private static final Map<ResourceKey<? extends Registry<?>>, String> CUSTOM_REGISTRY_DIRECTORIES;
   private final RegistryAccess registryAccess;
   private List<LoadResult<?>> results = List.of();

   public TagManager(RegistryAccess var1) {
      super();
      this.registryAccess = var1;
   }

   public List<LoadResult<?>> getResult() {
      return this.results;
   }

   public static String getTagDir(ResourceKey<? extends Registry<?>> var0) {
      String var1 = (String)CUSTOM_REGISTRY_DIRECTORIES.get(var0);
      return var1 != null ? var1 : "tags/" + var0.location().getPath();
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      List var7 = this.registryAccess.registries().map((var3x) -> {
         return this.createLoader(var2, var5, var3x);
      }).toList();
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])var7.toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var2x) -> {
         this.results = (List)var7.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());
      }, var6);
   }

   private <T> CompletableFuture<LoadResult<T>> createLoader(ResourceManager var1, Executor var2, RegistryAccess.RegistryEntry<T> var3) {
      ResourceKey var4 = var3.key();
      Registry var5 = var3.value();
      TagLoader var6 = new TagLoader((var2x) -> {
         return var5.getHolder(ResourceKey.create(var4, var2x));
      }, getTagDir(var4));
      return CompletableFuture.supplyAsync(() -> {
         return new LoadResult(var4, var6.loadAndBuild(var1));
      }, var2);
   }

   static {
      CUSTOM_REGISTRY_DIRECTORIES = Map.of(Registry.BLOCK_REGISTRY, "tags/blocks", Registry.ENTITY_TYPE_REGISTRY, "tags/entity_types", Registry.FLUID_REGISTRY, "tags/fluids", Registry.GAME_EVENT_REGISTRY, "tags/game_events", Registry.ITEM_REGISTRY, "tags/items");
   }

   public static record LoadResult<T>(ResourceKey<? extends Registry<T>> a, Map<ResourceLocation, Collection<Holder<T>>> b) {
      private final ResourceKey<? extends Registry<T>> key;
      private final Map<ResourceLocation, Collection<Holder<T>>> tags;

      public LoadResult(ResourceKey<? extends Registry<T>> var1, Map<ResourceLocation, Collection<Holder<T>>> var2) {
         super();
         this.key = var1;
         this.tags = var2;
      }

      public ResourceKey<? extends Registry<T>> key() {
         return this.key;
      }

      public Map<ResourceLocation, Collection<Holder<T>>> tags() {
         return this.tags;
      }
   }
}
