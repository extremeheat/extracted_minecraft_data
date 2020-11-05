package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShovelItem extends DiggerItem {
   private static final Set<Block> DIGGABLES;
   protected static final Map<Block, BlockState> FLATTENABLES;

   public ShovelItem(Tier var1, float var2, float var3, Item.Properties var4) {
      super(var2, var3, var1, DIGGABLES, var4);
   }

   public boolean isCorrectToolForDrops(BlockState var1) {
      return var1.is(Blocks.SNOW) || var1.is(Blocks.SNOW_BLOCK);
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

            CampfireBlock.dowse(var2, var3, var4);
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
      DIGGABLES = Sets.newHashSet(new Block[]{Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.DIRT_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL});
      FLATTENABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.defaultBlockState(), Blocks.DIRT, Blocks.DIRT_PATH.defaultBlockState(), Blocks.PODZOL, Blocks.DIRT_PATH.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT_PATH.defaultBlockState(), Blocks.MYCELIUM, Blocks.DIRT_PATH.defaultBlockState()));
   }
}
