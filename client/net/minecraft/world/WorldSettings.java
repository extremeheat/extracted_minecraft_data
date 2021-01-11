package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.storage.WorldInfo;

public final class WorldSettings {
   private final long field_77174_a;
   private final WorldSettings.GameType field_77172_b;
   private final boolean field_77173_c;
   private final boolean field_77170_d;
   private final WorldType field_77171_e;
   private boolean field_77168_f;
   private boolean field_77169_g;
   private String field_82751_h;

   public WorldSettings(long var1, WorldSettings.GameType var3, boolean var4, boolean var5, WorldType var6) {
      super();
      this.field_82751_h = "";
      this.field_77174_a = var1;
      this.field_77172_b = var3;
      this.field_77173_c = var4;
      this.field_77170_d = var5;
      this.field_77171_e = var6;
   }

   public WorldSettings(WorldInfo var1) {
      this(var1.func_76063_b(), var1.func_76077_q(), var1.func_76089_r(), var1.func_76093_s(), var1.func_76067_t());
   }

   public WorldSettings func_77159_a() {
      this.field_77169_g = true;
      return this;
   }

   public WorldSettings func_77166_b() {
      this.field_77168_f = true;
      return this;
   }

   public WorldSettings func_82750_a(String var1) {
      this.field_82751_h = var1;
      return this;
   }

   public boolean func_77167_c() {
      return this.field_77169_g;
   }

   public long func_77160_d() {
      return this.field_77174_a;
   }

   public WorldSettings.GameType func_77162_e() {
      return this.field_77172_b;
   }

   public boolean func_77158_f() {
      return this.field_77170_d;
   }

   public boolean func_77164_g() {
      return this.field_77173_c;
   }

   public WorldType func_77165_h() {
      return this.field_77171_e;
   }

   public boolean func_77163_i() {
      return this.field_77168_f;
   }

   public static WorldSettings.GameType func_77161_a(int var0) {
      return WorldSettings.GameType.func_77146_a(var0);
   }

   public String func_82749_j() {
      return this.field_82751_h;
   }

   public static enum GameType {
      NOT_SET(-1, ""),
      SURVIVAL(0, "survival"),
      CREATIVE(1, "creative"),
      ADVENTURE(2, "adventure"),
      SPECTATOR(3, "spectator");

      int field_77154_e;
      String field_77151_f;

      private GameType(int var3, String var4) {
         this.field_77154_e = var3;
         this.field_77151_f = var4;
      }

      public int func_77148_a() {
         return this.field_77154_e;
      }

      public String func_77149_b() {
         return this.field_77151_f;
      }

      public void func_77147_a(PlayerCapabilities var1) {
         if (this == CREATIVE) {
            var1.field_75101_c = true;
            var1.field_75098_d = true;
            var1.field_75102_a = true;
         } else if (this == SPECTATOR) {
            var1.field_75101_c = true;
            var1.field_75098_d = false;
            var1.field_75102_a = true;
            var1.field_75100_b = true;
         } else {
            var1.field_75101_c = false;
            var1.field_75098_d = false;
            var1.field_75102_a = false;
            var1.field_75100_b = false;
         }

         var1.field_75099_e = !this.func_82752_c();
      }

      public boolean func_82752_c() {
         return this == ADVENTURE || this == SPECTATOR;
      }

      public boolean func_77145_d() {
         return this == CREATIVE;
      }

      public boolean func_77144_e() {
         return this == SURVIVAL || this == ADVENTURE;
      }

      public static WorldSettings.GameType func_77146_a(int var0) {
         WorldSettings.GameType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            WorldSettings.GameType var4 = var1[var3];
            if (var4.field_77154_e == var0) {
               return var4;
            }
         }

         return SURVIVAL;
      }

      public static WorldSettings.GameType func_77142_a(String var0) {
         WorldSettings.GameType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            WorldSettings.GameType var4 = var1[var3];
            if (var4.field_77151_f.equals(var0)) {
               return var4;
            }
         }

         return SURVIVAL;
      }
   }
}
