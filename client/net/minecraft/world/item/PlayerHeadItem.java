package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class PlayerHeadItem extends StandingAndWallBlockItem {
   public static final String TAG_SKULL_OWNER = "SkullOwner";

   public PlayerHeadItem(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, var3, Direction.DOWN);
   }

   @Override
   public Component getName(ItemStack var1) {
      if (var1.is(Items.PLAYER_HEAD) && var1.hasTag()) {
         String var2 = null;
         CompoundTag var3 = var1.getTag();
         if (var3.contains("SkullOwner", 8)) {
            var2 = var3.getString("SkullOwner");
         } else if (var3.contains("SkullOwner", 10)) {
            CompoundTag var4 = var3.getCompound("SkullOwner");
            if (var4.contains("Name", 8)) {
               var2 = var4.getString("Name");
            }
         }

         if (var2 != null) {
            return Component.translatable(this.getDescriptionId() + ".named", var2);
         }
      }

      return super.getName(var1);
   }

   @Override
   public void verifyTagAfterLoad(CompoundTag var1) {
      super.verifyTagAfterLoad(var1);
      SkullBlockEntity.resolveGameProfile(var1);
   }
}
