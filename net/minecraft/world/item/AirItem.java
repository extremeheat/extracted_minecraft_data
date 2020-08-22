package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AirItem extends Item {
   private final Block block;

   public AirItem(Block var1, Item.Properties var2) {
      super(var2);
      this.block = var1;
   }

   public String getDescriptionId() {
      return this.block.getDescriptionId();
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      this.block.appendHoverText(var1, var2, var3, var4);
   }
}
