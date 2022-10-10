package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   private final int field_77154_e;
   private final String field_77151_f;

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

   public ITextComponent func_196220_c() {
      return new TextComponentTranslation("gameMode." + this.field_77151_f, new Object[0]);
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

   public static GameType func_77146_a(int var0) {
      return func_185329_a(var0, SURVIVAL);
   }

   public static GameType func_185329_a(int var0, GameType var1) {
      GameType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         if (var5.field_77154_e == var0) {
            return var5;
         }
      }

      return var1;
   }

   public static GameType func_77142_a(String var0) {
      return func_185328_a(var0, SURVIVAL);
   }

   public static GameType func_185328_a(String var0, GameType var1) {
      GameType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         if (var5.field_77151_f.equals(var0)) {
            return var5;
         }
      }

      return var1;
   }
}
