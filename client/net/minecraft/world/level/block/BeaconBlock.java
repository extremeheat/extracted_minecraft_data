package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class BeaconBlock extends BaseEntityBlock implements BeaconBeamBlock {
   public static final MapCodec<BeaconBlock> CODEC = simpleCodec(BeaconBlock::new);

   public MapCodec<BeaconBlock> codec() {
      return CODEC;
   }

   public BeaconBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new BeaconBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityType.BEACON, BeaconBlockEntity::tick);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         BlockEntity var7 = level.getBlockEntity(pos);
         if (var7 instanceof BeaconBlockEntity) {
            BeaconBlockEntity beacon = (BeaconBlockEntity)var7;
            player.openMenu(beacon);
            player.awardStat(Stats.INTERACT_WITH_BEACON);
         }
      }

      return InteractionResult.SUCCESS;
   }
}
