package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBannerBlock extends BaseEntityBlock {
   private final DyeColor color;

   protected AbstractBannerBlock(final DyeColor color, final BlockBehaviour.Properties properties) {
      super(properties);
      this.color = color;
   }

   protected abstract MapCodec<? extends AbstractBannerBlock> codec();

   public boolean isPossibleToRespawnInThis(final BlockState state) {
      return true;
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new BannerBlockEntity(worldPosition, blockState, this.color);
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      BlockEntity var6 = level.getBlockEntity(pos);
      if (var6 instanceof BannerBlockEntity banner) {
         return banner.getItem();
      } else {
         return super.getCloneItemStack(level, pos, state, includeData);
      }
   }

   public DyeColor getColor() {
      return this.color;
   }
}
