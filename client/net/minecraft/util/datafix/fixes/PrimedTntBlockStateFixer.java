package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;

public class PrimedTntBlockStateFixer extends NamedEntityWriteReadFix {
   public PrimedTntBlockStateFixer(Schema var1) {
      super(var1, true, "PrimedTnt BlockState fixer", References.ENTITY, "minecraft:tnt");
   }

   private static <T> Dynamic<T> renameFuse(Dynamic<T> var0) {
      Optional var1 = var0.get("Fuse").get().result();
      return var1.isPresent() ? var0.set("fuse", (Dynamic)var1.get()) : var0;
   }

   private static <T> Dynamic<T> insertBlockState(Dynamic<T> var0) {
      return var0.set("block_state", var0.createMap(Map.of(var0.createString("Name"), var0.createString("minecraft:tnt"))));
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      return renameFuse(insertBlockState(var1));
   }
}
