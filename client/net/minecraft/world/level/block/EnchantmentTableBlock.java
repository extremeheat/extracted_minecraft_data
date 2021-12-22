package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantmentTableBlock extends BaseEntityBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected EnchantmentTableBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      super.animateTick(var1, var2, var3, var4);

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            if (var5 > -2 && var5 < 2 && var6 == -1) {
               var6 = 2;
            }

            if (var4.nextInt(16) == 0) {
               for(int var7 = 0; var7 <= 1; ++var7) {
                  BlockPos var8 = var3.offset(var5, var7, var6);
                  if (var2.getBlockState(var8).is(Blocks.BOOKSHELF)) {
                     if (!var2.isEmptyBlock(var3.offset(var5 / 2, 0, var6 / 2))) {
                        break;
                     }

                     var2.addParticle(ParticleTypes.ENCHANT, (double)var3.getX() + 0.5D, (double)var3.getY() + 2.0D, (double)var3.getZ() + 0.5D, (double)((float)var5 + var4.nextFloat()) - 0.5D, (double)((float)var7 - var4.nextFloat() - 1.0F), (double)((float)var6 + var4.nextFloat()) - 0.5D);
                  }
               }
            }
         }
      }

   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new EnchantmentTableBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? createTickerHelper(var3, BlockEntityType.ENCHANTING_TABLE, EnchantmentTableBlockEntity::bookAnimationTick) : null;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof EnchantmentTableBlockEntity) {
         Component var5 = ((Nameable)var4).getDisplayName();
         return new SimpleMenuProvider((var2x, var3x, var4x) -> {
            return new EnchantmentMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3));
         }, var5);
      } else {
         return null;
      }
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof EnchantmentTableBlockEntity) {
            ((EnchantmentTableBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
