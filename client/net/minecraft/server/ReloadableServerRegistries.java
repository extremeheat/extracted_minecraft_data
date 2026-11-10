package net.minecraft.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.storage.loot.LootTable;

public class ReloadableServerRegistries {
   public ReloadableServerRegistries() {
      super();
   }

   public static CompletableFuture<LoadResult> reload(final LayeredRegistryAccess<RegistryLayer> context, final List<Registry.PendingTags<?>> updatedContextTags, final ResourceManager manager, final Executor executor) {
      List<HolderLookup.RegistryLookup<?>> contextRegistriesWithTags = TagLoader.buildUpdatedLookups(context.getAccessForLoading(RegistryLayer.RELOADABLE), updatedContextTags);
      HolderLookup.Provider loadingContextWithTags = HolderLookup.Provider.create(contextRegistriesWithTags.stream());
      return RegistryDataLoader.load(manager, contextRegistriesWithTags, RegistryDataLoader.RELOADABLE_REGISTRIES, executor).thenApplyAsync((newlyLoadedRegistries) -> createAndValidateFullContext(context, loadingContextWithTags, newlyLoadedRegistries), executor);
   }

   private static LoadResult createAndValidateFullContext(final LayeredRegistryAccess<RegistryLayer> contextLayers, final HolderLookup.Provider contextLookupWithUpdatedTags, final RegistryAccess.Frozen reloadableRegistries) {
      LayeredRegistryAccess<RegistryLayer> fullLayers = contextLayers.replaceFrom(RegistryLayer.RELOADABLE, reloadableRegistries);
      HolderLookup.Provider fullLookupWithUpdatedTags = concatenateLookups(contextLookupWithUpdatedTags, reloadableRegistries);
      VanillaRegistries.validateLootData(fullLookupWithUpdatedTags);
      return new LoadResult(fullLayers, fullLookupWithUpdatedTags);
   }

   private static HolderLookup.Provider concatenateLookups(final HolderLookup.Provider first, final HolderLookup.Provider second) {
      return HolderLookup.Provider.create(Stream.concat(first.listRegistries(), second.listRegistries()));
   }

   public static record LoadResult(LayeredRegistryAccess<RegistryLayer> layers, HolderLookup.Provider lookupWithUpdatedTags) {
      public LoadResult {
         super();
      }
   }

   public static class Holder {
      private final HolderLookup.Provider registries;

      public Holder(final HolderLookup.Provider registries) {
         super();
         this.registries = registries;
      }

      public HolderLookup.Provider lookup() {
         return this.registries;
      }

      public LootTable getLootTable(final ResourceKey<LootTable> id) {
         return (LootTable)this.registries.lookup(Registries.LOOT_TABLE).flatMap((r) -> r.get(id)).map(net.minecraft.core.Holder::value).orElse(LootTable.EMPTY);
      }
   }
}
