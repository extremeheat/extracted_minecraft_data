package net.minecraft.world.level.gamerules;

import net.minecraft.util.StringRepresentable;

public enum GameRuleType implements StringRepresentable {
   INT("integer"),
   BOOL("boolean");

   private final String name;

   private GameRuleType(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static GameRuleType[] $values() {
      return new GameRuleType[]{INT, BOOL};
   }
}
