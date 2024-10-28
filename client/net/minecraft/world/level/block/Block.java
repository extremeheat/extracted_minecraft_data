package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class Block extends BlockBehaviour implements ItemLike {
   public static final MapCodec<Block> CODEC = simpleCodec(Block::new);
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Holder.Reference<Block> builtInRegistryHolder;
   public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY = new IdMapper();
   private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
      public Boolean load(VoxelShape var1) {
         return !Shapes.joinIsNotEmpty(Shapes.block(), var1, BooleanOp.NOT_SAME);
      }

      // $FF: synthetic method
      public Object load(final Object var1) throws Exception {
         return this.load((VoxelShape)var1);
      }
   });
   public static final int UPDATE_NEIGHBORS = 1;
   public static final int UPDATE_CLIENTS = 2;
   public static final int UPDATE_INVISIBLE = 4;
   public static final int UPDATE_IMMEDIATE = 8;
   public static final int UPDATE_KNOWN_SHAPE = 16;
   public static final int UPDATE_SUPPRESS_DROPS = 32;
   public static final int UPDATE_MOVE_BY_PISTON = 64;
   public static final int UPDATE_SKIP_SHAPE_UPDATE_ON_WIRE = 128;
   public static final int UPDATE_NONE = 4;
   public static final int UPDATE_ALL = 3;
   public static final int UPDATE_ALL_IMMEDIATE = 11;
   public static final float INDESTRUCTIBLE = -1.0F;
   public static final float INSTANT = 0.0F;
   public static final int UPDATE_LIMIT = 512;
   protected final StateDefinition<Block, BlockState> stateDefinition;
   private BlockState defaultBlockState;
   @Nullable
   private Item item;
   private static final int CACHE_SIZE = 256;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<ShapePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<ShapePairKey>(256, 0.25F) {
         protected void rehash(int var1) {
         }
      };
      var0.defaultReturnValue((byte)127);
      return var0;
   });

   protected MapCodec<? extends Block> codec() {
      return CODEC;
   }

   public static int getId(@Nullable BlockState var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = BLOCK_STATE_REGISTRY.getId(var0);
         return var1 == -1 ? 0 : var1;
      }
   }

   public static BlockState stateById(int var0) {
      BlockState var1 = (BlockState)BLOCK_STATE_REGISTRY.byId(var0);
      return var1 == null ? Blocks.AIR.defaultBlockState() : var1;
   }

   public static Block byItem(@Nullable Item var0) {
      return var0 instanceof BlockItem ? ((BlockItem)var0).getBlock() : Blocks.AIR;
   }

   public static BlockState pushEntitiesUp(BlockState var0, BlockState var1, LevelAccessor var2, BlockPos var3) {
      VoxelShape var4 = Shapes.joinUnoptimized(var0.getCollisionShape(var2, var3), var1.getCollisionShape(var2, var3), BooleanOp.ONLY_SECOND).move((double)var3.getX(), (double)var3.getY(), (double)var3.getZ());
      if (var4.isEmpty()) {
         return var1;
      } else {
         List var5 = var2.getEntities((Entity)null, var4.bounds());
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Entity var7 = (Entity)var6.next();
            double var8 = Shapes.collide(Direction.Axis.Y, var7.getBoundingBox().move(0.0, 1.0, 0.0), List.of(var4), -1.0);
            var7.teleportRelative(0.0, 1.0 + var8, 0.0);
         }

         return var1;
      }
   }

   public static VoxelShape box(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Shapes.box(var0 / 16.0, var2 / 16.0, var4 / 16.0, var6 / 16.0, var8 / 16.0, var10 / 16.0);
   }

   public static BlockState updateFromNeighbourShapes(BlockState var0, LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var0;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      Direction[] var5 = UPDATE_SHAPE_ORDER;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         var4.setWithOffset(var2, (Direction)var8);
         var3 = var3.updateShape(var1, var1, var2, var8, var4, var1.getBlockState(var4), var1.getRandom());
      }

      return var3;
   }

   public static void updateOrDestroy(BlockState var0, BlockState var1, LevelAccessor var2, BlockPos var3, int var4) {
      updateOrDestroy(var0, var1, var2, var3, var4, 512);
   }

   public static void updateOrDestroy(BlockState var0, BlockState var1, LevelAccessor var2, BlockPos var3, int var4, int var5) {
      if (var1 != var0) {
         if (var1.isAir()) {
            if (!var2.isClientSide()) {
               var2.destroyBlock(var3, (var4 & 32) == 0, (Entity)null, var5);
            }
         } else {
            var2.setBlock(var3, var1, var4 & -33, var5);
         }
      }

   }

   public Block(BlockBehaviour.Properties var1) {
      super(var1);
      this.builtInRegistryHolder = BuiltInRegistries.BLOCK.createIntrusiveHolder(this);
      StateDefinition.Builder var2 = new StateDefinition.Builder(this);
      this.createBlockStateDefinition(var2);
      this.stateDefinition = var2.create(Block::defaultBlockState, BlockState::new);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         String var3 = this.getClass().getSimpleName();
         if (!var3.endsWith("Block")) {
            LOGGER.error("Block classes should end with Block and {} doesn't.", var3);
         }
      }

   }

   public static boolean isExceptionForConnection(BlockState var0) {
      return var0.getBlock() instanceof LeavesBlock || var0.is(Blocks.BARRIER) || var0.is(Blocks.CARVED_PUMPKIN) || var0.is(Blocks.JACK_O_LANTERN) || var0.is(Blocks.MELON) || var0.is(Blocks.PUMPKIN) || var0.is(BlockTags.SHULKER_BOXES);
   }

   public static boolean shouldRenderFace(BlockState var0, BlockState var1, Direction var2) {
      VoxelShape var3 = var1.getFaceOcclusionShape(var2.getOpposite());
      if (var3 == Shapes.block()) {
         return false;
      } else if (var0.skipRendering(var1, var2)) {
         return false;
      } else if (var3 == Shapes.empty()) {
         return true;
      } else {
         VoxelShape var4 = var0.getFaceOcclusionShape(var2);
         if (var4 == Shapes.empty()) {
            return true;
         } else {
            ShapePairKey var5 = new ShapePairKey(var4, var3);
            Object2ByteLinkedOpenHashMap var6 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
            byte var7 = var6.getAndMoveToFirst(var5);
            if (var7 != 127) {
               return var7 != 0;
            } else {
               boolean var8 = Shapes.joinIsNotEmpty(var4, var3, BooleanOp.ONLY_FIRST);
               if (var6.size() == 256) {
                  var6.removeLastByte();
               }

               var6.putAndMoveToFirst(var5, (byte)(var8 ? 1 : 0));
               return var8;
            }
         }
      }
   }

   public static boolean canSupportRigidBlock(BlockGetter var0, BlockPos var1) {
      return var0.getBlockState(var1).isFaceSturdy(var0, var1, Direction.UP, SupportType.RIGID);
   }

   public static boolean canSupportCenter(LevelReader var0, BlockPos var1, Direction var2) {
      BlockState var3 = var0.getBlockState(var1);
      return var2 == Direction.DOWN && var3.is(BlockTags.UNSTABLE_BOTTOM_CENTER) ? false : var3.isFaceSturdy(var0, var1, var2, SupportType.CENTER);
   }

   public static boolean isFaceFull(VoxelShape var0, Direction var1) {
      VoxelShape var2 = var0.getFaceShape(var1);
      return isShapeFullBlock(var2);
   }

   public static boolean isShapeFullBlock(VoxelShape var0) {
      return (Boolean)SHAPE_FULL_BLOCK_CACHE.getUnchecked(var0);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
   }

   public void destroy(LevelAccessor var1, BlockPos var2, BlockState var3) {
   }

   public static List<ItemStack> getDrops(BlockState var0, ServerLevel var1, BlockPos var2, @Nullable BlockEntity var3) {
      LootParams.Builder var4 = (new LootParams.Builder(var1)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var2)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var3);
      return var0.getDrops(var4);
   }

   public static List<ItemStack> getDrops(BlockState var0, ServerLevel var1, BlockPos var2, @Nullable BlockEntity var3, @Nullable Entity var4, ItemStack var5) {
      LootParams.Builder var6 = (new LootParams.Builder(var1)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var2)).withParameter(LootContextParams.TOOL, var5).withOptionalParameter(LootContextParams.THIS_ENTITY, var4).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var3);
      return var0.getDrops(var6);
   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, (BlockEntity)null).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
         var0.spawnAfterBreak((ServerLevel)var1, var2, ItemStack.EMPTY, true);
      }

   }

   public static void dropResources(BlockState var0, LevelAccessor var1, BlockPos var2, @Nullable BlockEntity var3) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, var3).forEach((var2x) -> {
            popResource((ServerLevel)var1, (BlockPos)var2, var2x);
         });
         var0.spawnAfterBreak((ServerLevel)var1, var2, ItemStack.EMPTY, true);
      }

   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2, @Nullable BlockEntity var3, @Nullable Entity var4, ItemStack var5) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, var3, var4, var5).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
         var0.spawnAfterBreak((ServerLevel)var1, var2, var5, true);
      }

   }

   public static void popResource(Level var0, BlockPos var1, ItemStack var2) {
      double var3 = (double)EntityType.ITEM.getHeight() / 2.0;
      double var5 = (double)var1.getX() + 0.5 + Mth.nextDouble(var0.random, -0.25, 0.25);
      double var7 = (double)var1.getY() + 0.5 + Mth.nextDouble(var0.random, -0.25, 0.25) - var3;
      double var9 = (double)var1.getZ() + 0.5 + Mth.nextDouble(var0.random, -0.25, 0.25);
      popResource(var0, () -> {
         return new ItemEntity(var0, var5, var7, var9, var2);
      }, var2);
   }

   public static void popResourceFromFace(Level var0, BlockPos var1, Direction var2, ItemStack var3) {
      int var4 = var2.getStepX();
      int var5 = var2.getStepY();
      int var6 = var2.getStepZ();
      double var7 = (double)EntityType.ITEM.getWidth() / 2.0;
      double var9 = (double)EntityType.ITEM.getHeight() / 2.0;
      double var11 = (double)var1.getX() + 0.5 + (var4 == 0 ? Mth.nextDouble(var0.random, -0.25, 0.25) : (double)var4 * (0.5 + var7));
      double var13 = (double)var1.getY() + 0.5 + (var5 == 0 ? Mth.nextDouble(var0.random, -0.25, 0.25) : (double)var5 * (0.5 + var9)) - var9;
      double var15 = (double)var1.getZ() + 0.5 + (var6 == 0 ? Mth.nextDouble(var0.random, -0.25, 0.25) : (double)var6 * (0.5 + var7));
      double var17 = var4 == 0 ? Mth.nextDouble(var0.random, -0.1, 0.1) : (double)var4 * 0.1;
      double var19 = var5 == 0 ? Mth.nextDouble(var0.random, 0.0, 0.1) : (double)var5 * 0.1 + 0.1;
      double var21 = var6 == 0 ? Mth.nextDouble(var0.random, -0.1, 0.1) : (double)var6 * 0.1;
      popResource(var0, () -> {
         return new ItemEntity(var0, var11, var13, var15, var3, var17, var19, var21);
      }, var3);
   }

   private static void popResource(Level var0, Supplier<ItemEntity> var1, ItemStack var2) {
      if (var0 instanceof ServerLevel var3) {
         if (!var2.isEmpty() && var3.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemEntity var4 = (ItemEntity)var1.get();
            var4.setDefaultPickUpDelay();
            var0.addFreshEntity(var4);
            return;
         }
      }

   }

   protected void popExperience(ServerLevel var1, BlockPos var2, int var3) {
      if (var1.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         ExperienceOrb.award(var1, Vec3.atCenterOf(var2), var3);
      }

   }

   public float getExplosionResistance() {
      return this.explosionResistance;
   }

   public void wasExploded(ServerLevel var1, BlockPos var2, Explosion var3) {
   }

   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState();
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      var2.awardStat(Stats.BLOCK_MINED.get(this));
      var2.causeFoodExhaustion(0.005F);
      dropResources(var4, var1, var3, var5, var2, var6);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
   }

   public boolean isPossibleToRespawnInThis(BlockState var1) {
      return !var1.isSolid() && !var1.liquid();
   }

   public MutableComponent getName() {
      return Component.translatable(this.getDescriptionId());
   }

   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      var4.causeFallDamage(var5, 1.0F, var4.damageSources().fall());
   }

   public void updateEntityMovementAfterFallOn(BlockGetter var1, Entity var2) {
      var2.setDeltaMovement(var2.getDeltaMovement().multiply(1.0, 0.0, 1.0));
   }

   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this);
   }

   public float getFriction() {
      return this.friction;
   }

   public float getSpeedFactor() {
      return this.speedFactor;
   }

   public float getJumpFactor() {
      return this.jumpFactor;
   }

   protected void spawnDestroyParticles(Level var1, Player var2, BlockPos var3, BlockState var4) {
      var1.levelEvent(var2, 2001, var3, getId(var4));
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      this.spawnDestroyParticles(var1, var4, var2, var3);
      if (var3.is(BlockTags.GUARDED_BY_PIGLINS) && var1 instanceof ServerLevel var5) {
         PiglinAi.angerNearbyPiglins(var5, var4, false);
      }

      var1.gameEvent(GameEvent.BLOCK_DESTROY, var2, GameEvent.Context.of(var4, var3));
      return var3;
   }

   public void handlePrecipitation(BlockState var1, Level var2, BlockPos var3, Biome.Precipitation var4) {
   }

   public boolean dropFromExplosion(Explosion var1) {
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
   }

   public StateDefinition<Block, BlockState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(BlockState var1) {
      this.defaultBlockState = var1;
   }

   public final BlockState defaultBlockState() {
      return this.defaultBlockState;
   }

   public final BlockState withPropertiesOf(BlockState var1) {
      BlockState var2 = this.defaultBlockState();
      Iterator var3 = var1.getBlock().getStateDefinition().getProperties().iterator();

      while(var3.hasNext()) {
         Property var4 = (Property)var3.next();
         if (var2.hasProperty(var4)) {
            var2 = copyProperty(var1, var2, var4);
         }
      }

      return var2;
   }

   private static <T extends Comparable<T>> BlockState copyProperty(BlockState var0, BlockState var1, Property<T> var2) {
      return (BlockState)var1.setValue(var2, var0.getValue(var2));
   }

   public Item asItem() {
      if (this.item == null) {
         this.item = Item.byBlock(this);
      }

      return this.item;
   }

   public boolean hasDynamicShape() {
      return this.dynamicShape;
   }

   public String toString() {
      return "Block{" + BuiltInRegistries.BLOCK.wrapAsHolder(this).getRegisteredName() + "}";
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
   }

   protected Block asBlock() {
      return this;
   }

   protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> var1) {
      return (ImmutableMap)this.stateDefinition.getPossibleStates().stream().collect(ImmutableMap.toImmutableMap(Function.identity(), var1));
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<Block> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   protected void tryDropExperience(ServerLevel var1, BlockPos var2, ItemStack var3, IntProvider var4) {
      int var5 = EnchantmentHelper.processBlockExperience(var1, var3, var4.sample(var1.getRandom()));
      if (var5 > 0) {
         this.popExperience(var1, var2, var5);
      }

   }

   static record ShapePairKey(VoxelShape first, VoxelShape second) {
      ShapePairKey(VoxelShape var1, VoxelShape var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (var1 instanceof ShapePairKey var2) {
            if (this.first == var2.first && this.second == var2.second) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         return System.identityHashCode(this.first) * 31 + System.identityHashCode(this.second);
      }

      public VoxelShape first() {
         return this.first;
      }

      public VoxelShape second() {
         return this.second;
      }
   }
}
