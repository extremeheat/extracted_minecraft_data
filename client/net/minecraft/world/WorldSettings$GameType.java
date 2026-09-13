package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;

public enum WorldSettings$GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure");

   int field_77154_e;
   String field_77151_f;

   private WorldSettings$GameType(int var3, String var4) {
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
      } else {
         var1.field_75101_c = false;
         var1.field_75098_d = false;
         var1.field_75102_a = false;
         var1.field_75100_b = false;
      }

      var1.field_75099_e = !this.func_82752_c();
   }

   public boolean func_82752_c() {
      return this == ADVENTURE;
   }

   public boolean func_77145_d() {
      return this == CREATIVE;
   }

   public boolean func_77144_e() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static WorldSettings$GameType func_77146_a(int var0) {
      for(WorldSettings$GameType var4 : values()) {
         if (var4.field_77154_e == var0) {
            return var4;
         }
      }

      return SURVIVAL;
   }

   public static WorldSettings$GameType func_77142_a(String var0) {
      for(WorldSettings$GameType var4 : values()) {
         if (var4.field_77151_f.equals(var0)) {
            return var4;
         }
      }

      return SURVIVAL;
   }
}
