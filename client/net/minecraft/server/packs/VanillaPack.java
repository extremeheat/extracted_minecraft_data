package net.minecraft.server.packs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements Pack {
   public static Path generatedDir;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> clientObject;
   private static final Map<PackType, FileSystem> JAR_FILESYSTEM_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      Class var1 = VanillaPack.class;
      synchronized(VanillaPack.class) {
         PackType[] var2 = PackType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PackType var5 = var2[var4];
            URL var6 = VanillaPack.class.getResource("/" + var5.getDirectory() + "/.mcassetsroot");

            try {
               URI var7 = var6.toURI();
               if ("jar".equals(var7.getScheme())) {
                  FileSystem var8;
                  try {
                     var8 = FileSystems.getFileSystem(var7);
                  } catch (FileSystemNotFoundException var11) {
                     var8 = FileSystems.newFileSystem(var7, Collections.emptyMap());
                  }

                  var0.put(var5, var8);
               }
            } catch (IOException | URISyntaxException var12) {
               LOGGER.error("Couldn't get a list of all vanilla resources", var12);
            }
         }

      }
   });
   public final Set<String> namespaces;

   public VanillaPack(String... var1) {
      super();
      this.namespaces = ImmutableSet.copyOf(var1);
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

   public Collection<ResourceLocation> getResources(PackType var1, String var2, int var3, Predicate<String> var4) {
      HashSet var5 = Sets.newHashSet();
      URI var7;
      if (generatedDir != null) {
         try {
            var5.addAll(this.getResources(var3, "minecraft", generatedDir.resolve(var1.getDirectory()).resolve("minecraft"), var2, var4));
         } catch (IOException var14) {
         }

         if (var1 == PackType.CLIENT_RESOURCES) {
            Enumeration var6 = null;

            try {
               var6 = clientObject.getClassLoader().getResources(var1.getDirectory() + "/minecraft");
            } catch (IOException var13) {
            }

            while(var6 != null && var6.hasMoreElements()) {
               try {
                  var7 = ((URL)var6.nextElement()).toURI();
                  if ("file".equals(var7.getScheme())) {
                     var5.addAll(this.getResources(var3, "minecraft", Paths.get(var7), var2, var4));
                  }
               } catch (IOException | URISyntaxException var12) {
               }
            }
         }
      }

      try {
         URL var15 = VanillaPack.class.getResource("/" + var1.getDirectory() + "/.mcassetsroot");
         if (var15 == null) {
            LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
            return var5;
         }

         var7 = var15.toURI();
         if ("file".equals(var7.getScheme())) {
            URL var8 = new URL(var15.toString().substring(0, var15.toString().length() - ".mcassetsroot".length()) + "minecraft");
            if (var8 == null) {
               return var5;
            }

            Path var9 = Paths.get(var8.toURI());
            var5.addAll(this.getResources(var3, "minecraft", var9, var2, var4));
         } else if ("jar".equals(var7.getScheme())) {
            Path var16 = ((FileSystem)JAR_FILESYSTEM_BY_TYPE.get(var1)).getPath("/" + var1.getDirectory() + "/minecraft");
            var5.addAll(this.getResources(var3, "minecraft", var16, var2, var4));
         } else {
            LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", var7);
         }
      } catch (NoSuchFileException | FileNotFoundException var10) {
      } catch (IOException | URISyntaxException var11) {
         LOGGER.error("Couldn't get a list of all vanilla resources", var11);
      }

      return var5;
   }

   private Collection<ResourceLocation> getResources(int var1, String var2, Path var3, String var4, Predicate<String> var5) throws IOException {
      ArrayList var6 = Lists.newArrayList();
      Iterator var7 = Files.walk(var3.resolve(var4), var1, new FileVisitOption[0]).iterator();

      while(var7.hasNext()) {
         Path var8 = (Path)var7.next();
         if (!var8.endsWith(".mcmeta") && Files.isRegularFile(var8, new LinkOption[0]) && var5.test(var8.getFileName().toString())) {
            var6.add(new ResourceLocation(var2, var3.relativize(var8).toString().replaceAll("\\\\", "/")));
         }
      }

      return var6;
   }

   @Nullable
   protected InputStream getResourceAsStream(PackType var1, ResourceLocation var2) {
      String var3 = createPath(var1, var2);
      if (generatedDir != null) {
         Path var4 = generatedDir.resolve(var1.getDirectory() + "/" + var2.getNamespace() + "/" + var2.getPath());
         if (Files.exists(var4, new LinkOption[0])) {
            try {
               return Files.newInputStream(var4);
            } catch (IOException var7) {
            }
         }
      }

      try {
         URL var8 = VanillaPack.class.getResource(var3);
         return isResourceUrlValid(var3, var8) ? var8.openStream() : null;
      } catch (IOException var6) {
         return VanillaPack.class.getResourceAsStream(var3);
      }
   }

   private static String createPath(PackType var0, ResourceLocation var1) {
      return "/" + var0.getDirectory() + "/" + var1.getNamespace() + "/" + var1.getPath();
   }

   private static boolean isResourceUrlValid(String var0, @Nullable URL var1) throws IOException {
      return var1 != null && (var1.getProtocol().equals("jar") || FolderResourcePack.validatePath(new File(var1.getFile()), var0));
   }

   @Nullable
   protected InputStream getResourceAsStream(String var1) {
      return VanillaPack.class.getResourceAsStream("/" + var1);
   }

   public boolean hasResource(PackType var1, ResourceLocation var2) {
      String var3 = createPath(var1, var2);
      if (generatedDir != null) {
         Path var4 = generatedDir.resolve(var1.getDirectory() + "/" + var2.getNamespace() + "/" + var2.getPath());
         if (Files.exists(var4, new LinkOption[0])) {
            return true;
         }
      }

      try {
         URL var6 = VanillaPack.class.getResource(var3);
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
         Throwable var3 = null;

         Object var4;
         try {
            var4 = AbstractResourcePack.getMetadataFromStream(var1, var2);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var4;
      } catch (FileNotFoundException | RuntimeException var16) {
         return null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }
}
