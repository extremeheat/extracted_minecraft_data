package net.minecraft.server.packs;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.slf4j.Logger;

public class FolderPackResources extends AbstractPackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean ON_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
   private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

   public FolderPackResources(File var1) {
      super(var1);
   }

   public static boolean validatePath(File var0, String var1) throws IOException {
      String var2 = var0.getCanonicalPath();
      if (ON_WINDOWS) {
         var2 = BACKSLASH_MATCHER.replaceFrom(var2, '/');
      }

      return var2.endsWith(var1);
   }

   @Override
   protected InputStream getResource(String var1) throws IOException {
      File var2 = this.getFile(var1);
      if (var2 == null) {
         throw new ResourcePackFileNotFoundException(this.file, var1);
      } else {
         return new FileInputStream(var2);
      }
   }

   @Override
   protected boolean hasResource(String var1) {
      return this.getFile(var1) != null;
   }

   @Nullable
   private File getFile(String var1) {
      try {
         File var2 = new File(this.file, var1);
         if (var2.isFile() && validatePath(var2, var1)) {
            return var2;
         }
      } catch (IOException var3) {
      }

      return null;
   }

   @Override
   public Set<String> getNamespaces(PackType var1) {
      HashSet var2 = Sets.newHashSet();
      File var3 = new File(this.file, var1.getDirectory());
      File[] var4 = var3.listFiles(DirectoryFileFilter.DIRECTORY);
      if (var4 != null) {
         for(File var8 : var4) {
            String var9 = getRelativePath(var3, var8);
            if (var9.equals(var9.toLowerCase(Locale.ROOT))) {
               var2.add(var9.substring(0, var9.length() - 1));
            } else {
               this.logWarning(var9);
            }
         }
      }

      return var2;
   }

   @Override
   public void close() {
   }

   @Override
   public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, Predicate<ResourceLocation> var4) {
      File var5 = new File(this.file, var1.getDirectory());
      ArrayList var6 = Lists.newArrayList();
      this.listResources(new File(new File(var5, var2), var3), var2, var6, var3 + "/", var4);
      return var6;
   }

   private void listResources(File var1, String var2, List<ResourceLocation> var3, String var4, Predicate<ResourceLocation> var5) {
      File[] var6 = var1.listFiles();
      if (var6 != null) {
         for(File var10 : var6) {
            if (var10.isDirectory()) {
               this.listResources(var10, var2, var3, var4 + var10.getName() + "/", var5);
            } else if (!var10.getName().endsWith(".mcmeta")) {
               try {
                  String var11 = var4 + var10.getName();
                  ResourceLocation var12 = ResourceLocation.tryBuild(var2, var11);
                  if (var12 == null) {
                     LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", var2, var11);
                  } else if (var5.test(var12)) {
                     var3.add(var12);
                  }
               } catch (ResourceLocationException var13) {
                  LOGGER.error(var13.getMessage());
               }
            }
         }
      }
   }
}
