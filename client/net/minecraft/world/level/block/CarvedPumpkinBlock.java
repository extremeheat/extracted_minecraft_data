package net.minecraft.world.level.block;

import java.util.Iterator;
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
   public static final DirectionProperty FACING;
   @Nullable
   private BlockPattern snowGolemBase;
   @Nullable
   private BlockPattern snowGolemFull;
   @Nullable
   private BlockPattern ironGolemBase;
   @Nullable
   private BlockPattern ironGolemFull;
   private static final Predicate<BlockState> PUMPKINS_PREDICATE;

   protected CarvedPumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

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
      int var4;
      Iterator var6;
      ServerPlayer var7;
      int var14;
      if (var3 != null) {
         for(var4 = 0; var4 < this.getOrCreateSnowGolemFull().getHeight(); ++var4) {
            BlockInWorld var5 = var3.getBlock(0, var4, 0);
            var1.setBlock(var5.getPos(), Blocks.AIR.defaultBlockState(), 2);
            var1.levelEvent(2001, var5.getPos(), Block.getId(var5.getState()));
         }

         SnowGolem var9 = (SnowGolem)EntityType.SNOW_GOLEM.create(var1);
         BlockPos var10 = var3.getBlock(0, 2, 0).getPos();
         var9.moveTo((double)var10.getX() + 0.5D, (double)var10.getY() + 0.05D, (double)var10.getZ() + 0.5D, 0.0F, 0.0F);
         var1.addFreshEntity(var9);
         var6 = var1.getEntitiesOfClass(ServerPlayer.class, var9.getBoundingBox().inflate(5.0D)).iterator();

         while(var6.hasNext()) {
            var7 = (ServerPlayer)var6.next();
            CriteriaTriggers.SUMMONED_ENTITY.trigger(var7, var9);
         }

         for(var14 = 0; var14 < this.getOrCreateSnowGolemFull().getHeight(); ++var14) {
            BlockInWorld var16 = var3.getBlock(0, var14, 0);
            var1.blockUpdated(var16.getPos(), Blocks.AIR);
         }
      } else {
         var3 = this.getOrCreateIronGolemFull().find(var1, var2);
         if (var3 != null) {
            for(var4 = 0; var4 < this.getOrCreateIronGolemFull().getWidth(); ++var4) {
               for(int var12 = 0; var12 < this.getOrCreateIronGolemFull().getHeight(); ++var12) {
                  BlockInWorld var15 = var3.getBlock(var4, var12, 0);
                  var1.setBlock(var15.getPos(), Blocks.AIR.defaultBlockState(), 2);
                  var1.levelEvent(2001, var15.getPos(), Block.getId(var15.getState()));
               }
            }

            BlockPos var11 = var3.getBlock(1, 2, 0).getPos();
            IronGolem var13 = (IronGolem)EntityType.IRON_GOLEM.create(var1);
            var13.setPlayerCreated(true);
            var13.moveTo((double)var11.getX() + 0.5D, (double)var11.getY() + 0.05D, (double)var11.getZ() + 0.5D, 0.0F, 0.0F);
            var1.addFreshEntity(var13);
            var6 = var1.getEntitiesOfClass(ServerPlayer.class, var13.getBoundingBox().inflate(5.0D)).iterator();

            while(var6.hasNext()) {
               var7 = (ServerPlayer)var6.next();
               CriteriaTriggers.SUMMONED_ENTITY.trigger(var7, var13);
            }

            for(var14 = 0; var14 < this.getOrCreateIronGolemFull().getWidth(); ++var14) {
               for(int var17 = 0; var17 < this.getOrCreateIronGolemFull().getHeight(); ++var17) {
                  BlockInWorld var8 = var3.getBlock(var14, var17, 0);
                  var1.blockUpdated(var8.getPos(), Blocks.AIR);
               }
            }
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
         this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return this.ironGolemBase;
   }

   private BlockPattern getOrCreateIronGolemFull() {
      if (this.ironGolemFull == null) {
         this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
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
