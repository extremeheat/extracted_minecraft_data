package com.mojang.realmsclient.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public enum LevelType {
   DEFAULT(0, WorldPresets.NORMAL),
   FLAT(1, WorldPresets.FLAT),
   AMPLIFIED(2, WorldPresets.AMPLIFIED);

   private final int index;
   private final Component name;

   private LevelType(final int var3, final ResourceKey<WorldPreset> var4) {
      this.index = var3;
      this.name = Component.translatable(var4.location().toLanguageKey("generator"));
   }

   public Component getName() {
      return this.name;
   }

   public int getDtoIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static LevelType[] $values() {
      return new LevelType[]{DEFAULT, FLAT, AMPLIFIED};
   }
}
