package net.minecraft.world.entity.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class GlowItemFrame extends ItemFrame {
   public GlowItemFrame(EntityType<? extends ItemFrame> var1, Level var2) {
      super(var1, var2);
   }

   public GlowItemFrame(Level var1, BlockPos var2, Direction var3) {
      super(EntityType.GLOW_ITEM_FRAME, var1, var2, var3);
   }

   public SoundEvent getRemoveItemSound() {
      return SoundEvents.GLOW_ITEM_FRAME_REMOVE_ITEM;
   }

   public SoundEvent getBreakSound() {
      return SoundEvents.GLOW_ITEM_FRAME_BREAK;
   }

   public SoundEvent getPlaceSound() {
      return SoundEvents.GLOW_ITEM_FRAME_PLACE;
   }

   public SoundEvent getAddItemSound() {
      return SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM;
   }

   public SoundEvent getRotateItemSound() {
      return SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM;
   }

   protected ItemStack getFrameItemStack() {
      return new ItemStack(Items.GLOW_ITEM_FRAME);
   }
}
