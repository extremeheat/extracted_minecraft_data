package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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
   private final List<StructureTemplateManager.Source> sources;
   private final HolderGetter<Block> blockLookup;
   private static final FileToIdConverter RESOURCE_LISTER = new FileToIdConverter("structure", ".nbt");

   public StructureTemplateManager(ResourceManager var1, LevelStorageSource.LevelStorageAccess var2, DataFixer var3, HolderGetter<Block> var4) {
      super();
      this.resourceManager = var1;
      this.fixerUpper = var3;
      this.generatedDir = var2.getLevelPath(LevelResource.GENERATED_DIR).normalize();
      this.blockLookup = var4;
      Builder var5 = ImmutableList.builder();
      var5.add(new StructureTemplateManager.Source(this::loadFromGenerated, this::listGenerated));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         var5.add(new StructureTemplateManager.Source(this::loadFromTestStructures, this::listTestStructures));
      }

      var5.add(new StructureTemplateManager.Source(this::loadFromResource, this::listResources));
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
      return this.structureRepository.computeIfAbsent(var1, this::tryLoad);
   }

   public Stream<ResourceLocation> listTemplates() {
      return this.sources.stream().flatMap(var0 -> var0.lister().get()).distinct();
   }

   private Optional<StructureTemplate> tryLoad(ResourceLocation var1) {
      for (StructureTemplateManager.Source var3 : this.sources) {
         try {
            Optional var4 = var3.loader().apply(var1);
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
      return this.load(() -> this.resourceManager.open(var2), var1x -> LOGGER.error("Couldn't load structure {}", var1, var1x));
   }

   private Stream<ResourceLocation> listResources() {
      return RESOURCE_LISTER.listMatchingResources(this.resourceManager).keySet().stream().map(RESOURCE_LISTER::fileToId);
   }

   private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation var1) {
      return this.loadFromSnbt(var1, Paths.get(StructureUtils.testStructuresDir));
   }

   private Stream<ResourceLocation> listTestStructures() {
      Path var1 = Paths.get(StructureUtils.testStructuresDir);
      if (!Files.isDirectory(var1)) {
         return Stream.empty();
      } else {
         ArrayList var2 = new ArrayList();
         this.listFolderContents(var1, "minecraft", ".snbt", var2::add);
         return var2.stream();
      }
   }

   private Optional<StructureTemplate> loadFromGenerated(ResourceLocation var1) {
      if (!Files.isDirectory(this.generatedDir)) {
         return Optional.empty();
      } else {
         Path var2 = this.createAndValidatePathToGeneratedStructure(var1, ".nbt");
         return this.load(() -> new FileInputStream(var2.toFile()), var1x -> LOGGER.error("Couldn't load structure from {}", var2, var1x));
      }
   }

   private Stream<ResourceLocation> listGenerated() {
      if (!Files.isDirectory(this.generatedDir)) {
         return Stream.empty();
      } else {
         try {
            ArrayList var1 = new ArrayList();

            try (DirectoryStream var2 = Files.newDirectoryStream(this.generatedDir, var0 -> Files.isDirectory(var0))) {
               for (Path var4 : var2) {
                  String var5 = var4.getFileName().toString();
                  Path var6 = var4.resolve("structures");
                  this.listFolderContents(var6, var5, ".nbt", var1::add);
               }
            }

            return var1.stream();
         } catch (IOException var9) {
            return Stream.empty();
         }
      }
   }

   private void listFolderContents(Path var1, String var2, String var3, Consumer<ResourceLocation> var4) {
      int var5 = var3.length();
      Function var6 = var1x -> var1x.substring(0, var1x.length() - var5);

      try (Stream var7 = Files.find(var1, 2147483647, (var1x, var2x) -> var2x.isRegularFile() && var1x.toString().endsWith(var3))) {
         var7.forEach(var5x -> {
            try {
               var4.accept(ResourceLocation.fromNamespaceAndPath(var2, (String)var6.apply(this.relativize(var1, var5x))));
            } catch (ResourceLocationException var7x) {
               LOGGER.error("Invalid location while listing folder {} contents", var1, var7x);
            }
         });
      } catch (IOException var12) {
         LOGGER.error("Failed to list folder {} contents", var1, var12);
      }
   }

   private String relativize(Path var1, Path var2) {
      return var1.relativize(var2).toString().replace(File.separator, "/");
   }

   private Optional<StructureTemplate> loadFromSnbt(ResourceLocation var1, Path var2) {
      if (!Files.isDirectory(var2)) {
         return Optional.empty();
      } else {
         Path var3 = FileUtil.createPathToResource(var2, var1.getPath(), ".snbt");

         try {
            Optional var6;
            try (BufferedReader var4 = Files.newBufferedReader(var3)) {
               String var5 = IOUtils.toString(var4);
               var6 = Optional.of(this.readStructure(NbtUtils.snbtToStructure(var5)));
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

   private Optional<StructureTemplate> load(StructureTemplateManager.InputStreamOpener var1, Consumer<Throwable> var2) {
      try {
         Optional var5;
         try (
            InputStream var3 = var1.open();
            FastBufferedInputStream var4 = new FastBufferedInputStream(var3);
         ) {
            var5 = Optional.of(this.readStructure(var4));
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
      Optional var2 = this.structureRepository.get(var1);
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
               Files.createDirectories(Files.exists(var5) ? var5.toRealPath() : var5);
            } catch (IOException var13) {
               LOGGER.error("Failed to create parent directory: {}", var5);
               return false;
            }

            CompoundTag var6 = var3.save(new CompoundTag());

            try {
               try (FileOutputStream var7 = new FileOutputStream(var4.toFile())) {
                  NbtIo.writeCompressed(var6, var7);
               }

               return true;
            } catch (Throwable var12) {
               return false;
            }
         }
      }
   }

   public Path createAndValidatePathToGeneratedStructure(ResourceLocation var1, String var2) {
      if (var1.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + var1);
      } else {
         try {
            Path var3 = this.generatedDir.resolve(var1.getNamespace());
            Path var4 = var3.resolve("structures");
            Path var5 = FileUtil.createPathToResource(var4, var1.getPath(), var2);
            if (var5.startsWith(this.generatedDir) && FileUtil.isPathNormalized(var5) && FileUtil.isPathPortable(var5)) {
               return var5;
            } else {
               throw new ResourceLocationException("Invalid resource path: " + var5);
            }
         } catch (InvalidPathException var6) {
            throw new ResourceLocationException("Invalid resource path: " + var1, var6);
         }
      }
   }

   public void remove(ResourceLocation var1) {
      this.structureRepository.remove(var1);
   }

   @FunctionalInterface
   interface InputStreamOpener {
      InputStream open() throws IOException;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
