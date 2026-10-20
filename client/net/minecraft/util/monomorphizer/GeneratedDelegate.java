package net.minecraft.util.monomorphizer;

public abstract class GeneratedDelegate {
   private final Object target;

   protected GeneratedDelegate(final Object target) {
      super();
      this.target = target;
   }

   public final boolean equals(final Object obj) {
      return super.equals(obj);
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final String toString() {
      return "GeneratedDelegate[" + String.valueOf(this.target) + "]";
   }
}
