package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ServerLevelAccessor;

public class CappedProcessor extends StructureProcessor {
   public static final MapCodec<CappedProcessor> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(StructureProcessorType.SINGLE_CODEC.fieldOf("delegate").forGetter((var0x) -> {
         return var0x.delegate;
      }), IntProvider.POSITIVE_CODEC.fieldOf("limit").forGetter((var0x) -> {
         return var0x.limit;
      })).apply(var0, CappedProcessor::new);
   });
   private final StructureProcessor delegate;
   private final IntProvider limit;

   public CappedProcessor(StructureProcessor var1, IntProvider var2) {
      super();
      this.delegate = var1;
      this.limit = var2;
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.CAPPED;
   }

   public final List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor var1, BlockPos var2, BlockPos var3, List<StructureTemplate.StructureBlockInfo> var4, List<StructureTemplate.StructureBlockInfo> var5, StructurePlaceSettings var6) {
      if (this.limit.getMaxValue() != 0 && !var5.isEmpty()) {
         if (var4.size() != var5.size()) {
            int var10000 = var4.size();
            Util.logAndPauseIfInIde("Original block info list not in sync with processed list, skipping processing. Original size: " + var10000 + ", Processed size: " + var5.size());
            return var5;
         } else {
            RandomSource var7 = RandomSource.create(var1.getLevel().getSeed()).forkPositional().at(var2);
            int var8 = Math.min(this.limit.sample(var7), var5.size());
            if (var8 < 1) {
               return var5;
            } else {
               IntArrayList var9 = Util.toShuffledList(IntStream.range(0, var5.size()), var7);
               IntIterator var10 = var9.intIterator();
               int var11 = 0;

               while(var10.hasNext() && var11 < var8) {
                  int var12 = var10.nextInt();
                  StructureTemplate.StructureBlockInfo var13 = (StructureTemplate.StructureBlockInfo)var4.get(var12);
                  StructureTemplate.StructureBlockInfo var14 = (StructureTemplate.StructureBlockInfo)var5.get(var12);
                  StructureTemplate.StructureBlockInfo var15 = this.delegate.processBlock(var1, var2, var3, var13, var14, var6);
                  if (var15 != null && !var14.equals(var15)) {
                     ++var11;
                     var5.set(var12, var15);
                  }
               }

               return var5;
            }
         }
      } else {
         return var5;
      }
   }
}
