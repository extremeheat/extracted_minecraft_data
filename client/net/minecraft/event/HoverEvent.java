package net.minecraft.event;

import net.minecraft.util.IChatComponent;

public class HoverEvent {
   private final HoverEvent$Action field_150704_a;
   private final IChatComponent field_150703_b;

   public HoverEvent(HoverEvent$Action var1, IChatComponent var2) {
      super();
      this.field_150704_a = var1;
      this.field_150703_b = var2;
   }

   public HoverEvent$Action func_150701_a() {
      return this.field_150704_a;
   }

   public IChatComponent func_150702_b() {
      return this.field_150703_b;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HoverEvent var2 = (HoverEvent)var1;
         if (this.field_150704_a != var2.field_150704_a) {
            return false;
         } else {
            return this.field_150703_b != null ? this.field_150703_b.equals(var2.field_150703_b) : var2.field_150703_b == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "HoverEvent{action=" + this.field_150704_a + ", value='" + this.field_150703_b + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var1 = this.field_150704_a.hashCode();
      return 31 * var1 + (this.field_150703_b != null ? this.field_150703_b.hashCode() : 0);
   }
}
