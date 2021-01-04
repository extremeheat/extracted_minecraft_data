package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HellSpringConfiguration implements FeatureConfiguration {
   public final boolean insideRock;

   public HellSpringConfiguration(boolean var1) {
      super();
      this.insideRock = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("inside_rock"), var1.createBoolean(this.insideRock))));
   }

   public static <T> HellSpringConfiguration deserialize(Dynamic<T> var0) {
      boolean var1 = var0.get("inside_rock").asBoolean(false);
      return new HellSpringConfiguration(var1);
   }
}
