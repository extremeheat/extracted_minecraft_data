package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockState extends AbstractStateHolder<Block, BlockState> implements StateHolder<BlockState> {
   @Nullable
   private BlockState.Cache cache;
   private final int lightEmission;
   private final boolean useShapeForLightOcclusion;

   public BlockState(Block var1, ImmutableMap<Property<?>, Comparable<?>> var2) {
      super(var1, var2);
      this.lightEmission = var1.getLightEmission(this);
      this.useShapeForLightOcclusion = var1.useShapeForLightOcclusion(this);
   }

   public void initCache() {
      if (!this.getBlock().hasDynamicShape()) {
         this.cache = new BlockState.Cache(this);
      }

   }

   public Block getBlock() {
      return (Block)this.owner;
   }

   public Material getMaterial() {
      return this.getBlock().getMaterial(this);
   }

   public boolean isValidSpawn(BlockGetter var1, BlockPos var2, EntityType<?> var3) {
      return this.getBlock().isValidSpawn(this, var1, var2, var3);
   }

   public boolean propagatesSkylightDown(BlockGetter var1, BlockPos var2) {
      return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this, var1, var2);
   }

   public int getLightBlock(BlockGetter var1, BlockPos var2) {
      return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this, var1, var2);
   }

   public VoxelShape getFaceOcclusionShape(BlockGetter var1, BlockPos var2, Direction var3) {
      return this.cache != null && this.cache.occlusionShapes != null ? this.cache.occlusionShapes[var3.ordinal()] : Shapes.getFaceShape(this.getOcclusionShape(var1, var2), var3);
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
      return this.getBlock().isAir(this);
   }

   public MaterialColor getMapColor(BlockGetter var1, BlockPos var2) {
      return this.getBlock().getMapColor(this, var1, var2);
   }

   public BlockState rotate(Rotation var1) {
      return this.getBlock().rotate(this, var1);
   }

   public BlockState mirror(Mirror var1) {
      return this.getBlock().mirror(this, var1);
   }

   public boolean hasCustomBreakingProgress() {
      return this.getBlock().hasCustomBreakingProgress(this);
   }

   public RenderShape getRenderShape() {
      return this.getBlock().getRenderShape(this);
   }

   public int getLightColor(BlockAndBiomeGetter var1, BlockPos var2) {
      return this.getBlock().getLightColor(this, var1, var2);
   }

   public float getShadeBrightness(BlockGetter var1, BlockPos var2) {
      return this.getBlock().getShadeBrightness(this, var1, var2);
   }

   public boolean isRedstoneConductor(BlockGetter var1, BlockPos var2) {
      return this.getBlock().isRedstoneConductor(this, var1, var2);
   }

   public boolean isSignalSource() {
      return this.getBlock().isSignalSource(this);
   }

   public int getSignal(BlockGetter var1, BlockPos var2, Direction var3) {
      return this.getBlock().getSignal(this, var1, var2, var3);
   }

   public boolean hasAnalogOutputSignal() {
      return this.getBlock().hasAnalogOutputSignal(this);
   }

   public int getAnalogOutputSignal(Level var1, BlockPos var2) {
      return this.getBlock().getAnalogOutputSignal(this, var1, var2);
   }

   public float getDestroySpeed(BlockGetter var1, BlockPos var2) {
      return this.getBlock().getDestroySpeed(this, var1, var2);
   }

   public float getDestroyProgress(Player var1, BlockGetter var2, BlockPos var3) {
      return this.getBlock().getDestroyProgress(this, var1, var2, var3);
   }

   public int getDirectSignal(BlockGetter var1, BlockPos var2, Direction var3) {
      return this.getBlock().getDirectSignal(this, var1, var2, var3);
   }

   public PushReaction getPistonPushReaction() {
      return this.getBlock().getPistonPushReaction(this);
   }

   public boolean isSolidRender(BlockGetter var1, BlockPos var2) {
      return this.cache != null ? this.cache.solidRender : this.getBlock().isSolidRender(this, var1, var2);
   }

   public boolean canOcclude() {
      return this.cache != null ? this.cache.canOcclude : this.getBlock().canOcclude(this);
   }

   public boolean skipRendering(BlockState var1, Direction var2) {
      return this.getBlock().skipRendering(this, var1, var2);
   }

   public VoxelShape getShape(BlockGetter var1, BlockPos var2) {
      return this.getShape(var1, var2, CollisionContext.empty());
   }

   public VoxelShape getShape(BlockGetter var1, BlockPos var2, CollisionContext var3) {
      return this.getBlock().getShape(this, var1, var2, var3);
   }

   public VoxelShape getCollisionShape(BlockGetter var1, BlockPos var2) {
      return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(var1, var2, CollisionContext.empty());
   }

   public VoxelShape getCollisionShape(BlockGetter var1, BlockPos var2, CollisionContext var3) {
      return this.getBlock().getCollisionShape(this, var1, var2, var3);
   }

   public VoxelShape getOcclusionShape(BlockGetter var1, BlockPos var2) {
      return this.getBlock().getOcclusionShape(this, var1, var2);
   }

   public VoxelShape getInteractionShape(BlockGetter var1, BlockPos var2) {
      return this.getBlock().getInteractionShape(this, var1, var2);
   }

   public final boolean entityCanStandOn(BlockGetter var1, BlockPos var2, Entity var3) {
      return Block.isFaceFull(this.getCollisionShape(var1, var2, CollisionContext.of(var3)), Direction.UP);
   }

   public Vec3 getOffset(BlockGetter var1, BlockPos var2) {
      return this.getBlock().getOffset(this, var1, var2);
   }

   public boolean triggerEvent(Level var1, BlockPos var2, int var3, int var4) {
      return this.getBlock().triggerEvent(this, var1, var2, var3, var4);
   }

   public void neighborChanged(Level var1, BlockPos var2, Block var3, BlockPos var4, boolean var5) {
      this.getBlock().neighborChanged(this, var1, var2, var3, var4, var5);
   }

   public void updateNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3) {
      this.getBlock().updateNeighbourShapes(this, var1, var2, var3);
   }

   public void updateIndirectNeighbourShapes(LevelAccessor var1, BlockPos var2, int var3) {
      this.getBlock().updateIndirectNeighbourShapes(this, var1, var2, var3);
   }

   public void onPlace(Level var1, BlockPos var2, BlockState var3, boolean var4) {
      this.getBlock().onPlace(this, var1, var2, var3, var4);
   }

   public void onRemove(Level var1, BlockPos var2, BlockState var3, boolean var4) {
      this.getBlock().onRemove(this, var1, var2, var3, var4);
   }

   public void tick(Level var1, BlockPos var2, Random var3) {
      this.getBlock().tick(this, var1, var2, var3);
   }

   public void randomTick(Level var1, BlockPos var2, Random var3) {
      this.getBlock().randomTick(this, var1, var2, var3);
   }

   public void entityInside(Level var1, BlockPos var2, Entity var3) {
      this.getBlock().entityInside(this, var1, var2, var3);
   }

   public void spawnAfterBreak(Level var1, BlockPos var2, ItemStack var3) {
      this.getBlock().spawnAfterBreak(this, var1, var2, var3);
   }

   public List<ItemStack> getDrops(LootContext.Builder var1) {
      return this.getBlock().getDrops(this, var1);
   }

   public boolean use(Level var1, Player var2, InteractionHand var3, BlockHitResult var4) {
      return this.getBlock().use(this, var1, var4.getBlockPos(), var2, var3, var4);
   }

   public void attack(Level var1, BlockPos var2, Player var3) {
      this.getBlock().attack(this, var1, var2, var3);
   }

   public boolean isViewBlocking(BlockGetter var1, BlockPos var2) {
      return this.getBlock().isViewBlocking(this, var1, var2);
   }

   public BlockState updateShape(Direction var1, BlockState var2, LevelAccessor var3, BlockPos var4, BlockPos var5) {
      return this.getBlock().updateShape(this, var1, var2, var3, var4, var5);
   }

   public boolean isPathfindable(BlockGetter var1, BlockPos var2, PathComputationType var3) {
      return this.getBlock().isPathfindable(this, var1, var2, var3);
   }

   public boolean canBeReplaced(BlockPlaceContext var1) {
      return this.getBlock().canBeReplaced(this, var1);
   }

   public boolean canSurvive(LevelReader var1, BlockPos var2) {
      return this.getBlock().canSurvive(this, var1, var2);
   }

   public boolean hasPostProcess(BlockGetter var1, BlockPos var2) {
      return this.getBlock().hasPostProcess(this, var1, var2);
   }

   @Nullable
   public MenuProvider getMenuProvider(Level var1, BlockPos var2) {
      return this.getBlock().getMenuProvider(this, var1, var2);
   }

   public boolean is(Tag<Block> var1) {
      return this.getBlock().is(var1);
   }

   public FluidState getFluidState() {
      return this.getBlock().getFluidState(this);
   }

   public boolean isRandomlyTicking() {
      return this.getBlock().isRandomlyTicking(this);
   }

   public long getSeed(BlockPos var1) {
      return this.getBlock().getSeed(this, var1);
   }

   public SoundType getSoundType() {
      return this.getBlock().getSoundType(this);
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Entity var4) {
      this.getBlock().onProjectileHit(var1, var2, var3, var4);
   }

   public boolean isFaceSturdy(BlockGetter var1, BlockPos var2, Direction var3) {
      return this.cache != null ? this.cache.isFaceSturdy[var3.ordinal()] : Block.isFaceSturdy(this, var1, var2, var3);
   }

   public boolean isCollisionShapeFullBlock(BlockGetter var1, BlockPos var2) {
      return this.cache != null ? this.cache.isCollisionShapeFullBlock : Block.isShapeFullBlock(this.getCollisionShape(var1, var2));
   }

   public static <T> Dynamic<T> serialize(DynamicOps<T> var0, BlockState var1) {
      ImmutableMap var2 = var1.getValues();
      Object var3;
      if (var2.isEmpty()) {
         var3 = var0.createMap(ImmutableMap.of(var0.createString("Name"), var0.createString(Registry.BLOCK.getKey(var1.getBlock()).toString())));
      } else {
         var3 = var0.createMap(ImmutableMap.of(var0.createString("Name"), var0.createString(Registry.BLOCK.getKey(var1.getBlock()).toString()), var0.createString("Properties"), var0.createMap((Map)var2.entrySet().stream().map((var1x) -> {
            return Pair.of(var0.createString(((Property)var1x.getKey()).getName()), var0.createString(StateHolder.getName((Property)var1x.getKey(), (Comparable)var1x.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic(var0, var3);
   }

   public static <T> BlockState deserialize(Dynamic<T> var0) {
      DefaultedRegistry var10000 = Registry.BLOCK;
      Optional var10003 = var0.getElement("Name");
      DynamicOps var10004 = var0.getOps();
      var10004.getClass();
      Block var1 = (Block)var10000.get(new ResourceLocation((String)var10003.flatMap(var10004::getStringValue).orElse("minecraft:air")));
      Map var2 = var0.get("Properties").asMap((var0x) -> {
         return var0x.asString("");
      }, (var0x) -> {
         return var0x.asString("");
      });
      BlockState var3 = var1.defaultBlockState();
      StateDefinition var4 = var1.getStateDefinition();
      Iterator var5 = var2.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         String var7 = (String)var6.getKey();
         Property var8 = var4.getProperty(var7);
         if (var8 != null) {
            var3 = (BlockState)StateHolder.setValueHelper(var3, var8, var7, var0.toString(), (String)var6.getValue());
         }
      }

      return var3;
   }

   static final class Cache {
      private static final Direction[] DIRECTIONS = Direction.values();
      private final boolean canOcclude;
      private final boolean solidRender;
      private final boolean propagatesSkylightDown;
      private final int lightBlock;
      private final VoxelShape[] occlusionShapes;
      private final VoxelShape collisionShape;
      private final boolean largeCollisionShape;
      private final boolean[] isFaceSturdy;
      private final boolean isCollisionShapeFullBlock;

      private Cache(BlockState var1) {
         super();
         Block var2 = var1.getBlock();
         this.canOcclude = var2.canOcclude(var1);
         this.solidRender = var2.isSolidRender(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         this.propagatesSkylightDown = var2.propagatesSkylightDown(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         this.lightBlock = var2.getLightBlock(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         int var5;
         if (!var1.canOcclude()) {
            this.occlusionShapes = null;
         } else {
            this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
            VoxelShape var3 = var2.getOcclusionShape(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            Direction[] var4 = DIRECTIONS;
            var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Direction var7 = var4[var6];
               this.occlusionShapes[var7.ordinal()] = Shapes.getFaceShape(var3, var7);
            }
         }

         this.collisionShape = var2.getCollisionShape(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
         this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch((var1x) -> {
            return this.collisionShape.min(var1x) < 0.0D || this.collisionShape.max(var1x) > 1.0D;
         });
         this.isFaceSturdy = new boolean[6];
         Direction[] var8 = DIRECTIONS;
         int var9 = var8.length;

         for(var5 = 0; var5 < var9; ++var5) {
            Direction var10 = var8[var5];
            this.isFaceSturdy[var10.ordinal()] = Block.isFaceSturdy(var1, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, var10);
         }

         this.isCollisionShapeFullBlock = Block.isShapeFullBlock(var1.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
      }

      // $FF: synthetic method
      Cache(BlockState var1, Object var2) {
         this(var1);
      }
   }
}
