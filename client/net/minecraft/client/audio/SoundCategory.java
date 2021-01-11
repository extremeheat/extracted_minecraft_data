package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.util.Map;

public enum SoundCategory {
   MASTER("master", 0),
   MUSIC("music", 1),
   RECORDS("record", 2),
   WEATHER("weather", 3),
   BLOCKS("block", 4),
   MOBS("hostile", 5),
   ANIMALS("neutral", 6),
   PLAYERS("player", 7),
   AMBIENT("ambient", 8);

   private static final Map<String, SoundCategory> field_147168_j = Maps.newHashMap();
   private static final Map<Integer, SoundCategory> field_147169_k = Maps.newHashMap();
   private final String field_147166_l;
   private final int field_147167_m;

   private SoundCategory(String var3, int var4) {
      this.field_147166_l = var3;
      this.field_147167_m = var4;
   }

   public String func_147155_a() {
      return this.field_147166_l;
   }

   public int func_147156_b() {
      return this.field_147167_m;
   }

   public static SoundCategory func_147154_a(String var0) {
      return (SoundCategory)field_147168_j.get(var0);
   }

   static {
      SoundCategory[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         SoundCategory var3 = var0[var2];
         if (field_147168_j.containsKey(var3.func_147155_a()) || field_147169_k.containsKey(var3.func_147156_b())) {
            throw new Error("Clash in Sound Category ID & Name pools! Cannot insert " + var3);
         }

         field_147168_j.put(var3.func_147155_a(), var3);
         field_147169_k.put(var3.func_147156_b(), var3);
      }

   }
}
