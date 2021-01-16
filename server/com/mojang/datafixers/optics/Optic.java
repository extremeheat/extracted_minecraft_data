package com.mojang.datafixers.optics;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.K2;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface Optic<Proof extends K1, S, T, A, B> {
   <P extends K2> Function<App2<P, A, B>, App2<P, S, T>> eval(App<? extends Proof, P> var1);

   default <Proof2 extends Proof, A1, B1> Optic<Proof2, S, T, A1, B1> compose(Optic<? super Proof2, A, B, A1, B1> var1) {
      return new Optic.CompositionOptic(this, var1);
   }

   default <Proof2 extends K1, A1, B1> Optic<?, S, T, A1, B1> composeUnchecked(Optic<?, A, B, A1, B1> var1) {
      return new Optic.CompositionOptic(this, var1);
   }

   default <Proof2 extends K1> Optional<Optic<? super Proof2, S, T, A, B>> upCast(Set<TypeToken<? extends K1>> var1, TypeToken<Proof2> var2) {
      return var1.stream().allMatch((var1x) -> {
         return var1x.isSupertypeOf(var2);
      }) ? Optional.of(this) : Optional.empty();
   }

   public static final class CompositionOptic<Proof extends K1, S, T, A, B, A1, B1> implements Optic<Proof, S, T, A1, B1> {
      protected final Optic<? super Proof, S, T, A, B> outer;
      protected final Optic<? super Proof, A, B, A1, B1> inner;

      public CompositionOptic(Optic<? super Proof, S, T, A, B> var1, Optic<? super Proof, A, B, A1, B1> var2) {
         super();
         this.outer = var1;
         this.inner = var2;
      }

      public <P extends K2> Function<App2<P, A1, B1>, App2<P, S, T>> eval(App<? extends Proof, P> var1) {
         return this.outer.eval(var1).compose(this.inner.eval(var1));
      }

      public String toString() {
         return "(" + this.outer + " \u25e6 " + this.inner + ")";
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Optic.CompositionOptic var2 = (Optic.CompositionOptic)var1;
            return Objects.equals(this.outer, var2.outer) && Objects.equals(this.inner, var2.inner);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.outer, this.inner});
      }

      public Optic<? super Proof, S, T, A, B> outer() {
         return this.outer;
      }

      public Optic<? super Proof, A, B, A1, B1> inner() {
         return this.inner;
      }
   }
}
