package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;

public abstract class NamedEntityWriteReadFix extends DataFix {
   private final String name;
   private final String entityName;
   private final TypeReference type;

   public NamedEntityWriteReadFix(Schema var1, boolean var2, String var3, TypeReference var4, String var5) {
      super(var1, var2);
      this.name = var3;
      this.type = var4;
      this.entityName = var5;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(this.type);
      Type var2 = this.getInputSchema().getChoiceType(this.type, this.entityName);
      Type var3 = this.getOutputSchema().getType(this.type);
      Type var4 = this.getOutputSchema().getChoiceType(this.type, this.entityName);
      OpticFinder var5 = DSL.namedChoice(this.entityName, var2);
      return this.fixTypeEverywhereTyped(
         this.name, var1, var3, var3x -> var3x.updateTyped(var5, var4, var2xx -> Util.writeAndReadTypedOrThrow(var2xx, var4, this::fix))
      );
   }

   protected abstract <T> Dynamic<T> fix(Dynamic<T> var1);
}
