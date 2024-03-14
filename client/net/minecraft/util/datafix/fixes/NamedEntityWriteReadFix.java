package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
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
      Type var6 = var2.all(typePatcher(var1, var3), true, false).view().newType();
      return this.fix(var1, var3, var5, var4, var6);
   }

   private <S, T, A, B> TypeRewriteRule fix(Type<S> var1, Type<T> var2, OpticFinder<A> var3, Type<B> var4, Type<?> var5) {
      return this.fixTypeEverywhere(this.name, var1, var2, var5x -> var6 -> {
            Typed var7 = new Typed(var1, var5x, var6);
            return var7.update(var3, var4, var4xxx -> {
               Typed var5xxxx = new Typed(var5, var5x, var4xxx);
               return Util.writeAndReadTypedOrThrow(var5xxxx, var4, this::fix).getValue();
            }).getValue();
         });
   }

   private static <A, B> TypeRewriteRule typePatcher(Type<A> var0, Type<B> var1) {
      RewriteResult var2 = RewriteResult.create(View.create("Patcher", var0, var1, var0x -> var0xx -> {
            throw new UnsupportedOperationException();
         }), new BitSet());
      return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(var0, var2), PointFreeRule.nop(), true, true);
   }

   protected abstract <T> Dynamic<T> fix(Dynamic<T> var1);
}
