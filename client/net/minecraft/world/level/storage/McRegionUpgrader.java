package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.biome.OverworldBiomeSourceSettings;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.chunk.storage.OldChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class McRegionUpgrader {
   private static final Logger LOGGER = LogManager.getLogger();

   static boolean convertLevel(Path var0, DataFixer var1, String var2, ProgressListener var3) {
      var3.progressStagePercentage(0);
      ArrayList var4 = Lists.newArrayList();
      ArrayList var5 = Lists.newArrayList();
      ArrayList var6 = Lists.newArrayList();
      File var7 = new File(var0.toFile(), var2);
      File var8 = DimensionType.NETHER.getStorageFolder(var7);
      File var9 = DimensionType.THE_END.getStorageFolder(var7);
      LOGGER.info("Scanning folders...");
      addRegionFiles(var7, var4);
      if (var8.exists()) {
         addRegionFiles(var8, var5);
      }

      if (var9.exists()) {
         addRegionFiles(var9, var6);
      }

      int var10 = var4.size() + var5.size() + var6.size();
      LOGGER.info("Total conversion count is {}", var10);
      LevelData var11 = LevelStorageSource.getDataTagFor(var0, var1, var2);
      BiomeSourceType var13 = BiomeSourceType.FIXED;
      BiomeSourceType var14 = BiomeSourceType.VANILLA_LAYERED;
      BiomeSource var12;
      if (var11 != null && var11.getGeneratorType() == LevelType.FLAT) {
         var12 = var13.create(((FixedBiomeSourceSettings)var13.createSettings()).setBiome(Biomes.PLAINS));
      } else {
         var12 = var14.create(((OverworldBiomeSourceSettings)var14.createSettings()).setLevelData(var11).setGeneratorSettings((OverworldGeneratorSettings)ChunkGeneratorType.SURFACE.createSettings()));
      }

      convertRegions(new File(var7, "region"), var4, var12, 0, var10, var3);
      convertRegions(new File(var8, "region"), var5, var13.create(((FixedBiomeSourceSettings)var13.createSettings()).setBiome(Biomes.NETHER)), var4.size(), var10, var3);
      convertRegions(new File(var9, "region"), var6, var13.create(((FixedBiomeSourceSettings)var13.createSettings()).setBiome(Biomes.THE_END)), var4.size() + var5.size(), var10, var3);
      var11.setVersion(19133);
      if (var11.getGeneratorType() == LevelType.NORMAL_1_1) {
         var11.setGenerator(LevelType.NORMAL);
      }

      makeMcrLevelDatBackup(var0, var2);
      LevelStorage var15 = LevelStorageSource.selectLevel(var0, var1, var2, (MinecraftServer)null);
      var15.saveLevelData(var11);
      return true;
   }

   private static void makeMcrLevelDatBackup(Path var0, String var1) {
      File var2 = new File(var0.toFile(), var1);
      if (!var2.exists()) {
         LOGGER.warn("Unable to create level.dat_mcr backup");
      } else {
         File var3 = new File(var2, "level.dat");
         if (!var3.exists()) {
            LOGGER.warn("Unable to create level.dat_mcr backup");
         } else {
            File var4 = new File(var2, "level.dat_mcr");
            if (!var3.renameTo(var4)) {
               LOGGER.warn("Unable to create level.dat_mcr backup");
            }

         }
      }
   }

   private static void convertRegions(File var0, Iterable<File> var1, BiomeSource var2, int var3, int var4, ProgressListener var5) {
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         File var7 = (File)var6.next();
         convertRegion(var0, var7, var2, var3, var4, var5);
         ++var3;
         int var8 = (int)Math.round(100.0D * (double)var3 / (double)var4);
         var5.progressStagePercentage(var8);
      }

   }

   private static void convertRegion(File var0, File var1, BiomeSource var2, int var3, int var4, ProgressListener var5) {
      String var6 = var1.getName();

      try {
         RegionFile var7 = new RegionFile(var1);
         Throwable var8 = null;

         try {
            RegionFile var9 = new RegionFile(new File(var0, var6.substring(0, var6.length() - ".mcr".length()) + ".mca"));
            Throwable var10 = null;

            try {
               for(int var11 = 0; var11 < 32; ++var11) {
                  int var12;
                  for(var12 = 0; var12 < 32; ++var12) {
                     ChunkPos var13 = new ChunkPos(var11, var12);
                     if (var7.hasChunk(var13) && !var9.hasChunk(var13)) {
                        CompoundTag var14;
                        try {
                           DataInputStream var15 = var7.getChunkDataInputStream(var13);
                           Throwable var16 = null;

                           try {
                              if (var15 == null) {
                                 LOGGER.warn("Failed to fetch input stream for chunk {}", var13);
                                 continue;
                              }

                              var14 = NbtIo.read(var15);
                           } catch (Throwable var104) {
                              var16 = var104;
                              throw var104;
                           } finally {
                              if (var15 != null) {
                                 if (var16 != null) {
                                    try {
                                       var15.close();
                                    } catch (Throwable var101) {
                                       var16.addSuppressed(var101);
                                    }
                                 } else {
                                    var15.close();
                                 }
                              }

                           }
                        } catch (IOException var106) {
                           LOGGER.warn("Failed to read data for chunk {}", var13, var106);
                           continue;
                        }

                        CompoundTag var113 = var14.getCompound("Level");
                        OldChunkStorage.OldLevelChunk var114 = OldChunkStorage.load(var113);
                        CompoundTag var17 = new CompoundTag();
                        CompoundTag var18 = new CompoundTag();
                        var17.put("Level", var18);
                        OldChunkStorage.convertToAnvilFormat(var114, var18, var2);
                        DataOutputStream var19 = var9.getChunkDataOutputStream(var13);
                        Throwable var20 = null;

                        try {
                           NbtIo.write(var17, (DataOutput)var19);
                        } catch (Throwable var102) {
                           var20 = var102;
                           throw var102;
                        } finally {
                           if (var19 != null) {
                              if (var20 != null) {
                                 try {
                                    var19.close();
                                 } catch (Throwable var100) {
                                    var20.addSuppressed(var100);
                                 }
                              } else {
                                 var19.close();
                              }
                           }

                        }
                     }
                  }

                  var12 = (int)Math.round(100.0D * (double)(var3 * 1024) / (double)(var4 * 1024));
                  int var112 = (int)Math.round(100.0D * (double)((var11 + 1) * 32 + var3 * 1024) / (double)(var4 * 1024));
                  if (var112 > var12) {
                     var5.progressStagePercentage(var112);
                  }
               }
            } catch (Throwable var107) {
               var10 = var107;
               throw var107;
            } finally {
               if (var9 != null) {
                  if (var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var99) {
                        var10.addSuppressed(var99);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (Throwable var109) {
            var8 = var109;
            throw var109;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var98) {
                     var8.addSuppressed(var98);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (IOException var111) {
         LOGGER.error("Failed to upgrade region file {}", var1, var111);
      }

   }

   private static void addRegionFiles(File var0, Collection<File> var1) {
      File var2 = new File(var0, "region");
      File[] var3 = var2.listFiles((var0x, var1x) -> {
         return var1x.endsWith(".mcr");
      });
      if (var3 != null) {
         Collections.addAll(var1, var3);
      }

   }
}
