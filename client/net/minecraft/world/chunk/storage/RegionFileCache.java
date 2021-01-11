package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RegionFileCache {
   private static final Map<File, RegionFile> field_76553_a = Maps.newHashMap();

   public static synchronized RegionFile func_76550_a(File var0, int var1, int var2) {
      File var3 = new File(var0, "region");
      File var4 = new File(var3, "r." + (var1 >> 5) + "." + (var2 >> 5) + ".mca");
      RegionFile var5 = (RegionFile)field_76553_a.get(var4);
      if (var5 != null) {
         return var5;
      } else {
         if (!var3.exists()) {
            var3.mkdirs();
         }

         if (field_76553_a.size() >= 256) {
            func_76551_a();
         }

         RegionFile var6 = new RegionFile(var4);
         field_76553_a.put(var4, var6);
         return var6;
      }
   }

   public static synchronized void func_76551_a() {
      Iterator var0 = field_76553_a.values().iterator();

      while(var0.hasNext()) {
         RegionFile var1 = (RegionFile)var0.next();

         try {
            if (var1 != null) {
               var1.func_76708_c();
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      field_76553_a.clear();
   }

   public static DataInputStream func_76549_c(File var0, int var1, int var2) {
      RegionFile var3 = func_76550_a(var0, var1, var2);
      return var3.func_76704_a(var1 & 31, var2 & 31);
   }

   public static DataOutputStream func_76552_d(File var0, int var1, int var2) {
      RegionFile var3 = func_76550_a(var0, var1, var2);
      return var3.func_76710_b(var1 & 31, var2 & 31);
   }
}
