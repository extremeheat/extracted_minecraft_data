package net.minecraft.world.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class AxeItem extends DiggerItem {
   protected static final Map<Block, Block> STRIPPABLES;

   protected AxeItem(Tier var1, float var2, float var3, Item.Properties var4) {
      super(var2, var3, var1, BlockTags.MINEABLE_WITH_AXE, var4);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Player var4 = var1.getPlayer();
      BlockState var5 = var2.getBlockState(var3);
      Optional var6 = this.getStripped(var5);
      Optional var7 = WeatheringCopper.getPrevious(var5);
      Optional var8 = Optional.ofNullable((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(var5.getBlock())).map((var1x) -> {
         return var1x.withPropertiesOf(var5);
      });
      ItemStack var9 = var1.getItemInHand();
      Optional var10 = Optional.empty();
      if (var6.isPresent()) {
         var2.playSound(var4, var3, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
         var10 = var6;
      } else if (var7.isPresent()) {
         var2.playSound(var4, var3, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
         var2.levelEvent(var4, 3005, var3, 0);
         var10 = var7;
      } else if (var8.isPresent()) {
         var2.playSound(var4, var3, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
         var2.levelEvent(var4, 3004, var3, 0);
         var10 = var8;
      }

      if (var10.isPresent()) {
         if (var4 instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)var4, var3, var9);
         }

         var2.setBlock(var3, (BlockState)var10.get(), 11);
         if (var4 != null) {
            var9.hurtAndBreak(1, var4, (var1x) -> {
               var1x.broadcastBreakEvent(var1.getHand());
            });
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   private Optional<BlockState> getStripped(BlockState var1) {
      return Optional.ofNullable((Block)STRIPPABLES.get(var1.getBlock())).map((var1x) -> {
         return (BlockState)var1x.defaultBlockState().setValue(RotatedPillarBlock.AXIS, (Direction.Axis)var1.getValue(RotatedPillarBlock.AXIS));
      });
   }

   static {
      STRIPPABLES = (new Builder()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).build();
   }
}
