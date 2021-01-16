package org.apache.commons.lang3.tuple;

public class MutablePair<L, R> extends Pair<L, R> {
   private static final long serialVersionUID = 4954918890077093841L;
   public L left;
   public R right;

   public static <L, R> MutablePair<L, R> of(L var0, R var1) {
      return new MutablePair(var0, var1);
   }

   public MutablePair() {
      super();
   }

   public MutablePair(L var1, R var2) {
      super();
      this.left = var1;
      this.right = var2;
   }

   public L getLeft() {
      return this.left;
   }

   public void setLeft(L var1) {
      this.left = var1;
   }

   public R getRight() {
      return this.right;
   }

   public void setRight(R var1) {
      this.right = var1;
   }

   public R setValue(R var1) {
      Object var2 = this.getRight();
      this.setRight(var1);
      return var2;
   }
}
