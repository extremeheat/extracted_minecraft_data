package com.mojang.datafixers.types.templates;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public interface TypeTemplate {
   int size();

   TypeFamily apply(TypeFamily var1);

   default Type<?> toSimpleType() {
      return this.apply(new TypeFamily() {
         public Type<?> apply(int var1) {
            return DSL.emptyPartType();
         }
      }).apply(-1);
   }

   <A, B> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<A> var3, Type<B> var4);

   IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2);

   <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3);
}
