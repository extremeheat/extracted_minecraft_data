package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RemoveEmptyItemInBrushableBlockFix extends NamedEntityWriteReadFix {
   public RemoveEmptyItemInBrushableBlockFix(Schema var1) {
      super(var1, false, "RemoveEmptyItemInSuspiciousBlockFix", References.BLOCK_ENTITY, "minecraft:brushable_block");
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.get("item").result();
      return var2.isPresent() && isEmptyStack((Dynamic)var2.get()) ? var1.remove("item") : var1;
   }

   private static boolean isEmptyStack(Dynamic<?> var0) {
      String var1 = NamespacedSchema.ensureNamespaced(var0.get("id").asString("minecraft:air"));
      int var2 = var0.get("count").asInt(0);
      return var1.equals("minecraft:air") || var2 == 0;
   }
}
