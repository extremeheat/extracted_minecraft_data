package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
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

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4, InsideBlockEffectApplier var5) {
      if (!(var4 instanceof LivingEntity) || var4.getInBlockState().is(this)) {
         var4.makeStuckInBlock(var1, new Vec3(0.8999999761581421, 1.5, 0.8999999761581421));
         if (var2.isClientSide) {
            RandomSource var6 = var2.getRandom();
            boolean var7 = var4.xOld != var4.getX() || var4.zOld != var4.getZ();
            if (var7 && var6.nextBoolean()) {
               var2.addParticle(ParticleTypes.SNOWFLAKE, var4.getX(), (double)(var3.getY() + 1), var4.getZ(), (double)(Mth.randomBetween(var6, -1.0F, 1.0F) * 0.083333336F), 0.05000000074505806, (double)(Mth.randomBetween(var6, -1.0F, 1.0F) * 0.083333336F));
            }
         }
      }

      BlockPos var8 = var3.immutable();
      var5.runBefore(InsideBlockEffectType.EXTINGUISH, (var2x) -> {
         if (var2 instanceof ServerLevel var3) {
            if (var2x.isOnFire() && (var3.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || var2x instanceof Player) && var2x.mayInteract(var3, var8)) {
               var2.destroyBlock(var8, false);
            }
         }

      });
      var5.apply(InsideBlockEffectType.FREEZE);
      var5.apply(InsideBlockEffectType.EXTINGUISH);
   }

   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, double var5) {
      if (!(var5 < 4.0) && var4 instanceof LivingEntity var7) {
         LivingEntity.Fallsounds var8 = var7.getFallSounds();
         SoundEvent var9 = var5 < 7.0 ? var8.small() : var8.big();
         var4.playSound(var9, 1.0F, 1.0F);
      }
   }

   protected VoxelShape getEntityInsideCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, Entity var4) {
      VoxelShape var5 = this.getCollisionShape(var1, var2, var3, CollisionContext.of(var4));
      return var5.isEmpty() ? Shapes.block() : var5;
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (!var4.isPlacement() && var4 instanceof EntityCollisionContext var5) {
         Entity var6 = var5.getEntity();
         if (var6 != null) {
            if (var6.fallDistance > 2.5) {
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

   public ItemStack pickupBlock(@Nullable LivingEntity var1, LevelAccessor var2, BlockPos var3, BlockState var4) {
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
