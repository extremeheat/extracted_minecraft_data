package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.resources.ResourceLocation;

public class VillageConfiguration implements FeatureConfiguration {
   public final ResourceLocation startPool;
   public final int size;

   public VillageConfiguration(String var1, int var2) {
      super();
      this.startPool = new ResourceLocation(var1);
      this.size = var2;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("start_pool"), var1.createString(this.startPool.toString()), var1.createString("size"), var1.createInt(this.size))));
   }

   public static <T> VillageConfiguration deserialize(Dynamic<T> var0) {
      String var1 = var0.get("start_pool").asString("");
      int var2 = var0.get("size").asInt(6);
      return new VillageConfiguration(var1, var2);
   }
}
