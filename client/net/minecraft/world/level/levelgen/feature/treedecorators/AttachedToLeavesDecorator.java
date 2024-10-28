package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class AttachedToLeavesDecorator extends TreeDecorator {
   public static final MapCodec<AttachedToLeavesDecorator> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((var0x) -> {
         return var0x.probability;
      }), Codec.intRange(0, 16).fieldOf("exclusion_radius_xz").forGetter((var0x) -> {
         return var0x.exclusionRadiusXZ;
      }), Codec.intRange(0, 16).fieldOf("exclusion_radius_y").forGetter((var0x) -> {
         return var0x.exclusionRadiusY;
      }), BlockStateProvider.CODEC.fieldOf("block_provider").forGetter((var0x) -> {
         return var0x.blockProvider;
      }), Codec.intRange(1, 16).fieldOf("required_empty_blocks").forGetter((var0x) -> {
         return var0x.requiredEmptyBlocks;
      }), ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter((var0x) -> {
         return var0x.directions;
      })).apply(var0, AttachedToLeavesDecorator::new);
   });
   protected final float probability;
   protected final int exclusionRadiusXZ;
   protected final int exclusionRadiusY;
   protected final BlockStateProvider blockProvider;
   protected final int requiredEmptyBlocks;
   protected final List<Direction> directions;

   public AttachedToLeavesDecorator(float var1, int var2, int var3, BlockStateProvider var4, int var5, List<Direction> var6) {
      super();
      this.probability = var1;
      this.exclusionRadiusXZ = var2;
      this.exclusionRadiusY = var3;
      this.blockProvider = var4;
      this.requiredEmptyBlocks = var5;
      this.directions = var6;
   }

   public void place(TreeDecorator.Context var1) {
      HashSet var2 = new HashSet();
      RandomSource var3 = var1.random();
      Iterator var4 = Util.shuffledCopy(var1.leaves(), var3).iterator();

      while(true) {
         BlockPos var5;
         Direction var6;
         BlockPos var7;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  var5 = (BlockPos)var4.next();
                  var6 = (Direction)Util.getRandom(this.directions, var3);
                  var7 = var5.relative(var6);
               } while(var2.contains(var7));
            } while(!(var3.nextFloat() < this.probability));
         } while(!this.hasRequiredEmptyBlocks(var1, var5, var6));

         BlockPos var8 = var7.offset(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
         BlockPos var9 = var7.offset(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
         Iterator var10 = BlockPos.betweenClosed(var8, var9).iterator();

         while(var10.hasNext()) {
            BlockPos var11 = (BlockPos)var10.next();
            var2.add(var11.immutable());
         }

         var1.setBlock(var7, this.blockProvider.getState(var3, var7));
      }
   }

   private boolean hasRequiredEmptyBlocks(TreeDecorator.Context var1, BlockPos var2, Direction var3) {
      for(int var4 = 1; var4 <= this.requiredEmptyBlocks; ++var4) {
         BlockPos var5 = var2.relative(var3, var4);
         if (!var1.isAir(var5)) {
            return false;
         }
      }

      return true;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ATTACHED_TO_LEAVES;
   }
}
