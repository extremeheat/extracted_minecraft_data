package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class WolfCollarColor extends NamedEntityFix {
   public WolfCollarColor(Schema var1, boolean var2) {
      super(var1, var2, "EntityWolfColorFix", TypeReferences.field_211299_o, "minecraft:wolf");
   }

   public Dynamic<?> func_209655_a(Dynamic<?> var1) {
      return var1.update("CollarColor", (var0) -> {
         return var0.createByte((byte)(15 - var0.getNumberValue(0).intValue()));
      });
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::func_209655_a);
   }
}
