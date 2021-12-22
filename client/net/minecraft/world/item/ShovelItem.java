package net.minecraft.world.item;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShovelItem extends DiggerItem {
   protected static final Map<Block, BlockState> FLATTENABLES;

   public ShovelItem(Tier var1, float var2, float var3, Item.Properties var4) {
      super(var2, var3, var1, BlockTags.MINEABLE_WITH_SHOVEL, var4);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var1.getClickedFace() == Direction.DOWN) {
         return InteractionResult.PASS;
      } else {
         Player var5 = var1.getPlayer();
         BlockState var6 = (BlockState)FLATTENABLES.get(var4.getBlock());
         BlockState var7 = null;
         if (var6 != null && var2.getBlockState(var3.above()).isAir()) {
            var2.playSound(var5, var3, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            var7 = var6;
         } else if (var4.getBlock() instanceof CampfireBlock && (Boolean)var4.getValue(CampfireBlock.LIT)) {
            if (!var2.isClientSide()) {
               var2.levelEvent((Player)null, 1009, var3, 0);
            }

            CampfireBlock.dowse(var1.getPlayer(), var2, var3, var4);
            var7 = (BlockState)var4.setValue(CampfireBlock.LIT, false);
         }

         if (var7 != null) {
            if (!var2.isClientSide) {
               var2.setBlock(var3, var7, 11);
               if (var5 != null) {
                  var1.getItemInHand().hurtAndBreak(1, var5, (var1x) -> {
                     var1x.broadcastBreakEvent(var1.getHand());
                  });
               }
            }

            return InteractionResult.sidedSuccess(var2.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   static {
      FLATTENABLES = Maps.newHashMap((new Builder()).put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.PODZOL, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.MYCELIUM, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.defaultBlockState()).build());
   }
}
