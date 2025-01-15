package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public abstract class NamedEntityWriteReadFix extends DataFix {
   private final String name;
   private final String entityName;
   private final DSL.TypeReference type;

   public NamedEntityWriteReadFix(Schema var1, boolean var2, String var3, DSL.TypeReference var4, String var5) {
      super(var1, var2);
      this.name = var3;
      this.type = var4;
      this.entityName = var5;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(this.type);
      Type var2 = this.getInputSchema().getChoiceType(this.type, this.entityName);
      Type var3 = this.getOutputSchema().getType(this.type);
      OpticFinder var4 = DSL.namedChoice(this.entityName, var2);
      Type var5 = ExtraDataFixUtils.patchSubType(var1, var1, var3);
      return this.fix(var1, var3, var5, var4);
   }

   private <S, T, A> TypeRewriteRule fix(Type<S> var1, Type<T> var2, Type<?> var3, OpticFinder<A> var4) {
      return this.fixTypeEverywhereTyped(this.name, var1, var2, (var4x) -> {
         if (var4x.getOptional(var4).isEmpty()) {
            return ExtraDataFixUtils.cast(var2, var4x);
         } else {
            Typed var5 = ExtraDataFixUtils.cast(var3, var4x);
            return Util.writeAndReadTypedOrThrow(var5, var2, this::fix);
         }
      });
   }

   protected abstract <T> Dynamic<T> fix(Dynamic<T> var1);
}
