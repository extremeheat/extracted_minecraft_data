package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WritableBookItem extends Item {
   public WritableBookItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(Blocks.LECTERN)) {
         return LecternBlock.tryPlaceBook(var1.getPlayer(), var2, var3, var4, var1.getItemInHand()) ? InteractionResult.sidedSuccess(var2.isClientSide) : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.openItemGui(var4, var3);
      var2.awardStat(Stats.ITEM_USED.get(this));
      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundTag var0) {
      if (var0 == null) {
         return false;
      } else if (!var0.contains("pages", 9)) {
         return false;
      } else {
         ListTag var1 = var0.getList("pages", 8);

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            String var3 = var1.getString(var2);
            if (var3.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
