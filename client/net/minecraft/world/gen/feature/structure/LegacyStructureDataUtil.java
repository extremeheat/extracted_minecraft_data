package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LegacyStructureDataUtil {
   private static final Logger field_208219_a = LogManager.getLogger();
   private static final Map<String, String> field_208220_b = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      var0.put("Village", "Village");
      var0.put("Mineshaft", "Mineshaft");
      var0.put("Mansion", "Mansion");
      var0.put("Igloo", "Temple");
      var0.put("Desert_Pyramid", "Temple");
      var0.put("Jungle_Pyramid", "Temple");
      var0.put("Swamp_Hut", "Temple");
      var0.put("Stronghold", "Stronghold");
      var0.put("Monument", "Monument");
      var0.put("Fortress", "Fortress");
      var0.put("EndCity", "EndCity");
   });
   private static final Map<String, String> field_208221_c = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      var0.put("Iglu", "Igloo");
      var0.put("TeDP", "Desert_Pyramid");
      var0.put("TeJP", "Jungle_Pyramid");
      var0.put("TeSH", "Swamp_Hut");
   });
   private final boolean field_208222_d;
   private final Map<String, Long2ObjectMap<NBTTagCompound>> field_208223_e = Maps.newHashMap();
   private final Map<String, StructureIndexesSavedData> field_208224_f = Maps.newHashMap();

   public LegacyStructureDataUtil(@Nullable WorldSavedDataStorage var1) {
      super();
      this.func_212184_a(var1);
      boolean var2 = false;
      String[] var3 = this.func_208218_b();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var2 |= this.field_208223_e.get(var6) != null;
      }

      this.field_208222_d = var2;
   }

   public void func_208216_a(long var1) {
      String[] var3 = this.func_208214_a();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         StructureIndexesSavedData var7 = (StructureIndexesSavedData)this.field_208224_f.get(var6);
         if (var7 != null && var7.func_208023_c(var1)) {
            var7.func_201762_c(var1);
            var7.func_76185_a();
         }
      }

   }

   public NBTTagCompound func_212181_a(NBTTagCompound var1) {
      NBTTagCompound var2 = var1.func_74775_l("Level");
      ChunkPos var3 = new ChunkPos(var2.func_74762_e("xPos"), var2.func_74762_e("zPos"));
      if (this.func_208209_a(var3.field_77276_a, var3.field_77275_b)) {
         var1 = this.func_212182_a(var1, var3);
      }

      NBTTagCompound var4 = var2.func_74775_l("Structures");
      NBTTagCompound var5 = var4.func_74775_l("References");
      String[] var6 = this.func_208218_b();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         Structure var10 = (Structure)Feature.field_202300_at.get(var9.toLowerCase(Locale.ROOT));
         if (!var5.func_150297_b(var9, 12) && var10 != null) {
            int var11 = var10.func_202367_b();
            LongArrayList var12 = new LongArrayList();

            for(int var13 = var3.field_77276_a - var11; var13 <= var3.field_77276_a + var11; ++var13) {
               for(int var14 = var3.field_77275_b - var11; var14 <= var3.field_77275_b + var11; ++var14) {
                  if (this.func_208211_a(var13, var14, var9)) {
                     var12.add(ChunkPos.func_77272_a(var13, var14));
                  }
               }
            }

            var5.func_202168_c(var9, var12);
         }
      }

      var4.func_74782_a("References", var5);
      var2.func_74782_a("Structures", var4);
      var1.func_74782_a("Level", var2);
      return var1;
   }

   protected abstract String[] func_208214_a();

   protected abstract String[] func_208218_b();

   private boolean func_208211_a(int var1, int var2, String var3) {
      if (!this.field_208222_d) {
         return false;
      } else {
         return this.field_208223_e.get(var3) != null && ((StructureIndexesSavedData)this.field_208224_f.get(field_208220_b.get(var3))).func_208024_b(ChunkPos.func_77272_a(var1, var2));
      }
   }

   private boolean func_208209_a(int var1, int var2) {
      if (!this.field_208222_d) {
         return false;
      } else {
         String[] var3 = this.func_208218_b();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (this.field_208223_e.get(var6) != null && ((StructureIndexesSavedData)this.field_208224_f.get(field_208220_b.get(var6))).func_208023_c(ChunkPos.func_77272_a(var1, var2))) {
               return true;
            }
         }

         return false;
      }
   }

   private NBTTagCompound func_212182_a(NBTTagCompound var1, ChunkPos var2) {
      NBTTagCompound var3 = var1.func_74775_l("Level");
      NBTTagCompound var4 = var3.func_74775_l("Structures");
      NBTTagCompound var5 = var4.func_74775_l("Starts");
      String[] var6 = this.func_208218_b();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         Long2ObjectMap var10 = (Long2ObjectMap)this.field_208223_e.get(var9);
         if (var10 != null) {
            long var11 = var2.func_201841_a();
            if (((StructureIndexesSavedData)this.field_208224_f.get(field_208220_b.get(var9))).func_208023_c(var11)) {
               NBTTagCompound var13 = (NBTTagCompound)var10.get(var11);
               if (var13 != null) {
                  var5.func_74782_a(var9, var13);
               }
            }
         }
      }

      var4.func_74782_a("Starts", var5);
      var3.func_74782_a("Structures", var4);
      var1.func_74782_a("Level", var3);
      return var1;
   }

   private void func_212184_a(@Nullable WorldSavedDataStorage var1) {
      if (var1 != null) {
         String[] var2 = this.func_208214_a();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            NBTTagCompound var6 = new NBTTagCompound();

            try {
               var6 = var1.func_208028_a(var5, 1493).func_74775_l("data").func_74775_l("Features");
               if (var6.isEmpty()) {
                  continue;
               }
            } catch (IOException var15) {
            }

            Iterator var7 = var6.func_150296_c().iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               NBTTagCompound var9 = var6.func_74775_l(var8);
               long var10 = ChunkPos.func_77272_a(var9.func_74762_e("ChunkX"), var9.func_74762_e("ChunkZ"));
               NBTTagList var12 = var9.func_150295_c("Children", 10);
               String var13;
               if (!var12.isEmpty()) {
                  var13 = var12.func_150305_b(0).func_74779_i("id");
                  String var14 = (String)field_208221_c.get(var13);
                  if (var14 != null) {
                     var9.func_74778_a("id", var14);
                  }
               }

               var13 = var9.func_74779_i("id");
               ((Long2ObjectMap)this.field_208223_e.computeIfAbsent(var13, (var0) -> {
                  return new Long2ObjectOpenHashMap();
               })).put(var10, var9);
            }

            String var16 = var5 + "_index";
            StructureIndexesSavedData var17 = (StructureIndexesSavedData)var1.func_212426_a(DimensionType.OVERWORLD, StructureIndexesSavedData::new, var16);
            if (var17 != null && !var17.func_208025_a().isEmpty()) {
               this.field_208224_f.put(var5, var17);
            } else {
               StructureIndexesSavedData var18 = new StructureIndexesSavedData(var16);
               this.field_208224_f.put(var5, var18);
               Iterator var19 = var6.func_150296_c().iterator();

               while(var19.hasNext()) {
                  String var11 = (String)var19.next();
                  NBTTagCompound var20 = var6.func_74775_l(var11);
                  var18.func_201763_a(ChunkPos.func_77272_a(var20.func_74762_e("ChunkX"), var20.func_74762_e("ChunkZ")));
               }

               var1.func_212424_a(DimensionType.OVERWORLD, var16, var18);
               var18.func_76185_a();
            }
         }

      }
   }

   public static LegacyStructureDataUtil func_212183_a(DimensionType var0, @Nullable WorldSavedDataStorage var1) {
      if (var0 == DimensionType.OVERWORLD) {
         return new LegacyStructureDataUtil.Overworld(var1);
      } else if (var0 == DimensionType.NETHER) {
         return new LegacyStructureDataUtil.Nether(var1);
      } else if (var0 == DimensionType.THE_END) {
         return new LegacyStructureDataUtil.End(var1);
      } else {
         throw new RuntimeException(String.format("Unknown dimension type : %s", var0));
      }
   }

   public static class End extends LegacyStructureDataUtil {
      private static final String[] field_208227_a = new String[]{"EndCity"};

      public End(@Nullable WorldSavedDataStorage var1) {
         super(var1);
      }

      protected String[] func_208214_a() {
         return field_208227_a;
      }

      protected String[] func_208218_b() {
         return field_208227_a;
      }
   }

   public static class Nether extends LegacyStructureDataUtil {
      private static final String[] field_208228_a = new String[]{"Fortress"};

      public Nether(@Nullable WorldSavedDataStorage var1) {
         super(var1);
      }

      protected String[] func_208214_a() {
         return field_208228_a;
      }

      protected String[] func_208218_b() {
         return field_208228_a;
      }
   }

   public static class Overworld extends LegacyStructureDataUtil {
      private static final String[] field_208225_a = new String[]{"Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"};
      private static final String[] field_208226_b = new String[]{"Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"};

      public Overworld(@Nullable WorldSavedDataStorage var1) {
         super(var1);
      }

      protected String[] func_208214_a() {
         return field_208225_a;
      }

      protected String[] func_208218_b() {
         return field_208226_b;
      }
   }
}
