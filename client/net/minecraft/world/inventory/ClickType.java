package net.minecraft.world.inventory;

public enum ClickType {
   PICKUP,
   QUICK_MOVE,
   SWAP,
   CLONE,
   THROW,
   QUICK_CRAFT,
   PICKUP_ALL;

   private ClickType() {
   }

   // $FF: synthetic method
   private static ClickType[] $values() {
      return new ClickType[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
   }
}
