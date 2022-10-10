package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
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
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack {
   public static Path field_199754_a;
   private static final Logger field_195784_b = LogManager.getLogger();
   public static Class<?> field_211688_b;
   public final Set<String> field_195783_a;

   public VanillaPack(String... var1) {
      super();
      this.field_195783_a = ImmutableSet.copyOf(var1);
   }

   public InputStream func_195763_b(String var1) throws IOException {
      if (!var1.contains("/") && !var1.contains("\\")) {
         if (field_199754_a != null) {
            Path var2 = field_199754_a.resolve(var1);
            if (Files.exists(var2, new LinkOption[0])) {
               return Files.newInputStream(var2);
            }
         }

         return this.func_200010_a(var1);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream func_195761_a(ResourcePackType var1, ResourceLocation var2) throws IOException {
      InputStream var3 = this.func_195782_c(var1, var2);
      if (var3 != null) {
         return var3;
      } else {
         throw new FileNotFoundException(var2.func_110623_a());
      }
   }

   public Collection<ResourceLocation> func_195758_a(ResourcePackType var1, String var2, int var3, Predicate<String> var4) {
      HashSet var5 = Sets.newHashSet();
      URI var7;
      if (field_199754_a != null) {
         try {
            var5.addAll(this.func_195781_a(var3, "minecraft", field_199754_a.resolve(var1.func_198956_a()).resolve("minecraft"), var2, var4));
         } catch (IOException var26) {
         }

         if (var1 == ResourcePackType.CLIENT_RESOURCES) {
            Enumeration var6 = null;

            try {
               var6 = field_211688_b.getClassLoader().getResources(var1.func_198956_a() + "/minecraft");
            } catch (IOException var25) {
            }

            while(var6 != null && var6.hasMoreElements()) {
               try {
                  var7 = ((URL)var6.nextElement()).toURI();
                  if ("file".equals(var7.getScheme())) {
                     var5.addAll(this.func_195781_a(var3, "minecraft", Paths.get(var7), var2, var4));
                  }
               } catch (IOException | URISyntaxException var24) {
               }
            }
         }
      }

      try {
         URL var30 = VanillaPack.class.getResource("/" + var1.func_198956_a() + "/.mcassetsroot");
         if (var30 == null) {
            field_195784_b.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
            return var5;
         }

         var7 = var30.toURI();
         if ("file".equals(var7.getScheme())) {
            URL var8 = new URL(var30.toString().substring(0, var30.toString().length() - ".mcassetsroot".length()) + "minecraft");
            if (var8 == null) {
               return var5;
            }

            Path var9 = Paths.get(var8.toURI());
            var5.addAll(this.func_195781_a(var3, "minecraft", var9, var2, var4));
         } else if ("jar".equals(var7.getScheme())) {
            FileSystem var31 = FileSystems.newFileSystem(var7, Collections.emptyMap());
            Throwable var32 = null;

            try {
               Path var10 = var31.getPath("/" + var1.func_198956_a() + "/minecraft");
               var5.addAll(this.func_195781_a(var3, "minecraft", var10, var2, var4));
            } catch (Throwable var23) {
               var32 = var23;
               throw var23;
            } finally {
               if (var31 != null) {
                  if (var32 != null) {
                     try {
                        var31.close();
                     } catch (Throwable var22) {
                        var32.addSuppressed(var22);
                     }
                  } else {
                     var31.close();
                  }
               }

            }
         } else {
            field_195784_b.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", var7);
         }
      } catch (NoSuchFileException | FileNotFoundException var28) {
      } catch (IOException | URISyntaxException var29) {
         field_195784_b.error("Couldn't get a list of all vanilla resources", var29);
      }

      return var5;
   }

   private Collection<ResourceLocation> func_195781_a(int var1, String var2, Path var3, String var4, Predicate<String> var5) throws IOException {
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
   protected InputStream func_195782_c(ResourcePackType var1, ResourceLocation var2) {
      String var3 = "/" + var1.func_198956_a() + "/" + var2.func_110624_b() + "/" + var2.func_110623_a();
      if (field_199754_a != null) {
         Path var4 = field_199754_a.resolve(var1.func_198956_a() + "/" + var2.func_110624_b() + "/" + var2.func_110623_a());
         if (Files.exists(var4, new LinkOption[0])) {
            try {
               return Files.newInputStream(var4);
            } catch (IOException var7) {
            }
         }
      }

      try {
         URL var8 = VanillaPack.class.getResource(var3);
         return var8 != null && FolderPack.func_195777_a(new File(var8.getFile()), var3) ? VanillaPack.class.getResourceAsStream(var3) : null;
      } catch (IOException var6) {
         return VanillaPack.class.getResourceAsStream(var3);
      }
   }

   @Nullable
   protected InputStream func_200010_a(String var1) {
      return VanillaPack.class.getResourceAsStream("/" + var1);
   }

   public boolean func_195764_b(ResourcePackType var1, ResourceLocation var2) {
      InputStream var3 = this.func_195782_c(var1, var2);
      boolean var4 = var3 != null;
      IOUtils.closeQuietly(var3);
      return var4;
   }

   public Set<String> func_195759_a(ResourcePackType var1) {
      return this.field_195783_a;
   }

   @Nullable
   public <T> T func_195760_a(IMetadataSectionSerializer<T> var1) throws IOException {
      try {
         InputStream var2 = this.func_195763_b("pack.mcmeta");
         Throwable var3 = null;

         Object var4;
         try {
            var4 = AbstractResourcePack.func_195770_a(var1, var2);
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

   public String func_195762_a() {
      return "Default";
   }

   public void close() {
   }
}
