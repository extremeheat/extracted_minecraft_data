package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String STRUCTURE_RESOURCE_DIRECTORY_NAME = "structure";
   private static final String STRUCTURE_GENERATED_DIRECTORY_NAME = "structures";
   private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
   private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
   private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
   private final DataFixer fixerUpper;
   private ResourceManager resourceManager;
   private final Path generatedDir;
   private final List<Source> sources;
   private final HolderGetter<Block> blockLookup;
   private static final FileToIdConverter RESOURCE_LISTER = new FileToIdConverter("structure", ".nbt");

   public StructureTemplateManager(ResourceManager var1, LevelStorageSource.LevelStorageAccess var2, DataFixer var3, HolderGetter<Block> var4) {
      super();
      this.resourceManager = var1;
      this.fixerUpper = var3;
      this.generatedDir = var2.getLevelPath(LevelResource.GENERATED_DIR).normalize();
      this.blockLookup = var4;
      ImmutableList.Builder var5 = ImmutableList.builder();
      var5.add(new Source(this::loadFromGenerated, this::listGenerated));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         var5.add(new Source(this::loadFromTestStructures, this::listTestStructures));
      }

      var5.add(new Source(this::loadFromResource, this::listResources));
      this.sources = var5.build();
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
      return (Optional)this.structureRepository.computeIfAbsent(var1, this::tryLoad);
   }

   public Stream<ResourceLocation> listTemplates() {
      return this.sources.stream().flatMap((var0) -> (Stream)var0.lister().get()).distinct();
   }

   private Optional<StructureTemplate> tryLoad(ResourceLocation var1) {
      for(Source var3 : this.sources) {
         try {
            Optional var4 = (Optional)var3.loader().apply(var1);
            if (var4.isPresent()) {
               return var4;
            }
         } catch (Exception var5) {
         }
      }

      return Optional.empty();
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.resourceManager = var1;
      this.structureRepository.clear();
   }

   private Optional<StructureTemplate> loadFromResource(ResourceLocation var1) {
      ResourceLocation var2 = RESOURCE_LISTER.idToFile(var1);
      return this.load(() -> this.resourceManager.open(var2), (var1x) -> LOGGER.error("Couldn't load structure {}", var1, var1x));
   }

   private Stream<ResourceLocation> listResources() {
      Stream var10000 = RESOURCE_LISTER.listMatchingResources(this.resourceManager).keySet().stream();
      FileToIdConverter var10001 = RESOURCE_LISTER;
      Objects.requireNonNull(var10001);
      return var10000.map(var10001::fileToId);
   }

   private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation var1) {
      return this.loadFromSnbt(var1, Paths.get(StructureUtils.testStructuresDir));
   }

   private Stream<ResourceLocation> listTestStructures() {
      Path var1 = Paths.get(StructureUtils.testStructuresDir);
      if (!Files.isDirectory(var1, new LinkOption[0])) {
         return Stream.empty();
      } else {
         ArrayList var2 = new ArrayList();
         Objects.requireNonNull(var2);
         this.listFolderContents(var1, "minecraft", ".snbt", var2::add);
         return var2.stream();
      }
   }

   private Optional<StructureTemplate> loadFromGenerated(ResourceLocation var1) {
      if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
         return Optional.empty();
      } else {
         Path var2 = this.createAndValidatePathToGeneratedStructure(var1, ".nbt");
         return this.load(() -> new FileInputStream(var2.toFile()), (var1x) -> LOGGER.error("Couldn't load structure from {}", var2, var1x));
      }
   }

   private Stream<ResourceLocation> listGenerated() {
      if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
         return Stream.empty();
      } else {
         try {
            ArrayList var1 = new ArrayList();
            DirectoryStream var2 = Files.newDirectoryStream(this.generatedDir, (var0) -> Files.isDirectory(var0, new LinkOption[0]));

            try {
               for(Path var4 : var2) {
                  String var5 = var4.getFileName().toString();
                  Path var6 = var4.resolve("structures");
                  Objects.requireNonNull(var1);
                  this.listFolderContents(var6, var5, ".nbt", var1::add);
               }
            } catch (Throwable var8) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var2 != null) {
               var2.close();
            }

            return var1.stream();
         } catch (IOException var9) {
            return Stream.empty();
         }
      }
   }

   private void listFolderContents(Path var1, String var2, String var3, Consumer<ResourceLocation> var4) {
      int var5 = var3.length();
      Function var6 = (var1x) -> var1x.substring(0, var1x.length() - var5);

      try {
         Stream var7 = Files.find(var1, 2147483647, (var1x, var2x) -> var2x.isRegularFile() && var1x.toString().endsWith(var3), new FileVisitOption[0]);

         try {
            var7.forEach((var5x) -> {
               try {
                  var4.accept(ResourceLocation.fromNamespaceAndPath(var2, (String)var6.apply(this.relativize(var1, var5x))));
               } catch (ResourceLocationException var7) {
                  LOGGER.error("Invalid location while listing folder {} contents", var1, var7);
               }

            });
         } catch (Throwable var11) {
            if (var7 != null) {
               try {
                  var7.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (var7 != null) {
            var7.close();
         }
      } catch (IOException var12) {
         LOGGER.error("Failed to list folder {} contents", var1, var12);
      }

   }

   private String relativize(Path var1, Path var2) {
      return var1.relativize(var2).toString().replace(File.separator, "/");
   }

   private Optional<StructureTemplate> loadFromSnbt(ResourceLocation var1, Path var2) {
      if (!Files.isDirectory(var2, new LinkOption[0])) {
         return Optional.empty();
      } else {
         Path var3 = FileUtil.createPathToResource(var2, var1.getPath(), ".snbt");

         try {
            BufferedReader var4 = Files.newBufferedReader(var3);

            Optional var6;
            try {
               String var5 = IOUtils.toString(var4);
               var6 = Optional.of(this.readStructure(NbtUtils.snbtToStructure(var5)));
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               var4.close();
            }

            return var6;
         } catch (NoSuchFileException var9) {
            return Optional.empty();
         } catch (CommandSyntaxException | IOException var10) {
            LOGGER.error("Couldn't load structure from {}", var3, var10);
            return Optional.empty();
         }
      }
   }

   private Optional<StructureTemplate> load(InputStreamOpener var1, Consumer<Throwable> var2) {
      try {
         InputStream var3 = var1.open();

         Optional var5;
         try {
            FastBufferedInputStream var4 = new FastBufferedInputStream(var3);

            try {
               var5 = Optional.of(this.readStructure((InputStream)var4));
            } catch (Throwable var9) {
               try {
                  ((InputStream)var4).close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            ((InputStream)var4).close();
         } catch (Throwable var10) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (var3 != null) {
            var3.close();
         }

         return var5;
      } catch (FileNotFoundException var11) {
         return Optional.empty();
      } catch (Throwable var12) {
         var2.accept(var12);
         return Optional.empty();
      }
   }

   private StructureTemplate readStructure(InputStream var1) throws IOException {
      CompoundTag var2 = NbtIo.readCompressed(var1, NbtAccounter.unlimitedHeap());
      return this.readStructure(var2);
   }

   public StructureTemplate readStructure(CompoundTag var1) {
      StructureTemplate var2 = new StructureTemplate();
      int var3 = NbtUtils.getDataVersion(var1, 500);
      var2.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, var1, var3));
      return var2;
   }

   public boolean save(ResourceLocation var1) {
      Optional var2 = (Optional)this.structureRepository.get(var1);
      if (var2.isEmpty()) {
         return false;
      } else {
         StructureTemplate var3 = (StructureTemplate)var2.get();
         Path var4 = this.createAndValidatePathToGeneratedStructure(var1, ".nbt");
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
                     ((OutputStream)var7).close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }

                  throw var11;
               }

               ((OutputStream)var7).close();
               return true;
            } catch (Throwable var12) {
               return false;
            }
         }
      }
   }

   public Path createAndValidatePathToGeneratedStructure(ResourceLocation var1, String var2) {
      if (var1.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + String.valueOf(var1));
      } else {
         try {
            Path var3 = this.generatedDir.resolve(var1.getNamespace());
            Path var4 = var3.resolve("structures");
            Path var5 = FileUtil.createPathToResource(var4, var1.getPath(), var2);
            if (var5.startsWith(this.generatedDir) && FileUtil.isPathNormalized(var5) && FileUtil.isPathPortable(var5)) {
               return var5;
            } else {
               throw new ResourceLocationException("Invalid resource path: " + String.valueOf(var5));
            }
         } catch (InvalidPathException var6) {
            throw new ResourceLocationException("Invalid resource path: " + String.valueOf(var1), var6);
         }
      }
   }

   public void remove(ResourceLocation var1) {
      this.structureRepository.remove(var1);
   }

   static record Source(Function<ResourceLocation, Optional<StructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {
      Source(Function<ResourceLocation, Optional<StructureTemplate>> var1, Supplier<Stream<ResourceLocation>> var2) {
         super();
         this.loader = var1;
         this.lister = var2;
      }
   }

   @FunctionalInterface
   interface InputStreamOpener {
      InputStream open() throws IOException;
   }
}
