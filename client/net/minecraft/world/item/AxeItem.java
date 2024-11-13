package net.minecraft.world.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class AxeItem extends DiggerItem {
   protected static final Map<Block, Block> STRIPPABLES;

   public AxeItem(ToolMaterial var1, float var2, float var3, Item.Properties var4) {
      super(var1, BlockTags.MINEABLE_WITH_AXE, var2, var3, var4);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Player var4 = var1.getPlayer();
      if (playerHasShieldUseIntent(var1)) {
         return InteractionResult.PASS;
      } else {
         Optional var5 = this.evaluateNewBlockState(var2, var3, var4, var2.getBlockState(var3));
         if (var5.isEmpty()) {
            return InteractionResult.PASS;
         } else {
            ItemStack var6 = var1.getItemInHand();
            if (var4 instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)var4, var3, var6);
            }

            var2.setBlock(var3, (BlockState)var5.get(), 11);
            var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var4, (BlockState)var5.get()));
            if (var4 != null) {
               var6.hurtAndBreak(1, var4, LivingEntity.getSlotForHand(var1.getHand()));
            }

            return InteractionResult.SUCCESS;
         }
      }
   }

   private static boolean playerHasShieldUseIntent(UseOnContext var0) {
      Player var1 = var0.getPlayer();
      return var0.getHand().equals(InteractionHand.MAIN_HAND) && var1.getOffhandItem().is(Items.SHIELD) && !var1.isSecondaryUseActive();
   }

   private Optional<BlockState> evaluateNewBlockState(Level var1, BlockPos var2, @Nullable Player var3, BlockState var4) {
      Optional var5 = this.getStripped(var4);
      if (var5.isPresent()) {
         var1.playSound(var3, var2, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
         return var5;
      } else {
         Optional var6 = WeatheringCopper.getPrevious(var4);
         if (var6.isPresent()) {
            var1.playSound(var3, var2, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            var1.levelEvent(var3, 3005, var2, 0);
            return var6;
         } else {
            Optional var7 = Optional.ofNullable((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(var4.getBlock())).map((var1x) -> var1x.withPropertiesOf(var4));
            if (var7.isPresent()) {
               var1.playSound(var3, var2, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
               var1.levelEvent(var3, 3004, var2, 0);
               return var7;
            } else {
               return Optional.empty();
            }
         }
      }
   }

   private Optional<BlockState> getStripped(BlockState var1) {
      return Optional.ofNullable((Block)STRIPPABLES.get(var1.getBlock())).map((var1x) -> (BlockState)var1x.defaultBlockState().setValue(RotatedPillarBlock.AXIS, (Direction.Axis)var1.getValue(RotatedPillarBlock.AXIS)));
   }

   static {
      STRIPPABLES = (new ImmutableMap.Builder()).put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD).put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.PALE_OAK_WOOD, Blocks.STRIPPED_PALE_OAK_WOOD).put(Blocks.PALE_OAK_LOG, Blocks.STRIPPED_PALE_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG).put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD).put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG).put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD).put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG).put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM).put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE).put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM).put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE).put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD).put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG).put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK).build();
   }
}
