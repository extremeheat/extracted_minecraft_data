package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockBehaviour {
   protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{
      Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP
   };
   protected final Material material;
   protected final boolean hasCollision;
   protected final float explosionResistance;
   protected final boolean isRandomlyTicking;
   protected final SoundType soundType;
   protected final float friction;
   protected final float speedFactor;
   protected final float jumpFactor;
   protected final boolean dynamicShape;
   protected final BlockBehaviour.Properties properties;
   @Nullable
   protected ResourceLocation drops;

   public BlockBehaviour(BlockBehaviour.Properties var1) {
      super();
      this.material = var1.material;
      this.hasCollision = var1.hasCollision;
      this.drops = var1.drops;
      this.explosionResistance = var1.explosionResistance;
      this.isRandomlyTicking = var1.isRandomlyTicking;
      this.soundType = var1.soundType;
      this.friction = var1.friction;
      this.speedFactor = var1.speedFactor;
      this.jumpFactor = var1.jumpFactor;
      this.dynamicShape = var1.dynamicShape;
      this.properties = var1;
   }

   @Deprecated
   public void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4, int var5) {
   }

   @Deprecated
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      switch(var4) {
         case LAND:
            return !var1.isCollisionShapeFullBlock(var2, var3);
         case WATER:
            return var2.getFluidState(var3).is(FluidTags.WATER);
         case AIR:
            return !var1.isCollisionShapeFullBlock(var2, var3);
         default:
            return false;
      }
   }

   @Deprecated
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var1;
   }

   @Deprecated
   public boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return false;
   }

   @Deprecated
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      DebugPackets.sendNeighborsUpdatePacket(var2, var3);
   }

   @Deprecated
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
   }

   @Deprecated
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.hasBlockEntity() && !var1.is(var4.getBlock())) {
         var2.removeBlockEntity(var3);
      }
   }

   @Deprecated
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      return InteractionResult.PASS;
   }

   @Deprecated
   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      return false;
   }

   @Deprecated
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Deprecated
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return false;
   }

   @Deprecated
   public boolean isSignalSource(BlockState var1) {
      return false;
   }

   @Deprecated
   public PushReaction getPistonPushReaction(BlockState var1) {
      return this.material.getPushReaction();
   }

   @Deprecated
   public FluidState getFluidState(BlockState var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Deprecated
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return false;
   }

   public float getMaxHorizontalOffset() {
      return 0.25F;
   }

   public float getMaxVerticalOffset() {
      return 0.2F;
   }

   @Deprecated
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1;
   }

   @Deprecated
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1;
   }

   @Deprecated
   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return this.material.isReplaceable() && (var2.getItemInHand().isEmpty() || !var2.getItemInHand().is(this.asItem()));
   }

   @Deprecated
   public boolean canBeReplaced(BlockState var1, Fluid var2) {
      return this.material.isReplaceable() || !this.material.isSolid();
   }

   @Deprecated
   public List<ItemStack> getDrops(BlockState var1, LootContext.Builder var2) {
      ResourceLocation var3 = this.getLootTable();
      if (var3 == BuiltInLootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootContext var4 = var2.withParameter(LootContextParams.BLOCK_STATE, var1).create(LootContextParamSets.BLOCK);
         ServerLevel var5 = var4.getLevel();
         LootTable var6 = var5.getServer().getLootTables().get(var3);
         return var6.getRandomItems(var4);
      }
   }

   @Deprecated
   public long getSeed(BlockState var1, BlockPos var2) {
      return Mth.getSeed(var2);
   }

   @Deprecated
   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getShape(var2, var3);
   }

   @Deprecated
   public VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.getCollisionShape(var1, var2, var3, CollisionContext.empty());
   }

   @Deprecated
   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Deprecated
   public int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      if (var1.isSolidRender(var2, var3)) {
         return var2.getMaxLightLevel();
      } else {
         return var1.propagatesSkylightDown(var2, var3) ? 0 : 1;
      }
   }

   @Nullable
   @Deprecated
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return null;
   }

   @Deprecated
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return true;
   }

   @Deprecated
   public float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isCollisionShapeFullBlock(var2, var3) ? 0.2F : 1.0F;
   }

   @Deprecated
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return 0;
   }

   @Deprecated
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.block();
   }

   @Deprecated
   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.hasCollision ? var1.getShape(var2, var3) : Shapes.empty();
   }

   @Deprecated
   public boolean isCollisionShapeFullBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Block.isShapeFullBlock(var1.getCollisionShape(var2, var3));
   }

   @Deprecated
   public boolean isOcclusionShapeFullBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Block.isShapeFullBlock(var1.getOcclusionShape(var2, var3));
   }

   @Deprecated
   public VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getCollisionShape(var1, var2, var3, var4);
   }

   @Deprecated
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.tick(var1, var2, var3, var4);
   }

   @Deprecated
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
   }

   @Deprecated
   public float getDestroyProgress(BlockState var1, Player var2, BlockGetter var3, BlockPos var4) {
      float var5 = var1.getDestroySpeed(var3, var4);
      if (var5 == -1.0F) {
         return 0.0F;
      } else {
         int var6 = var2.hasCorrectToolForDrops(var1) ? 30 : 100;
         return var2.getDestroySpeed(var1) / var5 / (float)var6;
      }
   }

   @Deprecated
   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
   }

   @Deprecated
   public void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
   }

   @Deprecated
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 0;
   }

   @Deprecated
   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
   }

   @Deprecated
   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 0;
   }

   public final ResourceLocation getLootTable() {
      if (this.drops == null) {
         ResourceLocation var1 = Registry.BLOCK.getKey(this.asBlock());
         this.drops = new ResourceLocation(var1.getNamespace(), "blocks/" + var1.getPath());
      }

      return this.drops;
   }

   @Deprecated
   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
   }

   public abstract Item asItem();

   protected abstract Block asBlock();

   public MaterialColor defaultMaterialColor() {
      return this.properties.materialColor.apply(this.asBlock().defaultBlockState());
   }

   public float defaultDestroyTime() {
      return this.properties.destroyTime;
   }

   public abstract static class BlockStateBase extends StateHolder<Block, BlockState> {
      private final int lightEmission;
      private final boolean useShapeForLightOcclusion;
      private final boolean isAir;
      private final Material material;
      private final MaterialColor materialColor;
      private final float destroySpeed;
      private final boolean requiresCorrectToolForDrops;
      private final boolean canOcclude;
      private final BlockBehaviour.StatePredicate isRedstoneConductor;
      private final BlockBehaviour.StatePredicate isSuffocating;
      private final BlockBehaviour.StatePredicate isViewBlocking;
      private final BlockBehaviour.StatePredicate hasPostProcess;
      private final BlockBehaviour.StatePredicate emissiveRendering;
      private final BlockBehaviour.OffsetType offsetType;
      @Nullable
      protected BlockBehaviour.BlockStateBase.Cache cache;

      protected BlockStateBase(Block var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<BlockState> var3) {
         super(var1, var2, var3);
         BlockBehaviour.Properties var4 = var1.properties;
         this.lightEmission = var4.lightEmission.applyAsInt(this.asState());
         this.useShapeForLightOcclusion = var1.useShapeForLightOcclusion(this.asState());
         this.isAir = var4.isAir;
         this.material = var4.material;
         this.materialColor = var4.materialColor.apply(this.asState());
         this.destroySpeed = var4.destroyTime;
         this.requiresCorrectToolForDrops = var4.requiresCorrectToolForDrops;
         this.canOcclude = var4.canOcclude;
         this.isRedstoneConductor = var4.isRedstoneConductor;
         this.isSuffocating = var4.isSuffocating;
         this.isViewBlocking = var4.isViewBlocking;
         this.hasPostProcess = var4.hasPostProcess;
         this.emissiveRendering = var4.emissiveRendering;
         this.offsetType = var4.offsetType.apply(this.asState());
      }

      public void initCache() {
         if (!this.getBlock().hasDynamicShape()) {
            this.cache = new BlockBehaviour.BlockStateBase.Cache(this.asState());
         }
      }

      public Block getBlock() {
         return this.owner;
      }

      public Holder<Block> getBlockHolder() {
         return this.owner.builtInRegistryHolder();
      }

      public Material getMaterial() {
         return this.material;
      }

      public boolean isValidSpawn(BlockGetter var1, BlockPos var2, EntityType<?> var3) {
         return this.getBlock().properties.isValidSpawn.test(this.asState(), var1, var2, var3);
      }

      public boolean propagatesSkylightDown(BlockGetter var1, BlockPos var2) {
         return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this.asState(), var1, var2);
      }

      public int getLightBlock(BlockGetter var1, BlockPos var2) {
         return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this.asState(), var1, var2);
      }

      public VoxelShape getFaceOcclusionShape(BlockGetter var1, BlockPos var2, Direction var3) {
         return this.cache != null && this.cache.occlusionShapes != null
            ? this.cache.occlusionShapes[var3.ordinal()]
            : Shapes.getFaceShape(this.getOcclusionShape(var1, var2), var3);
      }

      public VoxelShape getOcclusionShape(BlockGetter var1, BlockPos var2) {
         return this.getBlock().getOcclusionShape(this.asState(), var1, var2);
      }

      public boolean hasLargeCollisionShape() {
         return this.cache == null || this.cache.largeCollisionShape;
      }

      public boolean useShapeForLightOcclusion() {
         return this.useShapeForLightOcclusion;
      }

      public int getLightEmission() {
         return this.lightEmission;
      }

      public boolean isAir() {
         return this.isAir;
      }

      public MaterialColor getMapColor(BlockGetter var1, BlockPos var2) {
         return this.materialColor;
      }

      public BlockState rotate(Rotation var1) {
         return this.getBlock().rotate(this.asState(), var1);
      }

      public BlockState mirror(Mirror var1) {
         return this.getBlock().mirror(this.asState(), var1);
      }

      public RenderShape getRenderShape() {
         return this.getBlock().getRenderShape(this.asState());
      }

      public boolean emissiveRendering(BlockGetter var1, BlockPos var2) {
         return this.emissiveRendering.test(this.asState(), var1, var2);
      }

      public float getShadeBrightness(BlockGetter var1, BlockPos var2) {
         return this.getBlock().getShadeBrightness(this.asState(), var1, var2);
      }

      public boolean isRedstoneConductor(BlockGetter var1, BlockPos var2) {
         return this.isRedstoneConductor.test(this.asState(), var1, var2);
      }

      public boolean isSignalSource() {
         return this.getBlock().isSignalSource(this.asState());
      }

      public int getSignal(BlockGetter var1, BlockPos var2, Direction var3) {
         return this.getBlock().getSignal(this.asState(), var1, var2, var3);
      }

      public boolean hasAnalogOutputSignal() {
         return this.getBlock().hasAnalogOutputSignal(this.asState());
      }

      public int getAnalogOutputSignal(Level var1, BlockPos var2) {
         return this.getBlock().getAnalogOutputSignal(this.asState(), var1, var2);
      }

      public float getDestroySpeed(BlockGetter var1, BlockPos var2) {
         return this.destroySpeed;
      }

      public float getDestroyProgress(Player var1, BlockGetter var2, BlockPos var3) {
         return this.getBlock().getDestroyProgress(this.asState(), var1, var2, var3);
      }

      public int getDirectSignal(BlockGetter var1, BlockPos var2, Direction var3) {
         return this.getBlock().getDirectSignal(this.asState(), var1, var2, var3);
      }

      public PushReaction getPistonPushReaction() {
         return this.getBlock().getPistonPushReaction(this.asState());
      }

      public boolean isSolidRender(BlockGetter var1, BlockPos var2) {
         if (this.cache != null) {
            return this.cache.solidRender;
         } else {
            BlockState var3 = this.asState();
            return var3.canOcclude() ? Block.isShapeFullBlock(var3.getOcclusionShape(var1, var2)) : false;
         }
      }

      public boolean canOcclude() {
         return this.canOcclude;
      }

      public boolean skipRendering(BlockState var1, Direction var2) {
         return this.getBlock().skipRendering(this.asState(), var1, var2);
      }

      public VoxelShape getShape(BlockGetter var1, BlockPos var2) {
         return this.getShape(var1, var2, CollisionContext.empty());
      }

      public VoxelShape getShape(BlockGetter var1, BlockPos var2, CollisionContext var3) {
         return this.getBlock().getShape(this.asState(), var1, var2, var3);
      }

      public VoxelShape getCollisionShape(BlockGetter var1, BlockPos var2) {
         return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(var1, var2, CollisionContext.empty());
      }

      public VoxelShape getCollisionShape(BlockGetter var1, BlockPos var2, CollisionContext var3) {
         return this.getBlock().getCollisionShape(this.asState(), var1, var2, var3);
      }

      public VoxelShape getBlockSupportShape(BlockGetter var1, BlockPos var2) {
         return this.getBlock().getBlockSupportShape(this.asState(), var1, var2);
      }

      public VoxelShape getVisualShape(BlockGetter var1, BlockPos var2, CollisionContext var3) {
         return this.getBlock().getVisualShape(this.asState(), var1, var2, var3);
      }

      public VoxelShape getInteractionShape(BlockGetter var1, BlockPos var2) {
         return this.getBlock().getInteractionShape(this.asState(), var1, var2);
      }

      public final boolean entityCanStandOn(BlockGetter var1, BlockPos var2, Entity var3) {
         return this.entityCanStandOnFace(var1, var2, var3, Direction.UP);
      }

      public final boolean entityCanStandOnFace(BlockGetter var1, BlockPos var2, Entity var3, Direction var4) {
         return Block.isFaceFull(this.getCollisionShape(var1, var2, CollisionContext.of(var3)), var4);
      }

      public Vec3 getOffset(BlockGetter var1, BlockPos var2) {
         if (this.offsetType == BlockBehaviour.OffsetType.NONE) {
            return Vec3.ZERO;
         } else {
            Block var3 = this.getBlock();
            long var4 = Mth.getSeed(var2.getX(), 0, var2.getZ());
            float var6 = var3.getMaxHorizontalOffset();
            double var7 = Mth.clamp(((double)((float)(var4 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var6), (double)var6);
            double var9 = this.offsetType == BlockBehaviour.OffsetType.XYZ
               ? ((double)((float)(var4 >> 4 & 15L) / 15.0F) - 1.0) * (double)var3.getMaxVerticalOffset()
               : 0.0;
            double var11 = Mth.clamp(((double)((float)(var4 >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var6), (double)var6);
            return new Vec3(var7, var9, var11);
         }
      }

      public boolean triggerEvent(Level var1, BlockPos var2, int var3, int var4) {
         return this.getBlock().triggerEvent(this.asState(), var1, var2, var3, var4);
      }

      @Deprecated
      public void neighborChanged(Level var1, BlockPos var2, Block var3, BlockPos var4, boolean var5) {
         this.getBlock().neighborChanged(this.asState(), var1, var2, var3, var4, var5);
      }

      public final void updateNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3) {
         this.updateNeighbourShapes(var1, var2, var3, 512);
      }

      public final void updateNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3, int var4) {
         this.getBlock();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(Direction var9 : BlockBehaviour.UPDATE_SHAPE_ORDER) {
            var5.setWithOffset(var2, var9);
            var1.neighborShapeChanged(var9.getOpposite(), this.asState(), var5, var2, var3, var4);
         }
      }

      public final void updateIndirectNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3) {
         this.updateIndirectNeighbourShapes(var1, var2, var3, 512);
      }

      public void updateIndirectNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3, int var4) {
         this.getBlock().updateIndirectNeighbourShapes(this.asState(), var1, var2, var3, var4);
      }

      public void onPlace(Level var1, BlockPos var2, BlockState var3, boolean var4) {
         this.getBlock().onPlace(this.asState(), var1, var2, var3, var4);
      }

      public void onRemove(Level var1, BlockPos var2, BlockState var3, boolean var4) {
         this.getBlock().onRemove(this.asState(), var1, var2, var3, var4);
      }

      public void tick(ServerLevel var1, BlockPos var2, RandomSource var3) {
         this.getBlock().tick(this.asState(), var1, var2, var3);
      }

      public void randomTick(ServerLevel var1, BlockPos var2, RandomSource var3) {
         this.getBlock().randomTick(this.asState(), var1, var2, var3);
      }

      public void entityInside(Level var1, BlockPos var2, Entity var3) {
         this.getBlock().entityInside(this.asState(), var1, var2, var3);
      }

      public void spawnAfterBreak(ServerLevel var1, BlockPos var2, ItemStack var3, boolean var4) {
         this.getBlock().spawnAfterBreak(this.asState(), var1, var2, var3, var4);
      }

      public List<ItemStack> getDrops(LootContext.Builder var1) {
         return this.getBlock().getDrops(this.asState(), var1);
      }

      public InteractionResult use(Level var1, Player var2, InteractionHand var3, BlockHitResult var4) {
         return this.getBlock().use(this.asState(), var1, var4.getBlockPos(), var2, var3, var4);
      }

      public void attack(Level var1, BlockPos var2, Player var3) {
         this.getBlock().attack(this.asState(), var1, var2, var3);
      }

      public boolean isSuffocating(BlockGetter var1, BlockPos var2) {
         return this.isSuffocating.test(this.asState(), var1, var2);
      }

      public boolean isViewBlocking(BlockGetter var1, BlockPos var2) {
         return this.isViewBlocking.test(this.asState(), var1, var2);
      }

      public BlockState updateShape(Direction var1, BlockState var2, LevelAccessor var3, BlockPos var4, BlockPos var5) {
         return this.getBlock().updateShape(this.asState(), var1, var2, var3, var4, var5);
      }

      public boolean isPathfindable(BlockGetter var1, BlockPos var2, PathComputationType var3) {
         return this.getBlock().isPathfindable(this.asState(), var1, var2, var3);
      }

      public boolean canBeReplaced(BlockPlaceContext var1) {
         return this.getBlock().canBeReplaced(this.asState(), var1);
      }

      public boolean canBeReplaced(Fluid var1) {
         return this.getBlock().canBeReplaced(this.asState(), var1);
      }

      public boolean canSurvive(LevelReader var1, BlockPos var2) {
         return this.getBlock().canSurvive(this.asState(), var1, var2);
      }

      public boolean hasPostProcess(BlockGetter var1, BlockPos var2) {
         return this.hasPostProcess.test(this.asState(), var1, var2);
      }

      @Nullable
      public MenuProvider getMenuProvider(Level var1, BlockPos var2) {
         return this.getBlock().getMenuProvider(this.asState(), var1, var2);
      }

      public boolean is(TagKey<Block> var1) {
         return this.getBlock().builtInRegistryHolder().is(var1);
      }

      public boolean is(TagKey<Block> var1, Predicate<BlockBehaviour.BlockStateBase> var2) {
         return this.is(var1) && var2.test(this);
      }

      public boolean is(HolderSet<Block> var1) {
         return var1.contains(this.getBlock().builtInRegistryHolder());
      }

      public Stream<TagKey<Block>> getTags() {
         return this.getBlock().builtInRegistryHolder().tags();
      }

      public boolean hasBlockEntity() {
         return this.getBlock() instanceof EntityBlock;
      }

      @Nullable
      public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockEntityType<T> var2) {
         return this.getBlock() instanceof EntityBlock ? ((EntityBlock)this.getBlock()).getTicker(var1, this.asState(), var2) : null;
      }

      public boolean is(Block var1) {
         return this.getBlock() == var1;
      }

      public FluidState getFluidState() {
         return this.getBlock().getFluidState(this.asState());
      }

      public boolean isRandomlyTicking() {
         return this.getBlock().isRandomlyTicking(this.asState());
      }

      public long getSeed(BlockPos var1) {
         return this.getBlock().getSeed(this.asState(), var1);
      }

      public SoundType getSoundType() {
         return this.getBlock().getSoundType(this.asState());
      }

      public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
         this.getBlock().onProjectileHit(var1, var2, var3, var4);
      }

      public boolean isFaceSturdy(BlockGetter var1, BlockPos var2, Direction var3) {
         return this.isFaceSturdy(var1, var2, var3, SupportType.FULL);
      }

      public boolean isFaceSturdy(BlockGetter var1, BlockPos var2, Direction var3, SupportType var4) {
         return this.cache != null ? this.cache.isFaceSturdy(var3, var4) : var4.isSupporting(this.asState(), var1, var2, var3);
      }

      public boolean isCollisionShapeFullBlock(BlockGetter var1, BlockPos var2) {
         return this.cache != null ? this.cache.isCollisionShapeFullBlock : this.getBlock().isCollisionShapeFullBlock(this.asState(), var1, var2);
      }

      protected abstract BlockState asState();

      public boolean requiresCorrectToolForDrops() {
         return this.requiresCorrectToolForDrops;
      }

      public BlockBehaviour.OffsetType getOffsetType() {
         return this.offsetType;
      }

      static final class Cache {
         private static final Direction[] DIRECTIONS = Direction.values();
         private static final int SUPPORT_TYPE_COUNT = SupportType.values().length;
         protected final boolean solidRender;
         final boolean propagatesSkylightDown;
         final int lightBlock;
         @Nullable
         final VoxelShape[] occlusionShapes;
         protected final VoxelShape collisionShape;
         protected final boolean largeCollisionShape;
         private final boolean[] faceSturdy;
         protected final boolean isCollisionShapeFullBlock;

         Cache(BlockState var1) {
            super();
            Block var2 = var1.getBlock();
            this.solidRender = var1.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            this.propagatesSkylightDown = var2.propagatesSkylightDown(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            this.lightBlock = var2.getLightBlock(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (!var1.canOcclude()) {
               this.occlusionShapes = null;
            } else {
               this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
               VoxelShape var3 = var2.getOcclusionShape(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

               for(Direction var7 : DIRECTIONS) {
                  this.occlusionShapes[var7.ordinal()] = Shapes.getFaceShape(var3, var7);
               }
            }

            this.collisionShape = var2.getCollisionShape(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
            if (!this.collisionShape.isEmpty() && var1.getOffsetType() != BlockBehaviour.OffsetType.NONE) {
               throw new IllegalStateException(
                  String.format(
                     "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registry.BLOCK.getKey(var2)
                  )
               );
            } else {
               this.largeCollisionShape = Arrays.stream(Direction.Axis.values())
                  .anyMatch(var1x -> this.collisionShape.min(var1x) < 0.0 || this.collisionShape.max(var1x) > 1.0);
               this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];

               for(Direction var14 : DIRECTIONS) {
                  for(SupportType var10 : SupportType.values()) {
                     this.faceSturdy[getFaceSupportIndex(var14, var10)] = var10.isSupporting(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, var14);
                  }
               }

               this.isCollisionShapeFullBlock = Block.isShapeFullBlock(var1.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
            }
         }

         public boolean isFaceSturdy(Direction var1, SupportType var2) {
            return this.faceSturdy[getFaceSupportIndex(var1, var2)];
         }

         private static int getFaceSupportIndex(Direction var0, SupportType var1) {
            return var0.ordinal() * SUPPORT_TYPE_COUNT + var1.ordinal();
         }
      }
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;

      private OffsetType() {
      }
   }

   public static class Properties {
      Material material;
      Function<BlockState, MaterialColor> materialColor;
      boolean hasCollision = true;
      SoundType soundType = SoundType.STONE;
      ToIntFunction<BlockState> lightEmission = var0 -> 0;
      float explosionResistance;
      float destroyTime;
      boolean requiresCorrectToolForDrops;
      boolean isRandomlyTicking;
      float friction = 0.6F;
      float speedFactor = 1.0F;
      float jumpFactor = 1.0F;
      ResourceLocation drops;
      boolean canOcclude = true;
      boolean isAir;
      BlockBehaviour.StateArgumentPredicate<EntityType<?>> isValidSpawn = (var0, var1x, var2x, var3) -> var0.isFaceSturdy(var1x, var2x, Direction.UP)
            && var0.getLightEmission() < 14;
      BlockBehaviour.StatePredicate isRedstoneConductor = (var0, var1x, var2x) -> var0.getMaterial().isSolidBlocking()
            && var0.isCollisionShapeFullBlock(var1x, var2x);
      BlockBehaviour.StatePredicate isSuffocating = (var1x, var2x, var3) -> this.material.blocksMotion() && var1x.isCollisionShapeFullBlock(var2x, var3);
      BlockBehaviour.StatePredicate isViewBlocking = this.isSuffocating;
      BlockBehaviour.StatePredicate hasPostProcess = (var0, var1x, var2x) -> false;
      BlockBehaviour.StatePredicate emissiveRendering = (var0, var1x, var2x) -> false;
      boolean dynamicShape;
      Function<BlockState, BlockBehaviour.OffsetType> offsetType = var0 -> BlockBehaviour.OffsetType.NONE;

      private Properties(Material var1, MaterialColor var2) {
         this(var1, var1x -> var2);
      }

      private Properties(Material var1, Function<BlockState, MaterialColor> var2) {
         super();
         this.material = var1;
         this.materialColor = var2;
      }

      public static BlockBehaviour.Properties of(Material var0) {
         return of(var0, var0.getColor());
      }

      public static BlockBehaviour.Properties of(Material var0, DyeColor var1) {
         return of(var0, var1.getMaterialColor());
      }

      public static BlockBehaviour.Properties of(Material var0, MaterialColor var1) {
         return new BlockBehaviour.Properties(var0, var1);
      }

      public static BlockBehaviour.Properties of(Material var0, Function<BlockState, MaterialColor> var1) {
         return new BlockBehaviour.Properties(var0, var1);
      }

      public static BlockBehaviour.Properties copy(BlockBehaviour var0) {
         BlockBehaviour.Properties var1 = new BlockBehaviour.Properties(var0.material, var0.properties.materialColor);
         var1.material = var0.properties.material;
         var1.destroyTime = var0.properties.destroyTime;
         var1.explosionResistance = var0.properties.explosionResistance;
         var1.hasCollision = var0.properties.hasCollision;
         var1.isRandomlyTicking = var0.properties.isRandomlyTicking;
         var1.lightEmission = var0.properties.lightEmission;
         var1.materialColor = var0.properties.materialColor;
         var1.soundType = var0.properties.soundType;
         var1.friction = var0.properties.friction;
         var1.speedFactor = var0.properties.speedFactor;
         var1.dynamicShape = var0.properties.dynamicShape;
         var1.canOcclude = var0.properties.canOcclude;
         var1.isAir = var0.properties.isAir;
         var1.requiresCorrectToolForDrops = var0.properties.requiresCorrectToolForDrops;
         var1.offsetType = var0.properties.offsetType;
         return var1;
      }

      public BlockBehaviour.Properties noCollission() {
         this.hasCollision = false;
         this.canOcclude = false;
         return this;
      }

      public BlockBehaviour.Properties noOcclusion() {
         this.canOcclude = false;
         return this;
      }

      public BlockBehaviour.Properties friction(float var1) {
         this.friction = var1;
         return this;
      }

      public BlockBehaviour.Properties speedFactor(float var1) {
         this.speedFactor = var1;
         return this;
      }

      public BlockBehaviour.Properties jumpFactor(float var1) {
         this.jumpFactor = var1;
         return this;
      }

      public BlockBehaviour.Properties sound(SoundType var1) {
         this.soundType = var1;
         return this;
      }

      public BlockBehaviour.Properties lightLevel(ToIntFunction<BlockState> var1) {
         this.lightEmission = var1;
         return this;
      }

      public BlockBehaviour.Properties strength(float var1, float var2) {
         return this.destroyTime(var1).explosionResistance(var2);
      }

      public BlockBehaviour.Properties instabreak() {
         return this.strength(0.0F);
      }

      public BlockBehaviour.Properties strength(float var1) {
         this.strength(var1, var1);
         return this;
      }

      public BlockBehaviour.Properties randomTicks() {
         this.isRandomlyTicking = true;
         return this;
      }

      public BlockBehaviour.Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      public BlockBehaviour.Properties noLootTable() {
         this.drops = BuiltInLootTables.EMPTY;
         return this;
      }

      public BlockBehaviour.Properties dropsLike(Block var1) {
         this.drops = var1.getLootTable();
         return this;
      }

      public BlockBehaviour.Properties air() {
         this.isAir = true;
         return this;
      }

      public BlockBehaviour.Properties isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> var1) {
         this.isValidSpawn = var1;
         return this;
      }

      public BlockBehaviour.Properties isRedstoneConductor(BlockBehaviour.StatePredicate var1) {
         this.isRedstoneConductor = var1;
         return this;
      }

      public BlockBehaviour.Properties isSuffocating(BlockBehaviour.StatePredicate var1) {
         this.isSuffocating = var1;
         return this;
      }

      public BlockBehaviour.Properties isViewBlocking(BlockBehaviour.StatePredicate var1) {
         this.isViewBlocking = var1;
         return this;
      }

      public BlockBehaviour.Properties hasPostProcess(BlockBehaviour.StatePredicate var1) {
         this.hasPostProcess = var1;
         return this;
      }

      public BlockBehaviour.Properties emissiveRendering(BlockBehaviour.StatePredicate var1) {
         this.emissiveRendering = var1;
         return this;
      }

      public BlockBehaviour.Properties requiresCorrectToolForDrops() {
         this.requiresCorrectToolForDrops = true;
         return this;
      }

      public BlockBehaviour.Properties color(MaterialColor var1) {
         this.materialColor = var1x -> var1;
         return this;
      }

      public BlockBehaviour.Properties destroyTime(float var1) {
         this.destroyTime = var1;
         return this;
      }

      public BlockBehaviour.Properties explosionResistance(float var1) {
         this.explosionResistance = Math.max(0.0F, var1);
         return this;
      }

      public BlockBehaviour.Properties offsetType(BlockBehaviour.OffsetType var1) {
         return this.offsetType(var1x -> var1);
      }

      public BlockBehaviour.Properties offsetType(Function<BlockState, BlockBehaviour.OffsetType> var1) {
         this.offsetType = var1;
         return this;
      }
   }

   public interface StateArgumentPredicate<A> {
      boolean test(BlockState var1, BlockGetter var2, BlockPos var3, A var4);
   }

   public interface StatePredicate {
      boolean test(BlockState var1, BlockGetter var2, BlockPos var3);
   }
}
