package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AlterGroundDecorator extends TreeDecorator {
   private final BlockStateProvider provider;

   public AlterGroundDecorator(BlockStateProvider var1) {
      super(TreeDecoratorType.ALTER_GROUND);
      this.provider = var1;
   }

   public AlterGroundDecorator(Dynamic var1) {
      this(((BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var1.get("provider").get("type").asString().orElseThrow(RuntimeException::new)))).deserialize(var1.get("provider").orElseEmptyMap()));
   }

   public void place(LevelAccessor var1, Random var2, List var3, List var4, Set var5, BoundingBox var6) {
      int var7 = ((BlockPos)var3.get(0)).getY();
      var3.stream().filter((var1x) -> {
         return var1x.getY() == var7;
      }).forEach((var3x) -> {
         this.placeCircle(var1, var2, var3x.west().north());
         this.placeCircle(var1, var2, var3x.east(2).north());
         this.placeCircle(var1, var2, var3x.west().south(2));
         this.placeCircle(var1, var2, var3x.east(2).south(2));

         for(int var4 = 0; var4 < 5; ++var4) {
            int var5 = var2.nextInt(64);
            int var6 = var5 % 8;
            int var7 = var5 / 8;
            if (var6 == 0 || var6 == 7 || var7 == 0 || var7 == 7) {
               this.placeCircle(var1, var2, var3x.offset(-3 + var6, 0, -3 + var7));
            }
         }

      });
   }

   private void placeCircle(LevelSimulatedRW var1, Random var2, BlockPos var3) {
      for(int var4 = -2; var4 <= 2; ++var4) {
         for(int var5 = -2; var5 <= 2; ++var5) {
            if (Math.abs(var4) != 2 || Math.abs(var5) != 2) {
               this.placeBlockAt(var1, var2, var3.offset(var4, 0, var5));
            }
         }
      }

   }

   private void placeBlockAt(LevelSimulatedRW var1, Random var2, BlockPos var3) {
      for(int var4 = 2; var4 >= -3; --var4) {
         BlockPos var5 = var3.above(var4);
         if (AbstractTreeFeature.isGrassOrDirt(var1, var5)) {
            var1.setBlock(var5, this.provider.getState(var2, var3), 19);
            break;
         }

         if (!AbstractTreeFeature.isAir(var1, var5) && var4 < 0) {
            break;
         }
      }

   }

   public Object serialize(DynamicOps var1) {
      return (new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.TREE_DECORATOR_TYPES.getKey(this.type).toString()), var1.createString("provider"), this.provider.serialize(var1))))).getValue();
   }
}
