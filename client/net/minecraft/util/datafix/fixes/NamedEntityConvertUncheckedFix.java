package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class NamedEntityConvertUncheckedFix extends NamedEntityFix {
   public NamedEntityConvertUncheckedFix(Schema var1, String var2, DSL.TypeReference var3, String var4) {
      super(var1, true, var2, var3, var4);
   }

   protected Typed<?> fix(Typed<?> var1) {
      Type var2 = this.getOutputSchema().getChoiceType(this.type, this.entityName);
      return ExtraDataFixUtils.cast(var2, var1);
   }
}
