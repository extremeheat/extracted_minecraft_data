package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class ColorlessShulkerEntityFix extends NamedEntityFix {
   public ColorlessShulkerEntityFix(Schema var1, boolean var2) {
      super(var1, var2, "Colorless shulker entity fix", References.ENTITY, "minecraft:shulker");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         return var0.get("Color").asInt(0) == 10 ? var0.set("Color", var0.createByte((byte)16)) : var0;
      });
   }
}
