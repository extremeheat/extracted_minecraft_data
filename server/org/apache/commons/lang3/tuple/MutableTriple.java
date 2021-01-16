package org.apache.commons.lang3.tuple;

public class MutableTriple<L, M, R> extends Triple<L, M, R> {
   private static final long serialVersionUID = 1L;
   public L left;
   public M middle;
   public R right;

   public static <L, M, R> MutableTriple<L, M, R> of(L var0, M var1, R var2) {
      return new MutableTriple(var0, var1, var2);
   }

   public MutableTriple() {
      super();
   }

   public MutableTriple(L var1, M var2, R var3) {
      super();
      this.left = var1;
      this.middle = var2;
      this.right = var3;
   }

   public L getLeft() {
      return this.left;
   }

   public void setLeft(L var1) {
      this.left = var1;
   }

   public M getMiddle() {
      return this.middle;
   }

   public void setMiddle(M var1) {
      this.middle = var1;
   }

   public R getRight() {
      return this.right;
   }

   public void setRight(R var1) {
      this.right = var1;
   }
}
