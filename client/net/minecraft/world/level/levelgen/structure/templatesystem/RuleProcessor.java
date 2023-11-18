package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class RuleProcessor extends StructureProcessor {
   public static final Codec<RuleProcessor> CODEC = ProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleProcessor::new, var0 -> var0.rules).codec();
   private final ImmutableList<ProcessorRule> rules;

   public RuleProcessor(List<? extends ProcessorRule> var1) {
      super();
      this.rules = ImmutableList.copyOf(var1);
   }

   @Nullable
   @Override
   public StructureTemplate.StructureBlockInfo processBlock(
      LevelReader var1,
      BlockPos var2,
      BlockPos var3,
      StructureTemplate.StructureBlockInfo var4,
      StructureTemplate.StructureBlockInfo var5,
      StructurePlaceSettings var6
   ) {
      RandomSource var7 = RandomSource.create(Mth.getSeed(var5.pos()));
      BlockState var8 = var1.getBlockState(var5.pos());
      UnmodifiableIterator var9 = this.rules.iterator();

      while(var9.hasNext()) {
         ProcessorRule var10 = (ProcessorRule)var9.next();
         if (var10.test(var5.state(), var8, var4.pos(), var5.pos(), var3, var7)) {
            return new StructureTemplate.StructureBlockInfo(var5.pos(), var10.getOutputState(), var10.getOutputTag(var7, var5.nbt()));
         }
      }

      return var5;
   }

   @Override
   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.RULE;
   }
}
