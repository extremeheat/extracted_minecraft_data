package net.minecraft.event;

public class ClickEvent {
   private final ClickEvent$Action field_150671_a;
   private final String field_150670_b;

   public ClickEvent(ClickEvent$Action var1, String var2) {
      super();
      this.field_150671_a = var1;
      this.field_150670_b = var2;
   }

   public ClickEvent$Action func_150669_a() {
      return this.field_150671_a;
   }

   public String func_150668_b() {
      return this.field_150670_b;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ClickEvent var2 = (ClickEvent)var1;
         if (this.field_150671_a != var2.field_150671_a) {
            return false;
         } else {
            return this.field_150670_b != null ? this.field_150670_b.equals(var2.field_150670_b) : var2.field_150670_b == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "ClickEvent{action=" + this.field_150671_a + ", value='" + this.field_150670_b + '\'' + '}';
   }

   @Override
   public int hashCode() {
      int var1 = this.field_150671_a.hashCode();
      return 31 * var1 + (this.field_150670_b != null ? this.field_150670_b.hashCode() : 0);
   }
}
