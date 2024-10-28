package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock extends BaseEntityBlock implements Portal {
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
      if (var4.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create(var4.getBoundingBox().move((double)(-var3.getX()), (double)(-var3.getY()), (double)(-var3.getZ()))), var1.getShape(var2, var3), BooleanOp.AND)) {
         if (!var2.isClientSide && var2.dimension() == Level.END && var4 instanceof ServerPlayer) {
            ServerPlayer var5 = (ServerPlayer)var4;
            if (!var5.seenCredits) {
               var5.showEndCredits();
               return;
            }
         }

         var4.setAsInsidePortal(this, var3);
      }

   }

   public DimensionTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3) {
      ResourceKey var4 = var1.dimension() == Level.END ? Level.OVERWORLD : Level.END;
      ServerLevel var5 = var1.getServer().getLevel(var4);
      boolean var6 = var4 == Level.END;
      BlockPos var7 = var6 ? ServerLevel.END_SPAWN_POINT : var5.getSharedSpawnPos();
      Vec3 var8 = new Vec3((double)var7.getX() + 0.5, (double)var7.getY(), (double)var7.getZ() + 0.5);
      if (var6) {
         this.createEndPlatform(var5, BlockPos.containing(var8).below());
      } else {
         if (var2 instanceof ServerPlayer) {
            ServerPlayer var9 = (ServerPlayer)var2;
            return var9.findRespawnPositionAndUseSpawnBlock(false);
         }

         int var10 = var5.getChunkAt(var7).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var7.getX(), var7.getZ()) + 1;
         var8 = new Vec3(var8.x, (double)var10, var8.z);
      }

      return new DimensionTransition(var5, var8, var2.getDeltaMovement(), var2.getYRot(), var2.getXRot());
   }

   private void createEndPlatform(ServerLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for(int var4 = -2; var4 <= 2; ++var4) {
         for(int var5 = -2; var5 <= 2; ++var5) {
            for(int var6 = -1; var6 < 3; ++var6) {
               BlockState var7 = var6 == -1 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
               var1.setBlockAndUpdate(var3.set(var2).move(var5, var6, var4), var7);
            }
         }
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
