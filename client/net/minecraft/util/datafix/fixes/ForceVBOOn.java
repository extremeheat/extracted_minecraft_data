package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class ForceVBOOn extends DataFix {
   public ForceVBOOn(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsForceVBOFix", this.getInputSchema().getType(TypeReferences.field_211289_e), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return var0x.set("useVbo", var0x.createString("true"));
         });
      });
   }
}
