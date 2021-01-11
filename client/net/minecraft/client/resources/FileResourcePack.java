package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable {
   public static final Splitter field_110601_c = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile field_110600_d;

   public FileResourcePack(File var1) {
      super(var1);
   }

   private ZipFile func_110599_c() throws IOException {
      if (this.field_110600_d == null) {
         this.field_110600_d = new ZipFile(this.field_110597_b);
      }

      return this.field_110600_d;
   }

   protected InputStream func_110591_a(String var1) throws IOException {
      ZipFile var2 = this.func_110599_c();
      ZipEntry var3 = var2.getEntry(var1);
      if (var3 == null) {
         throw new ResourcePackFileNotFoundException(this.field_110597_b, var1);
      } else {
         return var2.getInputStream(var3);
      }
   }

   public boolean func_110593_b(String var1) {
      try {
         return this.func_110599_c().getEntry(var1) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public Set<String> func_110587_b() {
      ZipFile var1;
      try {
         var1 = this.func_110599_c();
      } catch (IOException var8) {
         return Collections.emptySet();
      }

      Enumeration var2 = var1.entries();
      HashSet var3 = Sets.newHashSet();

      while(var2.hasMoreElements()) {
         ZipEntry var4 = (ZipEntry)var2.nextElement();
         String var5 = var4.getName();
         if (var5.startsWith("assets/")) {
            ArrayList var6 = Lists.newArrayList(field_110601_c.split(var5));
            if (var6.size() > 1) {
               String var7 = (String)var6.get(1);
               if (!var7.equals(var7.toLowerCase())) {
                  this.func_110594_c(var7);
               } else {
                  var3.add(var7);
               }
            }
         }
      }

      return var3;
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() throws IOException {
      if (this.field_110600_d != null) {
         this.field_110600_d.close();
         this.field_110600_d = null;
      }

   }
}
