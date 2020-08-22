package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class RuleProcessor extends StructureProcessor {
   private final ImmutableList rules;

   public RuleProcessor(List var1) {
      this.rules = ImmutableList.copyOf(var1);
   }

   public RuleProcessor(Dynamic var1) {
      this(var1.get("rules").asList(ProcessorRule::deserialize));
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5) {
      Random var6 = new Random(Mth.getSeed(var4.pos));
      BlockState var7 = var1.getBlockState(var4.pos);
      UnmodifiableIterator var8 = this.rules.iterator();

      ProcessorRule var9;
      do {
         if (!var8.hasNext()) {
            return var4;
         }

         var9 = (ProcessorRule)var8.next();
      } while(!var9.test(var4.state, var7, var6));

      return new StructureTemplate.StructureBlockInfo(var4.pos, var9.getOutputState(), var9.getOutputTag());
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.RULE;
   }

   protected Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("rules"), var1.createList(this.rules.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      })))));
   }
}
