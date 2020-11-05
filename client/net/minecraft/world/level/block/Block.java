package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block extends BlockBehaviour implements ItemLike {
   protected static final Logger LOGGER = LogManager.getLogger();
   public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY = new IdMapper();
   private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
      public Boolean load(VoxelShape var1) {
         return !Shapes.joinIsNotEmpty(Shapes.block(), var1, BooleanOp.NOT_SAME);
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((VoxelShape)var1);
      }
   });
   protected final StateDefinition<Block, BlockState> stateDefinition;
   private BlockState defaultBlockState;
   @Nullable
   private String descriptionId;
   @Nullable
   private Item item;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(2048, 0.25F) {
         protected void rehash(int var1) {
         }
      };
      var0.defaultReturnValue((byte)127);
      return var0;
   });

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

   public static BlockState pushEntitiesUp(BlockState var0, BlockState var1, Level var2, BlockPos var3) {
      VoxelShape var4 = Shapes.joinUnoptimized(var0.getCollisionShape(var2, var3), var1.getCollisionShape(var2, var3), BooleanOp.ONLY_SECOND).move((double)var3.getX(), (double)var3.getY(), (double)var3.getZ());
      List var5 = var2.getEntities((Entity)null, var4.bounds());
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         double var8 = Shapes.collide(Direction.Axis.Y, var7.getBoundingBox().move(0.0D, 1.0D, 0.0D), Stream.of(var4), -1.0D);
         var7.teleportTo(var7.getX(), var7.getY() + 1.0D + var8, var7.getZ());
      }

      return var1;
   }

   public static VoxelShape box(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Shapes.box(var0 / 16.0D, var2 / 16.0D, var4 / 16.0D, var6 / 16.0D, var8 / 16.0D, var10 / 16.0D);
   }

   public boolean is(Tag<Block> var1) {
      return var1.contains(this);
   }

   public boolean is(Block var1) {
      return this == var1;
   }

   public static BlockState updateFromNeighbourShapes(BlockState var0, LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var0;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      Direction[] var5 = UPDATE_SHAPE_ORDER;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         var4.setWithOffset(var2, var8);
         var3 = var3.updateShape(var8, var1.getBlockState(var4), var1, var2, var4);
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
      StateDefinition.Builder var2 = new StateDefinition.Builder(this);
      this.createBlockStateDefinition(var2);
      this.stateDefinition = var2.create(Block::defaultBlockState, BlockState::new);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public static boolean isExceptionForConnection(Block var0) {
      return var0 instanceof LeavesBlock || var0 == Blocks.BARRIER || var0 == Blocks.CARVED_PUMPKIN || var0 == Blocks.JACK_O_LANTERN || var0 == Blocks.MELON || var0 == Blocks.PUMPKIN || var0.is((Tag)BlockTags.SHULKER_BOXES);
   }

   public boolean isRandomlyTicking(BlockState var1) {
      return this.isRandomlyTicking;
   }

   public static boolean shouldRenderFace(BlockState var0, BlockGetter var1, BlockPos var2, Direction var3) {
      BlockPos var4 = var2.relative(var3);
      BlockState var5 = var1.getBlockState(var4);
      if (var0.skipRendering(var5, var3)) {
         return false;
      } else if (var5.canOcclude()) {
         Block.BlockStatePairKey var6 = new Block.BlockStatePairKey(var0, var5, var3);
         Object2ByteLinkedOpenHashMap var7 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
         byte var8 = var7.getAndMoveToFirst(var6);
         if (var8 != 127) {
            return var8 != 0;
         } else {
            VoxelShape var9 = var0.getFaceOcclusionShape(var1, var2, var3);
            VoxelShape var10 = var5.getFaceOcclusionShape(var1, var4, var3.getOpposite());
            boolean var11 = Shapes.joinIsNotEmpty(var9, var10, BooleanOp.ONLY_FIRST);
            if (var7.size() == 2048) {
               var7.removeLastByte();
            }

            var7.putAndMoveToFirst(var6, (byte)(var11 ? 1 : 0));
            return var11;
         }
      } else {
         return true;
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

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return !isShapeFullBlock(var1.getShape(var2, var3)) && var1.getFluidState().isEmpty();
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
   }

   public void destroy(LevelAccessor var1, BlockPos var2, BlockState var3) {
   }

   public static List<ItemStack> getDrops(BlockState var0, ServerLevel var1, BlockPos var2, @Nullable BlockEntity var3) {
      LootContext.Builder var4 = (new LootContext.Builder(var1)).withRandom(var1.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var2)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var3);
      return var0.getDrops(var4);
   }

   public static List<ItemStack> getDrops(BlockState var0, ServerLevel var1, BlockPos var2, @Nullable BlockEntity var3, @Nullable Entity var4, ItemStack var5) {
      LootContext.Builder var6 = (new LootContext.Builder(var1)).withRandom(var1.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var2)).withParameter(LootContextParams.TOOL, var5).withOptionalParameter(LootContextParams.THIS_ENTITY, var4).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var3);
      return var0.getDrops(var6);
   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, (BlockEntity)null).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
         var0.spawnAfterBreak((ServerLevel)var1, var2, ItemStack.EMPTY);
      }

   }

   public static void dropResources(BlockState var0, LevelAccessor var1, BlockPos var2, @Nullable BlockEntity var3) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, var3).forEach((var2x) -> {
            popResource((ServerLevel)var1, var2, var2x);
         });
         var0.spawnAfterBreak((ServerLevel)var1, var2, ItemStack.EMPTY);
      }

   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2, @Nullable BlockEntity var3, Entity var4, ItemStack var5) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, var3, var4, var5).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
         var0.spawnAfterBreak((ServerLevel)var1, var2, var5);
      }

   }

   public static void popResource(Level var0, BlockPos var1, ItemStack var2) {
      if (!var0.isClientSide && !var2.isEmpty() && var0.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         float var3 = 0.5F;
         double var4 = (double)(var0.random.nextFloat() * 0.5F) + 0.25D;
         double var6 = (double)(var0.random.nextFloat() * 0.5F) + 0.25D;
         double var8 = (double)(var0.random.nextFloat() * 0.5F) + 0.25D;
         ItemEntity var10 = new ItemEntity(var0, (double)var1.getX() + var4, (double)var1.getY() + var6, (double)var1.getZ() + var8, var2);
         var10.setDefaultPickUpDelay();
         var0.addFreshEntity(var10);
      }
   }

   protected void popExperience(ServerLevel var1, BlockPos var2, int var3) {
      if (var1.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         while(var3 > 0) {
            int var4 = ExperienceOrb.getExperienceValue(var3);
            var3 -= var4;
            var1.addFreshEntity(new ExperienceOrb(var1, (double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, var4));
         }
      }

   }

   public float getExplosionResistance() {
      return this.explosionResistance;
   }

   public void wasExploded(Level var1, BlockPos var2, Explosion var3) {
   }

   public void stepOn(Level var1, BlockPos var2, Entity var3) {
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

   public boolean isPossibleToRespawnInThis() {
      return !this.material.isSolid() && !this.material.isLiquid();
   }

   public MutableComponent getName() {
      return new TranslatableComponent(this.getDescriptionId());
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
      }

      return this.descriptionId;
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      var3.causeFallDamage(var4, 1.0F);
   }

   public void updateEntityAfterFallOn(BlockGetter var1, Entity var2) {
      var2.setDeltaMovement(var2.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this);
   }

   public void fillItemCategory(CreativeModeTab var1, NonNullList<ItemStack> var2) {
      var2.add(new ItemStack(this));
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

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      var1.levelEvent(var4, 2001, var2, getId(var3));
      if (this.is((Tag)BlockTags.GUARDED_BY_PIGLINS)) {
         PiglinAi.angerNearbyPiglins(var4, false);
      }

   }

   public void handleRain(Level var1, BlockPos var2) {
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

   public SoundType getSoundType(BlockState var1) {
      return this.soundType;
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
      return "Block{" + Registry.BLOCK.getKey(this) + "}";
   }

   public void appendHoverText(ItemStack var1, @Nullable BlockGetter var2, List<Component> var3, TooltipFlag var4) {
   }

   protected Block asBlock() {
      return this;
   }

   public static final class BlockStatePairKey {
      private final BlockState first;
      private final BlockState second;
      private final Direction direction;

      public BlockStatePairKey(BlockState var1, BlockState var2, Direction var3) {
         super();
         this.first = var1;
         this.second = var2;
         this.direction = var3;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Block.BlockStatePairKey)) {
            return false;
         } else {
            Block.BlockStatePairKey var2 = (Block.BlockStatePairKey)var1;
            return this.first == var2.first && this.second == var2.second && this.direction == var2.direction;
         }
      }

      public int hashCode() {
         int var1 = this.first.hashCode();
         var1 = 31 * var1 + this.second.hashCode();
         var1 = 31 * var1 + this.direction.hashCode();
         return var1;
      }
   }
}
