package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.ComponentDataFixUtils;

public class BlockEntitySignTextStrictJsonFix extends NamedEntityFix {
   public BlockEntitySignTextStrictJsonFix(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
   }

   private Dynamic<?> updateLine(Dynamic<?> var1, String var2) {
      return var1.update(var2, ComponentDataFixUtils::rewriteFromLenient);
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         var1x = this.updateLine(var1x, "Text1");
         var1x = this.updateLine(var1x, "Text2");
         var1x = this.updateLine(var1x, "Text3");
         var1x = this.updateLine(var1x, "Text4");
         return var1x;
      });
   }
}
