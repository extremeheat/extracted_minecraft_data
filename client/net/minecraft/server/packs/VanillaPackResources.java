package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.FileUtil;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class VanillaPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;
   private final ResourceMetadata builtInMetadata;
   private @Nullable ResourceMetadata resourceMetadata;
   private final Set<String> namespaces;
   private final List<Path> rootPaths;
   private final Map<PackType, List<Path>> pathsForType;

   VanillaPackResources(final PackLocationInfo location, final ResourceMetadata metadata, final Set<String> namespaces, final List<Path> rootPaths, final Map<PackType, List<Path>> pathsForType) {
      super();
      this.location = location;
      this.builtInMetadata = metadata;
      this.namespaces = namespaces;
      this.rootPaths = rootPaths;
      this.pathsForType = pathsForType;
   }

   public @Nullable IoSupplier<InputStream> getRootResource(final String... path) {
      FileUtil.validatePath(path);
      List<String> pathList = List.of(path);

      for(Path rootPath : this.rootPaths) {
         Path pathInRoot = FileUtil.resolvePath(rootPath, pathList);
         if (Files.exists(pathInRoot, new LinkOption[0]) && PathPackResources.validatePath(pathInRoot)) {
            return IoSupplier.create(pathInRoot);
         }
      }

      return null;
   }

   public void listRawPaths(final PackType type, final Identifier resource, final Consumer<Path> output) {
      FileUtil.decomposePath(resource.getPath()).ifSuccess((decomposedPath) -> {
         String namespace = resource.getNamespace();

         for(Path typePath : (List)this.pathsForType.get(type)) {
            Path namespacedPath = typePath.resolve(namespace);
            output.accept(FileUtil.resolvePath(namespacedPath, decomposedPath));
         }

      }).ifError((error) -> LOGGER.error("Invalid path {}: {}", resource, error.message()));
   }

   public void listResources(final PackType type, final String namespace, final String directory, final PackResources.ResourceOutput output) {
      FileUtil.decomposePath(directory).ifSuccess((decomposedPath) -> {
         List<Path> paths = (List)this.pathsForType.get(type);
         int pathsSize = paths.size();
         if (pathsSize == 1) {
            getResources(output, namespace, (Path)paths.get(0), decomposedPath);
         } else if (pathsSize > 1) {
            Map<Identifier, IoSupplier<InputStream>> resources = new HashMap();

            for(int i = 0; i < pathsSize - 1; ++i) {
               Objects.requireNonNull(resources);
               getResources(resources::putIfAbsent, namespace, (Path)paths.get(i), decomposedPath);
            }

            Path lastPath = (Path)paths.get(pathsSize - 1);
            if (resources.isEmpty()) {
               getResources(output, namespace, lastPath, decomposedPath);
            } else {
               Objects.requireNonNull(resources);
               getResources(resources::putIfAbsent, namespace, lastPath, decomposedPath);
               resources.forEach(output);
            }
         }

      }).ifError((error) -> LOGGER.error("Invalid path {}: {}", directory, error.message()));
   }

   private static void getResources(final PackResources.ResourceOutput result, final String namespace, final Path root, final List<String> directory) {
      Path namespaceDir = root.resolve(namespace);
      PathPackResources.listPath(namespace, namespaceDir, directory, result);
   }

   public @Nullable IoSupplier<InputStream> getResource(final PackType type, final Identifier location) {
      return (IoSupplier)FileUtil.decomposePath(location.getPath()).mapOrElse((decomposedPath) -> {
         String namespace = location.getNamespace();

         for(Path typePath : (List)this.pathsForType.get(type)) {
            Path resource = FileUtil.resolvePath(typePath.resolve(namespace), decomposedPath);
            if (Files.exists(resource, new LinkOption[0]) && PathPackResources.validatePath(resource)) {
               return IoSupplier.create(resource);
            }
         }

         return null;
      }, (error) -> {
         LOGGER.error("Invalid path {}: {}", location, error.message());
         return null;
      });
   }

   public Set<String> getNamespaces(final PackType type) {
      return this.namespaces;
   }

   public <T> @Nullable T getMetadataSection(final MetadataSectionType<T> metadataSerializer) {
      try {
         if (this.resourceMetadata == null) {
            this.resourceMetadata = AbstractPackResources.loadMetadata(this);
         }

         Optional<T> section = this.resourceMetadata.<T>getSection(metadataSerializer);
         if (section.isPresent()) {
            return (T)section.get();
         }
      } catch (Exception e) {
         LOGGER.warn("Failed to parse vanilla pack metadata", e);
      }

      return (T)this.builtInMetadata.getSection(metadataSerializer).orElse((Object)null);
   }

   public PackLocationInfo location() {
      return this.location;
   }

   public void close() {
   }

   public ResourceProvider asProvider() {
      return (location) -> Optional.ofNullable(this.getResource(PackType.CLIENT_RESOURCES, location)).map((s) -> new Resource(this, s));
   }
}
