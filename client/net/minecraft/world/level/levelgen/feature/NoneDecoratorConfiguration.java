package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoneDecoratorConfiguration implements DecoratorConfiguration {
   public NoneDecoratorConfiguration() {
      super();
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.emptyMap());
   }

   public static NoneDecoratorConfiguration deserialize(Dynamic<?> var0) {
      return new NoneDecoratorConfiguration();
   }
}
