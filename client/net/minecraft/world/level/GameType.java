package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Abilities;

public enum GameType {
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   public static final GameType DEFAULT_MODE = SURVIVAL;
   private static final int NOT_SET = -1;
   private final int id;
   private final String name;
   private final Component shortName;
   private final Component longName;

   private GameType(int var3, String var4) {
      this.id = var3;
      this.name = var4;
      this.shortName = Component.translatable("selectWorld.gameMode." + var4);
      this.longName = Component.translatable("gameMode." + var4);
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Component getLongDisplayName() {
      return this.longName;
   }

   public Component getShortDisplayName() {
      return this.shortName;
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
      return byId(var0, DEFAULT_MODE);
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

   public static int getNullableId(@Nullable GameType var0) {
      return var0 != null ? var0.id : -1;
   }

   @Nullable
   public static GameType byNullableId(int var0) {
      return var0 == -1 ? null : byId(var0);
   }

   // $FF: synthetic method
   private static GameType[] $values() {
      return new GameType[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR};
   }
}
