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
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class WorldLoader {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldLoader() {
      super();
   }

   public static <D, R> CompletableFuture<R> load(InitConfig var0, WorldDataSupplier<D> var1, ResultFactory<D, R> var2, Executor var3, Executor var4) {
      try {
         Pair var5 = var0.packConfig.createResourceManager();
         CloseableResourceManager var6 = (CloseableResourceManager)var5.getSecond();
         LayeredRegistryAccess var7 = RegistryLayer.createRegistryAccess();
         LayeredRegistryAccess var8 = loadAndReplaceLayer(var6, var7, RegistryLayer.WORLDGEN, RegistryDataLoader.WORLDGEN_REGISTRIES);
         RegistryAccess.Frozen var9 = var8.getAccessForLoading(RegistryLayer.DIMENSIONS);
         RegistryAccess.Frozen var10 = RegistryDataLoader.load((ResourceManager)var6, var9, RegistryDataLoader.DIMENSION_REGISTRIES);
         WorldDataConfiguration var11 = (WorldDataConfiguration)var5.getFirst();
         DataLoadOutput var12 = var1.get(new DataLoadContext(var6, var11, var9, var10));
         LayeredRegistryAccess var13 = var8.replaceFrom(RegistryLayer.DIMENSIONS, (RegistryAccess.Frozen[])(var12.finalDimensions));
         return ReloadableServerResources.loadResources(var6, var13, var11.enabledFeatures(), var0.commandSelection(), var0.functionCompilationLevel(), var3, var4).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               var6.close();
            }

         }).thenApplyAsync((var4x) -> {
            var4x.updateRegistryTags();
            return var2.create(var6, var4x, var13, var12.cookie);
         }, var4);
      } catch (Exception var14) {
         return CompletableFuture.failedFuture(var14);
      }
   }

   private static RegistryAccess.Frozen loadLayer(ResourceManager var0, LayeredRegistryAccess<RegistryLayer> var1, RegistryLayer var2, List<RegistryDataLoader.RegistryData<?>> var3) {
      RegistryAccess.Frozen var4 = var1.getAccessForLoading(var2);
      return RegistryDataLoader.load((ResourceManager)var0, var4, var3);
   }

   private static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(ResourceManager var0, LayeredRegistryAccess<RegistryLayer> var1, RegistryLayer var2, List<RegistryDataLoader.RegistryData<?>> var3) {
      RegistryAccess.Frozen var4 = loadLayer(var0, var1, var2, var3);
      return var1.replaceFrom(var2, (RegistryAccess.Frozen[])(var4));
   }

   public static record InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
      final PackConfig packConfig;

      public InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
         super();
         this.packConfig = packConfig;
         this.commandSelection = commandSelection;
         this.functionCompilationLevel = functionCompilationLevel;
      }

      public PackConfig packConfig() {
         return this.packConfig;
      }

      public Commands.CommandSelection commandSelection() {
         return this.commandSelection;
      }

      public int functionCompilationLevel() {
         return this.functionCompilationLevel;
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
         WorldDataConfiguration var1 = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig, this.initMode, this.safeMode);
         List var2 = this.packRepository.openAllSelected();
         MultiPackResourceManager var3 = new MultiPackResourceManager(PackType.SERVER_DATA, var2);
         return Pair.of(var1, var3);
      }

      public PackRepository packRepository() {
         return this.packRepository;
      }

      public WorldDataConfiguration initialDataConfig() {
         return this.initialDataConfig;
      }

      public boolean safeMode() {
         return this.safeMode;
      }

      public boolean initMode() {
         return this.initMode;
      }
   }

   public static record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
      public DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
         super();
         this.resources = resources;
         this.dataConfiguration = dataConfiguration;
         this.datapackWorldgen = datapackWorldgen;
         this.datapackDimensions = datapackDimensions;
      }

      public ResourceManager resources() {
         return this.resources;
      }

      public WorldDataConfiguration dataConfiguration() {
         return this.dataConfiguration;
      }

      public RegistryAccess.Frozen datapackWorldgen() {
         return this.datapackWorldgen;
      }

      public RegistryAccess.Frozen datapackDimensions() {
         return this.datapackDimensions;
      }
   }

   @FunctionalInterface
   public interface WorldDataSupplier<D> {
      DataLoadOutput<D> get(DataLoadContext var1);
   }

   public static record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {
      final D cookie;
      final RegistryAccess.Frozen finalDimensions;

      public DataLoadOutput(D cookie, RegistryAccess.Frozen finalDimensions) {
         super();
         this.cookie = cookie;
         this.finalDimensions = finalDimensions;
      }

      public D cookie() {
         return this.cookie;
      }

      public RegistryAccess.Frozen finalDimensions() {
         return this.finalDimensions;
      }
   }

   @FunctionalInterface
   public interface ResultFactory<D, R> {
      R create(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, D var4);
   }
}
