package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantingTableBlock extends BaseEntityBlock {
   public static final MapCodec<EnchantingTableBlock> CODEC = simpleCodec(EnchantingTableBlock::new);
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
   public static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-2, 0, -2, 2, 1, 2)
      .filter(var0 -> Math.abs(var0.getX()) == 2 || Math.abs(var0.getZ()) == 2)
      .map(BlockPos::immutable)
      .toList();

   @Override
   public MapCodec<EnchantingTableBlock> codec() {
      return CODEC;
   }

   protected EnchantingTableBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public static boolean isValidBookShelf(Level var0, BlockPos var1, BlockPos var2) {
      return var0.getBlockState(var1.offset(var2)).is(BlockTags.ENCHANTMENT_POWER_PROVIDER)
         && var0.getBlockState(var1.offset(var2.getX() / 2, var2.getY(), var2.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
   }

   @Override
   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      super.animateTick(var1, var2, var3, var4);

      for (BlockPos var6 : BOOKSHELF_OFFSETS) {
         if (var4.nextInt(16) == 0 && isValidBookShelf(var2, var3, var6)) {
            var2.addParticle(
               ParticleTypes.ENCHANT,
               (double)var3.getX() + 0.5,
               (double)var3.getY() + 2.0,
               (double)var3.getZ() + 0.5,
               (double)((float)var6.getX() + var4.nextFloat()) - 0.5,
               (double)((float)var6.getY() - var4.nextFloat() - 1.0F),
               (double)((float)var6.getZ() + var4.nextFloat()) - 0.5
            );
         }
      }
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new EnchantingTableBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? createTickerHelper(var3, BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntity::bookAnimationTick) : null;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         return InteractionResult.CONSUME;
      }
   }

   @Nullable
   @Override
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof EnchantingTableBlockEntity) {
         Component var5 = ((Nameable)var4).getDisplayName();
         return new SimpleMenuProvider((var2x, var3x, var4x) -> new EnchantmentMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3)), var5);
      } else {
         return null;
      }
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
