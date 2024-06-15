package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.function.UnaryOperator;

public class BlockEntityRenameFix extends DataFix {
   private final String name;
   private final UnaryOperator<String> nameChangeLookup;

   private BlockEntityRenameFix(Schema var1, String var2, UnaryOperator<String> var3) {
      super(var1, true);
      this.name = var2;
      this.nameChangeLookup = var3;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhere(this.name, var1, var2, var1x -> var1xx -> var1xx.mapFirst(this.nameChangeLookup));
   }

   public static DataFix create(Schema var0, String var1, UnaryOperator<String> var2) {
      return new BlockEntityRenameFix(var0, var1, var2);
   }
}
