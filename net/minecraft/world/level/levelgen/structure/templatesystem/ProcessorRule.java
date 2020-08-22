package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.block.state.BlockState;

public class ProcessorRule {
   private final RuleTest inputPredicate;
   private final RuleTest locPredicate;
   private final BlockState outputState;
   @Nullable
   private final CompoundTag outputTag;

   public ProcessorRule(RuleTest var1, RuleTest var2, BlockState var3) {
      this(var1, var2, var3, (CompoundTag)null);
   }

   public ProcessorRule(RuleTest var1, RuleTest var2, BlockState var3, @Nullable CompoundTag var4) {
      this.inputPredicate = var1;
      this.locPredicate = var2;
      this.outputState = var3;
      this.outputTag = var4;
   }

   public boolean test(BlockState var1, BlockState var2, Random var3) {
      return this.inputPredicate.test(var1, var3) && this.locPredicate.test(var2, var3);
   }

   public BlockState getOutputState() {
      return this.outputState;
   }

   @Nullable
   public CompoundTag getOutputTag() {
      return this.outputTag;
   }

   public Dynamic serialize(DynamicOps var1) {
      Object var2 = var1.createMap(ImmutableMap.of(var1.createString("input_predicate"), this.inputPredicate.serialize(var1).getValue(), var1.createString("location_predicate"), this.locPredicate.serialize(var1).getValue(), var1.createString("output_state"), BlockState.serialize(var1, this.outputState).getValue()));
      return this.outputTag == null ? new Dynamic(var1, var2) : new Dynamic(var1, var1.mergeInto(var2, var1.createString("output_nbt"), (new Dynamic(NbtOps.INSTANCE, this.outputTag)).convert(var1).getValue()));
   }

   public static ProcessorRule deserialize(Dynamic var0) {
      Dynamic var1 = var0.get("input_predicate").orElseEmptyMap();
      Dynamic var2 = var0.get("location_predicate").orElseEmptyMap();
      RuleTest var3 = (RuleTest)Deserializer.deserialize(var1, Registry.RULE_TEST, "predicate_type", AlwaysTrueTest.INSTANCE);
      RuleTest var4 = (RuleTest)Deserializer.deserialize(var2, Registry.RULE_TEST, "predicate_type", AlwaysTrueTest.INSTANCE);
      BlockState var5 = BlockState.deserialize(var0.get("output_state").orElseEmptyMap());
      CompoundTag var6 = (CompoundTag)var0.get("output_nbt").map((var0x) -> {
         return (Tag)var0x.convert(NbtOps.INSTANCE).getValue();
      }).orElse((Object)null);
      return new ProcessorRule(var3, var4, var5, var6);
   }
}
