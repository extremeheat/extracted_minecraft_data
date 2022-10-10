package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ArmorStandSilent extends NamedEntityFix {
   public ArmorStandSilent(Schema var1, boolean var2) {
      super(var1, var2, "EntityArmorStandSilentFix", TypeReferences.field_211299_o, "ArmorStand");
   }

   public Dynamic<?> func_209650_a(Dynamic<?> var1) {
      return var1.getBoolean("Silent") && !var1.getBoolean("Marker") ? var1.remove("Silent") : var1;
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::func_209650_a);
   }
}
