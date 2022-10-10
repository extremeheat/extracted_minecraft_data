package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;

public class WorldChunkEnumerator {
   private static final Pattern field_212158_a = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final File field_212159_b;
   private final Map<DimensionType, List<ChunkPos>> field_212162_e;

   public WorldChunkEnumerator(File var1) {
      super();
      this.field_212159_b = var1;
      Builder var2 = ImmutableMap.builder();
      Iterator var3 = DimensionType.func_212681_b().iterator();

      while(var3.hasNext()) {
         DimensionType var4 = (DimensionType)var3.next();
         var2.put(var4, this.func_212153_a(var4));
      }

      this.field_212162_e = var2.build();
   }

   private List<ChunkPos> func_212153_a(DimensionType var1) {
      ArrayList var2 = Lists.newArrayList();
      File var3 = var1.func_212679_a(this.field_212159_b);
      List var4 = this.func_212155_b(var3);
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         File var6 = (File)var5.next();
         var2.addAll(this.func_212150_a(var6));
      }

      var4.sort(File::compareTo);
      return var2;
   }

   private List<ChunkPos> func_212150_a(File var1) {
      ArrayList var2 = Lists.newArrayList();
      RegionFile var3 = null;

      ArrayList var5;
      try {
         Matcher var4 = field_212158_a.matcher(var1.getName());
         if (!var4.matches()) {
            var5 = var2;
            return var5;
         }

         int var20 = Integer.parseInt(var4.group(1)) << 5;
         int var6 = Integer.parseInt(var4.group(2)) << 5;
         var3 = new RegionFile(var1);

         for(int var7 = 0; var7 < 32; ++var7) {
            for(int var8 = 0; var8 < 32; ++var8) {
               if (var3.func_212167_b(var7, var8)) {
                  var2.add(new ChunkPos(var7 + var20, var8 + var6));
               }
            }
         }

         return var2;
      } catch (Throwable var18) {
         var5 = Lists.newArrayList();
      } finally {
         if (var3 != null) {
            try {
               var3.func_76708_c();
            } catch (IOException var17) {
            }
         }

      }

      return var5;
   }

   private List<File> func_212155_b(File var1) {
      File var2 = new File(var1, "region");
      File[] var3 = var2.listFiles((var0, var1x) -> {
         return var1x.endsWith(".mca");
      });
      return var3 != null ? Lists.newArrayList(var3) : Lists.newArrayList();
   }

   public List<ChunkPos> func_212541_a(DimensionType var1) {
      return (List)this.field_212162_e.get(var1);
   }
}
