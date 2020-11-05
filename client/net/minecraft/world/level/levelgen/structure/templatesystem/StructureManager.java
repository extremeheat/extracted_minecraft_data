package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, StructureTemplate> structureRepository = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private ResourceManager resourceManager;
   private final Path generatedDir;

   public StructureManager(ResourceManager var1, LevelStorageSource.LevelStorageAccess var2, DataFixer var3) {
      super();
      this.resourceManager = var1;
      this.fixerUpper = var3;
      this.generatedDir = var2.getLevelPath(LevelResource.GENERATED_DIR).normalize();
   }

   public StructureTemplate getOrCreate(ResourceLocation var1) {
      StructureTemplate var2 = this.get(var1);
      if (var2 == null) {
         var2 = new StructureTemplate();
         this.structureRepository.put(var1, var2);
      }

      return var2;
   }

   @Nullable
   public StructureTemplate get(ResourceLocation var1) {
      return (StructureTemplate)this.structureRepository.computeIfAbsent(var1, (var1x) -> {
         StructureTemplate var2 = this.loadFromGenerated(var1x);
         return var2 != null ? var2 : this.loadFromResource(var1x);
      });
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.resourceManager = var1;
      this.structureRepository.clear();
   }

   @Nullable
   private StructureTemplate loadFromResource(ResourceLocation var1) {
      ResourceLocation var2 = new ResourceLocation(var1.getNamespace(), "structures/" + var1.getPath() + ".nbt");

      try {
         Resource var3 = this.resourceManager.getResource(var2);
         Throwable var4 = null;

         StructureTemplate var5;
         try {
            var5 = this.readStructure(var3.getInputStream());
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var5;
      } catch (FileNotFoundException var18) {
         return null;
      } catch (Throwable var19) {
         LOGGER.error("Couldn't load structure {}: {}", var1, var19.toString());
         return null;
      }
   }

   @Nullable
   private StructureTemplate loadFromGenerated(ResourceLocation var1) {
      if (!this.generatedDir.toFile().isDirectory()) {
         return null;
      } else {
         Path var2 = this.createAndValidatePathToStructure(var1, ".nbt");

         try {
            FileInputStream var3 = new FileInputStream(var2.toFile());
            Throwable var4 = null;

            StructureTemplate var5;
            try {
               var5 = this.readStructure((InputStream)var3);
            } catch (Throwable var16) {
               var4 = var16;
               throw var16;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                     }
                  } else {
                     var3.close();
                  }
               }

            }

            return var5;
         } catch (FileNotFoundException var18) {
            return null;
         } catch (IOException var19) {
            LOGGER.error("Couldn't load structure from {}", var2, var19);
            return null;
         }
      }
   }

   private StructureTemplate readStructure(InputStream var1) throws IOException {
      CompoundTag var2 = NbtIo.readCompressed(var1);
      return this.readStructure(var2);
   }

   public StructureTemplate readStructure(CompoundTag var1) {
      if (!var1.contains("DataVersion", 99)) {
         var1.putInt("DataVersion", 500);
      }

      StructureTemplate var2 = new StructureTemplate();
      var2.load(NbtUtils.update(this.fixerUpper, DataFixTypes.STRUCTURE, var1, var1.getInt("DataVersion")));
      return var2;
   }

   public boolean save(ResourceLocation var1) {
      StructureTemplate var2 = (StructureTemplate)this.structureRepository.get(var1);
      if (var2 == null) {
         return false;
      } else {
         Path var3 = this.createAndValidatePathToStructure(var1, ".nbt");
         Path var4 = var3.getParent();
         if (var4 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(var4, new LinkOption[0]) ? var4.toRealPath() : var4);
            } catch (IOException var19) {
               LOGGER.error("Failed to create parent directory: {}", var4);
               return false;
            }

            CompoundTag var5 = var2.save(new CompoundTag());

            try {
               FileOutputStream var6 = new FileOutputStream(var3.toFile());
               Throwable var7 = null;

               try {
                  NbtIo.writeCompressed(var5, (OutputStream)var6);
               } catch (Throwable var18) {
                  var7 = var18;
                  throw var18;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var17) {
                           var7.addSuppressed(var17);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }

               return true;
            } catch (Throwable var21) {
               return false;
            }
         }
      }
   }

   public Path createPathToStructure(ResourceLocation var1, String var2) {
      try {
         Path var3 = this.generatedDir.resolve(var1.getNamespace());
         Path var4 = var3.resolve("structures");
         return FileUtil.createPathToResource(var4, var1.getPath(), var2);
      } catch (InvalidPathException var5) {
         throw new ResourceLocationException("Invalid resource path: " + var1, var5);
      }
   }

   private Path createAndValidatePathToStructure(ResourceLocation var1, String var2) {
      if (var1.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + var1);
      } else {
         Path var3 = this.createPathToStructure(var1, var2);
         if (var3.startsWith(this.generatedDir) && FileUtil.isPathNormalized(var3) && FileUtil.isPathPortable(var3)) {
            return var3;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + var3);
         }
      }
   }

   public void remove(ResourceLocation var1) {
      this.structureRepository.remove(var1);
   }
}
