package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PotatoPeelItem extends Item {
   public static final DyeColor PEELGRASS_PEEL_COLOR = DyeColor.LIME;
   private final DyeColor color;

   public PotatoPeelItem(Item.Properties var1, DyeColor var2) {
      super(var1);
      this.color = var2;
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      if (this.color == PEELGRASS_PEEL_COLOR) {
         Level var2 = var1.getLevel();
         BlockPos var3 = var1.getClickedPos();
         BlockState var4 = var2.getBlockState(var3);
         if (var4.is(Blocks.TERREDEPOMME) && var1.getClickedFace() == Direction.UP) {
            Player var5 = var1.getPlayer();
            var1.getItemInHand().shrink(1);
            var2.playSound(var5, var3, SoundEvents.PEELGRASS_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, var2.random.nextFloat(0.9F, 1.1F));
            var2.setBlockAndUpdate(var3, Blocks.PEELGRASS_BLOCK.defaultBlockState());
            return InteractionResult.sidedSuccess(var2.isClientSide);
         }
      }

      return super.useOn(var1);
   }
}
