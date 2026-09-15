package net.minecraft.client.player;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ItemActivation {
   public static final int ANIMATION_LENGTH = 40;
   private @Nullable ItemStack item;
   private int ticks;
   private float offX;
   private float offY;

   public ItemActivation() {
      super();
   }

   public void tick() {
      if (this.ticks > 0) {
         --this.ticks;
         if (this.ticks == 0) {
            this.item = null;
         }
      }

   }

   public void reset() {
      this.item = null;
      this.ticks = 0;
   }

   public void activate(final ItemStack itemStack, final RandomSource random) {
      this.item = itemStack.copy();
      this.ticks = 40;
      this.offX = random.nextFloat() * 2.0F - 1.0F;
      this.offY = random.nextFloat() * 2.0F - 1.0F;
   }

   public boolean isActive() {
      return this.item != null && this.ticks > 0;
   }

   public ItemStack item() {
      return this.item;
   }

   public int ticks() {
      return this.ticks;
   }

   public float offX() {
      return this.offX;
   }

   public float offY() {
      return this.offY;
   }
}
