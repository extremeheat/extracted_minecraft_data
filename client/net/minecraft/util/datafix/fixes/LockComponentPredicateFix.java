package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class LockComponentPredicateFix extends ItemStackComponentRemainderFix {
   public LockComponentPredicateFix(Schema var1) {
      super(var1, "LockComponentPredicateFix", "minecraft:lock");
   }

   @Override
   protected <T> Dynamic<T> fixComponent(Dynamic<T> var1) {
      return fixLock(var1);
   }

   public static <T> Dynamic<T> fixLock(Dynamic<T> var0) {
      Optional var1 = var0.asString().result();
      if (var1.isPresent()) {
         Dynamic var2 = var0.createString("\"" + ((String)var1.get()).replace("\"", "\\\"") + "\"");
         Dynamic var3 = var0.emptyMap().set("minecraft:custom_name", var2);
         return var0.emptyMap().set("components", var3);
      } else {
         return var0.emptyMap();
      }
   }
}