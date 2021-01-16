package com.mojang.datafixers;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import javax.annotation.Nullable;

public interface OpticFinder<FT> {
   Type<FT> type();

   <A, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<A> var1, Type<FR> var2, boolean var3);

   default <A> Either<TypedOptic<A, ?, FT, FT>, Type.FieldNotFoundException> findType(Type<A> var1, boolean var2) {
      return this.findType(var1, this.type(), var2);
   }

   default <GT> OpticFinder<FT> inField(@Nullable final String var1, final Type<GT> var2) {
      return new OpticFinder<FT>() {
         public Type<FT> type() {
            return OpticFinder.this.type();
         }

         public <A, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<A> var1x, Type<FR> var2x, boolean var3) {
            Either var4 = OpticFinder.this.findType(var2, var2x, var3);
            return (Either)var4.map((var3x) -> {
               return this.cap(var1x, var3x, var3);
            }, Either::right);
         }

         private <A, FR, GR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> cap(Type<A> var1x, TypedOptic<GT, GR, FT, FR> var2x, boolean var3) {
            Either var4 = DSL.fieldFinder(var1, var2).findType(var1x, var2x.tType(), var3);
            return var4.mapLeft((var1xx) -> {
               return var1xx.compose(var2x);
            });
         }
      };
   }
}
