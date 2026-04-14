package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class CopperChestBlock extends ChestBlock {
   public static final MapCodec<CopperChestBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperChestBlock::getState), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenChestSound), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseChestSound), propertiesCodec()).apply(i, CopperChestBlock::new));
   private static final Supplier<Map<Block, Block>> COPPER_TO_COPPER_CHEST_MAPPING = Suppliers.memoize(() -> {
      ImmutableMap.Builder<Block, Block> result = ImmutableMap.builder();
      WeatheringCopperCollection var10000 = Blocks.COPPER_BLOCK;
      WeatheringCopperCollection var10001 = Blocks.COPPER_CHEST;
      Objects.requireNonNull(result);
      WeatheringCopperCollection.zipApply(var10000, var10001, result::put);
      return result.buildOrThrow();
   });
   private final WeatheringCopper.WeatherState weatherState;

   public MapCodec<? extends CopperChestBlock> codec() {
      return CODEC;
   }

   public CopperChestBlock(final WeatheringCopper.WeatherState weatherState, final SoundEvent openSound, final SoundEvent closeSound, final BlockBehaviour.Properties properties) {
      super(() -> BlockEntityTypes.CHEST, openSound, closeSound, properties);
      this.weatherState = weatherState;
   }

   public static SoundEvent getHingeSound(final WeatheringCopper.WeatherState state, final boolean open) {
      SoundEvent var10000;
      switch (state) {
         case WEATHERED -> var10000 = open ? SoundEvents.COPPER_CHEST_WEATHERED_OPEN : SoundEvents.COPPER_CHEST_WEATHERED_CLOSE;
         case OXIDIZED -> var10000 = open ? SoundEvents.COPPER_CHEST_OXIDIZED_OPEN : SoundEvents.COPPER_CHEST_OXIDIZED_CLOSE;
         default -> var10000 = open ? SoundEvents.COPPER_CHEST_OPEN : SoundEvents.COPPER_CHEST_CLOSE;
      }

      return var10000;
   }

   public boolean chestCanConnectTo(final BlockState blockState) {
      return blockState.is(BlockTags.COPPER_CHESTS) && blockState.hasProperty(ChestBlock.TYPE);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return getLeastOxidizedChestOfConnectedBlocks(state, context.getLevel(), context.getClickedPos());
   }

   private static BlockState getLeastOxidizedChestOfConnectedBlocks(final BlockState state, final Level level, final BlockPos pos) {
      BlockState connectedState = level.getBlockState(pos.relative(getConnectedDirection(state)));
      if (!((ChestType)state.getValue(ChestBlock.TYPE)).equals(ChestType.SINGLE)) {
         Block var6 = state.getBlock();
         if (var6 instanceof CopperChestBlock) {
            CopperChestBlock copperChestBlock = (CopperChestBlock)var6;
            var6 = connectedState.getBlock();
            if (var6 instanceof CopperChestBlock) {
               CopperChestBlock connectedCopperChestBlock = (CopperChestBlock)var6;
               BlockState updatedBlockState = state;
               BlockState connectedPredictedBlockState = connectedState;
               if (copperChestBlock.isWaxed() != connectedCopperChestBlock.isWaxed()) {
                  updatedBlockState = (BlockState)unwaxBlock(copperChestBlock, state).orElse(state);
                  connectedPredictedBlockState = (BlockState)unwaxBlock(connectedCopperChestBlock, connectedState).orElse(connectedState);
               }

               Block leastOxidizedBlock = copperChestBlock.weatherState.ordinal() <= connectedCopperChestBlock.weatherState.ordinal() ? updatedBlockState.getBlock() : connectedPredictedBlockState.getBlock();
               return leastOxidizedBlock.withPropertiesOf(updatedBlockState);
            }
         }
      }

      return state;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      BlockState blockState = super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      if (this.chestCanConnectTo(neighbourState)) {
         ChestType chestType = (ChestType)blockState.getValue(ChestBlock.TYPE);
         if (!chestType.equals(ChestType.SINGLE) && getConnectedDirection(blockState) == directionToNeighbour) {
            return neighbourState.getBlock().withPropertiesOf(blockState);
         }
      }

      return blockState;
   }

   private static Optional<BlockState> unwaxBlock(final CopperChestBlock copperChestBlock, final BlockState state) {
      return !copperChestBlock.isWaxed() ? Optional.of(state) : Optional.ofNullable((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(state.getBlock())).map((b) -> b.withPropertiesOf(state));
   }

   public WeatheringCopper.WeatherState getState() {
      return this.weatherState;
   }

   public static BlockState getFromCopperBlock(final Block copperBlock, final Direction facing, final Level level, final BlockPos pos) {
      CopperChestBlock block = (CopperChestBlock)((Map)COPPER_TO_COPPER_CHEST_MAPPING.get()).getOrDefault(copperBlock, Blocks.COPPER_CHEST.weathering().unaffected());
      ChestType chestType = block.getChestType(level, pos, facing);
      BlockState state = (BlockState)((BlockState)block.defaultBlockState().setValue(FACING, facing)).setValue(TYPE, chestType);
      return getLeastOxidizedChestOfConnectedBlocks(state, level, pos);
   }

   public boolean isWaxed() {
      return true;
   }

   public boolean shouldChangedStateKeepBlockEntity(final BlockState oldState) {
      return oldState.is(BlockTags.COPPER_CHESTS);
   }
}
