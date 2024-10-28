package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<CarvedPumpkinBlock> CODEC = simpleCodec(CarvedPumpkinBlock::new);
   public static final EnumProperty<Direction> FACING;
   @Nullable
   private BlockPattern snowGolemBase;
   @Nullable
   private BlockPattern snowGolemFull;
   @Nullable
   private BlockPattern ironGolemBase;
   @Nullable
   private BlockPattern ironGolemFull;
   private static final Predicate<BlockState> PUMPKINS_PREDICATE;

   public MapCodec<? extends CarvedPumpkinBlock> codec() {
      return CODEC;
   }

   protected CarvedPumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.trySpawnGolem(var2, var3);
      }
   }

   public boolean canSpawnGolem(LevelReader var1, BlockPos var2) {
      return this.getOrCreateSnowGolemBase().find(var1, var2) != null || this.getOrCreateIronGolemBase().find(var1, var2) != null;
   }

   private void trySpawnGolem(Level var1, BlockPos var2) {
      BlockPattern.BlockPatternMatch var3 = this.getOrCreateSnowGolemFull().find(var1, var2);
      if (var3 != null) {
         SnowGolem var4 = (SnowGolem)EntityType.SNOW_GOLEM.create(var1, EntitySpawnReason.TRIGGERED);
         if (var4 != null) {
            spawnGolemInWorld(var1, var3, var4, var3.getBlock(0, 2, 0).getPos());
         }
      } else {
         BlockPattern.BlockPatternMatch var6 = this.getOrCreateIronGolemFull().find(var1, var2);
         if (var6 != null) {
            IronGolem var5 = (IronGolem)EntityType.IRON_GOLEM.create(var1, EntitySpawnReason.TRIGGERED);
            if (var5 != null) {
               var5.setPlayerCreated(true);
               spawnGolemInWorld(var1, var6, var5, var6.getBlock(1, 2, 0).getPos());
            }
         }
      }

   }

   private static void spawnGolemInWorld(Level var0, BlockPattern.BlockPatternMatch var1, Entity var2, BlockPos var3) {
      clearPatternBlocks(var0, var1);
      var2.moveTo((double)var3.getX() + 0.5, (double)var3.getY() + 0.05, (double)var3.getZ() + 0.5, 0.0F, 0.0F);
      var0.addFreshEntity(var2);
      Iterator var4 = var0.getEntitiesOfClass(ServerPlayer.class, var2.getBoundingBox().inflate(5.0)).iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         CriteriaTriggers.SUMMONED_ENTITY.trigger(var5, var2);
      }

      updatePatternBlocks(var0, var1);
   }

   public static void clearPatternBlocks(Level var0, BlockPattern.BlockPatternMatch var1) {
      for(int var2 = 0; var2 < var1.getWidth(); ++var2) {
         for(int var3 = 0; var3 < var1.getHeight(); ++var3) {
            BlockInWorld var4 = var1.getBlock(var2, var3, 0);
            var0.setBlock(var4.getPos(), Blocks.AIR.defaultBlockState(), 2);
            var0.levelEvent(2001, var4.getPos(), Block.getId(var4.getState()));
         }
      }

   }

   public static void updatePatternBlocks(Level var0, BlockPattern.BlockPatternMatch var1) {
      for(int var2 = 0; var2 < var1.getWidth(); ++var2) {
         for(int var3 = 0; var3 < var1.getHeight(); ++var3) {
            BlockInWorld var4 = var1.getBlock(var2, var3, 0);
            var0.blockUpdated(var4.getPos(), Blocks.AIR);
         }
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   private BlockPattern getOrCreateSnowGolemBase() {
      if (this.snowGolemBase == null) {
         this.snowGolemBase = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowGolemBase;
   }

   private BlockPattern getOrCreateSnowGolemFull() {
      if (this.snowGolemFull == null) {
         this.snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowGolemFull;
   }

   private BlockPattern getOrCreateIronGolemBase() {
      if (this.ironGolemBase == null) {
         this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', (var0) -> {
            return var0.getState().isAir();
         }).build();
      }

      return this.ironGolemBase;
   }

   private BlockPattern getOrCreateIronGolemFull() {
      if (this.ironGolemFull == null) {
         this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', (var0) -> {
            return var0.getState().isAir();
         }).build();
      }

      return this.ironGolemFull;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      PUMPKINS_PREDICATE = (var0) -> {
         return var0 != null && (var0.is(Blocks.CARVED_PUMPKIN) || var0.is(Blocks.JACK_O_LANTERN));
      };
   }
}
