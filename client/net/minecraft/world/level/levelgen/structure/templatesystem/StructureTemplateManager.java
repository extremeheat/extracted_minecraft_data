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
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String STRUCTURE_DIRECTORY_NAME = "structures";
   private static final String TEST_STRUCTURES_DIR = "gameteststructures";
   private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
   private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
   private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
   private final DataFixer fixerUpper;
   private ResourceManager resourceManager;
   private final Path generatedDir;
   private final List<StructureTemplateManager.Source> sources;
   private final HolderGetter<Block> blockLookup;
   private static final FileToIdConverter LISTER = new FileToIdConverter("structures", ".nbt");

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
      for(StructureTemplateManager.Source var3 : this.sources) {
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
      ResourceLocation var2 = LISTER.idToFile(var1);
      return this.load(() -> this.resourceManager.open(var2), var1x -> LOGGER.error("Couldn't load structure {}", var1, var1x));
   }

   private Stream<ResourceLocation> listResources() {
      return LISTER.listMatchingResources(this.resourceManager).keySet().stream().map(LISTER::fileToId);
   }

   private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation var1) {
      return this.loadFromSnbt(var1, Paths.get("gameteststructures"));
   }

   private Stream<ResourceLocation> listTestStructures() {
      return this.listFolderContents(Paths.get("gameteststructures"), "minecraft", ".snbt");
   }

   private Optional<StructureTemplate> loadFromGenerated(ResourceLocation var1) {
      if (!Files.isDirectory(this.generatedDir)) {
         return Optional.empty();
      } else {
         Path var2 = createAndValidatePathToStructure(this.generatedDir, var1, ".nbt");
         return this.load(() -> new FileInputStream(var2.toFile()), var1x -> LOGGER.error("Couldn't load structure from {}", var2, var1x));
      }
   }

   private Stream<ResourceLocation> listGenerated() {
      if (!Files.isDirectory(this.generatedDir)) {
         return Stream.empty();
      } else {
         try {
            return Files.list(this.generatedDir).filter(var0 -> Files.isDirectory(var0)).flatMap(var1 -> this.listGeneratedInNamespace(var1));
         } catch (IOException var2) {
            return Stream.empty();
         }
      }
   }

   private Stream<ResourceLocation> listGeneratedInNamespace(Path var1) {
      Path var2 = var1.resolve("structures");
      return this.listFolderContents(var2, var1.getFileName().toString(), ".nbt");
   }

   private Stream<ResourceLocation> listFolderContents(Path var1, String var2, String var3) {
      if (!Files.isDirectory(var1)) {
         return Stream.empty();
      } else {
         int var4 = var3.length();
         Function var5 = var1x -> var1x.substring(0, var1x.length() - var4);

         try {
            return Files.walk(var1).filter(var1x -> var1x.toString().endsWith(var3)).mapMulti((var4x, var5x) -> {
               try {
                  var5x.accept(new ResourceLocation(var2, (String)var5.apply(this.relativize(var1, var4x))));
               } catch (ResourceLocationException var7x) {
                  LOGGER.error("Invalid location while listing pack contents", var7x);
               }
            });
         } catch (IOException var7) {
            LOGGER.error("Failed to list folder contents", var7);
            return Stream.empty();
         }
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
         Optional var4;
         try (InputStream var3 = var1.open()) {
            var4 = Optional.of(this.readStructure(var3));
         }

         return var4;
      } catch (FileNotFoundException var8) {
         return Optional.empty();
      } catch (Throwable var9) {
         var2.accept(var9);
         return Optional.empty();
      }
   }

   private StructureTemplate readStructure(InputStream var1) throws IOException {
      CompoundTag var2 = NbtIo.readCompressed(var1);
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
      if (!var2.isPresent()) {
         return false;
      } else {
         StructureTemplate var3 = (StructureTemplate)var2.get();
         Path var4 = createAndValidatePathToStructure(this.generatedDir, var1, ".nbt");
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

   public Path getPathToGeneratedStructure(ResourceLocation var1, String var2) {
      return createPathToStructure(this.generatedDir, var1, var2);
   }

   public static Path createPathToStructure(Path var0, ResourceLocation var1, String var2) {
      try {
         Path var3 = var0.resolve(var1.getNamespace());
         Path var4 = var3.resolve("structures");
         return FileUtil.createPathToResource(var4, var1.getPath(), var2);
      } catch (InvalidPathException var5) {
         throw new ResourceLocationException("Invalid resource path: " + var1, var5);
      }
   }

   private static Path createAndValidatePathToStructure(Path var0, ResourceLocation var1, String var2) {
      if (var1.getPath().contains("//")) {
         throw new ResourceLocationException("Invalid resource path: " + var1);
      } else {
         Path var3 = createPathToStructure(var0, var1, var2);
         if (var3.startsWith(var0) && FileUtil.isPathNormalized(var3) && FileUtil.isPathPortable(var3)) {
            return var3;
         } else {
            throw new ResourceLocationException("Invalid resource path: " + var3);
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

   static record Source(Function<ResourceLocation, Optional<StructureTemplate>> a, Supplier<Stream<ResourceLocation>> b) {
      private final Function<ResourceLocation, Optional<StructureTemplate>> loader;
      private final Supplier<Stream<ResourceLocation>> lister;

      Source(Function<ResourceLocation, Optional<StructureTemplate>> var1, Supplier<Stream<ResourceLocation>> var2) {
         super();
         this.loader = var1;
         this.lister = var2;
      }
   }
}
