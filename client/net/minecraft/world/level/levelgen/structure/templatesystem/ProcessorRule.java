package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

public class ProcessorRule {
   public static final Passthrough DEFAULT_BLOCK_ENTITY_MODIFIER;
   public static final Codec<ProcessorRule> CODEC;
   private final RuleTest inputPredicate;
   private final RuleTest locPredicate;
   private final PosRuleTest posPredicate;
   private final BlockState outputState;
   private final RuleBlockEntityModifier blockEntityModifier;

   public ProcessorRule(RuleTest var1, RuleTest var2, BlockState var3) {
      this(var1, var2, PosAlwaysTrueTest.INSTANCE, var3);
   }

   public ProcessorRule(RuleTest var1, RuleTest var2, PosRuleTest var3, BlockState var4) {
      this(var1, var2, var3, var4, DEFAULT_BLOCK_ENTITY_MODIFIER);
   }

   public ProcessorRule(RuleTest var1, RuleTest var2, PosRuleTest var3, BlockState var4, RuleBlockEntityModifier var5) {
      super();
      this.inputPredicate = var1;
      this.locPredicate = var2;
      this.posPredicate = var3;
      this.outputState = var4;
      this.blockEntityModifier = var5;
   }

   public boolean test(BlockState var1, BlockState var2, BlockPos var3, BlockPos var4, BlockPos var5, RandomSource var6) {
      return this.inputPredicate.test(var1, var6) && this.locPredicate.test(var2, var6) && this.posPredicate.test(var3, var4, var5, var6);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundTag getOutputTag(RandomSource var1, @Nullable CompoundTag var2) {
      return this.blockEntityModifier.apply(var1, var2);
   }

   static {
      DEFAULT_BLOCK_ENTITY_MODIFIER = Passthrough.INSTANCE;
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(RuleTest.CODEC.fieldOf("input_predicate").forGetter((var0x) -> {
            return var0x.inputPredicate;
         }), RuleTest.CODEC.fieldOf("location_predicate").forGetter((var0x) -> {
            return var0x.locPredicate;
         }), PosRuleTest.CODEC.lenientOptionalFieldOf("position_predicate", PosAlwaysTrueTest.INSTANCE).forGetter((var0x) -> {
            return var0x.posPredicate;
         }), BlockState.CODEC.fieldOf("output_state").forGetter((var0x) -> {
            return var0x.outputState;
         }), RuleBlockEntityModifier.CODEC.lenientOptionalFieldOf("block_entity_modifier", DEFAULT_BLOCK_ENTITY_MODIFIER).forGetter((var0x) -> {
            return var0x.blockEntityModifier;
         })).apply(var0, ProcessorRule::new);
      });
   }
}
