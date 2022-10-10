package net.minecraft.resources;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderPack extends AbstractResourcePack {
   private static final Logger field_200699_b = LogManager.getLogger();
   private static final boolean field_195779_b;
   private static final CharMatcher field_195780_c;

   public FolderPack(File var1) {
      super(var1);
   }

   public static boolean func_195777_a(File var0, String var1) throws IOException {
      String var2 = var0.getCanonicalPath();
      if (field_195779_b) {
         var2 = field_195780_c.replaceFrom(var2, '/');
      }

      return var2.endsWith(var1);
   }

   protected InputStream func_195766_a(String var1) throws IOException {
      File var2 = this.func_195776_e(var1);
      if (var2 == null) {
         throw new ResourcePackFileNotFoundException(this.field_195771_a, var1);
      } else {
         return new FileInputStream(var2);
      }
   }

   protected boolean func_195768_c(String var1) {
      return this.func_195776_e(var1) != null;
   }

   @Nullable
   private File func_195776_e(String var1) {
      try {
         File var2 = new File(this.field_195771_a, var1);
         if (var2.isFile() && func_195777_a(var2, var1)) {
            return var2;
         }
      } catch (IOException var3) {
      }

      return null;
   }

   public Set<String> func_195759_a(ResourcePackType var1) {
      HashSet var2 = Sets.newHashSet();
      File var3 = new File(this.field_195771_a, var1.func_198956_a());
      File[] var4 = var3.listFiles(DirectoryFileFilter.DIRECTORY);
      if (var4 != null) {
         File[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File var8 = var5[var7];
            String var9 = func_195767_a(var3, var8);
            if (var9.equals(var9.toLowerCase(Locale.ROOT))) {
               var2.add(var9.substring(0, var9.length() - 1));
            } else {
               this.func_195769_d(var9);
            }
         }
      }

      return var2;
   }

   public void close() throws IOException {
   }

   public Collection<ResourceLocation> func_195758_a(ResourcePackType var1, String var2, int var3, Predicate<String> var4) {
      File var5 = new File(this.field_195771_a, var1.func_198956_a());
      ArrayList var6 = Lists.newArrayList();
      Iterator var7 = this.func_195759_a(var1).iterator();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         this.func_199546_a(new File(new File(var5, var8), var2), var3, var8, var6, var2 + "/", var4);
      }

      return var6;
   }

   private void func_199546_a(File var1, int var2, String var3, List<ResourceLocation> var4, String var5, Predicate<String> var6) {
      File[] var7 = var1.listFiles();
      if (var7 != null) {
         File[] var8 = var7;
         int var9 = var7.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            File var11 = var8[var10];
            if (var11.isDirectory()) {
               if (var2 > 0) {
                  this.func_199546_a(var11, var2 - 1, var3, var4, var5 + var11.getName() + "/", var6);
               }
            } else if (!var11.getName().endsWith(".mcmeta") && var6.test(var11.getName())) {
               try {
                  var4.add(new ResourceLocation(var3, var5 + var11.getName()));
               } catch (ResourceLocationException var13) {
                  field_200699_b.error(var13.getMessage());
               }
            }
         }
      }

   }

   static {
      field_195779_b = Util.func_110647_a() == Util.EnumOS.WINDOWS;
      field_195780_c = CharMatcher.is('\\');
   }
}
