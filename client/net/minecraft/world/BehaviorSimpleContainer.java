package net.minecraft.world;

import net.minecraft.world.entity.player.Player;

public class BehaviorSimpleContainer extends SimpleContainer {
   private boolean valid = true;

   public BehaviorSimpleContainer(final int inventorySize) {
      super(inventorySize);
   }

   public void setValid(final boolean valid) {
      this.valid = valid;
   }

   public boolean stillValid(final Player player) {
      return this.valid;
   }
}
