package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class ProcessorRule {
   public static final Codec<ProcessorRule> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RuleTest.CODEC.fieldOf("input_predicate").forGetter((var0x) -> {
         return var0x.inputPredicate;
      }), RuleTest.CODEC.fieldOf("location_predicate").forGetter((var0x) -> {
         return var0x.locPredicate;
      }), PosRuleTest.CODEC.optionalFieldOf("position_predicate", PosAlwaysTrueTest.INSTANCE).forGetter((var0x) -> {
         return var0x.posPredicate;
      }), BlockState.CODEC.fieldOf("output_state").forGetter((var0x) -> {
         return var0x.outputState;
      }), CompoundTag.CODEC.optionalFieldOf("output_nbt").forGetter((var0x) -> {
         return Optional.ofNullable(var0x.outputTag);
      })).apply(var0, ProcessorRule::new);
   });
   private final RuleTest inputPredicate;
   private final RuleTest locPredicate;
   private final PosRuleTest posPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundTag outputTag;

   public ProcessorRule(RuleTest var1, RuleTest var2, BlockState var3) {
      this(var1, var2, PosAlwaysTrueTest.INSTANCE, var3, Optional.empty());
   }

   public ProcessorRule(RuleTest var1, RuleTest var2, PosRuleTest var3, BlockState var4) {
      this(var1, var2, var3, var4, Optional.empty());
   }

   public ProcessorRule(RuleTest var1, RuleTest var2, PosRuleTest var3, BlockState var4, Optional<CompoundTag> var5) {
      super();
      this.inputPredicate = var1;
      this.locPredicate = var2;
      this.posPredicate = var3;
      this.outputState = var4;
      this.outputTag = (CompoundTag)var5.orElse((Object)null);
   }

   public boolean test(BlockState var1, BlockState var2, BlockPos var3, BlockPos var4, BlockPos var5, RandomSource var6) {
      return this.inputPredicate.test(var1, var6) && this.locPredicate.test(var2, var6) && this.posPredicate.test(var3, var4, var5, var6);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundTag getOutputTag() {
      return this.outputTag;
   }
}
