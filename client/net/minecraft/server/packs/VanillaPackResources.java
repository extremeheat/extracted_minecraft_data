package net.minecraft.server.packs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;

public class VanillaPackResources implements PackResources {
   @Nullable
   public static Path generatedDir;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Class<?> clientObject;
   private static final Map<PackType, Path> ROOT_DIR_BY_TYPE = (Map)Util.make(() -> {
      Class var0 = VanillaPackResources.class;
      synchronized(VanillaPackResources.class) {
         ImmutableMap.Builder var1 = ImmutableMap.builder();
         PackType[] var2 = PackType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PackType var5 = var2[var4];
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
   public final PackMetadataSection packMetadata;
   public final Set<String> namespaces;

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

   public VanillaPackResources(PackMetadataSection var1, String... var2) {
      super();
      this.packMetadata = var1;
      this.namespaces = ImmutableSet.copyOf(var2);
   }

   public InputStream getRootResource(String var1) throws IOException {
      if (!var1.contains("/") && !var1.contains("\\")) {
         if (generatedDir != null) {
            Path var2 = generatedDir.resolve(var1);
            if (Files.exists(var2, new LinkOption[0])) {
               return Files.newInputStream(var2);
            }
         }

         return this.getResourceAsStream(var1);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResource(PackType var1, ResourceLocation var2) throws IOException {
      InputStream var3 = this.getResourceAsStream(var1, var2);
      if (var3 != null) {
         return var3;
      } else {
         throw new FileNotFoundException(var2.getPath());
      }
   }

   public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, Predicate<ResourceLocation> var4) {
      HashSet var5 = Sets.newHashSet();
      if (generatedDir != null) {
         try {
            getResources(var5, var2, generatedDir.resolve(var1.getDirectory()), var3, var4);
         } catch (IOException var12) {
         }

         if (var1 == PackType.CLIENT_RESOURCES) {
            Enumeration var6 = null;

            try {
               var6 = clientObject.getClassLoader().getResources(var1.getDirectory() + "/");
            } catch (IOException var11) {
            }

            while(var6 != null && var6.hasMoreElements()) {
               try {
                  URI var7 = ((URL)var6.nextElement()).toURI();
                  if ("file".equals(var7.getScheme())) {
                     getResources(var5, var2, Paths.get(var7), var3, var4);
                  }
               } catch (IOException | URISyntaxException var10) {
               }
            }
         }
      }

      try {
         Path var13 = (Path)ROOT_DIR_BY_TYPE.get(var1);
         if (var13 != null) {
            getResources(var5, var2, var13, var3, var4);
         } else {
            LOGGER.error("Can't access assets root for type: {}", var1);
         }
      } catch (NoSuchFileException | FileNotFoundException var8) {
      } catch (IOException var9) {
         LOGGER.error("Couldn't get a list of all vanilla resources", var9);
      }

      return var5;
   }

   private static void getResources(Collection<ResourceLocation> var0, String var1, Path var2, String var3, Predicate<ResourceLocation> var4) throws IOException {
      Path var5 = var2.resolve(var1);
      Stream var6 = Files.walk(var5.resolve(var3));

      try {
         Stream var10000 = var6.filter((var0x) -> {
            return !var0x.endsWith(".mcmeta") && Files.isRegularFile(var0x, new LinkOption[0]);
         }).mapMulti((var2x, var3x) -> {
            String var4 = var5.relativize(var2x).toString().replaceAll("\\\\", "/");
            ResourceLocation var5x = ResourceLocation.tryBuild(var1, var4);
            if (var5x == null) {
               Util.logAndPauseIfInIde("Invalid path in datapack: %s:%s, ignoring".formatted(var1, var4));
            } else {
               var3x.accept(var5x);
            }

         }).filter(var4);
         Objects.requireNonNull(var0);
         var10000.forEach(var0::add);
      } catch (Throwable var10) {
         if (var6 != null) {
            try {
               var6.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (var6 != null) {
         var6.close();
      }

   }

   @Nullable
   protected InputStream getResourceAsStream(PackType var1, ResourceLocation var2) {
      String var3 = createPath(var1, var2);
      if (generatedDir != null) {
         Path var10000 = generatedDir;
         String var10001 = var1.getDirectory();
         Path var4 = var10000.resolve(var10001 + "/" + var2.getNamespace() + "/" + var2.getPath());
         if (Files.exists(var4, new LinkOption[0])) {
            try {
               return Files.newInputStream(var4);
            } catch (IOException var7) {
            }
         }
      }

      try {
         URL var8 = VanillaPackResources.class.getResource(var3);
         return isResourceUrlValid(var3, var8) ? var8.openStream() : null;
      } catch (IOException var6) {
         return VanillaPackResources.class.getResourceAsStream(var3);
      }
   }

   private static String createPath(PackType var0, ResourceLocation var1) {
      String var10000 = var0.getDirectory();
      return "/" + var10000 + "/" + var1.getNamespace() + "/" + var1.getPath();
   }

   private static boolean isResourceUrlValid(String var0, @Nullable URL var1) throws IOException {
      return var1 != null && (var1.getProtocol().equals("jar") || FolderPackResources.validatePath(new File(var1.getFile()), var0));
   }

   @Nullable
   protected InputStream getResourceAsStream(String var1) {
      return VanillaPackResources.class.getResourceAsStream("/" + var1);
   }

   public boolean hasResource(PackType var1, ResourceLocation var2) {
      String var3 = createPath(var1, var2);
      if (generatedDir != null) {
         Path var10000 = generatedDir;
         String var10001 = var1.getDirectory();
         Path var4 = var10000.resolve(var10001 + "/" + var2.getNamespace() + "/" + var2.getPath());
         if (Files.exists(var4, new LinkOption[0])) {
            return true;
         }
      }

      try {
         URL var6 = VanillaPackResources.class.getResource(var3);
         return isResourceUrlValid(var3, var6);
      } catch (IOException var5) {
         return false;
      }
   }

   public Set<String> getNamespaces(PackType var1) {
      return this.namespaces;
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
      try {
         InputStream var2 = this.getRootResource("pack.mcmeta");

         label51: {
            Object var4;
            try {
               if (var2 == null) {
                  break label51;
               }

               Object var3 = AbstractPackResources.getMetadataFromStream(var1, var2);
               if (var3 == null) {
                  break label51;
               }

               var4 = var3;
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }

            return var4;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (FileNotFoundException | RuntimeException var7) {
      }

      return var1 == PackMetadataSection.SERIALIZER ? this.packMetadata : null;
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }

   public ResourceProvider asProvider() {
      return (var1) -> {
         return Optional.of(new Resource(this.getName(), () -> {
            return this.getResource(PackType.CLIENT_RESOURCES, var1);
         }));
      };
   }
}
