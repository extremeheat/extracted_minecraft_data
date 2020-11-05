package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoeItem extends DiggerItem {
   private static final Set<Block> DIGGABLES;
   protected static final Map<Block, BlockState> TILLABLES;

   protected HoeItem(Tier var1, int var2, float var3, Item.Properties var4) {
      super((float)var2, var3, var1, DIGGABLES, var4);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      if (var1.getClickedFace() != Direction.DOWN && var2.getBlockState(var3.above()).isAir()) {
         BlockState var4 = (BlockState)TILLABLES.get(var2.getBlockState(var3).getBlock());
         if (var4 != null) {
            Player var5 = var1.getPlayer();
            var2.playSound(var5, var3, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!var2.isClientSide) {
               var2.setBlock(var3, var4, 11);
               if (var5 != null) {
                  var1.getItemInHand().hurtAndBreak(1, var5, (var1x) -> {
                     var1x.broadcastBreakEvent(var1.getHand());
                  });
               }
            }

            return InteractionResult.sidedSuccess(var2.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   static {
      DIGGABLES = ImmutableSet.of(Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.HAY_BLOCK, Blocks.DRIED_KELP_BLOCK, Blocks.TARGET, Blocks.SHROOMLIGHT, new Block[]{Blocks.SPONGE, Blocks.WET_SPONGE, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES});
      TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));
   }
}
