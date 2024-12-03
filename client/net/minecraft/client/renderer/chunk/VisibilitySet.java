package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.core.Direction;

public class VisibilitySet {
   private static final int FACINGS = Direction.values().length;
   private final BitSet data;

   public VisibilitySet() {
      super();
      this.data = new BitSet(FACINGS * FACINGS);
   }

   public void add(Set<Direction> var1) {
      for(Direction var3 : var1) {
         for(Direction var5 : var1) {
            this.set(var3, var5, true);
         }
      }

   }

   public void set(Direction var1, Direction var2, boolean var3) {
      this.data.set(var1.ordinal() + var2.ordinal() * FACINGS, var3);
      this.data.set(var2.ordinal() + var1.ordinal() * FACINGS, var3);
   }

   public void setAll(boolean var1) {
      this.data.set(0, this.data.size(), var1);
   }

   public boolean visibilityBetween(Direction var1, Direction var2) {
      return this.data.get(var1.ordinal() + var2.ordinal() * FACINGS);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(' ');

      for(Direction var5 : Direction.values()) {
         var1.append(' ').append(var5.toString().toUpperCase().charAt(0));
      }

      var1.append('\n');

      for(Direction var14 : Direction.values()) {
         var1.append(var14.toString().toUpperCase().charAt(0));

         for(Direction var9 : Direction.values()) {
            if (var14 == var9) {
               var1.append("  ");
            } else {
               boolean var10 = this.visibilityBetween(var14, var9);
               var1.append(' ').append((char)(var10 ? 'Y' : 'n'));
            }
         }

         var1.append('\n');
      }

      return var1.toString();
   }
}
