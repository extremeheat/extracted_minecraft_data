package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public class WriteAndReadDataFix extends DataFix {
   private final String field_210598_a;
   private final TypeReference field_210599_b;

   public WriteAndReadDataFix(Schema var1, String var2, TypeReference var3) {
      super(var1, true);
      this.field_210598_a = var2;
      this.field_210599_b = var3;
   }

   protected TypeRewriteRule makeRule() {
      return this.writeAndRead(this.field_210598_a, this.getInputSchema().getType(this.field_210599_b), this.getOutputSchema().getType(this.field_210599_b));
   }
}
