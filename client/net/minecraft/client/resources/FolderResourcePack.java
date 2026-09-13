package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class FolderResourcePack extends AbstractResourcePack {
   public FolderResourcePack(File var1) {
      super(var1);
   }

   @Override
   protected InputStream func_110591_a(String var1) {
      return new BufferedInputStream(new FileInputStream(new File(this.field_110597_b, var1)));
   }

   @Override
   protected boolean func_110593_b(String var1) {
      return new File(this.field_110597_b, var1).isFile();
   }

   @Override
   public Set func_110587_b() {
      HashSet var1 = Sets.newHashSet();
      File var2 = new File(this.field_110597_b, "assets/");
      if (var2.isDirectory()) {
         for(File var6 : var2.listFiles(DirectoryFileFilter.DIRECTORY)) {
            String var7 = func_110595_a(var2, var6);
            if (!var7.equals(var7.toLowerCase())) {
               this.func_110594_c(var7);
            } else {
               var1.add(var7.substring(0, var7.length() - 1));
            }
         }
      }

      return var1;
   }
}
