package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class TextComponentStrictJsonFix extends DataFix {
   public TextComponentStrictJsonFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.TEXT_COMPONENT);
      return this.fixTypeEverywhere("TextComponentStrictJsonFix", var1, (var0) -> (var0x) -> var0x.mapSecond(LegacyComponentDataFixUtils::rewriteFromLenient));
   }
}
