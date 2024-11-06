package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
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

   protected VoxelShape getEntityInsideCollisionShape(BlockState var1, Level var2, BlockPos var3) {
      return var1.getShape(var2, var3);
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4.canUsePortal(false)) {
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

   public TeleportTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3) {
      ResourceKey var4 = var1.dimension() == Level.END ? Level.OVERWORLD : Level.END;
      ServerLevel var5 = var1.getServer().getLevel(var4);
      if (var5 == null) {
         return null;
      } else {
         boolean var6 = var4 == Level.END;
         BlockPos var7 = var6 ? ServerLevel.END_SPAWN_POINT : var5.getSharedSpawnPos();
         Vec3 var8 = var7.getBottomCenter();
         float var9;
         Set var10;
         if (var6) {
            EndPlatformFeature.createEndPlatform(var5, BlockPos.containing(var8).below(), true);
            var9 = Direction.WEST.toYRot();
            var10 = Relative.union(Relative.DELTA, Set.of(Relative.X_ROT));
            if (var2 instanceof ServerPlayer) {
               var8 = var8.subtract(0.0, 1.0, 0.0);
            }
         } else {
            var9 = 0.0F;
            var10 = Relative.union(Relative.DELTA, Relative.ROTATION);
            if (var2 instanceof ServerPlayer) {
               ServerPlayer var11 = (ServerPlayer)var2;
               return var11.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
            }

            var8 = var2.adjustSpawnLocation(var5, var7).getBottomCenter();
         }

         return new TeleportTransition(var5, var8, Vec3.ZERO, var9, 0.0F, var10, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET));
      }
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      double var5 = (double)var3.getX() + var4.nextDouble();
      double var7 = (double)var3.getY() + 0.8;
      double var9 = (double)var3.getZ() + var4.nextDouble();
      var2.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0, 0.0, 0.0);
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return ItemStack.EMPTY;
   }

   protected boolean canBeReplaced(BlockState var1, Fluid var2) {
      return false;
   }
}
