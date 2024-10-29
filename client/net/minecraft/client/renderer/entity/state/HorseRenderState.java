package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.item.ItemStack;

public class HorseRenderState extends EquineRenderState {
   public Variant variant;
   public Markings markings;
   public ItemStack bodyArmorItem;

   public HorseRenderState() {
      super();
      this.variant = Variant.WHITE;
      this.markings = Markings.NONE;
      this.bodyArmorItem = ItemStack.EMPTY;
   }
}
