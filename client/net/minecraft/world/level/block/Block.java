package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
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
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements ItemLike {
   protected static final Logger LOGGER = LogManager.getLogger();
   public static final IdMapper<BlockState> BLOCK_STATE_REGISTRY = new IdMapper();
   private static final Direction[] UPDATE_SHAPE_ORDER;
   private static final LoadingCache<VoxelShape, Boolean> SHAPE_FULL_BLOCK_CACHE;
   private static final VoxelShape RIGID_SUPPORT_SHAPE;
   private static final VoxelShape CENTER_SUPPORT_SHAPE;
   protected final int lightEmission;
   protected final float destroySpeed;
   protected final float explosionResistance;
   protected final boolean isTicking;
   protected final SoundType soundType;
   protected final Material material;
   protected final MaterialColor materialColor;
   private final float friction;
   protected final StateDefinition<Block, BlockState> stateDefinition;
   private BlockState defaultBlockState;
   protected final boolean hasCollision;
   private final boolean dynamicShape;
   @Nullable
   private ResourceLocation drops;
   @Nullable
   private String descriptionId;
   @Nullable
   private Item item;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE;

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
         var7.teleportTo(var7.x, var7.y + 1.0D + var8, var7.z);
      }

      return var1;
   }

   public static VoxelShape box(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Shapes.box(var0 / 16.0D, var2 / 16.0D, var4 / 16.0D, var6 / 16.0D, var8 / 16.0D, var10 / 16.0D);
   }

   @Deprecated
   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType<?> var4) {
      return var1.isFaceSturdy(var2, var3, Direction.UP) && this.lightEmission < 14;
   }

   @Deprecated
   public boolean isAir(BlockState var1) {
      return false;
   }

   @Deprecated
   public int getLightEmission(BlockState var1) {
      return this.lightEmission;
   }

   @Deprecated
   public Material getMaterial(BlockState var1) {
      return this.material;
   }

   @Deprecated
   public MaterialColor getMapColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.materialColor;
   }

   @Deprecated
   public void updateNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         Direction[] var7 = UPDATE_SHAPE_ORDER;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            var5.set((Vec3i)var3).move(var10);
            BlockState var11 = var2.getBlockState(var5);
            BlockState var12 = var11.updateShape(var10.getOpposite(), var1, var2, var5, var3);
            updateOrDestroy(var11, var12, var2, var5, var4);
         }
      } catch (Throwable var20) {
         var6 = var20;
         throw var20;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var19) {
                  var6.addSuppressed(var19);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   public boolean is(Tag<Block> var1) {
      return var1.contains(this);
   }

   public static BlockState updateFromNeighbourShapes(BlockState var0, LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var0;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      Direction[] var5 = UPDATE_SHAPE_ORDER;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         var4.set((Vec3i)var2).move(var8);
         var3 = var3.updateShape(var8, var1.getBlockState(var4), var1, var2, var4);
      }

      return var3;
   }

   public static void updateOrDestroy(BlockState var0, BlockState var1, LevelAccessor var2, BlockPos var3, int var4) {
      if (var1 != var0) {
         if (var1.isAir()) {
            if (!var2.isClientSide()) {
               var2.destroyBlock(var3, (var4 & 32) == 0);
            }
         } else {
            var2.setBlock(var3, var1, var4 & -33);
         }
      }

   }

   @Deprecated
   public void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4) {
   }

   @Deprecated
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var1;
   }

   @Deprecated
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1;
   }

   @Deprecated
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1;
   }

   public Block(Block.Properties var1) {
      super();
      StateDefinition.Builder var2 = new StateDefinition.Builder(this);
      this.createBlockStateDefinition(var2);
      this.material = var1.material;
      this.materialColor = var1.materialColor;
      this.hasCollision = var1.hasCollision;
      this.soundType = var1.soundType;
      this.lightEmission = var1.lightEmission;
      this.explosionResistance = var1.explosionResistance;
      this.destroySpeed = var1.destroyTime;
      this.isTicking = var1.isTicking;
      this.friction = var1.friction;
      this.dynamicShape = var1.dynamicShape;
      this.drops = var1.drops;
      this.stateDefinition = var2.create(BlockState::new);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public static boolean isExceptionForConnection(Block var0) {
      return var0 instanceof LeavesBlock || var0 == Blocks.BARRIER || var0 == Blocks.CARVED_PUMPKIN || var0 == Blocks.JACK_O_LANTERN || var0 == Blocks.MELON || var0 == Blocks.PUMPKIN;
   }

   @Deprecated
   public boolean isRedstoneConductor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getMaterial().isSolidBlocking() && var1.isCollisionShapeFullBlock(var2, var3) && !var1.isSignalSource();
   }

   @Deprecated
   public boolean isViewBlocking(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.material.blocksMotion() && var1.isCollisionShapeFullBlock(var2, var3);
   }

   @Deprecated
   public boolean hasCustomBreakingProgress(BlockState var1) {
      return false;
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
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Deprecated
   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return this.material.isReplaceable() && (var2.getItemInHand().isEmpty() || var2.getItemInHand().getItem() != this.asItem());
   }

   @Deprecated
   public float getDestroySpeed(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.destroySpeed;
   }

   public boolean isRandomlyTicking(BlockState var1) {
      return this.isTicking;
   }

   public boolean isEntityBlock() {
      return this instanceof EntityBlock;
   }

   @Deprecated
   public boolean hasPostProcess(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   @Deprecated
   public int getLightColor(BlockState var1, BlockAndBiomeGetter var2, BlockPos var3) {
      return var2.getLightColor(var3, var1.getLightEmission());
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
            if (var7.size() == 200) {
               var7.removeLastByte();
            }

            var7.putAndMoveToFirst(var6, (byte)(var11 ? 1 : 0));
            return var11;
         }
      } else {
         return true;
      }
   }

   @Deprecated
   public boolean canOcclude(BlockState var1) {
      return this.hasCollision && this.getRenderLayer() == BlockLayer.SOLID;
   }

   @Deprecated
   public boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return false;
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
   public VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getShape(var2, var3);
   }

   @Deprecated
   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   public static boolean canSupportRigidBlock(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return !var2.is(BlockTags.LEAVES) && !Shapes.joinIsNotEmpty(var2.getCollisionShape(var0, var1).getFaceShape(Direction.UP), RIGID_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
   }

   public static boolean canSupportCenter(LevelReader var0, BlockPos var1, Direction var2) {
      BlockState var3 = var0.getBlockState(var1);
      return !var3.is(BlockTags.LEAVES) && !Shapes.joinIsNotEmpty(var3.getCollisionShape(var0, var1).getFaceShape(var2), CENTER_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
   }

   public static boolean isFaceSturdy(BlockState var0, BlockGetter var1, BlockPos var2, Direction var3) {
      return !var0.is(BlockTags.LEAVES) && isFaceFull(var0.getCollisionShape(var1, var2), var3);
   }

   public static boolean isFaceFull(VoxelShape var0, Direction var1) {
      VoxelShape var2 = var0.getFaceShape(var1);
      return isShapeFullBlock(var2);
   }

   public static boolean isShapeFullBlock(VoxelShape var0) {
      return (Boolean)SHAPE_FULL_BLOCK_CACHE.getUnchecked(var0);
   }

   @Deprecated
   public final boolean isSolidRender(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.canOcclude() ? isShapeFullBlock(var1.getOcclusionShape(var2, var3)) : false;
   }

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return !isShapeFullBlock(var1.getShape(var2, var3)) && var1.getFluidState().isEmpty();
   }

   @Deprecated
   public int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      if (var1.isSolidRender(var2, var3)) {
         return var2.getMaxLightLevel();
      } else {
         return var1.propagatesSkylightDown(var2, var3) ? 0 : 1;
      }
   }

   @Deprecated
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return false;
   }

   @Deprecated
   public void randomTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      this.tick(var1, var2, var3, var4);
   }

   @Deprecated
   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
   }

   public void destroy(LevelAccessor var1, BlockPos var2, BlockState var3) {
   }

   @Deprecated
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      DebugPackets.sendNeighborsUpdatePacket(var2, var3);
   }

   public int getTickDelay(LevelReader var1) {
      return 10;
   }

   @Nullable
   @Deprecated
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return null;
   }

   @Deprecated
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
   }

   @Deprecated
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (this.isEntityBlock() && var1.getBlock() != var4.getBlock()) {
         var2.removeBlockEntity(var3);
      }

   }

   @Deprecated
   public float getDestroyProgress(BlockState var1, Player var2, BlockGetter var3, BlockPos var4) {
      float var5 = var1.getDestroySpeed(var3, var4);
      if (var5 == -1.0F) {
         return 0.0F;
      } else {
         int var6 = var2.canDestroy(var1) ? 30 : 100;
         return var2.getDestroySpeed(var1) / var5 / (float)var6;
      }
   }

   @Deprecated
   public void spawnAfterBreak(BlockState var1, Level var2, BlockPos var3, ItemStack var4) {
   }

   public ResourceLocation getLootTable() {
      if (this.drops == null) {
         ResourceLocation var1 = Registry.BLOCK.getKey(this);
         this.drops = new ResourceLocation(var1.getNamespace(), "blocks/" + var1.getPath());
      }

      return this.drops;
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

   public static List<ItemStack> getDrops(BlockState var0, ServerLevel var1, BlockPos var2, @Nullable BlockEntity var3) {
      LootContext.Builder var4 = (new LootContext.Builder(var1)).withRandom(var1.random).withParameter(LootContextParams.BLOCK_POS, var2).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var3);
      return var0.getDrops(var4);
   }

   public static List<ItemStack> getDrops(BlockState var0, ServerLevel var1, BlockPos var2, @Nullable BlockEntity var3, Entity var4, ItemStack var5) {
      LootContext.Builder var6 = (new LootContext.Builder(var1)).withRandom(var1.random).withParameter(LootContextParams.BLOCK_POS, var2).withParameter(LootContextParams.TOOL, var5).withParameter(LootContextParams.THIS_ENTITY, var4).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var3);
      return var0.getDrops(var6);
   }

   public static void dropResources(BlockState var0, LootContext.Builder var1) {
      ServerLevel var2 = var1.getLevel();
      BlockPos var3 = (BlockPos)var1.getParameter(LootContextParams.BLOCK_POS);
      var0.getDrops(var1).forEach((var2x) -> {
         popResource(var2, var3, var2x);
      });
      var0.spawnAfterBreak(var2, var3, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, (BlockEntity)null).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
      }

      var0.spawnAfterBreak(var1, var2, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2, @Nullable BlockEntity var3) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, var3).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
      }

      var0.spawnAfterBreak(var1, var2, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState var0, Level var1, BlockPos var2, @Nullable BlockEntity var3, Entity var4, ItemStack var5) {
      if (var1 instanceof ServerLevel) {
         getDrops(var0, (ServerLevel)var1, var2, var3, var4, var5).forEach((var2x) -> {
            popResource(var1, var2, var2x);
         });
      }

      var0.spawnAfterBreak(var1, var2, var5);
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

   protected void popExperience(Level var1, BlockPos var2, int var3) {
      if (!var1.isClientSide && var1.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
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

   public BlockLayer getRenderLayer() {
      return BlockLayer.SOLID;
   }

   @Deprecated
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return true;
   }

   @Deprecated
   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      return false;
   }

   public void stepOn(Level var1, BlockPos var2, Entity var3) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState();
   }

   @Deprecated
   public void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
   }

   @Deprecated
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 0;
   }

   @Deprecated
   public boolean isSignalSource(BlockState var1) {
      return false;
   }

   @Deprecated
   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
   }

   @Deprecated
   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return 0;
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

   public Component getName() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public String getDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
      }

      return this.descriptionId;
   }

   @Deprecated
   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      return false;
   }

   @Deprecated
   public PushReaction getPistonPushReaction(BlockState var1) {
      return this.material.getPushReaction();
   }

   @Deprecated
   public float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isCollisionShapeFullBlock(var2, var3) ? 0.2F : 1.0F;
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

   @Deprecated
   public FluidState getFluidState(BlockState var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public float getFriction() {
      return this.friction;
   }

   @Deprecated
   public long getSeed(BlockState var1, BlockPos var2) {
      return Mth.getSeed(var2);
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Entity var4) {
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      var1.levelEvent(var4, 2001, var2, getId(var3));
   }

   public void handleRain(Level var1, BlockPos var2) {
   }

   public boolean dropFromExplosion(Explosion var1) {
      return true;
   }

   @Deprecated
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return false;
   }

   @Deprecated
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return 0;
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

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.NONE;
   }

   @Deprecated
   public Vec3 getOffset(BlockState var1, BlockGetter var2, BlockPos var3) {
      Block.OffsetType var4 = this.getOffsetType();
      if (var4 == Block.OffsetType.NONE) {
         return Vec3.ZERO;
      } else {
         long var5 = Mth.getSeed(var3.getX(), 0, var3.getZ());
         return new Vec3(((double)((float)(var5 & 15L) / 15.0F) - 0.5D) * 0.5D, var4 == Block.OffsetType.XYZ ? ((double)((float)(var5 >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(var5 >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
      }
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

   public static boolean equalsStone(Block var0) {
      return var0 == Blocks.STONE || var0 == Blocks.GRANITE || var0 == Blocks.DIORITE || var0 == Blocks.ANDESITE;
   }

   public static boolean equalsDirt(Block var0) {
      return var0 == Blocks.DIRT || var0 == Blocks.COARSE_DIRT || var0 == Blocks.PODZOL;
   }

   static {
      UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
      SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader<VoxelShape, Boolean>() {
         public Boolean load(VoxelShape var1) {
            return !Shapes.joinIsNotEmpty(Shapes.block(), var1, BooleanOp.NOT_SAME);
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((VoxelShape)var1);
         }
      });
      RIGID_SUPPORT_SHAPE = Shapes.join(Shapes.block(), box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
      CENTER_SUPPORT_SHAPE = box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);
      OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200) {
            protected void rehash(int var1) {
            }
         };
         var0.defaultReturnValue((byte)127);
         return var0;
      });
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;

      private OffsetType() {
      }
   }

   public static class Properties {
      private Material material;
      private MaterialColor materialColor;
      private boolean hasCollision = true;
      private SoundType soundType;
      private int lightEmission;
      private float explosionResistance;
      private float destroyTime;
      private boolean isTicking;
      private float friction;
      private ResourceLocation drops;
      private boolean dynamicShape;

      private Properties(Material var1, MaterialColor var2) {
         super();
         this.soundType = SoundType.STONE;
         this.friction = 0.6F;
         this.material = var1;
         this.materialColor = var2;
      }

      public static Block.Properties of(Material var0) {
         return of(var0, var0.getColor());
      }

      public static Block.Properties of(Material var0, DyeColor var1) {
         return of(var0, var1.getMaterialColor());
      }

      public static Block.Properties of(Material var0, MaterialColor var1) {
         return new Block.Properties(var0, var1);
      }

      public static Block.Properties copy(Block var0) {
         Block.Properties var1 = new Block.Properties(var0.material, var0.materialColor);
         var1.material = var0.material;
         var1.destroyTime = var0.destroySpeed;
         var1.explosionResistance = var0.explosionResistance;
         var1.hasCollision = var0.hasCollision;
         var1.isTicking = var0.isTicking;
         var1.lightEmission = var0.lightEmission;
         var1.materialColor = var0.materialColor;
         var1.soundType = var0.soundType;
         var1.friction = var0.getFriction();
         var1.dynamicShape = var0.dynamicShape;
         return var1;
      }

      public Block.Properties noCollission() {
         this.hasCollision = false;
         return this;
      }

      public Block.Properties friction(float var1) {
         this.friction = var1;
         return this;
      }

      protected Block.Properties sound(SoundType var1) {
         this.soundType = var1;
         return this;
      }

      protected Block.Properties lightLevel(int var1) {
         this.lightEmission = var1;
         return this;
      }

      public Block.Properties strength(float var1, float var2) {
         this.destroyTime = var1;
         this.explosionResistance = Math.max(0.0F, var2);
         return this;
      }

      protected Block.Properties instabreak() {
         return this.strength(0.0F);
      }

      protected Block.Properties strength(float var1) {
         this.strength(var1, var1);
         return this;
      }

      protected Block.Properties randomTicks() {
         this.isTicking = true;
         return this;
      }

      protected Block.Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      protected Block.Properties noDrops() {
         this.drops = BuiltInLootTables.EMPTY;
         return this;
      }

      public Block.Properties dropsLike(Block var1) {
         this.drops = var1.getLootTable();
         return this;
      }
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
