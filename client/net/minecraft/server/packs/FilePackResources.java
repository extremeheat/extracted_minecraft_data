package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class FilePackResources extends AbstractPackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private final File file;
   @Nullable
   private ZipFile zipFile;
   private boolean failedToLoad;

   public FilePackResources(String var1, File var2, boolean var3) {
      super(var1, var3);
      this.file = var2;
   }

   @Nullable
   private ZipFile getOrCreateZipFile() {
      if (this.failedToLoad) {
         return null;
      } else {
         if (this.zipFile == null) {
            try {
               this.zipFile = new ZipFile(this.file);
            } catch (IOException var2) {
               LOGGER.error("Failed to open pack {}", this.file, var2);
               this.failedToLoad = true;
               return null;
            }
         }

         return this.zipFile;
      }
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

   @Nullable
   private IoSupplier<InputStream> getResource(String var1) {
      ZipFile var2 = this.getOrCreateZipFile();
      if (var2 == null) {
         return null;
      } else {
         ZipEntry var3 = var2.getEntry(var1);
         return var3 == null ? null : IoSupplier.create(var2, var3);
      }
   }

   @Override
   public Set<String> getNamespaces(PackType var1) {
      ZipFile var2 = this.getOrCreateZipFile();
      if (var2 == null) {
         return Set.of();
      } else {
         Enumeration var3 = var2.entries();
         HashSet var4 = Sets.newHashSet();

         while(var3.hasMoreElements()) {
            ZipEntry var5 = (ZipEntry)var3.nextElement();
            String var6 = var5.getName();
            if (var6.startsWith(var1.getDirectory() + "/")) {
               ArrayList var7 = Lists.newArrayList(SPLITTER.split(var6));
               if (var7.size() > 1) {
                  String var8 = (String)var7.get(1);
                  if (var8.equals(var8.toLowerCase(Locale.ROOT))) {
                     var4.add(var8);
                  } else {
                     LOGGER.warn("Ignored non-lowercase namespace: {} in {}", var8, this.file);
                  }
               }
            }
         }

         return var4;
      }
   }

   @Override
   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   @Override
   public void close() {
      if (this.zipFile != null) {
         IOUtils.closeQuietly(this.zipFile);
         this.zipFile = null;
      }
   }

   @Override
   public void listResources(PackType var1, String var2, String var3, PackResources.ResourceOutput var4) {
      ZipFile var5 = this.getOrCreateZipFile();
      if (var5 != null) {
         Enumeration var6 = var5.entries();
         String var7 = var1.getDirectory() + "/" + var2 + "/";
         String var8 = var7 + var3 + "/";

         while(var6.hasMoreElements()) {
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
}
