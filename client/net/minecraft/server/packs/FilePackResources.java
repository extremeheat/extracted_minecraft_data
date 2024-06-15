package net.minecraft.server.packs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class FilePackResources extends AbstractPackResources {
   static final Logger LOGGER = LogUtils.getLogger();
   private final FilePackResources.SharedZipFileAccess zipFileAccess;
   private final String prefix;

   FilePackResources(PackLocationInfo var1, FilePackResources.SharedZipFileAccess var2, String var3) {
      super(var1);
      this.zipFileAccess = var2;
      this.prefix = var3;
   }

   private static String getPathFromLocation(PackType var0, ResourceLocation var1) {
      return String.format(Locale.ROOT, "%s/%s/%s", var0.getDirectory(), var1.getNamespace(), var1.getPath());
   }

   @Nullable
   @Override
   public IoSupplier<InputStream> getRootResource(String... var1) {
      return this.getResource(String.join("/", var1));
   }

   @Override
   public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
      return this.getResource(getPathFromLocation(var1, var2));
   }

   private String addPrefix(String var1) {
      return this.prefix.isEmpty() ? var1 : this.prefix + "/" + var1;
   }

   @Nullable
   private IoSupplier<InputStream> getResource(String var1) {
      ZipFile var2 = this.zipFileAccess.getOrCreateZipFile();
      if (var2 == null) {
         return null;
      } else {
         ZipEntry var3 = var2.getEntry(this.addPrefix(var1));
         return var3 == null ? null : IoSupplier.create(var2, var3);
      }
   }

   @Override
   public Set<String> getNamespaces(PackType var1) {
      ZipFile var2 = this.zipFileAccess.getOrCreateZipFile();
      if (var2 == null) {
         return Set.of();
      } else {
         Enumeration var3 = var2.entries();
         HashSet var4 = Sets.newHashSet();
         String var5 = this.addPrefix(var1.getDirectory() + "/");

         while (var3.hasMoreElements()) {
            ZipEntry var6 = (ZipEntry)var3.nextElement();
            String var7 = var6.getName();
            String var8 = extractNamespace(var5, var7);
            if (!var8.isEmpty()) {
               if (ResourceLocation.isValidNamespace(var8)) {
                  var4.add(var8);
               } else {
                  LOGGER.warn("Non [a-z0-9_.-] character in namespace {} in pack {}, ignoring", var8, this.zipFileAccess.file);
               }
            }
         }

         return var4;
      }
   }

   @VisibleForTesting
   public static String extractNamespace(String var0, String var1) {
      if (!var1.startsWith(var0)) {
         return "";
      } else {
         int var2 = var0.length();
         int var3 = var1.indexOf(47, var2);
         return var3 == -1 ? var1.substring(var2) : var1.substring(var2, var3);
      }
   }

   @Override
   public void close() {
      this.zipFileAccess.close();
   }

   @Override
   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      ZipFile var5 = this.zipFileAccess.getOrCreateZipFile();
      if (var5 != null) {
         Enumeration var6 = var5.entries();
         String var7 = this.addPrefix(var1.getDirectory() + "/" + var2 + "/");
         String var8 = var7 + var3 + "/";

         while (var6.hasMoreElements()) {
            ZipEntry var9 = (ZipEntry)var6.nextElement();
            if (!var9.isDirectory()) {
               String var10 = var9.getName();
               if (var10.startsWith(var8)) {
                  String var11 = var10.substring(var7.length());
                  ResourceLocation var12 = ResourceLocation.tryBuild(var2, var11);
                  if (var12 != null) {
                     var4.accept(var12, IoSupplier.create(var5, var9));
                  } else {
                     LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", var2, var11);
                  }
               }
            }
         }
      }
   }

   public static class FileResourcesSupplier implements Pack.ResourcesSupplier {
      private final File content;

      public FileResourcesSupplier(Path var1) {
         this(var1.toFile());
      }

      public FileResourcesSupplier(File var1) {
         super();
         this.content = var1;
      }

      @Override
      public PackResources openPrimary(PackLocationInfo var1) {
         FilePackResources.SharedZipFileAccess var2 = new FilePackResources.SharedZipFileAccess(this.content);
         return new FilePackResources(var1, var2, "");
      }

      @Override
      public PackResources openFull(PackLocationInfo var1, Pack.Metadata var2) {
         FilePackResources.SharedZipFileAccess var3 = new FilePackResources.SharedZipFileAccess(this.content);
         FilePackResources var4 = new FilePackResources(var1, var3, "");
         List var5 = var2.overlays();
         if (var5.isEmpty()) {
            return var4;
         } else {
            ArrayList var6 = new ArrayList(var5.size());

            for (String var8 : var5) {
               var6.add(new FilePackResources(var1, var3, var8));
            }

            return new CompositePackResources(var4, var6);
         }
      }
   }

   static class SharedZipFileAccess implements AutoCloseable {
      final File file;
      @Nullable
      private ZipFile zipFile;
      private boolean failedToLoad;

      SharedZipFileAccess(File var1) {
         super();
         this.file = var1;
      }

      @Nullable
      ZipFile getOrCreateZipFile() {
         if (this.failedToLoad) {
            return null;
         } else {
            if (this.zipFile == null) {
               try {
                  this.zipFile = new ZipFile(this.file);
               } catch (IOException var2) {
                  FilePackResources.LOGGER.error("Failed to open pack {}", this.file, var2);
                  this.failedToLoad = true;
                  return null;
               }
            }

            return this.zipFile;
         }
      }

      @Override
      public void close() {
         if (this.zipFile != null) {
            IOUtils.closeQuietly(this.zipFile);
            this.zipFile = null;
         }
      }

      @Override
      protected void finalize() throws Throwable {
         this.close();
         super.finalize();
      }
   }
}
