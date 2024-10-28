package com.mojang.realmsclient.util;

import java.util.Set;

public record WorldGenerationInfo(String seed, LevelType levelType, boolean generateStructures, Set<String> experiments) {
   public WorldGenerationInfo(String seed, LevelType levelType, boolean generateStructures, Set<String> experiments) {
      super();
      this.seed = seed;
      this.levelType = levelType;
      this.generateStructures = generateStructures;
      this.experiments = experiments;
   }

   public String seed() {
      return this.seed;
   }

   public LevelType levelType() {
      return this.levelType;
   }

   public boolean generateStructures() {
      return this.generateStructures;
   }

   public Set<String> experiments() {
      return this.experiments;
   }
}
