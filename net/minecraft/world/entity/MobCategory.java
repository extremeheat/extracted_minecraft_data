package net.minecraft.world.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MobCategory {
   MONSTER("monster", 70, false, false),
   CREATURE("creature", 10, true, true),
   AMBIENT("ambient", 15, true, false),
   WATER_CREATURE("water_creature", 15, true, false),
   MISC("misc", 15, true, false);

   private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(MobCategory::getName, (var0) -> {
      return var0;
   }));
   private final int max;
   private final boolean isFriendly;
   private final boolean isPersistent;
   private final String name;

   private MobCategory(String var3, int var4, boolean var5, boolean var6) {
      this.name = var3;
      this.max = var4;
      this.isFriendly = var5;
      this.isPersistent = var6;
   }

   public String getName() {
      return this.name;
   }

   public int getMaxInstancesPerChunk() {
      return this.max;
   }

   public boolean isFriendly() {
      return this.isFriendly;
   }

   public boolean isPersistent() {
      return this.isPersistent;
   }
}
