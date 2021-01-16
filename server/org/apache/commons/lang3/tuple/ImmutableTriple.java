package org.apache.commons.lang3.tuple;

public final class ImmutableTriple<L, M, R> extends Triple<L, M, R> {
   private static final long serialVersionUID = 1L;
   public final L left;
   public final M middle;
   public final R right;

   public static <L, M, R> ImmutableTriple<L, M, R> of(L var0, M var1, R var2) {
      return new ImmutableTriple(var0, var1, var2);
   }

   public ImmutableTriple(L var1, M var2, R var3) {
      super();
      this.left = var1;
      this.middle = var2;
      this.right = var3;
   }

   public L getLeft() {
      return this.left;
   }

   public M getMiddle() {
      return this.middle;
   }

   public R getRight() {
      return this.right;
   }
}
