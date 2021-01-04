package net.minecraft.world.level;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Abilities;

public enum GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   private final int id;
   private final String name;

   private GameType(int var3, String var4) {
      this.id = var3;
      this.name = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return new TranslatableComponent("gameMode." + this.name, new Object[0]);
   }

   public void updatePlayerAbilities(Abilities var1) {
      if (this == CREATIVE) {
         var1.mayfly = true;
         var1.instabuild = true;
         var1.invulnerable = true;
      } else if (this == SPECTATOR) {
         var1.mayfly = true;
         var1.instabuild = false;
         var1.invulnerable = true;
         var1.flying = true;
      } else {
         var1.mayfly = false;
         var1.instabuild = false;
         var1.invulnerable = false;
         var1.flying = false;
      }

      var1.mayBuild = !this.isBlockPlacingRestricted();
   }

   public boolean isBlockPlacingRestricted() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvival() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType byId(int var0) {
      return byId(var0, SURVIVAL);
   }

   public static GameType byId(int var0, GameType var1) {
      GameType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         if (var5.id == var0) {
            return var5;
         }
      }

      return var1;
   }

   public static GameType byName(String var0) {
      return byName(var0, SURVIVAL);
   }

   public static GameType byName(String var0, GameType var1) {
      GameType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         if (var5.name.equals(var0)) {
            return var5;
         }
      }

      return var1;
   }
}
