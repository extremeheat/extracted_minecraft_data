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

   protected AbstractBannerBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
   }

   protected abstract MapCodec<? extends AbstractBannerBlock> codec();

   public boolean isPossibleToRespawnInThis(BlockState var1) {
      return true;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BannerBlockEntity(var1, var2, this.color);
   }

   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof BannerBlockEntity var4) {
         return var4.getItem();
      } else {
         return super.getCloneItemStack(var1, var2, var3);
      }
   }

   public DyeColor getColor() {
      return this.color;
   }
}
