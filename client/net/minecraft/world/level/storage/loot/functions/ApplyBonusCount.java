package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
   private static final Map<ResourceLocation, ApplyBonusCount.FormulaType> FORMULAS = Stream.of(
         ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.UniformBonusCount.TYPE
      )
      .collect(Collectors.toMap(ApplyBonusCount.FormulaType::id, Function.identity()));
   private static final Codec<ApplyBonusCount.FormulaType> FORMULA_TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(var0 -> {
      ApplyBonusCount.FormulaType var1 = FORMULAS.get(var0);
      return var1 != null ? DataResult.success(var1) : DataResult.error(() -> "No formula type with id: '" + var0 + "'");
   }, ApplyBonusCount.FormulaType::id);
   private static final MapCodec<ApplyBonusCount.Formula> FORMULA_CODEC = ExtraCodecs.dispatchOptionalValue(
      "formula", "parameters", FORMULA_TYPE_CODEC, ApplyBonusCount.Formula::getType, ApplyBonusCount.FormulaType::codec
   );
   public static final MapCodec<ApplyBonusCount> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(var0.group(Enchantment.CODEC.fieldOf("enchantment").forGetter(var0x -> var0x.enchantment), FORMULA_CODEC.forGetter(var0x -> var0x.formula)))
            .apply(var0, ApplyBonusCount::new)
   );
   private final Holder<Enchantment> enchantment;
   private final ApplyBonusCount.Formula formula;

   private ApplyBonusCount(List<LootItemCondition> var1, Holder<Enchantment> var2, ApplyBonusCount.Formula var3) {
      super(var1);
      this.enchantment = var2;
      this.formula = var3;
   }

   @Override
   public LootItemFunctionType<ApplyBonusCount> getType() {
      return LootItemFunctions.APPLY_BONUS;
   }

   @Override
   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.TOOL);
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      ItemStack var3 = var2.getOptionalParameter(LootContextParams.TOOL);
      if (var3 != null) {
         int var4 = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var3);
         int var5 = this.formula.calculateNewCount(var2.getRandom(), var1.getCount(), var4);
         var1.setCount(var5);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Holder<Enchantment> var0, float var1, int var2) {
      return simpleBuilder(var3 -> new ApplyBonusCount(var3, var0, new ApplyBonusCount.BinomialWithBonusCount(var2, var1)));
   }

   public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Holder<Enchantment> var0) {
      return simpleBuilder(var1 -> new ApplyBonusCount(var1, var0, new ApplyBonusCount.OreDrops()));
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Holder<Enchantment> var0) {
      return simpleBuilder(var1 -> new ApplyBonusCount(var1, var0, new ApplyBonusCount.UniformBonusCount(1)));
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Holder<Enchantment> var0, int var1) {
      return simpleBuilder(var2 -> new ApplyBonusCount(var2, var0, new ApplyBonusCount.UniformBonusCount(var1)));
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   interface Formula {
      int calculateNewCount(RandomSource var1, int var2, int var3);

      ApplyBonusCount.FormulaType getType();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static record OreDrops() implements ApplyBonusCount.Formula {
      public static final Codec<ApplyBonusCount.OreDrops> CODEC = Codec.unit(ApplyBonusCount.OreDrops::new);
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(ResourceLocation.withDefaultNamespace("ore_drops"), CODEC);

      OreDrops() {
         super();
      }

      @Override
      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         if (var3 > 0) {
            int var4 = var1.nextInt(var3 + 2) - 1;
            if (var4 < 0) {
               var4 = 0;
            }

            return var2 * (var4 + 1);
         } else {
            return var2;
         }
      }

      @Override
      public ApplyBonusCount.FormulaType getType() {
         return TYPE;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
