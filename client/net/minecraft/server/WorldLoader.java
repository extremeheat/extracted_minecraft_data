package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfig;

public class WorldLoader {
   public WorldLoader() {
      super();
   }

   public static <D, R> CompletableFuture<R> load(
      WorldLoader.InitConfig var0, WorldLoader.WorldDataSupplier<D> var1, WorldLoader.ResultFactory<D, R> var2, Executor var3, Executor var4
   ) {
      try {
         Pair var5 = var0.packConfig.createResourceManager();
         CloseableResourceManager var6 = (CloseableResourceManager)var5.getSecond();
         Pair var7 = var1.get(var6, (DataPackConfig)var5.getFirst());
         Object var8 = var7.getFirst();
         RegistryAccess.Frozen var9 = (RegistryAccess.Frozen)var7.getSecond();
         return ReloadableServerResources.loadResources(var6, var9, var0.commandSelection(), var0.functionCompilationLevel(), var3, var4)
            .whenComplete((var1x, var2x) -> {
               if (var2x != null) {
                  var6.close();
               }
            })
            .thenApplyAsync(var4x -> {
               var4x.updateRegistryTags(var9);
               return (R)var2.create(var6, var4x, var9, (D)var8);
            }, var4);
      } catch (Exception var10) {
         return CompletableFuture.failedFuture(var10);
      }
   }

   public static record InitConfig(WorldLoader.PackConfig a, Commands.CommandSelection b, int c) {
      final WorldLoader.PackConfig packConfig;
      private final Commands.CommandSelection commandSelection;
      private final int functionCompilationLevel;

      public InitConfig(WorldLoader.PackConfig var1, Commands.CommandSelection var2, int var3) {
         super();
         this.packConfig = var1;
         this.commandSelection = var2;
         this.functionCompilationLevel = var3;
      }
   }

   public static record PackConfig(PackRepository a, DataPackConfig b, boolean c) {
      private final PackRepository packRepository;
      private final DataPackConfig initialDataPacks;
      private final boolean safeMode;

      public PackConfig(PackRepository var1, DataPackConfig var2, boolean var3) {
         super();
         this.packRepository = var1;
         this.initialDataPacks = var2;
         this.safeMode = var3;
      }

      public Pair<DataPackConfig, CloseableResourceManager> createResourceManager() {
         DataPackConfig var1 = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataPacks, this.safeMode);
         List var2 = this.packRepository.openAllSelected();
         MultiPackResourceManager var3 = new MultiPackResourceManager(PackType.SERVER_DATA, var2);
         return Pair.of(var1, var3);
      }
   }

   @FunctionalInterface
   public interface ResultFactory<D, R> {
      R create(CloseableResourceManager var1, ReloadableServerResources var2, RegistryAccess.Frozen var3, D var4);
   }

   @FunctionalInterface
   public interface WorldDataSupplier<D> {
      Pair<D, RegistryAccess.Frozen> get(ResourceManager var1, DataPackConfig var2);
   }
}
