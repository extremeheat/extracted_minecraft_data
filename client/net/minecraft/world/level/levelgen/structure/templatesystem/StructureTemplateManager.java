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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.IdentifierException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.FileUtil;
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
   private final Map<Identifier, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
   private final DataFixer fixerUpper;
   private ResourceManager resourceManager;
   private final Path generatedDir;
   private final List<Source> sources;
   private final HolderGetter<Block> blockLookup;
   private static final FileToIdConverter RESOURCE_LISTER = new FileToIdConverter("structure", ".nbt");

   public StructureTemplateManager(final ResourceManager resourceManager, final LevelStorageSource.LevelStorageAccess storage, final DataFixer fixerUpper, final HolderGetter<Block> blockLookup) {
      super();
      this.resourceManager = resourceManager;
      this.fixerUpper = fixerUpper;
      this.generatedDir = storage.getLevelPath(LevelResource.GENERATED_DIR).normalize();
      this.blockLookup = blockLookup;
      ImmutableList.Builder<Source> builder = ImmutableList.builder();
      builder.add(new Source(this::loadFromGenerated, this::listGenerated));
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         builder.add(new Source(this::loadFromTestStructures, this::listTestStructures));
      }

      builder.add(new Source(this::loadFromResource, this::listResources));
      this.sources = builder.build();
   }

   public StructureTemplate getOrCreate(final Identifier id) {
      Optional<StructureTemplate> cachedTemplate = this.get(id);
      if (cachedTemplate.isPresent()) {
         return (StructureTemplate)cachedTemplate.get();
      } else {
         StructureTemplate template = new StructureTemplate();
         this.structureRepository.put(id, Optional.of(template));
         return template;
      }
   }

   public Optional<StructureTemplate> get(final Identifier id) {
      return (Optional)this.structureRepository.computeIfAbsent(id, this::tryLoad);
   }

   public Stream<Identifier> listTemplates() {
      return this.sources.stream().flatMap((s) -> (Stream)s.lister().get()).distinct();
   }

   private Optional<StructureTemplate> tryLoad(final Identifier id) {
      for(Source source : this.sources) {
         try {
            Optional<StructureTemplate> loaded = (Optional)source.loader().apply(id);
            if (loaded.isPresent()) {
               return loaded;
            }
         } catch (Exception var5) {
         }
      }

      return Optional.empty();
   }

   public void onResourceManagerReload(final ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
      this.structureRepository.clear();
   }

   private Optional<StructureTemplate> loadFromResource(final Identifier id) {
      Identifier identifier = RESOURCE_LISTER.idToFile(id);
      return this.load(() -> this.resourceManager.open(identifier), (e) -> LOGGER.error("Couldn't load structure {}", id, e));
   }

   private Stream<Identifier> listResources() {
      Stream var10000 = RESOURCE_LISTER.listMatchingResources(this.resourceManager).keySet().stream();
      FileToIdConverter var10001 = RESOURCE_LISTER;
      Objects.requireNonNull(var10001);
      return var10000.map(var10001::fileToId);
   }

   private Optional<StructureTemplate> loadFromTestStructures(final Identifier id) {
      return this.loadFromSnbt(id, StructureUtils.testStructuresDir);
   }

   private Stream<Identifier> listTestStructures() {
      if (!Files.isDirectory(StructureUtils.testStructuresDir, new LinkOption[0])) {
         return Stream.empty();
      } else {
         List<Identifier> result = new ArrayList();
         Path var10001 = StructureUtils.testStructuresDir;
         Objects.requireNonNull(result);
         this.listFolderContents(var10001, "minecraft", ".snbt", result::add);
         return result.stream();
      }
   }

   private Optional<StructureTemplate> loadFromGenerated(final Identifier id) {
      if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
         return Optional.empty();
      } else {
         Path file = this.createAndValidatePathToGeneratedStructure(id, ".nbt");
         return this.load(() -> new FileInputStream(file.toFile()), (e) -> LOGGER.error("Couldn't load structure from {}", file, e));
      }
   }

   private Stream<Identifier> listGenerated() {
      if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
         return Stream.empty();
      } else {
         try {
            List<Identifier> result = new ArrayList();
            DirectoryStream<Path> contents = Files.newDirectoryStream(this.generatedDir, (x$0) -> Files.isDirectory(x$0, new LinkOption[0]));

            try {
               for(Path namespaceDir : contents) {
                  String namespace = namespaceDir.getFileName().toString();
                  Path structureDir = namespaceDir.resolve("structures");
                  Objects.requireNonNull(result);
                  this.listFolderContents(structureDir, namespace, ".nbt", result::add);
               }
            } catch (Throwable var8) {
               if (contents != null) {
                  try {
                     contents.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (contents != null) {
               contents.close();
            }

            return result.stream();
         } catch (IOException var9) {
            return Stream.empty();
         }
      }
   }

   private void listFolderContents(final Path folder, final String namespace, final String extension, final Consumer<Identifier> output) {
      int extensionLength = extension.length();
      Function<String, String> pathProcessor = (s) -> s.substring(0, s.length() - extensionLength);

      try {
         Stream<Path> contents = Files.find(folder, 2147483647, (path, attributes) -> attributes.isRegularFile() && path.toString().endsWith(extension), new FileVisitOption[0]);

         try {
            contents.forEach((file) -> {
               try {
                  output.accept(Identifier.fromNamespaceAndPath(namespace, (String)pathProcessor.apply(this.relativize(folder, file))));
               } catch (IdentifierException e) {
                  LOGGER.error("Invalid location while listing folder {} contents", folder, e);
               }

            });
         } catch (Throwable var11) {
            if (contents != null) {
               try {
                  contents.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (contents != null) {
            contents.close();
         }
      } catch (IOException e) {
         LOGGER.error("Failed to list folder {} contents", folder, e);
      }

   }

   private String relativize(final Path root, final Path file) {
      return root.relativize(file).toString().replace(File.separator, "/");
   }

   private Optional<StructureTemplate> loadFromSnbt(final Identifier id, final Path dir) {
      if (!Files.isDirectory(dir, new LinkOption[0])) {
         return Optional.empty();
      } else {
         Path file = FileUtil.createPathToResource(dir, id.getPath(), ".snbt");

         try {
            BufferedReader reader = Files.newBufferedReader(file);

            Optional var6;
            try {
               String input = IOUtils.toString(reader);
               var6 = Optional.of(this.readStructure(NbtUtils.snbtToStructure(input)));
            } catch (Throwable var8) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (reader != null) {
               reader.close();
            }

            return var6;
         } catch (NoSuchFileException var9) {
            return Optional.empty();
         } catch (CommandSyntaxException | IOException e) {
            LOGGER.error("Couldn't load structure from {}", file, e);
            return Optional.empty();
         }
      }
   }

   private Optional<StructureTemplate> load(final InputStreamOpener opener, final Consumer<Throwable> onError) {
      try {
         InputStream rawInput = opener.open();

         Optional var5;
         try {
            InputStream input = new FastBufferedInputStream(rawInput);

            try {
               var5 = Optional.of(this.readStructure(input));
            } catch (Throwable var9) {
               try {
                  input.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            input.close();
         } catch (Throwable var10) {
            if (rawInput != null) {
               try {
                  rawInput.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (rawInput != null) {
            rawInput.close();
         }

         return var5;
      } catch (FileNotFoundException var11) {
         return Optional.empty();
      } catch (Throwable e) {
         onError.accept(e);
         return Optional.empty();
      }
   }

   private StructureTemplate readStructure(final InputStream input) throws IOException {
      CompoundTag tag = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
      return this.readStructure(tag);
   }

   public StructureTemplate readStructure(final CompoundTag tag) {
      StructureTemplate structureTemplate = new StructureTemplate();
      int version = NbtUtils.getDataVersion(tag, 500);
      structureTemplate.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, tag, version));
      return structureTemplate;
   }

   public boolean save(final Identifier id) {
      Optional<StructureTemplate> maybeStructureTemplate = (Optional)this.structureRepository.get(id);
      if (maybeStructureTemplate.isEmpty()) {
         return false;
      } else {
         StructureTemplate structureTemplate = (StructureTemplate)maybeStructureTemplate.get();
         Path file = this.createAndValidatePathToGeneratedStructure(id, SharedConstants.DEBUG_SAVE_STRUCTURES_AS_SNBT ? ".snbt" : ".nbt");
         Path parent = file.getParent();
         if (parent == null) {
            return false;
         } else {
            try {
               Files.createDirectories(Files.exists(parent, new LinkOption[0]) ? parent.toRealPath() : parent);
            } catch (IOException var14) {
               LOGGER.error("Failed to create parent directory: {}", parent);
               return false;
            }

            CompoundTag tag = structureTemplate.save(new CompoundTag());
            if (SharedConstants.DEBUG_SAVE_STRUCTURES_AS_SNBT) {
               try {
                  NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, file, NbtUtils.structureToSnbt(tag));
               } catch (Throwable var13) {
                  return false;
               }
            } else {
               try {
                  OutputStream output = new FileOutputStream(file.toFile());

                  try {
                     NbtIo.writeCompressed(tag, output);
                  } catch (Throwable var11) {
                     try {
                        output.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }

                     throw var11;
                  }

                  output.close();
               } catch (Throwable var12) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public Path createAndValidatePathToGeneratedStructure(final Identifier id, final String extension) {
      if (id.getPath().contains("//")) {
         throw new IdentifierException("Invalid resource path: " + String.valueOf(id));
      } else {
         try {
            Path namespaceDir = this.generatedDir.resolve(id.getNamespace());
            Path structureDir = namespaceDir.resolve("structures");
            Path pathToResource = FileUtil.createPathToResource(structureDir, id.getPath(), extension);
            if (pathToResource.startsWith(this.generatedDir) && FileUtil.isPathNormalized(pathToResource) && FileUtil.isPathPortable(pathToResource)) {
               return pathToResource;
            } else {
               throw new IdentifierException("Invalid resource path: " + String.valueOf(pathToResource));
            }
         } catch (InvalidPathException e) {
            throw new IdentifierException("Invalid resource path: " + String.valueOf(id), e);
         }
      }
   }

   public void remove(final Identifier id) {
      this.structureRepository.remove(id);
   }

   private static record Source(Function<Identifier, Optional<StructureTemplate>> loader, Supplier<Stream<Identifier>> lister) {
      private Source {
         super();
      }
   }

   @FunctionalInterface
   private interface InputStreamOpener {
      InputStream open() throws IOException;
   }
}
