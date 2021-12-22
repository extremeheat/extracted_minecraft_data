package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldUpgrader {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
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
   private volatile Component status = new TranslatableComponent("optimizeWorld.stage.counting");
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
         this.status = new TranslatableComponent("optimizeWorld.stage.failed");
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
         float var26 = (float)this.totalChunks;
         ImmutableMap var27 = var1.build();
         Builder var28 = ImmutableMap.builder();
         UnmodifiableIterator var6 = var2.iterator();

         while(var6.hasNext()) {
            ResourceKey var7 = (ResourceKey)var6.next();
            Path var8 = this.levelStorage.getDimensionPath(var7);
            var28.put(var7, new ChunkStorage(var8.resolve("region"), this.dataFixer, true));
         }

         ImmutableMap var29 = var28.build();
         long var30 = Util.getMillis();
         this.status = new TranslatableComponent("optimizeWorld.stage.upgrading");

         while(this.running) {
            boolean var9 = false;
            float var10 = 0.0F;

            float var33;
            for(UnmodifiableIterator var11 = var2.iterator(); var11.hasNext(); var10 += var33) {
               ResourceKey var12 = (ResourceKey)var11.next();
               ListIterator var13 = (ListIterator)var27.get(var12);
               ChunkStorage var14 = (ChunkStorage)var29.get(var12);
               if (var13.hasNext()) {
                  ChunkPos var15 = (ChunkPos)var13.next();
                  boolean var16 = false;

                  try {
                     CompoundTag var17 = var14.read(var15);
                     if (var17 != null) {
                        int var34 = ChunkStorage.getVersion(var17);
                        ChunkGenerator var19 = ((LevelStem)this.worldGenSettings.dimensions().get(WorldGenSettings.levelToLevelStem(var12))).generator();
                        CompoundTag var20 = var14.upgradeChunkTag(var12, () -> {
                           return this.overworldDataStorage;
                        }, var17, var19.getTypeNameForDataFixer());
                        ChunkPos var21 = new ChunkPos(var20.getInt("xPos"), var20.getInt("zPos"));
                        if (!var21.equals(var15)) {
                           LOGGER.warn("Chunk {} has invalid position {}", var15, var21);
                        }

                        boolean var22 = var34 < SharedConstants.getCurrentVersion().getWorldVersion();
                        if (this.eraseCache) {
                           var22 = var22 || var20.contains("Heightmaps");
                           var20.remove("Heightmaps");
                           var22 = var22 || var20.contains("isLightOn");
                           var20.remove("isLightOn");
                        }

                        if (var22) {
                           var14.write(var15, var20);
                           var16 = true;
                        }
                     }
                  } catch (ReportedException var24) {
                     Throwable var18 = var24.getCause();
                     if (!(var18 instanceof IOException)) {
                        throw var24;
                     }

                     LOGGER.error("Error upgrading chunk {}", var15, var18);
                  } catch (IOException var25) {
                     LOGGER.error("Error upgrading chunk {}", var15, var25);
                  }

                  if (var16) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  var9 = true;
               }

               var33 = (float)var13.nextIndex() / var26;
               this.progressMap.put(var12, var33);
            }

            this.progress = var10;
            if (!var9) {
               this.running = false;
            }
         }

         this.status = new TranslatableComponent("optimizeWorld.stage.finished");
         UnmodifiableIterator var31 = var29.values().iterator();

         while(var31.hasNext()) {
            ChunkStorage var32 = (ChunkStorage)var31.next();

            try {
               var32.close();
            } catch (IOException var23) {
               LOGGER.error("Error upgrading chunk", var23);
            }
         }

         this.overworldDataStorage.save();
         var30 = Util.getMillis() - var30;
         LOGGER.info("World optimizaton finished after {} ms", var30);
         this.finished = true;
      }
   }

   private List<ChunkPos> getAllChunkPos(ResourceKey<Level> var1) {
      File var2 = this.levelStorage.getDimensionPath(var1).toFile();
      File var3 = new File(var2, "region");
      File[] var4 = var3.listFiles((var0, var1x) -> {
         return var1x.endsWith(".mca");
      });
      if (var4 == null) {
         return ImmutableList.of();
      } else {
         ArrayList var5 = Lists.newArrayList();
         File[] var6 = var4;
         int var7 = var4.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            File var9 = var6[var8];
            Matcher var10 = REGEX.matcher(var9.getName());
            if (var10.matches()) {
               int var11 = Integer.parseInt(var10.group(1)) << 5;
               int var12 = Integer.parseInt(var10.group(2)) << 5;

               try {
                  RegionFile var13 = new RegionFile(var9.toPath(), var3.toPath(), true);

                  try {
                     for(int var14 = 0; var14 < 32; ++var14) {
                        for(int var15 = 0; var15 < 32; ++var15) {
                           ChunkPos var16 = new ChunkPos(var14 + var11, var15 + var12);
                           if (var13.doesChunkExist(var16)) {
                              var5.add(var16);
                           }
                        }
                     }
                  } catch (Throwable var18) {
                     try {
                        var13.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }

                     throw var18;
                  }

                  var13.close();
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
