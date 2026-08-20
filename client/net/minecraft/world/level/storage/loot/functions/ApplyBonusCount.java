package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
   private static final Map<Identifier, FormulaType> FORMULAS;
   private static final Codec<FormulaType> FORMULA_TYPE_CODEC;
   private static final MapCodec<Formula> FORMULA_CODEC;
   public static final MapCodec<ApplyBonusCount> MAP_CODEC;
   private final Holder<Enchantment> enchantment;
   private final Formula formula;

   private ApplyBonusCount(final Optional<Holder<LootItemCondition>> condition, final Holder<Enchantment> enchantment, final Formula formula) {
      super(condition);
      this.enchantment = enchantment;
      this.formula = formula;
   }

   public MapCodec<ApplyBonusCount> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.TOOL);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      ItemInstance tool = (ItemInstance)context.getOptionalParameter(LootContextParams.TOOL);
      if (tool != null) {
         int level = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, tool);
         int newCount = this.formula.calculateNewCount(context.getRandom(), itemStack.getCount(), level);
         itemStack.setCount(newCount);
      }

      return itemStack;
   }

   public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(final Holder<Enchantment> enchantment, final float probability, final int extraRounds) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, new BinomialWithBonusCount(extraRounds, probability)));
   }

   public static LootItemConditionalFunction.Builder<?> addOreBonusCount(final Holder<Enchantment> enchantment) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, ApplyBonusCount.OreDrops.INSTANCE));
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(final Holder<Enchantment> enchantment) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, new UniformBonusCount(1)));
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(final Holder<Enchantment> enchantment, final int bonusMultiplier) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, new UniformBonusCount(bonusMultiplier)));
   }

   static {
      FORMULAS = (Map)Stream.of(ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.UniformBonusCount.TYPE).collect(Collectors.toMap(FormulaType::id, Function.identity()));
      FORMULA_TYPE_CODEC = Identifier.CODEC.comapFlatMap((location) -> {
         FormulaType type = (FormulaType)FORMULAS.get(location);
         return type != null ? DataResult.success(type) : DataResult.error(() -> "No formula type with id: '" + String.valueOf(location) + "'");
      }, FormulaType::id);
      FORMULA_CODEC = ExtraCodecs.dispatchOptionalValue("formula", "parameters", FORMULA_TYPE_CODEC, Formula::getType, FormulaType::codec);
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(Enchantment.CODEC.fieldOf("enchantment").forGetter((f) -> f.enchantment), FORMULA_CODEC.forGetter((f) -> f.formula))).apply(i, ApplyBonusCount::new));
   }

   private static record FormulaType(Identifier id, Codec<? extends Formula> codec) {
      private FormulaType {
         super();
      }
   }

   private static record BinomialWithBonusCount(int extraRounds, float probability) implements Formula {
      private static final Codec<BinomialWithBonusCount> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extraRounds), Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)).apply(i, BinomialWithBonusCount::new));
      public static final FormulaType TYPE;

      private BinomialWithBonusCount {
         super();
      }

      public int calculateNewCount(final RandomSource random, int count, final int level) {
         for(int i = 0; i < level + this.extraRounds; ++i) {
            if (random.nextFloat() < this.probability) {
               ++count;
            }
         }

         return count;
      }

      public FormulaType getType() {
         return TYPE;
      }

      static {
         TYPE = new FormulaType(Identifier.withDefaultNamespace("binomial_with_bonus_count"), CODEC);
      }
   }

   private static record UniformBonusCount(int bonusMultiplier) implements Formula {
      public static final Codec<UniformBonusCount> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(UniformBonusCount::bonusMultiplier)).apply(i, UniformBonusCount::new));
      public static final FormulaType TYPE;

      private UniformBonusCount {
         super();
      }

      public int calculateNewCount(final RandomSource random, final int count, final int level) {
         return count + random.nextInt(this.bonusMultiplier * level + 1);
      }

      public FormulaType getType() {
         return TYPE;
      }

      static {
         TYPE = new FormulaType(Identifier.withDefaultNamespace("uniform_bonus_count"), CODEC);
      }
   }

   private static record OreDrops() implements Formula {
      public static final OreDrops INSTANCE = new OreDrops();
      public static final Codec<OreDrops> CODEC;
      public static final FormulaType TYPE;

      private OreDrops() {
         super();
      }

      public int calculateNewCount(final RandomSource random, final int count, final int level) {
         if (level > 0) {
            int bonus = random.nextInt(level + 2) - 1;
            if (bonus < 0) {
               bonus = 0;
            }

            return count * (bonus + 1);
         } else {
            return count;
         }
      }

      public FormulaType getType() {
         return TYPE;
      }

      static {
         CODEC = MapCodec.unitCodec(INSTANCE);
         TYPE = new FormulaType(Identifier.withDefaultNamespace("ore_drops"), CODEC);
      }
   }

   private interface Formula {
      int calculateNewCount(final RandomSource random, final int count, final int level);

      FormulaType getType();
   }
}
