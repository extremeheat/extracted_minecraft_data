package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;

public class BlockPropertyRenameAndFix extends AbstractBlockPropertyFix {
   private final String blockId;
   private final String oldPropertyName;
   private final String newPropertyName;
   private final UnaryOperator<String> valueFixer;

   public BlockPropertyRenameAndFix(Schema var1, String var2, String var3, String var4, String var5, UnaryOperator<String> var6) {
      super(var1, var2);
      this.blockId = var3;
      this.oldPropertyName = var4;
      this.newPropertyName = var5;
      this.valueFixer = var6;
   }

   protected boolean shouldFix(String var1) {
      return var1.equals(this.blockId);
   }

   protected <T> Dynamic<T> fixProperties(String var1, Dynamic<T> var2) {
      return var2.renameAndFixField(this.oldPropertyName, this.newPropertyName, (var1x) -> var1x.createString((String)this.valueFixer.apply(var1x.asString(""))));
   }
}
