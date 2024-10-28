package net.minecraft.util.worldupdate;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatMaps;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RecreatingChunkStorage;
import net.minecraft.world.level.chunk.storage.RecreatingSimpleRegionStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private static final String NEW_DIRECTORY_PREFIX = "new_";
   static final Component STATUS_UPGRADING_POI = Component.translatable("optimizeWorld.stage.upgrading.poi");
   static final Component STATUS_FINISHED_POI = Component.translatable("optimizeWorld.stage.finished.poi");
   static final Component STATUS_UPGRADING_ENTITIES = Component.translatable("optimizeWorld.stage.upgrading.entities");
   static final Component STATUS_FINISHED_ENTITIES = Component.translatable("optimizeWorld.stage.finished.entities");
   static final Component STATUS_UPGRADING_CHUNKS = Component.translatable("optimizeWorld.stage.upgrading.chunks");
   static final Component STATUS_FINISHED_CHUNKS = Component.translatable("optimizeWorld.stage.finished.chunks");
   final Registry<LevelStem> dimensions;
   final Set<ResourceKey<Level>> levels;
   final boolean eraseCache;
   final boolean recreateRegionFiles;
   final LevelStorageSource.LevelStorageAccess levelStorage;
   private final Thread thread;
   final DataFixer dataFixer;
   volatile boolean running = true;
   private volatile boolean finished;
   volatile float progress;
   volatile int totalChunks;
   volatile int totalFiles;
   volatile int converted;
   volatile int skipped;
   final Reference2FloatMap<ResourceKey<Level>> progressMap = Reference2FloatMaps.synchronize(new Reference2FloatOpenHashMap());
   volatile Component status = Component.translatable("optimizeWorld.stage.counting");
   static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   final DimensionDataStorage overworldDataStorage;

   public WorldUpgrader(LevelStorageSource.LevelStorageAccess var1, DataFixer var2, RegistryAccess var3, boolean var4, boolean var5) {
      super();
      this.dimensions = var3.lookupOrThrow(Registries.LEVEL_STEM);
      this.levels = (Set)this.dimensions.registryKeySet().stream().map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
      this.eraseCache = var4;
      this.dataFixer = var2;
      this.levelStorage = var1;
      this.overworldDataStorage = new DimensionDataStorage(this.levelStorage.getDimensionPath(Level.OVERWORLD).resolve("data"), var2, var3);
      this.recreateRegionFiles = var5;
      this.thread = THREAD_FACTORY.newThread(this::work);
      this.thread.setUncaughtExceptionHandler((var1x, var2x) -> {
         LOGGER.error("Error upgrading world", var2x);
         this.status = Component.translatable("optimizeWorld.stage.failed");
         this.finished = true;
      });
      this.thread.start();
   }

   public void cancel() {
      this.running = false;

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
      }

   }

   private void work() {
      long var1 = Util.getMillis();
      LOGGER.info("Upgrading entities");
      (new EntityUpgrader(this)).upgrade();
      LOGGER.info("Upgrading POIs");
      (new PoiUpgrader(this)).upgrade();
      LOGGER.info("Upgrading blocks");
      (new ChunkUpgrader()).upgrade();
      this.overworldDataStorage.saveAndJoin();
      var1 = Util.getMillis() - var1;
      LOGGER.info("World optimizaton finished after {} seconds", var1 / 1000L);
      this.finished = true;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public Set<ResourceKey<Level>> levels() {
      return this.levels;
   }

   public float dimensionProgress(ResourceKey<Level> var1) {
      return this.progressMap.getFloat(var1);
   }

   public float getProgress() {
      return this.progress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public Component getStatus() {
      return this.status;
   }

   public void close() {
      this.overworldDataStorage.close();
   }

   static Path resolveRecreateDirectory(Path var0) {
      return var0.resolveSibling("new_" + var0.getFileName().toString());
   }

   private class EntityUpgrader extends SimpleRegionStorageUpgrader {
      EntityUpgrader(final WorldUpgrader var1) {
         super(DataFixTypes.ENTITY_CHUNK, "entities", WorldUpgrader.STATUS_UPGRADING_ENTITIES, WorldUpgrader.STATUS_FINISHED_ENTITIES);
      }

      protected CompoundTag upgradeTag(SimpleRegionStorage var1, CompoundTag var2) {
         return var1.upgradeChunkTag((CompoundTag)var2, -1);
      }
   }

   private class PoiUpgrader extends SimpleRegionStorageUpgrader {
      PoiUpgrader(final WorldUpgrader var1) {
         super(DataFixTypes.POI_CHUNK, "poi", WorldUpgrader.STATUS_UPGRADING_POI, WorldUpgrader.STATUS_FINISHED_POI);
      }

      protected CompoundTag upgradeTag(SimpleRegionStorage var1, CompoundTag var2) {
         return var1.upgradeChunkTag((CompoundTag)var2, 1945);
      }
   }

   private class ChunkUpgrader extends AbstractUpgrader<ChunkStorage> {
      ChunkUpgrader() {
         super(DataFixTypes.CHUNK, "chunk", "region", WorldUpgrader.STATUS_UPGRADING_CHUNKS, WorldUpgrader.STATUS_FINISHED_CHUNKS);
      }

      protected boolean tryProcessOnePosition(ChunkStorage var1, ChunkPos var2, ResourceKey<Level> var3) {
         CompoundTag var4 = (CompoundTag)((Optional)var1.read(var2).join()).orElse((Object)null);
         if (var4 != null) {
            int var5 = ChunkStorage.getVersion(var4);
            ChunkGenerator var6 = ((LevelStem)WorldUpgrader.this.dimensions.getValueOrThrow(Registries.levelToLevelStem(var3))).generator();
            CompoundTag var7 = var1.upgradeChunkTag(var3, () -> {
               return WorldUpgrader.this.overworldDataStorage;
            }, var4, var6.getTypeNameForDataFixer());
            ChunkPos var8 = new ChunkPos(var7.getInt("xPos"), var7.getInt("zPos"));
            if (!var8.equals(var2)) {
               WorldUpgrader.LOGGER.warn("Chunk {} has invalid position {}", var2, var8);
            }

            boolean var9 = var5 < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
            if (WorldUpgrader.this.eraseCache) {
               var9 = var9 || var7.contains("Heightmaps");
               var7.remove("Heightmaps");
               var9 = var9 || var7.contains("isLightOn");
               var7.remove("isLightOn");
               ListTag var10 = var7.getList("sections", 10);

               for(int var11 = 0; var11 < var10.size(); ++var11) {
                  CompoundTag var12 = var10.getCompound(var11);
                  var9 = var9 || var12.contains("BlockLight");
                  var12.remove("BlockLight");
                  var9 = var9 || var12.contains("SkyLight");
                  var12.remove("SkyLight");
               }
            }

            if (var9 || WorldUpgrader.this.recreateRegionFiles) {
               if (this.previousWriteFuture != null) {
                  this.previousWriteFuture.join();
               }

               this.previousWriteFuture = var1.write(var2, () -> {
                  return var7;
               });
               return true;
            }
         }

         return false;
      }

      protected ChunkStorage createStorage(RegionStorageInfo var1, Path var2) {
         return (ChunkStorage)(WorldUpgrader.this.recreateRegionFiles ? new RecreatingChunkStorage(var1.withTypeSuffix("source"), var2, var1.withTypeSuffix("target"), WorldUpgrader.resolveRecreateDirectory(var2), WorldUpgrader.this.dataFixer, true) : new ChunkStorage(var1, var2, WorldUpgrader.this.dataFixer, true));
      }

      // $FF: synthetic method
      protected AutoCloseable createStorage(final RegionStorageInfo var1, final Path var2) {
         return this.createStorage(var1, var2);
      }
   }

   private abstract class SimpleRegionStorageUpgrader extends AbstractUpgrader<SimpleRegionStorage> {
      SimpleRegionStorageUpgrader(final DataFixTypes var2, final String var3, final Component var4, final Component var5) {
         super(var2, var3, var3, var4, var5);
      }

      protected SimpleRegionStorage createStorage(RegionStorageInfo var1, Path var2) {
         return (SimpleRegionStorage)(WorldUpgrader.this.recreateRegionFiles ? new RecreatingSimpleRegionStorage(var1.withTypeSuffix("source"), var2, var1.withTypeSuffix("target"), WorldUpgrader.resolveRecreateDirectory(var2), WorldUpgrader.this.dataFixer, true, this.dataFixType) : new SimpleRegionStorage(var1, var2, WorldUpgrader.this.dataFixer, true, this.dataFixType));
      }

      protected boolean tryProcessOnePosition(SimpleRegionStorage var1, ChunkPos var2, ResourceKey<Level> var3) {
         CompoundTag var4 = (CompoundTag)((Optional)var1.read(var2).join()).orElse((Object)null);
         if (var4 != null) {
            int var5 = ChunkStorage.getVersion(var4);
            CompoundTag var6 = this.upgradeTag(var1, var4);
            boolean var7 = var5 < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
            if (var7 || WorldUpgrader.this.recreateRegionFiles) {
               if (this.previousWriteFuture != null) {
                  this.previousWriteFuture.join();
               }

               this.previousWriteFuture = var1.write(var2, var6);
               return true;
            }
         }

         return false;
      }

      protected abstract CompoundTag upgradeTag(SimpleRegionStorage var1, CompoundTag var2);

      // $FF: synthetic method
      protected AutoCloseable createStorage(final RegionStorageInfo var1, final Path var2) {
         return this.createStorage(var1, var2);
      }
   }

   abstract class AbstractUpgrader<T extends AutoCloseable> {
      private final Component upgradingStatus;
      private final Component finishedStatus;
      private final String type;
      private final String folderName;
      @Nullable
      protected CompletableFuture<Void> previousWriteFuture;
      protected final DataFixTypes dataFixType;

      AbstractUpgrader(final DataFixTypes var2, final String var3, final String var4, final Component var5, final Component var6) {
         super();
         this.dataFixType = var2;
         this.type = var3;
         this.folderName = var4;
         this.upgradingStatus = var5;
         this.finishedStatus = var6;
      }

      public void upgrade() {
         WorldUpgrader.this.totalFiles = 0;
         WorldUpgrader.this.totalChunks = 0;
         WorldUpgrader.this.converted = 0;
         WorldUpgrader.this.skipped = 0;
         List var1 = this.getDimensionsToUpgrade();
         if (WorldUpgrader.this.totalChunks != 0) {
            float var2 = (float)WorldUpgrader.this.totalFiles;
            WorldUpgrader.this.status = this.upgradingStatus;

            while(WorldUpgrader.this.running) {
               boolean var3 = false;
               float var4 = 0.0F;

               float var17;
               for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 += var17) {
                  DimensionToUpgrade var6 = (DimensionToUpgrade)var5.next();
                  ResourceKey var7 = var6.dimensionKey;
                  ListIterator var8 = var6.files;
                  AutoCloseable var9 = (AutoCloseable)var6.storage;
                  if (var8.hasNext()) {
                     FileToUpgrade var10 = (FileToUpgrade)var8.next();
                     boolean var11 = true;
                     Iterator var12 = var10.chunksToUpgrade.iterator();

                     while(true) {
                        if (!var12.hasNext()) {
                           if (WorldUpgrader.this.recreateRegionFiles) {
                              if (var11) {
                                 this.onFileFinished(var10.file);
                              } else {
                                 WorldUpgrader.LOGGER.error("Failed to convert region file {}", var10.file.getPath());
                              }
                           }
                           break;
                        }

                        ChunkPos var13 = (ChunkPos)var12.next();
                        var11 = var11 && this.processOnePosition(var7, var9, var13);
                        var3 = true;
                     }
                  }

                  var17 = (float)var8.nextIndex() / var2;
                  WorldUpgrader.this.progressMap.put(var7, var17);
               }

               WorldUpgrader.this.progress = var4;
               if (!var3) {
                  break;
               }
            }

            WorldUpgrader.this.status = this.finishedStatus;
            Iterator var15 = var1.iterator();

            while(var15.hasNext()) {
               DimensionToUpgrade var16 = (DimensionToUpgrade)var15.next();

               try {
                  ((AutoCloseable)var16.storage).close();
               } catch (Exception var14) {
                  WorldUpgrader.LOGGER.error("Error upgrading chunk", var14);
               }
            }

         }
      }

      private List<DimensionToUpgrade<T>> getDimensionsToUpgrade() {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = WorldUpgrader.this.levels.iterator();

         while(var2.hasNext()) {
            ResourceKey var3 = (ResourceKey)var2.next();
            RegionStorageInfo var4 = new RegionStorageInfo(WorldUpgrader.this.levelStorage.getLevelId(), var3, this.type);
            Path var5 = WorldUpgrader.this.levelStorage.getDimensionPath(var3).resolve(this.folderName);
            AutoCloseable var6 = this.createStorage(var4, var5);
            ListIterator var7 = this.getFilesToProcess(var4, var5);
            var1.add(new DimensionToUpgrade(var3, var6, var7));
         }

         return var1;
      }

      protected abstract T createStorage(RegionStorageInfo var1, Path var2);

      private ListIterator<FileToUpgrade> getFilesToProcess(RegionStorageInfo var1, Path var2) {
         List var3 = getAllChunkPositions(var1, var2);
         WorldUpgrader var10000 = WorldUpgrader.this;
         var10000.totalFiles += var3.size();
         var10000 = WorldUpgrader.this;
         var10000.totalChunks += var3.stream().mapToInt((var0) -> {
            return var0.chunksToUpgrade.size();
         }).sum();
         return var3.listIterator();
      }

      private static List<FileToUpgrade> getAllChunkPositions(RegionStorageInfo var0, Path var1) {
         File[] var2 = var1.toFile().listFiles((var0x, var1x) -> {
            return var1x.endsWith(".mca");
         });
         if (var2 == null) {
            return List.of();
         } else {
            ArrayList var3 = Lists.newArrayList();
            File[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File var7 = var4[var6];
               Matcher var8 = WorldUpgrader.REGEX.matcher(var7.getName());
               if (var8.matches()) {
                  int var9 = Integer.parseInt(var8.group(1)) << 5;
                  int var10 = Integer.parseInt(var8.group(2)) << 5;
                  ArrayList var11 = Lists.newArrayList();

                  try {
                     RegionFile var12 = new RegionFile(var0, var7.toPath(), var1, true);

                     try {
                        for(int var13 = 0; var13 < 32; ++var13) {
                           for(int var14 = 0; var14 < 32; ++var14) {
                              ChunkPos var15 = new ChunkPos(var13 + var9, var14 + var10);
                              if (var12.doesChunkExist(var15)) {
                                 var11.add(var15);
                              }
                           }
                        }

                        if (!var11.isEmpty()) {
                           var3.add(new FileToUpgrade(var12, var11));
                        }
                     } catch (Throwable var17) {
                        try {
                           var12.close();
                        } catch (Throwable var16) {
                           var17.addSuppressed(var16);
                        }

                        throw var17;
                     }

                     var12.close();
                  } catch (Throwable var18) {
                     WorldUpgrader.LOGGER.error("Failed to read chunks from region file {}", var7.toPath(), var18);
                  }
               }
            }

            return var3;
         }
      }

      private boolean processOnePosition(ResourceKey<Level> var1, T var2, ChunkPos var3) {
         boolean var4 = false;

         try {
            var4 = this.tryProcessOnePosition(var2, var3, var1);
         } catch (CompletionException | ReportedException var7) {
            Throwable var6 = ((RuntimeException)var7).getCause();
            if (!(var6 instanceof IOException)) {
               throw var7;
            }

            WorldUpgrader.LOGGER.error("Error upgrading chunk {}", var3, var6);
         }

         if (var4) {
            ++WorldUpgrader.this.converted;
         } else {
            ++WorldUpgrader.this.skipped;
         }

         return var4;
      }

      protected abstract boolean tryProcessOnePosition(T var1, ChunkPos var2, ResourceKey<Level> var3);

      private void onFileFinished(RegionFile var1) {
         if (WorldUpgrader.this.recreateRegionFiles) {
            if (this.previousWriteFuture != null) {
               this.previousWriteFuture.join();
            }

            Path var2 = var1.getPath();
            Path var3 = var2.getParent();
            Path var4 = WorldUpgrader.resolveRecreateDirectory(var3).resolve(var2.getFileName().toString());

            try {
               if (var4.toFile().exists()) {
                  Files.delete(var2);
                  Files.move(var4, var2);
               } else {
                  WorldUpgrader.LOGGER.error("Failed to replace an old region file. New file {} does not exist.", var4);
               }
            } catch (IOException var6) {
               WorldUpgrader.LOGGER.error("Failed to replace an old region file", var6);
            }

         }
      }
   }

   static record FileToUpgrade(RegionFile file, List<ChunkPos> chunksToUpgrade) {
      final RegionFile file;
      final List<ChunkPos> chunksToUpgrade;

      FileToUpgrade(RegionFile var1, List<ChunkPos> var2) {
         super();
         this.file = var1;
         this.chunksToUpgrade = var2;
      }

      public RegionFile file() {
         return this.file;
      }

      public List<ChunkPos> chunksToUpgrade() {
         return this.chunksToUpgrade;
      }
   }

   static record DimensionToUpgrade<T>(ResourceKey<Level> dimensionKey, T storage, ListIterator<FileToUpgrade> files) {
      final ResourceKey<Level> dimensionKey;
      final T storage;
      final ListIterator<FileToUpgrade> files;

      DimensionToUpgrade(ResourceKey<Level> var1, T var2, ListIterator<FileToUpgrade> var3) {
         super();
         this.dimensionKey = var1;
         this.storage = var2;
         this.files = var3;
      }

      public ResourceKey<Level> dimensionKey() {
         return this.dimensionKey;
      }

      public T storage() {
         return this.storage;
      }

      public ListIterator<FileToUpgrade> files() {
         return this.files;
      }
   }
}
