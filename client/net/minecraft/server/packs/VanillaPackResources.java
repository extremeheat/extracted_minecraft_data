package net.minecraft.server.packs;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPackResources implements PackResources, ResourceProvider {
   @Nullable
   public static Path generatedDir;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> clientObject;
   private static final Map<PackType, Path> ROOT_DIR_BY_TYPE = (Map)Util.make(() -> {
      Class var0 = VanillaPackResources.class;
      synchronized(VanillaPackResources.class) {
         Builder var1 = ImmutableMap.builder();
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

   public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, int var4, Predicate<String> var5) {
      HashSet var6 = Sets.newHashSet();
      if (generatedDir != null) {
         try {
            getResources(var6, var4, var2, generatedDir.resolve(var1.getDirectory()), var3, var5);
         } catch (IOException var13) {
         }

         if (var1 == PackType.CLIENT_RESOURCES) {
            Enumeration var7 = null;

            try {
               var7 = clientObject.getClassLoader().getResources(var1.getDirectory() + "/");
            } catch (IOException var12) {
            }

            while(var7 != null && var7.hasMoreElements()) {
               try {
                  URI var8 = ((URL)var7.nextElement()).toURI();
                  if ("file".equals(var8.getScheme())) {
                     getResources(var6, var4, var2, Paths.get(var8), var3, var5);
                  }
               } catch (IOException | URISyntaxException var11) {
               }
            }
         }
      }

      try {
         Path var14 = (Path)ROOT_DIR_BY_TYPE.get(var1);
         if (var14 != null) {
            getResources(var6, var4, var2, var14, var3, var5);
         } else {
            LOGGER.error("Can't access assets root for type: {}", var1);
         }
      } catch (NoSuchFileException | FileNotFoundException var9) {
      } catch (IOException var10) {
         LOGGER.error("Couldn't get a list of all vanilla resources", var10);
      }

      return var6;
   }

   private static void getResources(Collection<ResourceLocation> var0, int var1, String var2, Path var3, String var4, Predicate<String> var5) throws IOException {
      Path var6 = var3.resolve(var2);
      Stream var7 = Files.walk(var6.resolve(var4), var1, new FileVisitOption[0]);

      try {
         Stream var10000 = var7.filter((var1x) -> {
            return !var1x.endsWith(".mcmeta") && Files.isRegularFile(var1x, new LinkOption[0]) && var5.test(var1x.getFileName().toString());
         }).map((var2x) -> {
            return new ResourceLocation(var2, var6.relativize(var2x).toString().replaceAll("\\\\", "/"));
         });
         Objects.requireNonNull(var0);
         var10000.forEach(var0::add);
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

   public Resource getResource(final ResourceLocation var1) throws IOException {
      return new Resource() {
         @Nullable
         InputStream inputStream;

         public void close() throws IOException {
            if (this.inputStream != null) {
               this.inputStream.close();
            }

         }

         public ResourceLocation getLocation() {
            return var1;
         }

         public InputStream getInputStream() {
            try {
               this.inputStream = VanillaPackResources.this.getResource(PackType.CLIENT_RESOURCES, var1);
            } catch (IOException var2) {
               throw new UncheckedIOException("Could not get client resource from vanilla pack", var2);
            }

            return this.inputStream;
         }

         public boolean hasMetadata() {
            return false;
         }

         @Nullable
         public <T> T getMetadata(MetadataSectionSerializer<T> var1x) {
            return null;
         }

         public String getSourceName() {
            return var1.toString();
         }
      };
   }
}
