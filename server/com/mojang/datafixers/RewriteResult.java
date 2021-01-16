package com.mojang.datafixers;

import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.RecursivePoint;
import java.util.BitSet;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;

public final class RewriteResult<A, B> {
   protected final View<A, B> view;
   protected final BitSet recData;

   public RewriteResult(View<A, B> var1, BitSet var2) {
      super();
      this.view = var1;
      this.recData = var2;
   }

   public static <A, B> RewriteResult<A, B> create(View<A, B> var0, BitSet var1) {
      return new RewriteResult(var0, var1);
   }

   public static <A> RewriteResult<A, A> nop(Type<A> var0) {
      return new RewriteResult(View.nopView(var0), new BitSet());
   }

   public <C> RewriteResult<C, B> compose(RewriteResult<C, A> var1) {
      BitSet var2;
      if (this.view.type() instanceof RecursivePoint.RecursivePointType && var1.view.type() instanceof RecursivePoint.RecursivePointType) {
         var2 = (BitSet)ObjectUtils.clone(this.recData);
         var2.or(var1.recData);
      } else {
         var2 = this.recData;
      }

      return create(this.view.compose(var1.view), var2);
   }

   public BitSet recData() {
      return this.recData;
   }

   public View<A, B> view() {
      return this.view;
   }

   public String toString() {
      return "RR[" + this.view + "]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         RewriteResult var2 = (RewriteResult)var1;
         return Objects.equals(this.view, var2.view);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.view});
   }
}
