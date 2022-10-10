package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ShulkerBoxEntityColor extends NamedEntityFix {
   public ShulkerBoxEntityColor(Schema var1, boolean var2) {
      super(var1, var2, "EntityShulkerColorFix", TypeReferences.field_211299_o, "minecraft:shulker");
   }

   public Dynamic<?> func_209653_a(Dynamic<?> var1) {
      return !var1.get("Color").map(Dynamic::getNumberValue).isPresent() ? var1.set("Color", var1.createByte((byte)10)) : var1;
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::func_209653_a);
   }
}
