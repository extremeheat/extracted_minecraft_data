package net.minecraft.util.datafix.fixes;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;

public class LockComponentPredicateFix extends DataComponentRemainderFix {
   public static final Escaper ESCAPER = Escapers.builder().addEscape('"', "\\\"").addEscape('\\', "\\\\").build();

   public LockComponentPredicateFix(Schema var1) {
      super(var1, "LockComponentPredicateFix", "minecraft:lock");
   }

   @Nullable
   protected <T> Dynamic<T> fixComponent(Dynamic<T> var1) {
      return fixLock(var1);
   }

   @Nullable
   public static <T> Dynamic<T> fixLock(Dynamic<T> var0) {
      Optional var1 = var0.asString().result();
      if (var1.isEmpty()) {
         return null;
      } else if (((String)var1.get()).isEmpty()) {
         return null;
      } else {
         Escaper var10001 = ESCAPER;
         Dynamic var2 = var0.createString("\"" + var10001.escape((String)var1.get()) + "\"");
         Dynamic var3 = var0.emptyMap().set("minecraft:custom_name", var2);
         return var0.emptyMap().set("components", var3);
      }
   }
}
