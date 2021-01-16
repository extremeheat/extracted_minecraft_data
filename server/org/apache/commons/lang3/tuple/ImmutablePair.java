package org.apache.commons.lang3.tuple;

public final class ImmutablePair<L, R> extends Pair<L, R> {
   private static final long serialVersionUID = 4954918890077093841L;
   public final L left;
   public final R right;

   public static <L, R> ImmutablePair<L, R> of(L var0, R var1) {
      return new ImmutablePair(var0, var1);
   }

   public ImmutablePair(L var1, R var2) {
      super();
      this.left = var1;
      this.right = var2;
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }

   public R setValue(R var1) {
      throw new UnsupportedOperationException();
   }
}
