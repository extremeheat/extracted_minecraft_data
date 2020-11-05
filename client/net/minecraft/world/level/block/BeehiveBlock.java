package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlock extends BaseEntityBlock {
   private static final Direction[] SPAWN_DIRECTIONS;
   public static final DirectionProperty FACING;
   public static final IntegerProperty HONEY_LEVEL;

   public BeehiveBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HONEY_LEVEL, 0)).setValue(FACING, Direction.NORTH));
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return (Integer)var1.getValue(HONEY_LEVEL);
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, var4, var5, var6);
      if (!var1.isClientSide && var5 instanceof BeehiveBlockEntity) {
         BeehiveBlockEntity var7 = (BeehiveBlockEntity)var5;
         if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var6) == 0) {
            var7.emptyAllLivingFromHive(var2, var4, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            var1.updateNeighbourForOutputSignal(var3, this);
            this.angerNearbyBees(var1, var3);
         }

         CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)var2, var4, var6, var7.getOccupantCount());
      }

   }

   private void angerNearbyBees(Level var1, BlockPos var2) {
      List var3 = var1.getEntitiesOfClass(Bee.class, (new AABB(var2)).inflate(8.0D, 6.0D, 8.0D));
      if (!var3.isEmpty()) {
         List var4 = var1.getEntitiesOfClass(Player.class, (new AABB(var2)).inflate(8.0D, 6.0D, 8.0D));
         int var5 = var4.size();
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            Bee var7 = (Bee)var6.next();
            if (var7.getTarget() == null) {
               var7.setTarget((LivingEntity)var4.get(var1.random.nextInt(var5)));
            }
         }
      }

   }

   public static void dropHoneycomb(Level var0, BlockPos var1) {
      popResource(var0, var1, new ItemStack(Items.HONEYCOMB, 3));
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      int var8 = (Integer)var1.getValue(HONEY_LEVEL);
      boolean var9 = false;
      if (var8 >= 5) {
         if (var7.is(Items.SHEARS)) {
            var2.playSound(var4, var4.getX(), var4.getY(), var4.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
            dropHoneycomb(var2, var3);
            var7.hurtAndBreak(1, var4, (var1x) -> {
               var1x.broadcastBreakEvent(var5);
            });
            var9 = true;
         } else if (var7.is(Items.GLASS_BOTTLE)) {
            var7.shrink(1);
            var2.playSound(var4, var4.getX(), var4.getY(), var4.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            if (var7.isEmpty()) {
               var4.setItemInHand(var5, new ItemStack(Items.HONEY_BOTTLE));
            } else if (!var4.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
               var4.drop(new ItemStack(Items.HONEY_BOTTLE), false);
            }

            var9 = true;
         }
      }

      if (var9) {
         if (!CampfireBlock.isSmokeyPos(var2, var3)) {
            if (this.hiveContainsBees(var2, var3)) {
               this.angerNearbyBees(var2, var3);
            }

            this.releaseBeesAndResetHoneyLevel(var2, var1, var3, var4, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         } else {
            this.resetHoneyLevel(var2, var1, var3);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return super.use(var1, var2, var3, var4, var5, var6);
      }
   }

   private boolean hiveContainsBees(Level var1, BlockPos var2) {
      BlockEntity var3 = var1.getBlockEntity(var2);
      if (var3 instanceof BeehiveBlockEntity) {
         BeehiveBlockEntity var4 = (BeehiveBlockEntity)var3;
         return !var4.isEmpty();
      } else {
         return false;
      }
   }

   public void releaseBeesAndResetHoneyLevel(Level var1, BlockState var2, BlockPos var3, @Nullable Player var4, BeehiveBlockEntity.BeeReleaseStatus var5) {
      this.resetHoneyLevel(var1, var2, var3);
      BlockEntity var6 = var1.getBlockEntity(var3);
      if (var6 instanceof BeehiveBlockEntity) {
         BeehiveBlockEntity var7 = (BeehiveBlockEntity)var6;
         var7.emptyAllLivingFromHive(var4, var2, var5);
      }

   }

   public void resetHoneyLevel(Level var1, BlockState var2, BlockPos var3) {
      var1.setBlock(var3, (BlockState)var2.setValue(HONEY_LEVEL, 0), 3);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Integer)var1.getValue(HONEY_LEVEL) >= 5) {
         for(int var5 = 0; var5 < var4.nextInt(1) + 1; ++var5) {
            this.trySpawnDripParticles(var2, var3, var1);
         }
      }

   }

   private void trySpawnDripParticles(Level var1, BlockPos var2, BlockState var3) {
      if (var3.getFluidState().isEmpty() && var1.random.nextFloat() >= 0.3F) {
         VoxelShape var4 = var3.getCollisionShape(var1, var2);
         double var5 = var4.max(Direction.Axis.Y);
         if (var5 >= 1.0D && !var3.is(BlockTags.IMPERMEABLE)) {
            double var7 = var4.min(Direction.Axis.Y);
            if (var7 > 0.0D) {
               this.spawnParticle(var1, var2, var4, (double)var2.getY() + var7 - 0.05D);
            } else {
               BlockPos var9 = var2.below();
               BlockState var10 = var1.getBlockState(var9);
               VoxelShape var11 = var10.getCollisionShape(var1, var9);
               double var12 = var11.max(Direction.Axis.Y);
               if ((var12 < 1.0D || !var10.isCollisionShapeFullBlock(var1, var9)) && var10.getFluidState().isEmpty()) {
                  this.spawnParticle(var1, var2, var4, (double)var2.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(Level var1, BlockPos var2, VoxelShape var3, double var4) {
      this.spawnFluidParticle(var1, (double)var2.getX() + var3.min(Direction.Axis.X), (double)var2.getX() + var3.max(Direction.Axis.X), (double)var2.getZ() + var3.min(Direction.Axis.Z), (double)var2.getZ() + var3.max(Direction.Axis.Z), var4);
   }

   private void spawnFluidParticle(Level var1, double var2, double var4, double var6, double var8, double var10) {
      var1.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(var1.random.nextDouble(), var2, var4), var10, Mth.lerp(var1.random.nextDouble(), var6, var8), 0.0D, 0.0D, 0.0D);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HONEY_LEVEL, FACING);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BeehiveBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && var4.isCreative() && var1.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         BlockEntity var5 = var1.getBlockEntity(var2);
         if (var5 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity var6 = (BeehiveBlockEntity)var5;
            ItemStack var7 = new ItemStack(this);
            int var8 = (Integer)var3.getValue(HONEY_LEVEL);
            boolean var9 = !var6.isEmpty();
            if (!var9 && var8 == 0) {
               return;
            }

            CompoundTag var10;
            if (var9) {
               var10 = new CompoundTag();
               var10.put("Bees", var6.writeBees());
               var7.addTagElement("BlockEntityTag", var10);
            }

            var10 = new CompoundTag();
            var10.putInt("honey_level", var8);
            var7.addTagElement("BlockStateTag", var10);
            ItemEntity var11 = new ItemEntity(var1, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), var7);
            var11.setDefaultPickUpDelay();
            var1.addFreshEntity(var11);
         }
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   public List<ItemStack> getDrops(BlockState var1, LootContext.Builder var2) {
      Entity var3 = (Entity)var2.getOptionalParameter(LootContextParams.THIS_ENTITY);
      if (var3 instanceof PrimedTnt || var3 instanceof Creeper || var3 instanceof WitherSkull || var3 instanceof WitherBoss || var3 instanceof MinecartTNT) {
         BlockEntity var4 = (BlockEntity)var2.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
         if (var4 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity var5 = (BeehiveBlockEntity)var4;
            var5.emptyAllLivingFromHive((Player)null, var1, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         }
      }

      return super.getDrops(var1, var2);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var4.getBlockState(var6).getBlock() instanceof FireBlock) {
         BlockEntity var7 = var4.getBlockEntity(var5);
         if (var7 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity var8 = (BeehiveBlockEntity)var7;
            var8.emptyAllLivingFromHive((Player)null, var1, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         }
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public static Direction getRandomOffset(Random var0) {
      return (Direction)Util.getRandom((Object[])SPAWN_DIRECTIONS, var0);
   }

   static {
      SPAWN_DIRECTIONS = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
      FACING = HorizontalDirectionalBlock.FACING;
      HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;
   }
}
