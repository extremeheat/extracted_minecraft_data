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
import java.util.Optional;
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
   private static final String STRUCTURE_DIRECTORY_NAME = "structures";
   private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
   private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
   private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
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
      Optional var2 = this.get(var1);
      if (var2.isPresent()) {
         return (StructureTemplate)var2.get();
      } else {
         StructureTemplate var3 = new StructureTemplate();
         this.structureRepository.put(var1, Optional.of(var3));
         return var3;
      }
   }

   public Optional<StructureTemplate> get(ResourceLocation var1) {
      return (Optional)this.structureRepository.computeIfAbsent(var1, (var1x) -> {
         Optional var2 = this.loadFromGenerated(var1x);
         return var2.isPresent() ? var2 : this.loadFromResource(var1x);
      });
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.resourceManager = var1;
      this.structureRepository.clear();
   }

   private Optional<StructureTemplate> loadFromResource(ResourceLocation var1) {
      ResourceLocation var2 = new ResourceLocation(var1.getNamespace(), "structures/" + var1.getPath() + ".nbt");

      try {
         Resource var3 = this.resourceManager.getResource(var2);

         Optional var4;
         try {
            var4 = Optional.of(this.readStructure(var3.getInputStream()));
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }

         return var4;
      } catch (FileNotFoundException var8) {
         return Optional.empty();
      } catch (Throwable var9) {
         LOGGER.error("Couldn't load structure {}: {}", var1, var9.toString());
         return Optional.empty();
      }
   }

   private Optional<StructureTemplate> loadFromGenerated(ResourceLocation var1) {
      if (!this.generatedDir.toFile().isDirectory()) {
         return Optional.empty();
      } else {
         Path var2 = this.createAndValidatePathToStructure(var1, ".nbt");

         try {
            FileInputStream var3 = new FileInputStream(var2.toFile());

            Optional var4;
            try {
               var4 = Optional.of(this.readStructure((InputStream)var3));
            } catch (Throwable var7) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            var3.close();
            return var4;
         } catch (FileNotFoundException var8) {
            return Optional.empty();
         } catch (IOException var9) {
            LOGGER.error("Couldn't load structure from {}", var2, var9);
            return Optional.empty();
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
      Optional var2 = (Optional)this.structureRepository.get(var1);
      if (!var2.isPresent()) {
         return false;
      } else {
         StructureTemplate var3 = (StructureTemplate)var2.get();
         Path var4 = this.createAndValidatePathToStructure(var1, ".nbt");
         Path var5 = var4.getParent();
         if (var5 == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(var5, new LinkOption[0]) ? var5.toRealPath() : var5);
            } catch (IOException var13) {
               LOGGER.error("Failed to create parent directory: {}", var5);
               return false;
            }

            CompoundTag var6 = var3.save(new CompoundTag());

            try {
               FileOutputStream var7 = new FileOutputStream(var4.toFile());

               try {
                  NbtIo.writeCompressed(var6, (OutputStream)var7);
               } catch (Throwable var11) {
                  try {
                     var7.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               var7.close();
               return true;
            } catch (Throwable var12) {
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
