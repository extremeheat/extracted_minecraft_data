package net.minecraft.world.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.StringRepresentable;

public interface SlotRange extends StringRepresentable {
   IntList slots();

   default int size() {
      return this.slots().size();
   }

   static SlotRange of(final String name, final IntList slots) {
      return new SlotRange() {
         public IntList slots() {
            return slots;
         }

         public String getSerializedName() {
            return name;
         }

         public String toString() {
            return name;
         }
      };
   }
}
