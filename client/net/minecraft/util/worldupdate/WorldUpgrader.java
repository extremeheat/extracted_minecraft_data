package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
   private final WorldGenSettings worldGenSettings;
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

   public WorldUpgrader(LevelStorageSource.LevelStorageAccess var1, DataFixer var2, WorldGenSettings var3, boolean var4) {
      super();
      this.worldGenSettings = var3;
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
      ImmutableSet var2 = this.worldGenSettings.levels();

      List var5;
      for(UnmodifiableIterator var3 = var2.iterator(); var3.hasNext(); this.totalChunks += var5.size()) {
         ResourceKey var4 = (ResourceKey)var3.next();
         var5 = this.getAllChunkPos(var4);
         var1.put(var4, var5.listIterator());
      }

      if (this.totalChunks == 0) {
         this.finished = true;
      } else {
         float var28 = (float)this.totalChunks;
         ImmutableMap var29 = var1.build();
         Builder var30 = ImmutableMap.builder();
         UnmodifiableIterator var6 = var2.iterator();

         while(var6.hasNext()) {
            ResourceKey var7 = (ResourceKey)var6.next();
            Path var8 = this.levelStorage.getDimensionPath(var7);
            var30.put(var7, new ChunkStorage(var8.resolve("region"), this.dataFixer, true));
         }

         ImmutableMap var31 = var30.build();
         long var32 = Util.getMillis();
         this.status = Component.translatable("optimizeWorld.stage.upgrading");

         while(this.running) {
            boolean var9 = false;
            float var10 = 0.0F;

            float var36;
            for(UnmodifiableIterator var11 = var2.iterator(); var11.hasNext(); var10 += var36) {
               ResourceKey var12 = (ResourceKey)var11.next();
               ListIterator var13 = (ListIterator)var29.get(var12);
               ChunkStorage var14 = (ChunkStorage)var31.get(var12);
               if (var13.hasNext()) {
                  ChunkPos var15 = (ChunkPos)var13.next();
                  boolean var16 = false;

                  try {
                     CompoundTag var17 = var14.read(var15).join().orElse(null);
                     if (var17 != null) {
                        int var37 = ChunkStorage.getVersion(var17);
                        ChunkGenerator var19 = this.worldGenSettings.dimensions().get(WorldGenSettings.levelToLevelStem(var12)).generator();
                        CompoundTag var20 = var14.upgradeChunkTag(var12, () -> this.overworldDataStorage, var17, var19.getTypeNameForDataFixer());
                        ChunkPos var21 = new ChunkPos(var20.getInt("xPos"), var20.getInt("zPos"));
                        if (!var21.equals(var15)) {
                           LOGGER.warn("Chunk {} has invalid position {}", var15, var21);
                        }

                        boolean var22 = var37 < SharedConstants.getCurrentVersion().getWorldVersion();
                        if (this.eraseCache) {
                           var22 = var22 || var20.contains("Heightmaps");
                           var20.remove("Heightmaps");
                           var22 = var22 || var20.contains("isLightOn");
                           var20.remove("isLightOn");
                           ListTag var23 = var20.getList("sections", 10);

                           for(int var24 = 0; var24 < var23.size(); ++var24) {
                              CompoundTag var25 = var23.getCompound(var24);
                              var22 = var22 || var25.contains("BlockLight");
                              var25.remove("BlockLight");
                              var22 = var22 || var25.contains("SkyLight");
                              var25.remove("SkyLight");
                           }
                        }

                        if (var22) {
                           var14.write(var15, var20);
                           var16 = true;
                        }
                     }
                  } catch (CompletionException | ReportedException var27) {
                     Throwable var18 = var27.getCause();
                     if (!(var18 instanceof IOException)) {
                        throw var27;
                     }

                     LOGGER.error("Error upgrading chunk {}", var15, var18);
                  }

                  if (var16) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  var9 = true;
               }

               var36 = (float)var13.nextIndex() / var28;
               this.progressMap.put(var12, var36);
            }

            this.progress = var10;
            if (!var9) {
               this.running = false;
            }
         }

         this.status = Component.translatable("optimizeWorld.stage.finished");
         UnmodifiableIterator var34 = var31.values().iterator();

         while(var34.hasNext()) {
            ChunkStorage var35 = (ChunkStorage)var34.next();

            try {
               var35.close();
            } catch (IOException var26) {
               LOGGER.error("Error upgrading chunk", var26);
            }
         }

         this.overworldDataStorage.save();
         var32 = Util.getMillis() - var32;
         LOGGER.info("World optimizaton finished after {} ms", var32);
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

   public ImmutableSet<ResourceKey<Level>> levels() {
      return this.worldGenSettings.levels();
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
