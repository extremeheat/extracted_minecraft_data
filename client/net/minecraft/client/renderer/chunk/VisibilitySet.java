package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Iterator;
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
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Direction var3 = (Direction)var2.next();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Direction var5 = (Direction)var4.next();
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
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      int var4;
      Direction var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         var1.append(' ').append(var5.toString().toUpperCase().charAt(0));
      }

      var1.append('\n');
      var2 = Direction.values();
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         var1.append(var5.toString().toUpperCase().charAt(0));
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            if (var5 == var9) {
               var1.append("  ");
            } else {
               boolean var10 = this.visibilityBetween(var5, var9);
               var1.append(' ').append((char)(var10 ? 'Y' : 'n'));
            }
         }

         var1.append('\n');
      }

      return var1.toString();
   }
}
