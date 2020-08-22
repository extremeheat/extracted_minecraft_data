package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BeehiveDecorator extends TreeDecorator {
   private final float probability;

   public BeehiveDecorator(float var1) {
      super(TreeDecoratorType.BEEHIVE);
      this.probability = var1;
   }

   public BeehiveDecorator(Dynamic var1) {
      this(var1.get("probability").asFloat(0.0F));
   }

   public void place(LevelAccessor var1, Random var2, List var3, List var4, Set var5, BoundingBox var6) {
      if (var2.nextFloat() < this.probability) {
         Direction var7 = BeehiveBlock.SPAWN_DIRECTIONS[var2.nextInt(BeehiveBlock.SPAWN_DIRECTIONS.length)];
         int var8 = !var4.isEmpty() ? Math.max(((BlockPos)var4.get(0)).getY() - 1, ((BlockPos)var3.get(0)).getY()) : Math.min(((BlockPos)var3.get(0)).getY() + 1 + var2.nextInt(3), ((BlockPos)var3.get(var3.size() - 1)).getY());
         List var9 = (List)var3.stream().filter((var1x) -> {
            return var1x.getY() == var8;
         }).collect(Collectors.toList());
         BlockPos var10 = (BlockPos)var9.get(var2.nextInt(var9.size()));
         BlockPos var11 = var10.relative(var7);
         if (AbstractTreeFeature.isAir(var1, var11) && AbstractTreeFeature.isAir(var1, var11.relative(Direction.SOUTH))) {
            BlockState var12 = (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, Direction.SOUTH);
            this.setBlock(var1, var11, var12, var5, var6);
            BlockEntity var13 = var1.getBlockEntity(var11);
            if (var13 instanceof BeehiveBlockEntity) {
               BeehiveBlockEntity var14 = (BeehiveBlockEntity)var13;
               int var15 = 2 + var2.nextInt(2);

               for(int var16 = 0; var16 < var15; ++var16) {
                  Bee var17 = new Bee(EntityType.BEE, var1.getLevel());
                  var14.addOccupantWithPresetTicks(var17, false, var2.nextInt(599));
               }
            }

         }
      }
   }

   public Object serialize(DynamicOps var1) {
      return (new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.TREE_DECORATOR_TYPES.getKey(this.type).toString()), var1.createString("probability"), var1.createFloat(this.probability))))).getValue();
   }
}
