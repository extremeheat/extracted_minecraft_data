package net.minecraft.world.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.StringRepresentable;

public interface SlotRange extends StringRepresentable {
   IntList slots();

   default int size() {
      return this.slots().size();
   }

   static SlotRange of(final String var0, final IntList var1) {
      return new SlotRange() {
         public IntList slots() {
            return var1;
         }

         public String getSerializedName() {
            return var0;
         }

         public String toString() {
            return var0;
         }
      };
   }
}
