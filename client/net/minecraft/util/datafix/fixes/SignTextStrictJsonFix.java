package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class SignTextStrictJsonFix extends NamedEntityFix {
   private static final List<String> LINE_FIELDS = List.of("Text1", "Text2", "Text3", "Text4");

   public SignTextStrictJsonFix(Schema var1) {
      super(var1, false, "SignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
   }

   protected Typed<?> fix(Typed<?> var1) {
      for(String var3 : LINE_FIELDS) {
         OpticFinder var4 = var1.getType().findField(var3);
         OpticFinder var5 = DSL.typeFinder(this.getInputSchema().getType(References.TEXT_COMPONENT));
         var1 = var1.updateTyped(var4, (var1x) -> var1x.update(var5, (var0) -> var0.mapSecond(LegacyComponentDataFixUtils::rewriteFromLenient)));
      }

      return var1;
   }
}
