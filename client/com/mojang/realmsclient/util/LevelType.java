package com.mojang.realmsclient.util;

import net.minecraft.network.chat.Component;

public enum LevelType {
   DEFAULT(0, Component.translatable("generator.default")),
   FLAT(1, Component.translatable("generator.flat")),
   LARGE_BIOMES(2, Component.translatable("generator.large_biomes")),
   AMPLIFIED(3, Component.translatable("generator.amplified"));

   private final int index;
   private final Component name;

   private LevelType(int var3, Component var4) {
      this.index = var3;
      this.name = var4;
   }

   public Component getName() {
      return this.name;
   }

   public int getDtoIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static LevelType[] $values() {
      return new LevelType[]{DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED};
   }
}
