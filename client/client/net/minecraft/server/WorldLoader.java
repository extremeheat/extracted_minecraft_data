package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class WorldLoader {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldLoader() {
      super();
   }

   public static <D, R> CompletableFuture<R> load(
      WorldLoader.InitConfig var0, WorldLoader.WorldDataSupplier<D> var1, WorldLoader.ResultFactory<D, R> var2, Executor var3, Executor var4
   ) {
      try {
         Pair var5 = var0.packConfig.createResourceManager();
         CloseableResourceManager var6 = (CloseableResourceManager)var5.getSecond();
         LayeredRegistryAccess var7 = RegistryLayer.createRegistryAccess();
         LayeredRegistryAccess var8 = loadAndReplaceLayer(var6, var7, RegistryLayer.WORLDGEN, RegistryDataLoader.WORLDGEN_REGISTRIES);
         RegistryAccess.Frozen var9 = var8.getAccessForLoading(RegistryLayer.DIMENSIONS);
         RegistryAccess.Frozen var10 = RegistryDataLoader.load(var6, var9, RegistryDataLoader.DIMENSION_REGISTRIES);
         WorldDataConfiguration var11 = (WorldDataConfiguration)var5.getFirst();
         WorldLoader.DataLoadOutput var12 = var1.get(new WorldLoader.DataLoadContext(var6, var11, var9, var10));
         LayeredRegistryAccess var13 = var8.replaceFrom(RegistryLayer.DIMENSIONS, var12.finalDimensions);
         return ReloadableServerResources.loadResources(
               var6, var13, var11.enabledFeatures(), var0.commandSelection(), var0.functionCompilationLevel(), var3, var4
            )
            .whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  var6.close();
               }
            })
            .thenApplyAsync(var4x -> {
               var4x.updateRegistryTags();
               return (R)var2.create(var6, var4x, var13, var12.cookie);
            }, var4);
      } catch (Exception var14) {
         return CompletableFuture.failedFuture(var14);
      }
   }

   private static RegistryAccess.Frozen loadLayer(
      ResourceManager var0, LayeredRegistryAccess<RegistryLayer> var1, RegistryLayer var2, List<RegistryDataLoader.RegistryData<?>> var3
   ) {
      RegistryAccess.Frozen var4 = var1.getAccessForLoading(var2);
      return RegistryDataLoader.load(var0, var4, var3);
   }

   private static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(
      ResourceManager var0, LayeredRegistryAccess<RegistryLayer> var1, RegistryLayer var2, List<RegistryDataLoader.RegistryData<?>> var3
   ) {
      RegistryAccess.Frozen var4 = loadLayer(var0, var1, var2, var3);
      return var1.replaceFrom(var2, var4);
   }

   public static record DataLoadContext(
      ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions
   ) {
      public DataLoadContext(
         ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions
      ) {
         super();
         this.resources = resources;
         this.dataConfiguration = dataConfiguration;
         this.datapackWorldgen = datapackWorldgen;
         this.datapackDimensions = datapackDimensions;
      }
   }

   public static record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {

      public DataLoadOutput(D cookie, RegistryAccess.Frozen finalDimensions) {
         super();
         this.cookie = (D)cookie;
         this.finalDimensions = finalDimensions;
      }
   }

   public static record InitConfig(WorldLoader.PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {

      public InitConfig(WorldLoader.PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
         super();
         this.packConfig = packConfig;
         this.commandSelection = commandSelection;
         this.functionCompilationLevel = functionCompilationLevel;
      }
   }

   public static record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
      public PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
         super();
         this.packRepository = packRepository;
         this.initialDataConfig = initialDataConfig;
         this.safeMode = safeMode;
         this.initMode = initMode;
      }

      public Pair<WorldDataConfiguration, CloseableResourceManager> createResourceManager() {
         FeatureFlagSet var1 = this.initMode ? FeatureFlags.REGISTRY.allFlags() : this.initialDataConfig.enabledFeatures();
         WorldDataConfiguration var2 = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig.dataPacks(), this.safeMode, var1);
         if (!this.initMode) {
            var2 = var2.expandFeatures(this.initialDataConfig.enabledFeatures());
         }

         List var3 = this.packRepository.openAllSelected();
         MultiPackResourceManager var4 = new MultiPackResourceManager(PackType.SERVER_DATA, var3);
         return Pair.of(var2, var4);
      }
   }

   @FunctionalInterface
   public interface ResultFactory<D, R> {
      R create(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, D var4);
   }

   @FunctionalInterface
   public interface WorldDataSupplier<D> {
      WorldLoader.DataLoadOutput<D> get(WorldLoader.DataLoadContext var1);
   }
}
