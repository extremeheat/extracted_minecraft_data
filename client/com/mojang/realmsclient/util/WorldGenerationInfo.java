package com.mojang.realmsclient.util;

import java.util.Set;

public record WorldGenerationInfo(String a, LevelType b, boolean c, Set<String> d) {
   private final String seed;
   private final LevelType levelType;
   private final boolean generateStructures;
   private final Set<String> experiments;

   public WorldGenerationInfo(String var1, LevelType var2, boolean var3, Set<String> var4) {
      super();
      this.seed = var1;
      this.levelType = var2;
      this.generateStructures = var3;
      this.experiments = var4;
   }
}
