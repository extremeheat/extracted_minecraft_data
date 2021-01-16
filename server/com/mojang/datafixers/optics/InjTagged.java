package com.mojang.datafixers.optics;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;

public final class InjTagged<K, A, B> implements Prism<Pair<K, ?>, Pair<K, ?>, A, B> {
   private final K key;

   public InjTagged(K var1) {
      super();
      this.key = var1;
   }

   public Either<Pair<K, ?>, A> match(Pair<K, ?> var1) {
      return Objects.equals(this.key, var1.getFirst()) ? Either.right(var1.getSecond()) : Either.left(var1);
   }

   public Pair<K, ?> build(B var1) {
      return Pair.of(this.key, var1);
   }

   public String toString() {
      return "inj[" + this.key + "]";
   }

   public boolean equals(Object var1) {
      return var1 instanceof InjTagged && Objects.equals(((InjTagged)var1).key, this.key);
   }
}
