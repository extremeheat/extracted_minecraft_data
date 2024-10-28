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
import net.minecraft.core.registries.BuiltInRegistries;
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
   private static final Map<ResourceLocation, FormulaType> FORMULAS;
   private static final Codec<FormulaType> FORMULA_TYPE_CODEC;
   private static final MapCodec<Formula> FORMULA_CODEC;
   public static final MapCodec<ApplyBonusCount> CODEC;
   private final Holder<Enchantment> enchantment;
   private final Formula formula;

   private ApplyBonusCount(List<LootItemCondition> var1, Holder<Enchantment> var2, Formula var3) {
      super(var1);
      this.enchantment = var2;
      this.formula = var3;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.APPLY_BONUS;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      ItemStack var3 = (ItemStack)var2.getParamOrNull(LootContextParams.TOOL);
      if (var3 != null) {
         int var4 = EnchantmentHelper.getItemEnchantmentLevel((Enchantment)this.enchantment.value(), var3);
         int var5 = this.formula.calculateNewCount(var2.getRandom(), var1.getCount(), var4);
         var1.setCount(var5);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment var0, float var1, int var2) {
      return simpleBuilder((var3) -> {
         return new ApplyBonusCount(var3, var0.builtInRegistryHolder(), new BinomialWithBonusCount(var2, var1));
      });
   }

   public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Enchantment var0) {
      return simpleBuilder((var1) -> {
         return new ApplyBonusCount(var1, var0.builtInRegistryHolder(), new OreDrops());
      });
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment var0) {
      return simpleBuilder((var1) -> {
         return new ApplyBonusCount(var1, var0.builtInRegistryHolder(), new UniformBonusCount(1));
      });
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment var0, int var1) {
      return simpleBuilder((var2) -> {
         return new ApplyBonusCount(var2, var0.builtInRegistryHolder(), new UniformBonusCount(var1));
      });
   }

   static {
      FORMULAS = (Map)Stream.of(ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.UniformBonusCount.TYPE).collect(Collectors.toMap(FormulaType::id, Function.identity()));
      FORMULA_TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap((var0) -> {
         FormulaType var1 = (FormulaType)FORMULAS.get(var0);
         return var1 != null ? DataResult.success(var1) : DataResult.error(() -> {
            return "No formula type with id: '" + String.valueOf(var0) + "'";
         });
      }, FormulaType::id);
      FORMULA_CODEC = ExtraCodecs.dispatchOptionalValue("formula", "parameters", FORMULA_TYPE_CODEC, Formula::getType, FormulaType::codec);
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return commonFields(var0).and(var0.group(BuiltInRegistries.ENCHANTMENT.holderByNameCodec().fieldOf("enchantment").forGetter((var0x) -> {
            return var0x.enchantment;
         }), FORMULA_CODEC.forGetter((var0x) -> {
            return var0x.formula;
         }))).apply(var0, ApplyBonusCount::new);
      });
   }

   interface Formula {
      int calculateNewCount(RandomSource var1, int var2, int var3);

      FormulaType getType();
   }

   static record UniformBonusCount(int bonusMultiplier) implements Formula {
      public static final Codec<UniformBonusCount> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(UniformBonusCount::bonusMultiplier)).apply(var0, UniformBonusCount::new);
      });
      public static final FormulaType TYPE;

      UniformBonusCount(int var1) {
         super();
         this.bonusMultiplier = var1;
      }

      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         return var2 + var1.nextInt(this.bonusMultiplier * var3 + 1);
      }

      public FormulaType getType() {
         return TYPE;
      }

      public int bonusMultiplier() {
         return this.bonusMultiplier;
      }

      static {
         TYPE = new FormulaType(new ResourceLocation("uniform_bonus_count"), CODEC);
      }
   }

   private static record OreDrops() implements Formula {
      public static final Codec<OreDrops> CODEC = Codec.unit(OreDrops::new);
      public static final FormulaType TYPE;

      OreDrops() {
         super();
      }

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

      public FormulaType getType() {
         return TYPE;
      }

      static {
         TYPE = new FormulaType(new ResourceLocation("ore_drops"), CODEC);
      }
   }

   private static record BinomialWithBonusCount(int extraRounds, float probability) implements Formula {
      private static final Codec<BinomialWithBonusCount> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extraRounds), Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)).apply(var0, BinomialWithBonusCount::new);
      });
      public static final FormulaType TYPE;

      BinomialWithBonusCount(int var1, float var2) {
         super();
         this.extraRounds = var1;
         this.probability = var2;
      }

      public int calculateNewCount(RandomSource var1, int var2, int var3) {
         for(int var4 = 0; var4 < var3 + this.extraRounds; ++var4) {
            if (var1.nextFloat() < this.probability) {
               ++var2;
            }
         }

         return var2;
      }

      public FormulaType getType() {
         return TYPE;
      }

      public int extraRounds() {
         return this.extraRounds;
      }

      public float probability() {
         return this.probability;
      }

      static {
         TYPE = new FormulaType(new ResourceLocation("binomial_with_bonus_count"), CODEC);
      }
   }

   private static record FormulaType(ResourceLocation id, Codec<? extends Formula> codec) {
      FormulaType(ResourceLocation var1, Codec<? extends Formula> var2) {
         super();
         this.id = var1;
         this.codec = var2;
      }

      public ResourceLocation id() {
         return this.id;
      }

      public Codec<? extends Formula> codec() {
         return this.codec;
      }
   }
}
