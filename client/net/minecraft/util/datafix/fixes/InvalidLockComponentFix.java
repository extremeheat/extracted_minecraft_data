package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class InvalidLockComponentFix extends DataComponentRemainderFix {
   private static final Optional<String> INVALID_LOCK_CUSTOM_NAME = Optional.of("\"\"");

   public InvalidLockComponentFix(Schema var1) {
      super(var1, "InvalidLockComponentPredicateFix", "minecraft:lock");
   }

   @Nullable
   protected <T> Dynamic<T> fixComponent(Dynamic<T> var1) {
      return fixLock(var1);
   }

   @Nullable
   public static <T> Dynamic<T> fixLock(Dynamic<T> var0) {
      return isBrokenLock(var0) ? null : var0;
   }

   private static <T> boolean isBrokenLock(Dynamic<T> var0) {
      return isMapWithOneField(var0, "components", (var0x) -> isMapWithOneField(var0x, "minecraft:custom_name", (var0) -> var0.asString().result().equals(INVALID_LOCK_CUSTOM_NAME)));
   }

   private static <T> boolean isMapWithOneField(Dynamic<T> var0, String var1, Predicate<Dynamic<T>> var2) {
      Optional var3 = var0.getMapValues().result();
      return !var3.isEmpty() && ((Map)var3.get()).size() == 1 ? var0.get(var1).result().filter(var2).isPresent() : false;
   }
}
