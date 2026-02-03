package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.level.WorldDataConfiguration;

public class WorldLoader {
   public WorldLoader() {
      super();
   }

   public static <D, R> CompletableFuture<R> load(final InitConfig config, final WorldDataSupplier<D> worldDataSupplier, final ResultFactory<D, R> resultFactory, final Executor backgroundExecutor, final Executor mainThreadExecutor) {
      PackConfig var10000 = config.packConfig;
      Objects.requireNonNull(var10000);
      return CompletableFuture.supplyAsync(var10000::createResourceManager, mainThreadExecutor).thenComposeAsync((packsAndResourceManager) -> {
         CloseableResourceManager resources = (CloseableResourceManager)packsAndResourceManager.getSecond();
         LayeredRegistryAccess<RegistryLayer> initialLayers = RegistryLayer.createRegistryAccess();
         List<Registry.PendingTags<?>> staticLayerTags = TagLoader.loadTagsForExistingRegistries(resources, initialLayers.getLayer(RegistryLayer.STATIC));
         RegistryAccess.Frozen worldgenLoadContext = initialLayers.getAccessForLoading(RegistryLayer.WORLDGEN);
         List<HolderLookup.RegistryLookup<?>> worldgenContextRegistries = TagLoader.buildUpdatedLookups(worldgenLoadContext, staticLayerTags);
         return RegistryDataLoader.load((ResourceManager)resources, worldgenContextRegistries, RegistryDataLoader.WORLDGEN_REGISTRIES, backgroundExecutor).thenComposeAsync((loadedWorldgenRegistries) -> {
            List<HolderLookup.RegistryLookup<?>> dimensionContextRegistries = Stream.concat(worldgenContextRegistries.stream(), loadedWorldgenRegistries.listRegistries()).toList();
            return RegistryDataLoader.load((ResourceManager)resources, dimensionContextRegistries, RegistryDataLoader.DIMENSION_REGISTRIES, backgroundExecutor).thenComposeAsync((initialWorldgenDimensions) -> {
               WorldDataConfiguration worldDataConfiguration = (WorldDataConfiguration)packsAndResourceManager.getFirst();
               HolderLookup.Provider dimensionContextProvider = HolderLookup.Provider.create(dimensionContextRegistries.stream());
               DataLoadOutput<D> worldDataAndRegistries = worldDataSupplier.get(new DataLoadContext(resources, worldDataConfiguration, dimensionContextProvider, initialWorldgenDimensions));
               LayeredRegistryAccess<RegistryLayer> resourcesLoadContext = initialLayers.replaceFrom(RegistryLayer.WORLDGEN, loadedWorldgenRegistries, worldDataAndRegistries.finalDimensions);
               return ReloadableServerResources.loadResources(resources, resourcesLoadContext, staticLayerTags, worldDataConfiguration.enabledFeatures(), config.commandSelection(), config.functionCompilationPermissions(), backgroundExecutor, mainThreadExecutor).whenComplete((managers, throwable) -> {
                  if (throwable != null) {
                     resources.close();
                  }

               }).thenApplyAsync((managers) -> {
                  managers.updateComponentsAndStaticRegistryTags();
                  return resultFactory.create(resources, managers, resourcesLoadContext, worldDataAndRegistries.cookie);
               }, mainThreadExecutor);
            }, backgroundExecutor);
         }, backgroundExecutor);
      }, backgroundExecutor);
   }

   public static record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, HolderLookup.Provider datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
      public DataLoadContext {
         super();
      }
   }

   public static record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {
      public DataLoadOutput {
         super();
      }
   }

   public static record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
      public PackConfig {
         super();
      }

      public Pair<WorldDataConfiguration, CloseableResourceManager> createResourceManager() {
         WorldDataConfiguration newPackConfig = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig, this.initMode, this.safeMode);
         List<PackResources> openedPacks = this.packRepository.openAllSelected();
         CloseableResourceManager resources = new MultiPackResourceManager(PackType.SERVER_DATA, openedPacks);
         return Pair.of(newPackConfig, resources);
      }
   }

   public static record InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, PermissionSet functionCompilationPermissions) {
      public InitConfig {
         super();
      }
   }

   @FunctionalInterface
   public interface ResultFactory<D, R> {
      R create(CloseableResourceManager resources, ReloadableServerResources managers, LayeredRegistryAccess<RegistryLayer> registries, D cookie);
   }

   @FunctionalInterface
   public interface WorldDataSupplier<D> {
      DataLoadOutput<D> get(DataLoadContext context);
   }
}
