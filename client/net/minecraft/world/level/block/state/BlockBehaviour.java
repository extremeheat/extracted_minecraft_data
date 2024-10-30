package net.minecraft.world.level.block.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.ScheduledTickAccess;
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
import net.minecraft.world.level.redstone.Orientation;
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
   protected static final Direction[] UPDATE_SHAPE_ORDER;
   protected final boolean hasCollision;
   protected final float explosionResistance;
   protected final boolean isRandomlyTicking;
   protected final SoundType soundType;
   protected final float friction;
   protected final float speedFactor;
   protected final float jumpFactor;
   protected final boolean dynamicShape;
   protected final FeatureFlagSet requiredFeatures;
   protected final Properties properties;
   protected final Optional<ResourceKey<LootTable>> drops;
   protected final String descriptionId;

   public BlockBehaviour(Properties var1) {
      super();
      this.hasCollision = var1.hasCollision;
      this.drops = var1.effectiveDrops();
      this.descriptionId = var1.effectiveDescriptionId();
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

   public Properties properties() {
      return this.properties;
   }

   protected abstract MapCodec<? extends Block> codec();

   protected static <B extends Block> RecordCodecBuilder<B, Properties> propertiesCodec() {
      return BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties);
   }

   public static <B extends Block> MapCodec<B> simpleCodec(Function<Properties, B> var0) {
      return RecordCodecBuilder.mapCodec((var1) -> {
         return var1.group(propertiesCodec()).apply(var1, var0);
      });
   }

   protected void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4, int var5) {
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      switch (var2) {
         case LAND -> {
            return !var1.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         }
         case WATER -> {
            return var1.getFluidState().is(FluidTags.WATER);
         }
         case AIR -> {
            return !var1.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         }
         default -> {
            return false;
         }
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var1;
   }

   protected boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return false;
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.hasBlockEntity() && !var1.is(var4.getBlock())) {
         var2.removeBlockEntity(var3);
      }

   }

   protected void onExplosionHit(BlockState var1, ServerLevel var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (!var1.isAir() && var4.getBlockInteraction() != Explosion.BlockInteraction.TRIGGER_BLOCK) {
         Block var6 = var1.getBlock();
         boolean var7 = var4.getIndirectSourceEntity() instanceof Player;
         if (var6.dropFromExplosion(var4)) {
            BlockEntity var8 = var1.hasBlockEntity() ? var2.getBlockEntity(var3) : null;
            LootParams.Builder var9 = (new LootParams.Builder(var2)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var3)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var8).withOptionalParameter(LootContextParams.THIS_ENTITY, var4.getDirectSourceEntity());
            if (var4.getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
               var9.withParameter(LootContextParams.EXPLOSION_RADIUS, var4.radius());
            }

            var1.spawnAfterBreak(var2, var3, ItemStack.EMPTY, var7);
            var1.getDrops(var9).forEach((var2x) -> {
               var5.accept(var2x, var3);
            });
         }

         var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 3);
         var6.wasExploded(var2, var3, var4);
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      return InteractionResult.PASS;
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      return InteractionResult.TRY_WITH_EMPTY_HAND;
   }

   protected boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      return false;
   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return false;
   }

   protected boolean isSignalSource(BlockState var1) {
      return false;
   }

   protected FluidState getFluidState(BlockState var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return false;
   }

   protected float getMaxHorizontalOffset() {
      return 0.25F;
   }

   protected float getMaxVerticalOffset() {
      return 0.2F;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1;
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1;
   }

   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return var1.canBeReplaced() && (var2.getItemInHand().isEmpty() || !var2.getItemInHand().is(this.asItem()));
   }

   protected boolean canBeReplaced(BlockState var1, Fluid var2) {
      return var1.canBeReplaced() || !var1.isSolid();
   }

   protected List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      if (this.drops.isEmpty()) {
         return Collections.emptyList();
      } else {
         LootParams var3 = var2.withParameter(LootContextParams.BLOCK_STATE, var1).create(LootContextParamSets.BLOCK);
         ServerLevel var4 = var3.getLevel();
         LootTable var5 = var4.getServer().reloadableRegistries().getLootTable((ResourceKey)this.drops.get());
         return var5.getRandomItems(var3);
      }
   }

   protected long getSeed(BlockState var1, BlockPos var2) {
      return Mth.getSeed(var2);
   }

   protected VoxelShape getOcclusionShape(BlockState var1) {
      return var1.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.getCollisionShape(var1, var2, var3, CollisionContext.empty());
   }

   protected VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   protected int getLightBlock(BlockState var1) {
      if (var1.isSolidRender()) {
         return 15;
      } else {
         return var1.propagatesSkylightDown() ? 0 : 1;
      }
   }

   @Nullable
   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return null;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return true;
   }

   protected float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isCollisionShapeFullBlock(var2, var3) ? 0.2F : 1.0F;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return 0;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.block();
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.hasCollision ? var1.getShape(var2, var3) : Shapes.empty();
   }

   protected boolean isCollisionShapeFullBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Block.isShapeFullBlock(var1.getCollisionShape(var2, var3));
   }

   protected VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getCollisionShape(var1, var2, var3, var4);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
   }

   protected float getDestroyProgress(BlockState var1, Player var2, BlockGetter var3, BlockPos var4) {
      float var5 = var1.getDestroySpeed(var3, var4);
      if (var5 == -1.0F) {
         return 0.0F;
      } else {
         int var6 = var2.hasCorrectToolForDrops(var1) ? 30 : 100;
         return var2.getDestroySpeed(var1) / var5 / (float)var6;
      }
   }

   protected void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
   }

   protected void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
   }

   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 0;
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
   }

   protected VoxelShape getEntityInsideCollisionShape(BlockState var1, Level var2, BlockPos var3) {
      return Shapes.block();
   }

   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 0;
   }

   public final Optional<ResourceKey<LootTable>> getLootTable() {
      return this.drops;
   }

   public final String getDescriptionId() {
      return this.descriptionId;
   }

   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return !Block.isShapeFullBlock(var1.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) && var1.getFluidState().isEmpty();
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return this.isRandomlyTicking;
   }

   protected SoundType getSoundType(BlockState var1) {
      return this.soundType;
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this.asItem());
   }

   public abstract Item asItem();

   protected abstract Block asBlock();

   public MapColor defaultMapColor() {
      return (MapColor)this.properties.mapColor.apply(this.asBlock().defaultBlockState());
   }

   public float defaultDestroyTime() {
      return this.properties.destroyTime;
   }

   static {
      UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
   }

   public static class Properties {
      public static final Codec<Properties> CODEC = Codec.unit(() -> {
         return of();
      });
      Function<BlockState, MapColor> mapColor = (var0) -> {
         return MapColor.NONE;
      };
      boolean hasCollision = true;
      SoundType soundType;
      ToIntFunction<BlockState> lightEmission;
      float explosionResistance;
      float destroyTime;
      boolean requiresCorrectToolForDrops;
      boolean isRandomlyTicking;
      float friction;
      float speedFactor;
      float jumpFactor;
      @Nullable
      private ResourceKey<Block> id;
      private DependantName<Block, Optional<ResourceKey<LootTable>>> drops;
      private DependantName<Block, String> descriptionId;
      boolean canOcclude;
      boolean isAir;
      boolean ignitedByLava;
      /** @deprecated */
      @Deprecated
      boolean liquid;
      /** @deprecated */
      @Deprecated
      boolean forceSolidOff;
      boolean forceSolidOn;
      PushReaction pushReaction;
      boolean spawnTerrainParticles;
      NoteBlockInstrument instrument;
      boolean replaceable;
      StateArgumentPredicate<EntityType<?>> isValidSpawn;
      StatePredicate isRedstoneConductor;
      StatePredicate isSuffocating;
      StatePredicate isViewBlocking;
      StatePredicate hasPostProcess;
      StatePredicate emissiveRendering;
      boolean dynamicShape;
      FeatureFlagSet requiredFeatures;
      @Nullable
      OffsetFunction offsetFunction;

      private Properties() {
         super();
         this.soundType = SoundType.STONE;
         this.lightEmission = (var0) -> {
            return 0;
         };
         this.friction = 0.6F;
         this.speedFactor = 1.0F;
         this.jumpFactor = 1.0F;
         this.drops = (var0) -> {
            return Optional.of(ResourceKey.create(Registries.LOOT_TABLE, var0.location().withPrefix("blocks/")));
         };
         this.descriptionId = (var0) -> {
            return Util.makeDescriptionId("block", var0.location());
         };
         this.canOcclude = true;
         this.pushReaction = PushReaction.NORMAL;
         this.spawnTerrainParticles = true;
         this.instrument = NoteBlockInstrument.HARP;
         this.isValidSpawn = (var0, var1, var2, var3) -> {
            return var0.isFaceSturdy(var1, var2, Direction.UP) && var0.getLightEmission() < 14;
         };
         this.isRedstoneConductor = (var0, var1, var2) -> {
            return var0.isCollisionShapeFullBlock(var1, var2);
         };
         this.isSuffocating = (var0, var1, var2) -> {
            return var0.blocksMotion() && var0.isCollisionShapeFullBlock(var1, var2);
         };
         this.isViewBlocking = this.isSuffocating;
         this.hasPostProcess = (var0, var1, var2) -> {
            return false;
         };
         this.emissiveRendering = (var0, var1, var2) -> {
            return false;
         };
         this.requiredFeatures = FeatureFlags.VANILLA_SET;
      }

      public static Properties of() {
         return new Properties();
      }

      public static Properties ofFullCopy(BlockBehaviour var0) {
         Properties var1 = ofLegacyCopy(var0);
         Properties var2 = var0.properties;
         var1.jumpFactor = var2.jumpFactor;
         var1.isRedstoneConductor = var2.isRedstoneConductor;
         var1.isValidSpawn = var2.isValidSpawn;
         var1.hasPostProcess = var2.hasPostProcess;
         var1.isSuffocating = var2.isSuffocating;
         var1.isViewBlocking = var2.isViewBlocking;
         var1.drops = var2.drops;
         var1.descriptionId = var2.descriptionId;
         return var1;
      }

      /** @deprecated */
      @Deprecated
      public static Properties ofLegacyCopy(BlockBehaviour var0) {
         Properties var1 = new Properties();
         Properties var2 = var0.properties;
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

      public Properties mapColor(DyeColor var1) {
         this.mapColor = (var1x) -> {
            return var1.getMapColor();
         };
         return this;
      }

      public Properties mapColor(MapColor var1) {
         this.mapColor = (var1x) -> {
            return var1;
         };
         return this;
      }

      public Properties mapColor(Function<BlockState, MapColor> var1) {
         this.mapColor = var1;
         return this;
      }

      public Properties noCollission() {
         this.hasCollision = false;
         this.canOcclude = false;
         return this;
      }

      public Properties noOcclusion() {
         this.canOcclude = false;
         return this;
      }

      public Properties friction(float var1) {
         this.friction = var1;
         return this;
      }

      public Properties speedFactor(float var1) {
         this.speedFactor = var1;
         return this;
      }

      public Properties jumpFactor(float var1) {
         this.jumpFactor = var1;
         return this;
      }

      public Properties sound(SoundType var1) {
         this.soundType = var1;
         return this;
      }

      public Properties lightLevel(ToIntFunction<BlockState> var1) {
         this.lightEmission = var1;
         return this;
      }

      public Properties strength(float var1, float var2) {
         return this.destroyTime(var1).explosionResistance(var2);
      }

      public Properties instabreak() {
         return this.strength(0.0F);
      }

      public Properties strength(float var1) {
         this.strength(var1, var1);
         return this;
      }

      public Properties randomTicks() {
         this.isRandomlyTicking = true;
         return this;
      }

      public Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      public Properties noLootTable() {
         this.drops = DependantName.fixed(Optional.empty());
         return this;
      }

      public Properties overrideLootTable(Optional<ResourceKey<LootTable>> var1) {
         this.drops = DependantName.fixed(var1);
         return this;
      }

      protected Optional<ResourceKey<LootTable>> effectiveDrops() {
         return (Optional)this.drops.get((ResourceKey)Objects.requireNonNull(this.id, "Block id not set"));
      }

      public Properties ignitedByLava() {
         this.ignitedByLava = true;
         return this;
      }

      public Properties liquid() {
         this.liquid = true;
         return this;
      }

      public Properties forceSolidOn() {
         this.forceSolidOn = true;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Properties forceSolidOff() {
         this.forceSolidOff = true;
         return this;
      }

      public Properties pushReaction(PushReaction var1) {
         this.pushReaction = var1;
         return this;
      }

      public Properties air() {
         this.isAir = true;
         return this;
      }

      public Properties isValidSpawn(StateArgumentPredicate<EntityType<?>> var1) {
         this.isValidSpawn = var1;
         return this;
      }

      public Properties isRedstoneConductor(StatePredicate var1) {
         this.isRedstoneConductor = var1;
         return this;
      }

      public Properties isSuffocating(StatePredicate var1) {
         this.isSuffocating = var1;
         return this;
      }

      public Properties isViewBlocking(StatePredicate var1) {
         this.isViewBlocking = var1;
         return this;
      }

      public Properties hasPostProcess(StatePredicate var1) {
         this.hasPostProcess = var1;
         return this;
      }

      public Properties emissiveRendering(StatePredicate var1) {
         this.emissiveRendering = var1;
         return this;
      }

      public Properties requiresCorrectToolForDrops() {
         this.requiresCorrectToolForDrops = true;
         return this;
      }

      public Properties destroyTime(float var1) {
         this.destroyTime = var1;
         return this;
      }

      public Properties explosionResistance(float var1) {
         this.explosionResistance = Math.max(0.0F, var1);
         return this;
      }

      public Properties offsetType(OffsetType var1) {
         OffsetFunction var10001;
         switch (var1.ordinal()) {
            case 0 -> var10001 = null;
            case 1 -> var10001 = (var0, var1x) -> {
   Block var2 = var0.getBlock();
   long var3 = Mth.getSeed(var1x.getX(), 0, var1x.getZ());
   float var5 = var2.getMaxHorizontalOffset();
   double var6 = Mth.clamp(((double)((float)(var3 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var5), (double)var5);
   double var8 = Mth.clamp(((double)((float)(var3 >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var5), (double)var5);
   return new Vec3(var6, 0.0, var8);
};
            case 2 -> var10001 = (var0, var1x) -> {
   Block var2 = var0.getBlock();
   long var3 = Mth.getSeed(var1x.getX(), 0, var1x.getZ());
   double var5 = ((double)((float)(var3 >> 4 & 15L) / 15.0F) - 1.0) * (double)var2.getMaxVerticalOffset();
   float var7 = var2.getMaxHorizontalOffset();
   double var8 = Mth.clamp(((double)((float)(var3 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var7), (double)var7);
   double var10 = Mth.clamp(((double)((float)(var3 >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-var7), (double)var7);
   return new Vec3(var8, var5, var10);
};
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         this.offsetFunction = var10001;
         return this;
      }

      public Properties noTerrainParticles() {
         this.spawnTerrainParticles = false;
         return this;
      }

      public Properties requiredFeatures(FeatureFlag... var1) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
         return this;
      }

      public Properties instrument(NoteBlockInstrument var1) {
         this.instrument = var1;
         return this;
      }

      public Properties replaceable() {
         this.replaceable = true;
         return this;
      }

      public Properties setId(ResourceKey<Block> var1) {
         this.id = var1;
         return this;
      }

      public Properties overrideDescription(String var1) {
         this.descriptionId = DependantName.fixed(var1);
         return this;
      }

      protected String effectiveDescriptionId() {
         return (String)this.descriptionId.get((ResourceKey)Objects.requireNonNull(this.id, "Block id not set"));
      }
   }

   @FunctionalInterface
   public interface StateArgumentPredicate<A> {
      boolean test(BlockState var1, BlockGetter var2, BlockPos var3, A var4);
   }

   @FunctionalInterface
   public interface OffsetFunction {
      Vec3 evaluate(BlockState var1, BlockPos var2);
   }

   @FunctionalInterface
   public interface StatePredicate {
      boolean test(BlockState var1, BlockGetter var2, BlockPos var3);
   }

   public abstract static class BlockStateBase extends StateHolder<Block, BlockState> {
      private static final Direction[] DIRECTIONS = Direction.values();
      private static final VoxelShape[] EMPTY_OCCLUSION_SHAPES;
      private static final VoxelShape[] FULL_BLOCK_OCCLUSION_SHAPES;
      private final int lightEmission;
      private final boolean useShapeForLightOcclusion;
      private final boolean isAir;
      private final boolean ignitedByLava;
      /** @deprecated */
      @Deprecated
      private final boolean liquid;
      /** @deprecated */
      @Deprecated
      private boolean legacySolid;
      private final PushReaction pushReaction;
      private final MapColor mapColor;
      private final float destroySpeed;
      private final boolean requiresCorrectToolForDrops;
      private final boolean canOcclude;
      private final StatePredicate isRedstoneConductor;
      private final StatePredicate isSuffocating;
      private final StatePredicate isViewBlocking;
      private final StatePredicate hasPostProcess;
      private final StatePredicate emissiveRendering;
      @Nullable
      private final OffsetFunction offsetFunction;
      private final boolean spawnTerrainParticles;
      private final NoteBlockInstrument instrument;
      private final boolean replaceable;
      @Nullable
      private Cache cache;
      private FluidState fluidState;
      private boolean isRandomlyTicking;
      private boolean solidRender;
      private VoxelShape occlusionShape;
      private VoxelShape[] occlusionShapesByFace;
      private boolean propagatesSkylightDown;
      private int lightBlock;

      protected BlockStateBase(Block var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<BlockState> var3) {
         super(var1, var2, var3);
         this.fluidState = Fluids.EMPTY.defaultFluidState();
         Properties var4 = var1.properties;
         this.lightEmission = var4.lightEmission.applyAsInt(this.asState());
         this.useShapeForLightOcclusion = var1.useShapeForLightOcclusion(this.asState());
         this.isAir = var4.isAir;
         this.ignitedByLava = var4.ignitedByLava;
         this.liquid = var4.liquid;
         this.pushReaction = var4.pushReaction;
         this.mapColor = (MapColor)var4.mapColor.apply(this.asState());
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
         if (((Block)this.owner).properties.forceSolidOn) {
            return true;
         } else if (((Block)this.owner).properties.forceSolidOff) {
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
         this.fluidState = ((Block)this.owner).getFluidState(this.asState());
         this.isRandomlyTicking = ((Block)this.owner).isRandomlyTicking(this.asState());
         if (!this.getBlock().hasDynamicShape()) {
            this.cache = new Cache(this.asState());
         }

         this.legacySolid = this.calculateSolid();
         this.occlusionShape = this.canOcclude ? ((Block)this.owner).getOcclusionShape(this.asState()) : Shapes.empty();
         this.solidRender = Block.isShapeFullBlock(this.occlusionShape);
         if (this.occlusionShape.isEmpty()) {
            this.occlusionShapesByFace = EMPTY_OCCLUSION_SHAPES;
         } else if (this.solidRender) {
            this.occlusionShapesByFace = FULL_BLOCK_OCCLUSION_SHAPES;
         } else {
            this.occlusionShapesByFace = new VoxelShape[DIRECTIONS.length];
            Direction[] var1 = DIRECTIONS;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               Direction var4 = var1[var3];
               this.occlusionShapesByFace[var4.ordinal()] = this.occlusionShape.getFaceShape(var4);
            }
         }

         this.propagatesSkylightDown = ((Block)this.owner).propagatesSkylightDown(this.asState());
         this.lightBlock = ((Block)this.owner).getLightBlock(this.asState());
      }

      public Block getBlock() {
         return (Block)this.owner;
      }

      public Holder<Block> getBlockHolder() {
         return ((Block)this.owner).builtInRegistryHolder();
      }

      /** @deprecated */
      @Deprecated
      public boolean blocksMotion() {
         Block var1 = this.getBlock();
         return var1 != Blocks.COBWEB && var1 != Blocks.BAMBOO_SAPLING && this.isSolid();
      }

      /** @deprecated */
      @Deprecated
      public boolean isSolid() {
         return this.legacySolid;
      }

      public boolean isValidSpawn(BlockGetter var1, BlockPos var2, EntityType<?> var3) {
         return this.getBlock().properties.isValidSpawn.test(this.asState(), var1, var2, var3);
      }

      public boolean propagatesSkylightDown() {
         return this.propagatesSkylightDown;
      }

      public int getLightBlock() {
         return this.lightBlock;
      }

      public VoxelShape getFaceOcclusionShape(Direction var1) {
         return this.occlusionShapesByFace[var1.ordinal()];
      }

      public VoxelShape getOcclusionShape() {
         return this.occlusionShape;
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

      /** @deprecated */
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

      public boolean isSolidRender() {
         return this.solidRender;
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

      public Vec3 getOffset(BlockPos var1) {
         OffsetFunction var2 = this.offsetFunction;
         return var2 != null ? var2.evaluate(this.asState(), var1) : Vec3.ZERO;
      }

      public boolean hasOffsetFunction() {
         return this.offsetFunction != null;
      }

      public boolean triggerEvent(Level var1, BlockPos var2, int var3, int var4) {
         return this.getBlock().triggerEvent(this.asState(), var1, var2, var3, var4);
      }

      public void handleNeighborChanged(Level var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
         DebugPackets.sendNeighborsUpdatePacket(var1, var2);
         this.getBlock().neighborChanged(this.asState(), var1, var2, var3, var4, var5);
      }

      public final void updateNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3) {
         this.updateNeighbourShapes(var1, var2, var3, 512);
      }

      public final void updateNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3, int var4) {
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
         Direction[] var6 = BlockBehaviour.UPDATE_SHAPE_ORDER;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            var5.setWithOffset(var2, (Direction)var9);
            var1.neighborShapeChanged(var9.getOpposite(), var5, var2, this.asState(), var3, var4);
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

      public void onExplosionHit(ServerLevel var1, BlockPos var2, Explosion var3, BiConsumer<ItemStack, BlockPos> var4) {
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

      public VoxelShape getEntityInsideCollisionShape(Level var1, BlockPos var2) {
         return this.getBlock().getEntityInsideCollisionShape(this.asState(), var1, var2);
      }

      public void spawnAfterBreak(ServerLevel var1, BlockPos var2, ItemStack var3, boolean var4) {
         this.getBlock().spawnAfterBreak(this.asState(), var1, var2, var3, var4);
      }

      public List<ItemStack> getDrops(LootParams.Builder var1) {
         return this.getBlock().getDrops(this.asState(), var1);
      }

      public InteractionResult useItemOn(ItemStack var1, Level var2, Player var3, InteractionHand var4, BlockHitResult var5) {
         return this.getBlock().useItemOn(var1, this.asState(), var2, var5.getBlockPos(), var3, var4, var5);
      }

      public InteractionResult useWithoutItem(Level var1, Player var2, BlockHitResult var3) {
         return this.getBlock().useWithoutItem(this.asState(), var1, var3.getBlockPos(), var2, var3);
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

      public BlockState updateShape(LevelReader var1, ScheduledTickAccess var2, BlockPos var3, Direction var4, BlockPos var5, BlockState var6, RandomSource var7) {
         return this.getBlock().updateShape(this.asState(), var1, var2, var3, var4, var5, var6, var7);
      }

      public boolean isPathfindable(PathComputationType var1) {
         return this.getBlock().isPathfindable(this.asState(), var1);
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

      public boolean is(TagKey<Block> var1, Predicate<BlockStateBase> var2) {
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

      public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2) {
         return this.getBlock().getCloneItemStack(var1, var2, this.asState());
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

      static {
         EMPTY_OCCLUSION_SHAPES = (VoxelShape[])Util.make(new VoxelShape[DIRECTIONS.length], (var0) -> {
            Arrays.fill(var0, Shapes.empty());
         });
         FULL_BLOCK_OCCLUSION_SHAPES = (VoxelShape[])Util.make(new VoxelShape[DIRECTIONS.length], (var0) -> {
            Arrays.fill(var0, Shapes.block());
         });
      }

      private static final class Cache {
         private static final Direction[] DIRECTIONS = Direction.values();
         private static final int SUPPORT_TYPE_COUNT = SupportType.values().length;
         protected final VoxelShape collisionShape;
         protected final boolean largeCollisionShape;
         private final boolean[] faceSturdy;
         protected final boolean isCollisionShapeFullBlock;

         Cache(BlockState var1) {
            super();
            Block var2 = var1.getBlock();
            this.collisionShape = var2.getCollisionShape(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
            if (!this.collisionShape.isEmpty() && var1.hasOffsetFunction()) {
               throw new IllegalStateException(String.format(Locale.ROOT, "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", BuiltInRegistries.BLOCK.getKey(var2)));
            } else {
               this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch((var1x) -> {
                  return this.collisionShape.min(var1x) < 0.0 || this.collisionShape.max(var1x) > 1.0;
               });
               this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];
               Direction[] var3 = DIRECTIONS;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Direction var6 = var3[var5];
                  SupportType[] var7 = SupportType.values();
                  int var8 = var7.length;

                  for(int var9 = 0; var9 < var8; ++var9) {
                     SupportType var10 = var7[var9];
                     this.faceSturdy[getFaceSupportIndex(var6, var10)] = var10.isSupporting(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, var6);
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

      // $FF: synthetic method
      private static OffsetType[] $values() {
         return new OffsetType[]{NONE, XZ, XYZ};
      }
   }
}
