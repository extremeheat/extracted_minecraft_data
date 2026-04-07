package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShovelItem extends Item {
   protected static final Map<Block, BlockState> FLATTENABLES;

   public ShovelItem(final ToolMaterial material, final float attackDamageBaseline, final float attackSpeedBaseline, final Item.Properties properties) {
      super(properties.shovel(material, attackDamageBaseline, attackSpeedBaseline));
   }

   public InteractionResult useOn(final UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      BlockState blockState = level.getBlockState(pos);
      if (context.getClickedFace() == Direction.DOWN) {
         return InteractionResult.PASS;
      } else {
         Player player = context.getPlayer();
         BlockState newState = (BlockState)FLATTENABLES.get(blockState.getBlock());
         BlockState updatedState = null;
         if (newState != null && level.getBlockState(pos.above()).isAir()) {
            level.playSound(player, (BlockPos)pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            updatedState = newState;
         } else if (blockState.getBlock() instanceof CampfireBlock && (Boolean)blockState.getValue(CampfireBlock.LIT)) {
            if (!level.isClientSide()) {
               level.levelEvent((Entity)null, 1009, pos, 0);
            }

            CampfireBlock.dowse(context.getPlayer(), level, pos, blockState);
            updatedState = (BlockState)blockState.setValue(CampfireBlock.LIT, false);
         }

         if (updatedState != null) {
            if (!level.isClientSide()) {
               level.setBlock(pos, updatedState, 11);
               level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, updatedState));
               if (player != null) {
                  context.getItemInHand().hurtAndBreak(1, player, (EquipmentSlot)context.getHand().asEquipmentSlot());
               }
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   static {
      FLATTENABLES = Maps.newHashMap((new ImmutableMap.Builder()).put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.PODZOL, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.COARSE_DIRT, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.MYCELIUM, Blocks.DIRT_PATH.defaultBlockState()).put(Blocks.ROOTED_DIRT, Blocks.DIRT_PATH.defaultBlockState()).build());
   }
}
