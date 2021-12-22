package com.mojang.realmsclient.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum LevelType {
   DEFAULT(0, new TranslatableComponent("generator.default")),
   FLAT(1, new TranslatableComponent("generator.flat")),
   LARGE_BIOMES(2, new TranslatableComponent("generator.large_biomes")),
   AMPLIFIED(3, new TranslatableComponent("generator.amplified"));

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
