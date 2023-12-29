package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockBehaviour implements FeatureElement {
   protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{
      Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP
   };
   protected final boolean hasCollision;
   protected final float explosionResistance;
   protected final boolean isRandomlyTicking;
   protected final SoundType soundType;
   protected final float friction;
   protected final float speedFactor;
   protected final float jumpFactor;
   protected final boolean dynamicShape;
   protected final FeatureFlagSet requiredFeatures;
   protected final BlockBehaviour.Properties properties;
   @Nullable
   protected ResourceLocation drops;

   public BlockBehaviour(BlockBehaviour.Properties var1) {
      super();
      this.hasCollision = var1.hasCollision;
      this.drops = var1.drops;
      this.explosionResistance = var1.explosionResistance;
      this.isRandomlyTicking = var1.isRandomlyTicking;
      this.soundType = var1.soundType;
      this.friction = var1.friction;
      this.speedFactor = var1.speedFactor;
      this.jumpFactor = var1.jumpFactor;
      this.dynamicShape = var1.dynamicShape;
      this.requiredFeatures = var1.requiredFeatures;
      this.properties = var1;
   }

   public BlockBehaviour.Properties properties() {
      return this.properties;
   }

   protected abstract MapCodec<? extends Block> codec();

   protected static <B extends Block> RecordCodecBuilder<B, BlockBehaviour.Properties> propertiesCodec() {
      return BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties);
   }

   public static <B extends Block> MapCodec<B> simpleCodec(Function<BlockBehaviour.Properties, B> var0) {
      return RecordCodecBuilder.mapCodec(var1 -> var1.group(propertiesCodec()).apply(var1, var0));
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
   public void onExplosionHit(BlockState var1, Level var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (!var1.isAir() && var4.getBlockInteraction() != Explosion.BlockInteraction.TRIGGER_BLOCK) {
         Block var6 = var1.getBlock();
         boolean var7 = var4.getIndirectSourceEntity() instanceof Player;
         if (var6.dropFromExplosion(var4) && var2 instanceof ServerLevel var8) {
            BlockEntity var9 = var1.hasBlockEntity() ? var2.getBlockEntity(var3) : null;
            LootParams.Builder var10 = new LootParams.Builder((ServerLevel)var8)
               .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var3))
               .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
               .withOptionalParameter(LootContextParams.BLOCK_ENTITY, var9)
               .withOptionalParameter(LootContextParams.THIS_ENTITY, var4.getDirectSourceEntity());
            if (var4.getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
               var10.withParameter(LootContextParams.EXPLOSION_RADIUS, var4.radius());
            }

            var1.spawnAfterBreak((ServerLevel)var8, var3, ItemStack.EMPTY, var7);
            var1.getDrops(var10).forEach(var2x -> var5.accept(var2x, var3));
         }

         var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 3);
         var6.wasExploded(var2, var3, var4);
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

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
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
      return var1.canBeReplaced() && (var2.getItemInHand().isEmpty() || !var2.getItemInHand().is(this.asItem()));
   }

   @Deprecated
   public boolean canBeReplaced(BlockState var1, Fluid var2) {
      return var1.canBeReplaced() || !var1.isSolid();
   }

   @Deprecated
   public List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      ResourceLocation var3 = this.getLootTable();
      if (var3 == BuiltInLootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootParams var4 = var2.withParameter(LootContextParams.BLOCK_STATE, var1).create(LootContextParamSets.BLOCK);
         ServerLevel var5 = var4.getLevel();
         LootTable var6 = var5.getServer().getLootData().getLootTable(var3);
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
         ResourceLocation var1 = BuiltInRegistries.BLOCK.getKey(this.asBlock());
         this.drops = var1.withPrefix("blocks/");
      }

      return this.drops;
   }

   @Deprecated
   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
   }

   public abstract Item asItem();

   protected abstract Block asBlock();

   public MapColor defaultMapColor() {
      return this.properties.mapColor.apply(this.asBlock().defaultBlockState());
   }

   public float defaultDestroyTime() {
      return this.properties.destroyTime;
   }

   public abstract static class BlockStateBase extends StateHolder<Block, BlockState> {
      private final int lightEmission;
      private final boolean useShapeForLightOcclusion;
      private final boolean isAir;
      private final boolean ignitedByLava;
      @Deprecated
      private final boolean liquid;
      @Deprecated
      private boolean legacySolid;
      private final PushReaction pushReaction;
      private final MapColor mapColor;
      private final float destroySpeed;
      private final boolean requiresCorrectToolForDrops;
      private final boolean canOcclude;
      private final BlockBehaviour.StatePredicate isRedstoneConductor;
      private final BlockBehaviour.StatePredicate isSuffocating;
      private final BlockBehaviour.StatePredicate isViewBlocking;
      private final BlockBehaviour.StatePredicate hasPostProcess;
      private final BlockBehaviour.StatePredicate emissiveRendering;
      private final Optional<BlockBehaviour.OffsetFunction> offsetFunction;
      private final boolean spawnTerrainParticles;
      private final NoteBlockInstrument instrument;
      private final boolean replaceable;
      @Nullable
      protected BlockBehaviour.BlockStateBase.Cache cache;
      private FluidState fluidState = Fluids.EMPTY.defaultFluidState();
      private boolean isRandomlyTicking;

      protected BlockStateBase(Block var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<BlockState> var3) {
         super(var1, var2, var3);
         BlockBehaviour.Properties var4 = var1.properties;
         this.lightEmission = var4.lightEmission.applyAsInt(this.asState());
         this.useShapeForLightOcclusion = var1.useShapeForLightOcclusion(this.asState());
         this.isAir = var4.isAir;
         this.ignitedByLava = var4.ignitedByLava;
         this.liquid = var4.liquid;
         this.pushReaction = var4.pushReaction;
         this.mapColor = var4.mapColor.apply(this.asState());
         this.destroySpeed = var4.destroyTime;
         this.requiresCorrectToolForDrops = var4.requiresCorrectToolForDrops;
         this.canOcclude = var4.canOcclude;
         this.isRedstoneConductor = var4.isRedstoneConductor;
         this.isSuffocating = var4.isSuffocating;
         this.isViewBlocking = var4.isViewBlocking;
         this.hasPostProcess = var4.hasPostProcess;
         this.emissiveRendering = var4.emissiveRendering;
         this.offsetFunction = var4.offsetFunction;
         this.spawnTerrainParticles = var4.spawnTerrainParticles;
         this.instrument = var4.instrument;
         this.replaceable = var4.replaceable;
      }

      private boolean calculateSolid() {
         if (this.owner.properties.forceSolidOn) {
            return true;
         } else if (this.owner.properties.forceSolidOff) {
            return false;
         } else if (this.cache == null) {
            return false;
         } else {
            VoxelShape var1 = this.cache.collisionShape;
            if (var1.isEmpty()) {
               return false;
            } else {
               AABB var2 = var1.bounds();
               if (var2.getSize() >= 0.7291666666666666) {
                  return true;
               } else {
                  return var2.getYsize() >= 1.0;
               }
            }
         }
      }

      public void initCache() {
         this.fluidState = this.owner.getFluidState(this.asState());
         this.isRandomlyTicking = this.owner.isRandomlyTicking(this.asState());
         if (!this.getBlock().hasDynamicShape()) {
            this.cache = new BlockBehaviour.BlockStateBase.Cache(this.asState());
         }

         this.legacySolid = this.calculateSolid();
      }

      public Block getBlock() {
         return this.owner;
      }

      public Holder<Block> getBlockHolder() {
         return this.owner.builtInRegistryHolder();
      }

      @Deprecated
      public boolean blocksMotion() {
         Block var1 = this.getBlock();
         return var1 != Blocks.COBWEB && var1 != Blocks.BAMBOO_SAPLING && this.isSolid();
      }

      @Deprecated
      public boolean isSolid() {
         return this.legacySolid;
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

      public boolean ignitedByLava() {
         return this.ignitedByLava;
      }

      @Deprecated
      public boolean liquid() {
         return this.liquid;
      }

      public MapColor getMapColor(BlockGetter var1, BlockPos var2) {
         return this.mapColor;
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
         return this.pushReaction;
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
         return this.offsetFunction.<Vec3>map(var3 -> var3.evaluate(this.asState(), var1, var2)).orElse(Vec3.ZERO);
      }

      public boolean hasOffsetFunction() {
         return this.offsetFunction.isPresent();
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

      public void onExplosionHit(Level var1, BlockPos var2, Explosion var3, BiConsumer<ItemStack, BlockPos> var4) {
         this.getBlock().onExplosionHit(this.asState(), var1, var2, var3, var4);
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

      public List<ItemStack> getDrops(LootParams.Builder var1) {
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

      public boolean canBeReplaced() {
         return this.replaceable;
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

      public boolean is(Holder<Block> var1) {
         return this.is((Block)var1.value());
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

      public boolean is(ResourceKey<Block> var1) {
         return this.getBlock().builtInRegistryHolder().is(var1);
      }

      public FluidState getFluidState() {
         return this.fluidState;
      }

      public boolean isRandomlyTicking() {
         return this.isRandomlyTicking;
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

      public boolean shouldSpawnTerrainParticles() {
         return this.spawnTerrainParticles;
      }

      public NoteBlockInstrument instrument() {
         return this.instrument;
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
            if (!this.collisionShape.isEmpty() && var1.hasOffsetFunction()) {
               throw new IllegalStateException(
                  String.format(
                     Locale.ROOT,
                     "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.",
                     BuiltInRegistries.BLOCK.getKey(var2)
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

   public interface OffsetFunction {
      Vec3 evaluate(BlockState var1, BlockGetter var2, BlockPos var3);
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;

      private OffsetType() {
      }
   }

   public static class Properties {
      public static final Codec<BlockBehaviour.Properties> CODEC = Codec.unit(() -> of());
      Function<BlockState, MapColor> mapColor = var0 -> MapColor.NONE;
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
      boolean ignitedByLava;
      @Deprecated
      boolean liquid;
      @Deprecated
      boolean forceSolidOff;
      boolean forceSolidOn;
      PushReaction pushReaction = PushReaction.NORMAL;
      boolean spawnTerrainParticles = true;
      NoteBlockInstrument instrument = NoteBlockInstrument.HARP;
      boolean replaceable;
      BlockBehaviour.StateArgumentPredicate<EntityType<?>> isValidSpawn = (var0, var1, var2, var3) -> var0.isFaceSturdy(var1, var2, Direction.UP)
            && var0.getLightEmission() < 14;
      BlockBehaviour.StatePredicate isRedstoneConductor = (var0, var1, var2) -> var0.isCollisionShapeFullBlock(var1, var2);
      BlockBehaviour.StatePredicate isSuffocating = (var0, var1, var2) -> var0.blocksMotion() && var0.isCollisionShapeFullBlock(var1, var2);
      BlockBehaviour.StatePredicate isViewBlocking = this.isSuffocating;
      BlockBehaviour.StatePredicate hasPostProcess = (var0, var1, var2) -> false;
      BlockBehaviour.StatePredicate emissiveRendering = (var0, var1, var2) -> false;
      boolean dynamicShape;
      FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
      Optional<BlockBehaviour.OffsetFunction> offsetFunction = Optional.empty();

      private Properties() {
         super();
      }

      public static BlockBehaviour.Properties of() {
         return new BlockBehaviour.Properties();
      }

      public static BlockBehaviour.Properties ofFullCopy(BlockBehaviour var0) {
         BlockBehaviour.Properties var1 = ofLegacyCopy(var0);
         BlockBehaviour.Properties var2 = var0.properties;
         var1.jumpFactor = var2.jumpFactor;
         var1.isRedstoneConductor = var2.isRedstoneConductor;
         var1.isValidSpawn = var2.isValidSpawn;
         var1.hasPostProcess = var2.hasPostProcess;
         var1.isSuffocating = var2.isSuffocating;
         var1.isViewBlocking = var2.isViewBlocking;
         var1.drops = var2.drops;
         return var1;
      }

      @Deprecated
      public static BlockBehaviour.Properties ofLegacyCopy(BlockBehaviour var0) {
         BlockBehaviour.Properties var1 = new BlockBehaviour.Properties();
         BlockBehaviour.Properties var2 = var0.properties;
         var1.destroyTime = var2.destroyTime;
         var1.explosionResistance = var2.explosionResistance;
         var1.hasCollision = var2.hasCollision;
         var1.isRandomlyTicking = var2.isRandomlyTicking;
         var1.lightEmission = var2.lightEmission;
         var1.mapColor = var2.mapColor;
         var1.soundType = var2.soundType;
         var1.friction = var2.friction;
         var1.speedFactor = var2.speedFactor;
         var1.dynamicShape = var2.dynamicShape;
         var1.canOcclude = var2.canOcclude;
         var1.isAir = var2.isAir;
         var1.ignitedByLava = var2.ignitedByLava;
         var1.liquid = var2.liquid;
         var1.forceSolidOff = var2.forceSolidOff;
         var1.forceSolidOn = var2.forceSolidOn;
         var1.pushReaction = var2.pushReaction;
         var1.requiresCorrectToolForDrops = var2.requiresCorrectToolForDrops;
         var1.offsetFunction = var2.offsetFunction;
         var1.spawnTerrainParticles = var2.spawnTerrainParticles;
         var1.requiredFeatures = var2.requiredFeatures;
         var1.emissiveRendering = var2.emissiveRendering;
         var1.instrument = var2.instrument;
         var1.replaceable = var2.replaceable;
         return var1;
      }

      public BlockBehaviour.Properties mapColor(DyeColor var1) {
         this.mapColor = var1x -> var1.getMapColor();
         return this;
      }

      public BlockBehaviour.Properties mapColor(MapColor var1) {
         this.mapColor = var1x -> var1;
         return this;
      }

      public BlockBehaviour.Properties mapColor(Function<BlockState, MapColor> var1) {
         this.mapColor = var1;
         return this;
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

      public BlockBehaviour.Properties ignitedByLava() {
         this.ignitedByLava = true;
         return this;
      }

      public BlockBehaviour.Properties liquid() {
         this.liquid = true;
         return this;
      }

      public BlockBehaviour.Properties forceSolidOn() {
         this.forceSolidOn = true;
         return this;
      }

      @Deprecated
      public BlockBehaviour.Properties forceSolidOff() {
         this.forceSolidOff = true;
         return this;
      }

      public BlockBehaviour.Properties pushReaction(PushReaction var1) {
         this.pushReaction = var1;
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

      public BlockBehaviour.Properties destroyTime(float var1) {
         this.destroyTime = var1;
         return this;
      }

      public BlockBehaviour.Properties explosionResistance(float var1) {
         this.explosionResistance = Math.max(0.0F, var1);
         return this;
      }

      public BlockBehaviour.Properties offsetType(BlockBehaviour.OffsetType var1) {
         switch(var1) {
            case XYZ:
               this.offsetFunction = Optional.of((var0, var1x, var2) -> {
                  Block var3 = var0.getBlock();
                  long var4 = Mth.getSeed(var2.getX(), 0, var2.getZ());
                  double var6 = ((double)((float)(var4 >> 4 & 15L) / 15.0F) - 1.0) * (double)var3.getMaxVerticalOffset();
                  float var8 = var3.getMaxHorizontalOffset();
                  double var9 = Mth.clamp(((double)((float)(var4 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var8), (double)var8);
                  double var11 = Mth.clamp(((double)((float)(var4 >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var8), (double)var8);
                  return new Vec3(var9, var6, var11);
               });
               break;
            case XZ:
               this.offsetFunction = Optional.of((var0, var1x, var2) -> {
                  Block var3 = var0.getBlock();
                  long var4 = Mth.getSeed(var2.getX(), 0, var2.getZ());
                  float var6 = var3.getMaxHorizontalOffset();
                  double var7 = Mth.clamp(((double)((float)(var4 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var6), (double)var6);
                  double var9 = Mth.clamp(((double)((float)(var4 >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var6), (double)var6);
                  return new Vec3(var7, 0.0, var9);
               });
               break;
            default:
               this.offsetFunction = Optional.empty();
         }

         return this;
      }

      public BlockBehaviour.Properties noTerrainParticles() {
         this.spawnTerrainParticles = false;
         return this;
      }

      public BlockBehaviour.Properties requiredFeatures(FeatureFlag... var1) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
         return this;
      }

      public BlockBehaviour.Properties instrument(NoteBlockInstrument var1) {
         this.instrument = var1;
         return this;
      }

      public BlockBehaviour.Properties replaceable() {
         this.replaceable = true;
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
