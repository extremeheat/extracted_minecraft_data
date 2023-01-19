package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
   private final Registry<LevelStem> dimensions;
   private final Set<ResourceKey<Level>> levels;
   private final boolean eraseCache;
   private final LevelStorageSource.LevelStorageAccess levelStorage;
   private final Thread thread;
   private final DataFixer dataFixer;
   private volatile boolean running = true;
   private volatile boolean finished;
   private volatile float progress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Object2FloatMap<ResourceKey<Level>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.identityStrategy()));
   private volatile Component status = Component.translatable("optimizeWorld.stage.counting");
   private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final DimensionDataStorage overworldDataStorage;

   public WorldUpgrader(LevelStorageSource.LevelStorageAccess var1, DataFixer var2, Registry<LevelStem> var3, boolean var4) {
      super();
      this.dimensions = var3;
      this.levels = var3.registryKeySet().stream().map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
      this.eraseCache = var4;
      this.dataFixer = var2;
      this.levelStorage = var1;
      this.overworldDataStorage = new DimensionDataStorage(this.levelStorage.getDimensionPath(Level.OVERWORLD).resolve("data").toFile(), var2);
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
      this.totalChunks = 0;
      Builder var1 = ImmutableMap.builder();

      for(ResourceKey var3 : this.levels) {
         List var4 = this.getAllChunkPos(var3);
         var1.put(var3, var4.listIterator());
         this.totalChunks += var4.size();
      }

      if (this.totalChunks == 0) {
         this.finished = true;
      } else {
         float var27 = (float)this.totalChunks;
         ImmutableMap var28 = var1.build();
         Builder var29 = ImmutableMap.builder();

         for(ResourceKey var6 : this.levels) {
            Path var7 = this.levelStorage.getDimensionPath(var6);
            var29.put(var6, new ChunkStorage(var7.resolve("region"), this.dataFixer, true));
         }

         ImmutableMap var30 = var29.build();
         long var31 = Util.getMillis();
         this.status = Component.translatable("optimizeWorld.stage.upgrading");

         while(this.running) {
            boolean var8 = false;
            float var9 = 0.0F;

            for(ResourceKey var11 : this.levels) {
               ListIterator var12 = (ListIterator)var28.get(var11);
               ChunkStorage var13 = (ChunkStorage)var30.get(var11);
               if (var12.hasNext()) {
                  ChunkPos var14 = (ChunkPos)var12.next();
                  boolean var15 = false;

                  try {
                     CompoundTag var16 = var13.read(var14).join().orElse(null);
                     if (var16 != null) {
                        int var36 = ChunkStorage.getVersion(var16);
                        ChunkGenerator var18 = this.dimensions.getOrThrow(Registries.levelToLevelStem(var11)).generator();
                        CompoundTag var19 = var13.upgradeChunkTag(var11, () -> this.overworldDataStorage, var16, var18.getTypeNameForDataFixer());
                        ChunkPos var20 = new ChunkPos(var19.getInt("xPos"), var19.getInt("zPos"));
                        if (!var20.equals(var14)) {
                           LOGGER.warn("Chunk {} has invalid position {}", var14, var20);
                        }

                        boolean var21 = var36 < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
                        if (this.eraseCache) {
                           var21 = var21 || var19.contains("Heightmaps");
                           var19.remove("Heightmaps");
                           var21 = var21 || var19.contains("isLightOn");
                           var19.remove("isLightOn");
                           ListTag var22 = var19.getList("sections", 10);

                           for(int var23 = 0; var23 < var22.size(); ++var23) {
                              CompoundTag var24 = var22.getCompound(var23);
                              var21 = var21 || var24.contains("BlockLight");
                              var24.remove("BlockLight");
                              var21 = var21 || var24.contains("SkyLight");
                              var24.remove("SkyLight");
                           }
                        }

                        if (var21) {
                           var13.write(var14, var19);
                           var15 = true;
                        }
                     }
                  } catch (CompletionException | ReportedException var26) {
                     Throwable var17 = var26.getCause();
                     if (!(var17 instanceof IOException)) {
                        throw var26;
                     }

                     LOGGER.error("Error upgrading chunk {}", var14, var17);
                  }

                  if (var15) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  var8 = true;
               }

               float var35 = (float)var12.nextIndex() / var27;
               this.progressMap.put(var11, var35);
               var9 += var35;
            }

            this.progress = var9;
            if (!var8) {
               this.running = false;
            }
         }

         this.status = Component.translatable("optimizeWorld.stage.finished");
         UnmodifiableIterator var33 = var30.values().iterator();

         while(var33.hasNext()) {
            ChunkStorage var34 = (ChunkStorage)var33.next();

            try {
               var34.close();
            } catch (IOException var25) {
               LOGGER.error("Error upgrading chunk", var25);
            }
         }

         this.overworldDataStorage.save();
         var31 = Util.getMillis() - var31;
         LOGGER.info("World optimizaton finished after {} ms", var31);
         this.finished = true;
      }
   }

   private List<ChunkPos> getAllChunkPos(ResourceKey<Level> var1) {
      File var2 = this.levelStorage.getDimensionPath(var1).toFile();
      File var3 = new File(var2, "region");
      File[] var4 = var3.listFiles((var0, var1x) -> var1x.endsWith(".mca"));
      if (var4 == null) {
         return ImmutableList.of();
      } else {
         ArrayList var5 = Lists.newArrayList();

         for(File var9 : var4) {
            Matcher var10 = REGEX.matcher(var9.getName());
            if (var10.matches()) {
               int var11 = Integer.parseInt(var10.group(1)) << 5;
               int var12 = Integer.parseInt(var10.group(2)) << 5;

               try (RegionFile var13 = new RegionFile(var9.toPath(), var3.toPath(), true)) {
                  for(int var14 = 0; var14 < 32; ++var14) {
                     for(int var15 = 0; var15 < 32; ++var15) {
                        ChunkPos var16 = new ChunkPos(var14 + var11, var15 + var12);
                        if (var13.doesChunkExist(var16)) {
                           var5.add(var16);
                        }
                     }
                  }
               } catch (Throwable var19) {
               }
            }
         }

         return var5;
      }
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
}
