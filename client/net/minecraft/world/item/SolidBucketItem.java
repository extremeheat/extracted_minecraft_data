package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class SolidBucketItem extends BlockItem implements DispensibleContainerItem {
   private final SoundEvent placeSound;

   public SolidBucketItem(Block var1, SoundEvent var2, Item.Properties var3) {
      super(var1, var3);
      this.placeSound = var2;
   }

   public InteractionResult useOn(UseOnContext var1) {
      InteractionResult var2 = super.useOn(var1);
      Player var3 = var1.getPlayer();
      if (var2.consumesAction() && var3 != null) {
         var3.setItemInHand(var1.getHand(), BucketItem.getEmptySuccessItem(var1.getItemInHand(), var3));
      }

      return var2;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   protected SoundEvent getPlaceSound(BlockState var1) {
      return this.placeSound;
   }

   public boolean emptyContents(@Nullable Player var1, Level var2, BlockPos var3, @Nullable BlockHitResult var4) {
      if (var2.isInWorldBounds(var3) && var2.isEmptyBlock(var3)) {
         if (!var2.isClientSide) {
            var2.setBlock(var3, this.getBlock().defaultBlockState(), 3);
         }

         var2.gameEvent(var1, GameEvent.FLUID_PLACE, var3);
         var2.playSound(var1, var3, this.placeSound, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }
}
