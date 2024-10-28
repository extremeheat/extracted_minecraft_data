package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock extends BaseEntityBlock {
   public static final MapCodec<EndPortalBlock> CODEC = simpleCodec(EndPortalBlock::new);
   protected static final VoxelShape SHAPE = Block.box(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);

   public MapCodec<EndPortalBlock> codec() {
      return CODEC;
   }

   protected EndPortalBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TheEndPortalBlockEntity(var1, var2);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var2 instanceof ServerLevel && var4.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create(var4.getBoundingBox().move((double)(-var3.getX()), (double)(-var3.getY()), (double)(-var3.getZ()))), var1.getShape(var2, var3), BooleanOp.AND)) {
         ResourceKey var5 = var2.dimension() == Level.END ? Level.OVERWORLD : Level.END;
         ServerLevel var6 = ((ServerLevel)var2).getServer().getLevel(var5);
         if (var6 == null) {
            return;
         }

         var4.changeDimension(var6);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      double var5 = (double)var3.getX() + var4.nextDouble();
      double var7 = (double)var3.getY() + 0.8;
      double var9 = (double)var3.getZ() + var4.nextDouble();
      var2.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0, 0.0, 0.0);
   }

   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   protected boolean canBeReplaced(BlockState var1, Fluid var2) {
      return false;
   }
}
