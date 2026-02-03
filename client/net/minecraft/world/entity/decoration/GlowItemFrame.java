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
   public GlowItemFrame(final EntityType<? extends ItemFrame> type, final Level level) {
      super(type, level);
   }

   public GlowItemFrame(final Level level, final BlockPos pos, final Direction direction) {
      super(EntityType.GLOW_ITEM_FRAME, level, pos, direction);
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
