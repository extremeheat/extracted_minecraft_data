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
      Type var4 = this.getOutputSchema().getChoiceType(this.type, this.entityName);
      OpticFinder var5 = DSL.namedChoice(this.entityName, var2);
      Type var6 = ExtraDataFixUtils.patchSubType(var2, var1, var3);
      return this.fix(var1, var3, var5, var4, var6);
   }

   private <S, T, A, B> TypeRewriteRule fix(Type<S> var1, Type<T> var2, OpticFinder<A> var3, Type<B> var4, Type<?> var5) {
      return this.fixTypeEverywhere(this.name, var1, var2, (var5x) -> {
         return (var6) -> {
            Typed var7 = new Typed(var1, var5x, var6);
            return var7.update(var3, var4, (var4x) -> {
               Typed var5xx = new Typed(var5, var5x, var4x);
               return Util.writeAndReadTypedOrThrow(var5xx, var4, this::fix).getValue();
            }).getValue();
         };
      });
   }

   protected abstract <T> Dynamic<T> fix(Dynamic<T> var1);
}
