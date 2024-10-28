package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PowderSnowBlock extends Block implements BucketPickup {
   public static final MapCodec<PowderSnowBlock> CODEC = simpleCodec(PowderSnowBlock::new);
   private static final float HORIZONTAL_PARTICLE_MOMENTUM_FACTOR = 0.083333336F;
   private static final float IN_BLOCK_HORIZONTAL_SPEED_MULTIPLIER = 0.9F;
   private static final float IN_BLOCK_VERTICAL_SPEED_MULTIPLIER = 1.5F;
   private static final float NUM_BLOCKS_TO_FALL_INTO_BLOCK = 2.5F;
   private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.8999999761581421, 1.0);
   private static final double MINIMUM_FALL_DISTANCE_FOR_SOUND = 4.0;
   private static final double MINIMUM_FALL_DISTANCE_FOR_BIG_SOUND = 7.0;

   public MapCodec<PowderSnowBlock> codec() {
      return CODEC;
   }

   public PowderSnowBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return var2.is(this) ? true : super.skipRendering(var1, var2, var3);
   }

   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!(var4 instanceof LivingEntity) || var4.getInBlockState().is(this)) {
         var4.makeStuckInBlock(var1, new Vec3(0.8999999761581421, 1.5, 0.8999999761581421));
         if (var2.isClientSide) {
            RandomSource var5 = var2.getRandom();
            boolean var6 = var4.xOld != var4.getX() || var4.zOld != var4.getZ();
            if (var6 && var5.nextBoolean()) {
               var2.addParticle(ParticleTypes.SNOWFLAKE, var4.getX(), (double)(var3.getY() + 1), var4.getZ(), (double)(Mth.randomBetween(var5, -1.0F, 1.0F) * 0.083333336F), 0.05000000074505806, (double)(Mth.randomBetween(var5, -1.0F, 1.0F) * 0.083333336F));
            }
         }
      }

      var4.setIsInPowderSnow(true);
      if (!var2.isClientSide) {
         if (var4.isOnFire() && (var2.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || var4 instanceof Player) && var4.mayInteract(var2, var3)) {
            var2.destroyBlock(var3, false);
         }

         var4.setSharedFlagOnFire(false);
      }

   }

   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      if (!((double)var5 < 4.0) && var4 instanceof LivingEntity var6) {
         LivingEntity.Fallsounds var7 = var6.getFallSounds();
         SoundEvent var8 = (double)var5 < 7.0 ? var7.small() : var7.big();
         var4.playSound(var8, 1.0F, 1.0F);
      }
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var4 instanceof EntityCollisionContext var5) {
         Entity var6 = var5.getEntity();
         if (var6 != null) {
            if (var6.fallDistance > 2.5F) {
               return FALLING_COLLISION_SHAPE;
            }

            boolean var7 = var6 instanceof FallingBlockEntity;
            if (var7 || canEntityWalkOnPowderSnow(var6) && var4.isAbove(Shapes.block(), var3, false) && !var4.isDescending()) {
               return super.getCollisionShape(var1, var2, var3, var4);
            }
         }
      }

      return Shapes.empty();
   }

   protected VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   public static boolean canEntityWalkOnPowderSnow(Entity var0) {
      if (var0.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
         return true;
      } else {
         return var0 instanceof LivingEntity ? ((LivingEntity)var0).getItemBySlot(EquipmentSlot.FEET).is(Items.LEATHER_BOOTS) : false;
      }
   }

   public ItemStack pickupBlock(@Nullable Player var1, LevelAccessor var2, BlockPos var3, BlockState var4) {
      var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 11);
      if (!var2.isClientSide()) {
         var2.levelEvent(2001, var3, Block.getId(var4));
      }

      return new ItemStack(Items.POWDER_SNOW_BUCKET);
   }

   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL_POWDER_SNOW);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return true;
   }
}
