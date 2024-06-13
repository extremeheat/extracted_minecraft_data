package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
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
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      ItemStack var3 = var2.getParamOrNull(LootContextParams.TOOL);
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

   static record BinomialWithBonusCount(int extraRounds, float probability) implements ApplyBonusCount.Formula {
      private static final Codec<ApplyBonusCount.BinomialWithBonusCount> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.INT.fieldOf("extra").forGetter(ApplyBonusCount.BinomialWithBonusCount::extraRounds),
                  Codec.FLOAT.fieldOf("probability").forGetter(ApplyBonusCount.BinomialWithBonusCount::probability)
               )
               .apply(var0, ApplyBonusCount.BinomialWithBonusCount::new)
      );
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(new ResourceLocation("binomial_with_bonus_count"), CODEC);

      BinomialWithBonusCount(int extraRounds, float probability) {
         super();
         this.extraRounds = extraRounds;
         this.probability = probability;
      }

      @Override
      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         for (int var4 = 0; var4 < var3 + this.extraRounds; var4++) {
            if (var1.nextFloat() < this.probability) {
               var2++;
            }
         }

         return var2;
      }

      @Override
      public ApplyBonusCount.FormulaType getType() {
         return TYPE;
      }
   }

   interface Formula {
      int calculateNewCount(RandomSource var1, int var2, int var3);

      ApplyBonusCount.FormulaType getType();
   }

   static record FormulaType(ResourceLocation id, Codec<? extends ApplyBonusCount.Formula> codec) {
      FormulaType(ResourceLocation id, Codec<? extends ApplyBonusCount.Formula> codec) {
         super();
         this.id = id;
         this.codec = codec;
      }
   }

   static record OreDrops() implements ApplyBonusCount.Formula {
      public static final Codec<ApplyBonusCount.OreDrops> CODEC = Codec.unit(ApplyBonusCount.OreDrops::new);
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(new ResourceLocation("ore_drops"), CODEC);

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

   static record UniformBonusCount(int bonusMultiplier) implements ApplyBonusCount.Formula {
      public static final Codec<ApplyBonusCount.UniformBonusCount> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(ApplyBonusCount.UniformBonusCount::bonusMultiplier))
               .apply(var0, ApplyBonusCount.UniformBonusCount::new)
      );
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(new ResourceLocation("uniform_bonus_count"), CODEC);

      UniformBonusCount(int bonusMultiplier) {
         super();
         this.bonusMultiplier = bonusMultiplier;
      }

      @Override
      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         return var2 + var1.nextInt(this.bonusMultiplier * var3 + 1);
      }

      @Override
      public ApplyBonusCount.FormulaType getType() {
         return TYPE;
      }
   }
}
