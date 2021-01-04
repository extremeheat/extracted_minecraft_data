package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FileResourcePack extends AbstractResourcePack {
   public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile zipFile;

   public FileResourcePack(File var1) {
      super(var1);
   }

   private ZipFile getOrCreateZipFile() throws IOException {
      if (this.zipFile == null) {
         this.zipFile = new ZipFile(this.file);
      }

      return this.zipFile;
   }

   protected InputStream getResource(String var1) throws IOException {
      ZipFile var2 = this.getOrCreateZipFile();
      ZipEntry var3 = var2.getEntry(var1);
      if (var3 == null) {
         throw new ResourcePackFileNotFoundException(this.file, var1);
      } else {
         return var2.getInputStream(var3);
      }
   }

   public boolean hasResource(String var1) {
      try {
         return this.getOrCreateZipFile().getEntry(var1) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public Set<String> getNamespaces(PackType var1) {
      ZipFile var2;
      try {
         var2 = this.getOrCreateZipFile();
      } catch (IOException var9) {
         return Collections.emptySet();
      }

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
                  this.logWarning(var8);
               }
            }
         }
      }

      return var4;
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() {
      if (this.zipFile != null) {
         IOUtils.closeQuietly(this.zipFile);
         this.zipFile = null;
      }

   }

   public Collection<ResourceLocation> getResources(PackType var1, String var2, int var3, Predicate<String> var4) {
      ZipFile var5;
      try {
         var5 = this.getOrCreateZipFile();
      } catch (IOException var15) {
         return Collections.emptySet();
      }

      Enumeration var6 = var5.entries();
      ArrayList var7 = Lists.newArrayList();
      String var8 = var1.getDirectory() + "/";

      while(var6.hasMoreElements()) {
         ZipEntry var9 = (ZipEntry)var6.nextElement();
         if (!var9.isDirectory() && var9.getName().startsWith(var8)) {
            String var10 = var9.getName().substring(var8.length());
            if (!var10.endsWith(".mcmeta")) {
               int var11 = var10.indexOf(47);
               if (var11 >= 0) {
                  String var12 = var10.substring(var11 + 1);
                  if (var12.startsWith(var2 + "/")) {
                     String[] var13 = var12.substring(var2.length() + 2).split("/");
                     if (var13.length >= var3 + 1 && var4.test(var12)) {
                        String var14 = var10.substring(0, var11);
                        var7.add(new ResourceLocation(var14, var12));
                     }
                  }
               }
            }
         }
      }

      return var7;
   }
}
