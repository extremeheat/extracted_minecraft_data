package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeehiveBlock extends BaseEntityBlock {
   public static final MapCodec<BeehiveBlock> CODEC = simpleCodec(BeehiveBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final IntegerProperty HONEY_LEVEL;
   public static final int MAX_HONEY_LEVELS = 5;
   private static final int SHEARED_HONEYCOMB_COUNT = 3;

   public MapCodec<BeehiveBlock> codec() {
      return CODEC;
   }

   public BeehiveBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HONEY_LEVEL, 0)).setValue(FACING, Direction.NORTH));
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return (Integer)var1.getValue(HONEY_LEVEL);
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, var4, var5, var6);
      if (!var1.isClientSide && var5 instanceof BeehiveBlockEntity var7) {
         if (!EnchantmentHelper.hasTag(var6, EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING)) {
            var7.emptyAllLivingFromHive(var2, var4, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            var1.updateNeighbourForOutputSignal(var3, this);
            this.angerNearbyBees(var1, var3);
         }

         CriteriaTriggers.BEE_NEST_DESTROYED.trigger((ServerPlayer)var2, var4, var6, var7.getOccupantCount());
      }

   }

   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      super.onExplosionHit(var1, var2, var3, var4, var5);
      this.angerNearbyBees(var2, var3);
   }

   private void angerNearbyBees(Level var1, BlockPos var2) {
      AABB var3 = (new AABB(var2)).inflate(8.0, 6.0, 8.0);
      List var4 = var1.getEntitiesOfClass(Bee.class, var3);
      if (!var4.isEmpty()) {
         List var5 = var1.getEntitiesOfClass(Player.class, var3);
         if (var5.isEmpty()) {
            return;
         }

         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            Bee var7 = (Bee)var6.next();
            if (var7.getTarget() == null) {
               Player var8 = (Player)Util.getRandom(var5, var1.random);
               var7.setTarget(var8);
            }
         }
      }

   }

   public static void dropHoneycomb(Level var0, BlockPos var1) {
      popResource(var0, var1, new ItemStack(Items.HONEYCOMB, 3));
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      int var8 = (Integer)var2.getValue(HONEY_LEVEL);
      boolean var9 = false;
      if (var8 >= 5) {
         Item var10 = var1.getItem();
         if (var1.is(Items.SHEARS)) {
            var3.playSound(var5, var5.getX(), var5.getY(), var5.getZ(), SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            dropHoneycomb(var3, var4);
            var1.hurtAndBreak(1, var5, LivingEntity.getSlotForHand(var6));
            var9 = true;
            var3.gameEvent(var5, GameEvent.SHEAR, var4);
         } else if (var1.is(Items.GLASS_BOTTLE)) {
            var1.shrink(1);
            var3.playSound(var5, var5.getX(), var5.getY(), var5.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (var1.isEmpty()) {
               var5.setItemInHand(var6, new ItemStack(Items.HONEY_BOTTLE));
            } else if (!var5.getInventory().add(new ItemStack(Items.HONEY_BOTTLE))) {
               var5.drop(new ItemStack(Items.HONEY_BOTTLE), false);
            }

            var9 = true;
            var3.gameEvent(var5, GameEvent.FLUID_PICKUP, var4);
         }

         if (!var3.isClientSide() && var9) {
            var5.awardStat(Stats.ITEM_USED.get(var10));
         }
      }

      if (var9) {
         if (!CampfireBlock.isSmokeyPos(var3, var4)) {
            if (this.hiveContainsBees(var3, var4)) {
               this.angerNearbyBees(var3, var4);
            }

            this.releaseBeesAndResetHoneyLevel(var3, var2, var4, var5, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         } else {
            this.resetHoneyLevel(var3, var2, var4);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   private boolean hiveContainsBees(Level var1, BlockPos var2) {
      BlockEntity var3 = var1.getBlockEntity(var2);
      if (var3 instanceof BeehiveBlockEntity var4) {
         return !var4.isEmpty();
      } else {
         return false;
      }
   }

   public void releaseBeesAndResetHoneyLevel(Level var1, BlockState var2, BlockPos var3, @Nullable Player var4, BeehiveBlockEntity.BeeReleaseStatus var5) {
      this.resetHoneyLevel(var1, var2, var3);
      BlockEntity var6 = var1.getBlockEntity(var3);
      if (var6 instanceof BeehiveBlockEntity var7) {
         var7.emptyAllLivingFromHive(var4, var2, var5);
      }

   }

   public void resetHoneyLevel(Level var1, BlockState var2, BlockPos var3) {
      var1.setBlock(var3, (BlockState)var2.setValue(HONEY_LEVEL, 0), 3);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if ((Integer)var1.getValue(HONEY_LEVEL) >= 5) {
         for(int var5 = 0; var5 < var4.nextInt(1) + 1; ++var5) {
            this.trySpawnDripParticles(var2, var3, var1);
         }
      }

   }

   private void trySpawnDripParticles(Level var1, BlockPos var2, BlockState var3) {
      if (var3.getFluidState().isEmpty() && !(var1.random.nextFloat() < 0.3F)) {
         VoxelShape var4 = var3.getCollisionShape(var1, var2);
         double var5 = var4.max(Direction.Axis.Y);
         if (var5 >= 1.0 && !var3.is(BlockTags.IMPERMEABLE)) {
            double var7 = var4.min(Direction.Axis.Y);
            if (var7 > 0.0) {
               this.spawnParticle(var1, var2, var4, (double)var2.getY() + var7 - 0.05);
            } else {
               BlockPos var9 = var2.below();
               BlockState var10 = var1.getBlockState(var9);
               VoxelShape var11 = var10.getCollisionShape(var1, var9);
               double var12 = var11.max(Direction.Axis.Y);
               if ((var12 < 1.0 || !var10.isCollisionShapeFullBlock(var1, var9)) && var10.getFluidState().isEmpty()) {
                  this.spawnParticle(var1, var2, var4, (double)var2.getY() - 0.05);
               }
            }
         }

      }
   }

   private void spawnParticle(Level var1, BlockPos var2, VoxelShape var3, double var4) {
      this.spawnFluidParticle(var1, (double)var2.getX() + var3.min(Direction.Axis.X), (double)var2.getX() + var3.max(Direction.Axis.X), (double)var2.getZ() + var3.min(Direction.Axis.Z), (double)var2.getZ() + var3.max(Direction.Axis.Z), var4);
   }

   private void spawnFluidParticle(Level var1, double var2, double var4, double var6, double var8, double var10) {
      var1.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(var1.random.nextDouble(), var2, var4), var10, Mth.lerp(var1.random.nextDouble(), var6, var8), 0.0, 0.0, 0.0);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HONEY_LEVEL, FACING);
   }

   protected RenderShape getRenderShape(BlockState var1) {
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

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (var1 instanceof ServerLevel var5) {
         if (var4.isCreative() && var5.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            BlockEntity var6 = var1.getBlockEntity(var2);
            if (var6 instanceof BeehiveBlockEntity) {
               BeehiveBlockEntity var7 = (BeehiveBlockEntity)var6;
               int var8 = (Integer)var3.getValue(HONEY_LEVEL);
               boolean var9 = !var7.isEmpty();
               if (var9 || var8 > 0) {
                  ItemStack var10 = new ItemStack(this);
                  var10.applyComponents(var7.collectComponents());
                  var10.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(HONEY_LEVEL, (Comparable)var8));
                  ItemEntity var11 = new ItemEntity(var1, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), var10);
                  var11.setDefaultPickUpDelay();
                  var1.addFreshEntity(var11);
               }
            }
         }
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   protected List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
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

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      ItemStack var5 = super.getCloneItemStack(var1, var2, var3, var4);
      if (var4) {
         var5.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(HONEY_LEVEL, (Comparable)((Integer)var3.getValue(HONEY_LEVEL))));
      }

      return var5;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (var2.getBlockState(var6).getBlock() instanceof FireBlock) {
         BlockEntity var9 = var2.getBlockEntity(var4);
         if (var9 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity var10 = (BeehiveBlockEntity)var9;
            var10.emptyAllLivingFromHive((Player)null, var1, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
         }
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      BlockItemStateProperties var5 = (BlockItemStateProperties)var1.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
      int var6 = (Integer)Objects.requireNonNullElse((Integer)var5.get(HONEY_LEVEL), 0);
      int var7 = ((List)var1.getOrDefault(DataComponents.BEES, List.of())).size();
      var3.add(Component.translatable("container.beehive.bees", var7, 3).withStyle(ChatFormatting.GRAY));
      var3.add(Component.translatable("container.beehive.honey", var6, 5).withStyle(ChatFormatting.GRAY));
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      HONEY_LEVEL = BlockStateProperties.LEVEL_HONEY;
   }
}
