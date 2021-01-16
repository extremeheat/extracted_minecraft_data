package com.mojang.datafixers.types.families;

import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.functions.PointFree;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ListAlgebra implements Algebra {
   private final String name;
   private final List<RewriteResult<?, ?>> views;
   private int hashCode;

   public ListAlgebra(String var1, List<RewriteResult<?, ?>> var2) {
      super();
      this.name = var1;
      this.views = var2;
   }

   public RewriteResult<?, ?> apply(int var1) {
      return (RewriteResult)this.views.get(var1);
   }

   public String toString() {
      return this.toString(0);
   }

   public String toString(int var1) {
      String var2 = "\n" + PointFree.indent(var1 + 1);
      return "Algebra[" + this.name + var2 + (String)this.views.stream().map((var1x) -> {
         return var1x.view().function().toString(var1 + 1);
      }).collect(Collectors.joining(var2)) + "\n" + PointFree.indent(var1) + "]";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ListAlgebra)) {
         return false;
      } else {
         ListAlgebra var2 = (ListAlgebra)var1;
         return Objects.equals(this.views, var2.views);
      }
   }

   public int hashCode() {
      if (this.hashCode == 0) {
         this.hashCode = this.views.hashCode();
      }

      return this.hashCode;
   }
}
