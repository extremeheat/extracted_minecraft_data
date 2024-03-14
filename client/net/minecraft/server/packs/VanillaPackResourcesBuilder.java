package net.minecraft.server.packs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.Util;
import org.slf4j.Logger;

public class VanillaPackResourcesBuilder {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Consumer<VanillaPackResourcesBuilder> developmentConfig = var0 -> {
   };
   private static final Map<PackType, Path> ROOT_DIR_BY_TYPE = Util.make(() -> {
      synchronized(VanillaPackResources.class) {
         Builder var1 = ImmutableMap.builder();

         for(PackType var5 : PackType.values()) {
            String var6 = "/" + var5.getDirectory() + "/.mcassetsroot";
            URL var7 = VanillaPackResources.class.getResource(var6);
            if (var7 == null) {
               LOGGER.error("File {} does not exist in classpath", var6);
            } else {
               try {
                  URI var8 = var7.toURI();
                  String var9 = var8.getScheme();
                  if (!"jar".equals(var9) && !"file".equals(var9)) {
                     LOGGER.warn("Assets URL '{}' uses unexpected schema", var8);
                  }

                  Path var10 = safeGetPath(var8);
                  var1.put(var5, var10.getParent());
               } catch (Exception var12) {
                  LOGGER.error("Couldn't resolve path to vanilla assets", var12);
               }
            }
         }

         return var1.build();
      }
   });
   private final Set<Path> rootPaths = new LinkedHashSet<>();
   private final Map<PackType, Set<Path>> pathsForType = new EnumMap<>(PackType.class);
   private BuiltInMetadata metadata = BuiltInMetadata.of();
   private final Set<String> namespaces = new HashSet<>();

   public VanillaPackResourcesBuilder() {
      super();
   }

   private static Path safeGetPath(URI var0) throws IOException {
      try {
         return Paths.get(var0);
      } catch (FileSystemNotFoundException var3) {
      } catch (Throwable var4) {
         LOGGER.warn("Unable to get path for: {}", var0, var4);
      }

      try {
         FileSystems.newFileSystem(var0, Collections.emptyMap());
      } catch (FileSystemAlreadyExistsException var2) {
      }

      return Paths.get(var0);
   }

   private boolean validateDirPath(Path var1) {
      if (!Files.exists(var1)) {
         return false;
      } else if (!Files.isDirectory(var1)) {
         throw new IllegalArgumentException("Path " + var1.toAbsolutePath() + " is not directory");
      } else {
         return true;
      }
   }

   private void pushRootPath(Path var1) {
      if (this.validateDirPath(var1)) {
         this.rootPaths.add(var1);
      }
   }

   private void pushPathForType(PackType var1, Path var2) {
      if (this.validateDirPath(var2)) {
         this.pathsForType.computeIfAbsent(var1, var0 -> new LinkedHashSet()).add(var2);
      }
   }

   public VanillaPackResourcesBuilder pushJarResources() {
      ROOT_DIR_BY_TYPE.forEach((var1, var2) -> {
         this.pushRootPath(var2.getParent());
         this.pushPathForType(var1, var2);
      });
      return this;
   }

   public VanillaPackResourcesBuilder pushClasspathResources(PackType var1, Class<?> var2) {
      Enumeration var3 = null;

      try {
         var3 = var2.getClassLoader().getResources(var1.getDirectory() + "/");
      } catch (IOException var8) {
      }

      while(var3 != null && var3.hasMoreElements()) {
         URL var4 = (URL)var3.nextElement();

         try {
            URI var5 = var4.toURI();
            if ("file".equals(var5.getScheme())) {
               Path var6 = Paths.get(var5);
               this.pushRootPath(var6.getParent());
               this.pushPathForType(var1, var6);
            }
         } catch (Exception var7) {
            LOGGER.error("Failed to extract path from {}", var4, var7);
         }
      }

      return this;
   }

   public VanillaPackResourcesBuilder applyDevelopmentConfig() {
      developmentConfig.accept(this);
      return this;
   }

   public VanillaPackResourcesBuilder pushUniversalPath(Path var1) {
      this.pushRootPath(var1);

      for(PackType var5 : PackType.values()) {
         this.pushPathForType(var5, var1.resolve(var5.getDirectory()));
      }

      return this;
   }

   public VanillaPackResourcesBuilder pushAssetPath(PackType var1, Path var2) {
      this.pushRootPath(var2);
      this.pushPathForType(var1, var2);
      return this;
   }

   public VanillaPackResourcesBuilder setMetadata(BuiltInMetadata var1) {
      this.metadata = var1;
      return this;
   }

   public VanillaPackResourcesBuilder exposeNamespace(String... var1) {
      this.namespaces.addAll(Arrays.asList(var1));
      return this;
   }

   public VanillaPackResources build(PackLocationInfo var1) {
      EnumMap var2 = new EnumMap<>(PackType.class);

      for(PackType var6 : PackType.values()) {
         List var7 = copyAndReverse(this.pathsForType.getOrDefault(var6, Set.of()));
         var2.put(var6, var7);
      }

      return new VanillaPackResources(var1, this.metadata, Set.copyOf(this.namespaces), copyAndReverse(this.rootPaths), var2);
   }

   private static List<Path> copyAndReverse(Collection<Path> var0) {
      ArrayList var1 = new ArrayList(var0);
      Collections.reverse(var1);
      return List.copyOf(var1);
   }
}
