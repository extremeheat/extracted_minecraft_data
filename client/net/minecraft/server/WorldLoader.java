package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
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
         List var8 = TagLoader.loadTagsForExistingRegistries(var6, var7.getLayer(RegistryLayer.STATIC));
         RegistryAccess.Frozen var9 = var7.getAccessForLoading(RegistryLayer.WORLDGEN);
         List var10 = TagLoader.buildUpdatedLookups(var9, var8);
         RegistryAccess.Frozen var11 = RegistryDataLoader.load((ResourceManager)var6, var10, RegistryDataLoader.WORLDGEN_REGISTRIES);
         List var12 = Stream.concat(var10.stream(), var11.listRegistries()).toList();
         RegistryAccess.Frozen var13 = RegistryDataLoader.load((ResourceManager)var6, var12, RegistryDataLoader.DIMENSION_REGISTRIES);
         WorldDataConfiguration var14 = (WorldDataConfiguration)var5.getFirst();
         HolderLookup.Provider var15 = HolderLookup.Provider.create(var12.stream());
         DataLoadOutput var16 = var1.get(new DataLoadContext(var6, var14, var15, var13));
         LayeredRegistryAccess var17 = var7.replaceFrom(RegistryLayer.WORLDGEN, (RegistryAccess.Frozen[])(var11, var16.finalDimensions));
         return ReloadableServerResources.loadResources(var6, var17, var8, var14.enabledFeatures(), var0.commandSelection(), var0.functionCompilationLevel(), var3, var4).whenComplete((var1x, var2x) -> {
            if (var2x != null) {
               var6.close();
            }

         }).thenApplyAsync((var4x) -> {
            var4x.updateStaticRegistryTags();
            return var2.create(var6, var4x, var17, var16.cookie);
         }, var4);
      } catch (Exception var18) {
         return CompletableFuture.failedFuture(var18);
      }
   }

   public static record InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
      final PackConfig packConfig;

      public InitConfig(PackConfig var1, Commands.CommandSelection var2, int var3) {
         super();
         this.packConfig = var1;
         this.commandSelection = var2;
         this.functionCompilationLevel = var3;
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
      public PackConfig(PackRepository var1, WorldDataConfiguration var2, boolean var3, boolean var4) {
         super();
         this.packRepository = var1;
         this.initialDataConfig = var2;
         this.safeMode = var3;
         this.initMode = var4;
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

   public static record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, HolderLookup.Provider datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
      public DataLoadContext(ResourceManager var1, WorldDataConfiguration var2, HolderLookup.Provider var3, RegistryAccess.Frozen var4) {
         super();
         this.resources = var1;
         this.dataConfiguration = var2;
         this.datapackWorldgen = var3;
         this.datapackDimensions = var4;
      }

      public ResourceManager resources() {
         return this.resources;
      }

      public WorldDataConfiguration dataConfiguration() {
         return this.dataConfiguration;
      }

      public HolderLookup.Provider datapackWorldgen() {
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

      public DataLoadOutput(D var1, RegistryAccess.Frozen var2) {
         super();
         this.cookie = var1;
         this.finalDimensions = var2;
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
