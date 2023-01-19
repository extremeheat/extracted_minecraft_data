package net.minecraft.world.level.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock implements Wearable {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   @Nullable
   private BlockPattern snowGolemBase;
   @Nullable
   private BlockPattern snowGolemFull;
   @Nullable
   private BlockPattern ironGolemBase;
   @Nullable
   private BlockPattern ironGolemFull;
   private static final Predicate<BlockState> PUMPKINS_PREDICATE = var0 -> var0 != null && (var0.is(Blocks.CARVED_PUMPKIN) || var0.is(Blocks.JACK_O_LANTERN));

   protected CarvedPumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
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
         for(int var4 = 0; var4 < this.getOrCreateSnowGolemFull().getHeight(); ++var4) {
            BlockInWorld var5 = var3.getBlock(0, var4, 0);
            var1.setBlock(var5.getPos(), Blocks.AIR.defaultBlockState(), 2);
            var1.levelEvent(2001, var5.getPos(), Block.getId(var5.getState()));
         }

         SnowGolem var10 = EntityType.SNOW_GOLEM.create(var1);
         BlockPos var13 = var3.getBlock(0, 2, 0).getPos();
         var10.moveTo((double)var13.getX() + 0.5, (double)var13.getY() + 0.05, (double)var13.getZ() + 0.5, 0.0F, 0.0F);
         var1.addFreshEntity(var10);

         for(ServerPlayer var7 : var1.getEntitiesOfClass(ServerPlayer.class, var10.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(var7, var10);
         }

         for(int var16 = 0; var16 < this.getOrCreateSnowGolemFull().getHeight(); ++var16) {
            BlockInWorld var20 = var3.getBlock(0, var16, 0);
            var1.blockUpdated(var20.getPos(), Blocks.AIR);
         }
      } else {
         var3 = this.getOrCreateIronGolemFull().find(var1, var2);
         if (var3 != null) {
            for(int var11 = 0; var11 < this.getOrCreateIronGolemFull().getWidth(); ++var11) {
               for(int var14 = 0; var14 < this.getOrCreateIronGolemFull().getHeight(); ++var14) {
                  BlockInWorld var17 = var3.getBlock(var11, var14, 0);
                  var1.setBlock(var17.getPos(), Blocks.AIR.defaultBlockState(), 2);
                  var1.levelEvent(2001, var17.getPos(), Block.getId(var17.getState()));
               }
            }

            BlockPos var12 = var3.getBlock(1, 2, 0).getPos();
            IronGolem var15 = EntityType.IRON_GOLEM.create(var1);
            var15.setPlayerCreated(true);
            var15.moveTo((double)var12.getX() + 0.5, (double)var12.getY() + 0.05, (double)var12.getZ() + 0.5, 0.0F, 0.0F);
            var1.addFreshEntity(var15);

            for(ServerPlayer var21 : var1.getEntitiesOfClass(ServerPlayer.class, var15.getBoundingBox().inflate(5.0))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(var21, var15);
            }

            for(int var19 = 0; var19 < this.getOrCreateIronGolemFull().getWidth(); ++var19) {
               for(int var22 = 0; var22 < this.getOrCreateIronGolemFull().getHeight(); ++var22) {
                  BlockInWorld var8 = var3.getBlock(var19, var22, 0);
                  var1.blockUpdated(var8.getPos(), Blocks.AIR);
               }
            }
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   private BlockPattern getOrCreateSnowGolemBase() {
      if (this.snowGolemBase == null) {
         this.snowGolemBase = BlockPatternBuilder.start()
            .aisle(" ", "#", "#")
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
            .build();
      }

      return this.snowGolemBase;
   }

   private BlockPattern getOrCreateSnowGolemFull() {
      if (this.snowGolemFull == null) {
         this.snowGolemFull = BlockPatternBuilder.start()
            .aisle("^", "#", "#")
            .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
            .build();
      }

      return this.snowGolemFull;
   }

   private BlockPattern getOrCreateIronGolemBase() {
      if (this.ironGolemBase == null) {
         this.ironGolemBase = BlockPatternBuilder.start()
            .aisle("~ ~", "###", "~#~")
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR)))
            .build();
      }

      return this.ironGolemBase;
   }

   private BlockPattern getOrCreateIronGolemFull() {
      if (this.ironGolemFull == null) {
         this.ironGolemFull = BlockPatternBuilder.start()
            .aisle("~^~", "###", "~#~")
            .where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE))
            .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
            .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR)))
            .build();
      }

      return this.ironGolemFull;
   }
}
