package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class RuleProcessor extends StructureProcessor {
   public static final Codec<RuleProcessor> CODEC;
   private final ImmutableList<ProcessorRule> rules;

   public RuleProcessor(List<? extends ProcessorRule> var1) {
      super();
      this.rules = ImmutableList.copyOf(var1);
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      Random var7 = new Random(Mth.getSeed(var5.pos));
      BlockState var8 = var1.getBlockState(var5.pos);
      UnmodifiableIterator var9 = this.rules.iterator();

      ProcessorRule var10;
      do {
         if (!var9.hasNext()) {
            return var5;
         }

         var10 = (ProcessorRule)var9.next();
      } while(!var10.test(var5.state, var8, var4.pos, var5.pos, var3, var7));

      return new StructureTemplate.StructureBlockInfo(var5.pos, var10.getOutputState(), var10.getOutputTag());
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.RULE;
   }

   static {
      CODEC = ProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleProcessor::new, (var0) -> {
         return var0.rules;
      }).codec();
   }
}
