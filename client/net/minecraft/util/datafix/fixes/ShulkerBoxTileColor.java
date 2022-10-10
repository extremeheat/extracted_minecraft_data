package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ShulkerBoxTileColor extends NamedEntityFix {
   public ShulkerBoxTileColor(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityShulkerBoxColorFix", TypeReferences.field_211294_j, "minecraft:shulker_box");
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         return var0.remove("Color");
      });
   }
}
