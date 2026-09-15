package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootPredicates;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.DispatcherProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;

public class ContextIntProviders {
   public static final Codec<ContextIntProvider> DIRECT_CODEC = Codec.lazyInitialized(() -> {
      Codec<ContextIntProvider> typedCodec = BuiltInRegistries.CONTEXT_INT_PROVIDER_TYPE.byNameCodec().dispatch(ContextIntProvider::codec, (c) -> c);
      return Codec.either(ConstantValue.INLINE_CODEC, typedCodec).xmap(Either::unwrap, (provider) -> {
         Either var10000;
         if (provider instanceof ConstantValue constant) {
            var10000 = Either.left(constant);
         } else {
            var10000 = Either.right(provider);
         }

         return var10000;
      });
   });
   public static final Codec<Holder<ContextIntProvider>> CODEC;
   public static final Codec<HolderSet<ContextIntProvider>> LIST_CODEC;
   public static final ResourceKey<ContextIntProvider> COMPOSTABLE_LOW;
   public static final ResourceKey<ContextIntProvider> COMPOSTABLE_LOW_MEDIUM;
   public static final ResourceKey<ContextIntProvider> COMPOSTABLE_MEDIUM;
   public static final ResourceKey<ContextIntProvider> COMPOSTABLE_MEDIUM_HIGH;
   public static final ResourceKey<ContextIntProvider> COMPOSTABLE_ALWAYS_ADD_ONE;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_BAMBOO;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOL_SLABS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOL_CARPETS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_DRY_PLANTS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOL;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_SLABS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_ITEMS_LARGE;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_ITEMS_SMALL;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_ROOTS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_WOOD_BLOCKS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_HANGING_SIGNS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_BOATS;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_COAL;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_BLAZE_ROD;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_DRIED_KELP_BLOCK;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_COAL_BLOCK;
   public static final ResourceKey<ContextIntProvider> COOKING_TIME_LAVA_BUCKET;
   public static final ResourceKey<ContextIntProvider> COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR;
   public static final ResourceKey<ContextIntProvider> COOKING_FAST_BURN_TIME_REDUCTION_FACTOR;
   public static final ResourceKey<ContextIntProvider> BREWING_DEFAULT_USES;

   public ContextIntProviders() {
      super();
   }

   private static ResourceKey<ContextIntProvider> createKey(final String location) {
      return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Identifier.withDefaultNamespace(location));
   }

   public static void bootstrap(final BootstrapContext<ContextIntProvider> context) {
      HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
      HolderGetter<LootItemCondition> predicates = context.lookup(Registries.PREDICATE);
      Holder.Reference<ContextIntProvider> normalBurnTime = context.register(COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR, new ConstantValue(1));
      Holder.Reference<ContextIntProvider> fastBurnTime = context.register(COOKING_FAST_BURN_TIME_REDUCTION_FACTOR, new ConstantValue(2));
      context.register(COMPOSTABLE_LOW, compostable(blocks, 30));
      context.register(COMPOSTABLE_LOW_MEDIUM, compostable(blocks, 50));
      context.register(COMPOSTABLE_MEDIUM, compostable(blocks, 65));
      context.register(COMPOSTABLE_MEDIUM_HIGH, compostable(blocks, 85));
      context.register(COMPOSTABLE_ALWAYS_ADD_ONE, compostable(blocks, 100));
      context.register(COOKING_TIME_BAMBOO, cooking(predicates, normalBurnTime, fastBurnTime, 50));
      context.register(COOKING_TIME_WOOL_SLABS, cooking(predicates, normalBurnTime, fastBurnTime, 50));
      context.register(COOKING_TIME_WOOL_CARPETS, cooking(predicates, normalBurnTime, fastBurnTime, 67));
      context.register(COOKING_TIME_DRY_PLANTS, cooking(predicates, normalBurnTime, fastBurnTime, 100));
      context.register(COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL, cooking(predicates, normalBurnTime, fastBurnTime, 100));
      context.register(COOKING_TIME_WOOL, cooking(predicates, normalBurnTime, fastBurnTime, 100));
      context.register(COOKING_TIME_WOOD_SLABS, cooking(predicates, normalBurnTime, fastBurnTime, 150));
      context.register(COOKING_TIME_WOOD_ITEMS_LARGE, cooking(predicates, normalBurnTime, fastBurnTime, 200));
      context.register(COOKING_TIME_ROOTS, cooking(predicates, normalBurnTime, fastBurnTime, 300));
      context.register(COOKING_TIME_WOOD_BLOCKS, cooking(predicates, normalBurnTime, fastBurnTime, 300));
      context.register(COOKING_TIME_WOOD_ITEMS_SMALL, cooking(predicates, normalBurnTime, fastBurnTime, 300));
      context.register(COOKING_TIME_HANGING_SIGNS, cooking(predicates, normalBurnTime, fastBurnTime, 800));
      context.register(COOKING_TIME_BOATS, cooking(predicates, normalBurnTime, fastBurnTime, 1200));
      context.register(COOKING_TIME_COAL, cooking(predicates, normalBurnTime, fastBurnTime, 1600));
      context.register(COOKING_TIME_BLAZE_ROD, cooking(predicates, normalBurnTime, fastBurnTime, 2400));
      context.register(COOKING_TIME_DRIED_KELP_BLOCK, cooking(predicates, normalBurnTime, fastBurnTime, 4001));
      context.register(COOKING_TIME_COAL_BLOCK, cooking(predicates, normalBurnTime, fastBurnTime, 16000));
      context.register(COOKING_TIME_LAVA_BUCKET, cooking(predicates, normalBurnTime, fastBurnTime, 20000));
      context.register(BREWING_DEFAULT_USES, new ConstantValue(20));
   }

   private static ContextIntProvider compostable(final HolderGetter<Block> blocks, final int layerIncreaseChance) {
      if (layerIncreaseChance >= 100) {
         return new ConstantValue(1);
      } else {
         DispatcherProvider.Case<ContextIntProvider> emptyCase = new DispatcherProvider.Case<ContextIntProvider>(Holder.direct(MatchBlock.blockMatches(blocks, Blocks.COMPOSTER, StatePropertiesPredicate.Builder.properties().hasProperty(ComposterBlock.LEVEL, 0)).build()), exactly(1));
         WeightedList<Holder<ContextIntProvider>> cases = WeightedList.<Holder<ContextIntProvider>>builder().add(exactly(1), layerIncreaseChance).add(exactly(0), 100 - layerIncreaseChance).build();
         return new NumberDispatcher(List.of(emptyCase), weighted(cases));
      }
   }

   private static ContextIntProvider cooking(final HolderGetter<LootItemCondition> predicates, final Holder.Reference<ContextIntProvider> normalBurnTimeDivisor, final Holder.Reference<ContextIntProvider> fastBurnTimeDivisor, final int timeSeconds) {
      Holder<LootItemCondition> fasterCookingBlocks = predicates.getOrThrow(LootPredicates.FAST_FURNACE);
      ContextIntProvider fastConditional = new ConditionalValue(fasterCookingBlocks, fastBurnTimeDivisor, normalBurnTimeDivisor);
      return (ContextIntProvider)div(exactly(timeSeconds), Holder.direct(fastConditional)).value();
   }

   public static Holder<ContextIntProvider> fromFloat(final Holder<ContextFloatProvider> input) {
      return Holder.<ContextIntProvider>direct(new FromFloat(input));
   }

   public static Holder<ContextIntProvider> binomial(final int n, final float p) {
      return Holder.<ContextIntProvider>direct(new BinomialDistributionGenerator(exactly(n), ContextFloatProviders.exactly(p)));
   }

   public static Holder<ContextIntProvider> exactly(final int value) {
      return Holder.<ContextIntProvider>direct(new ConstantValue(value));
   }

   public static Holder<ContextIntProvider> fromScoreboard(final LootContext.EntityTarget entityTarget, final String score) {
      return Holder.<ContextIntProvider>direct(new ScoreboardValue(ContextScoreboardNameProvider.forTarget(entityTarget), score, exactly(0)));
   }

   @SafeVarargs
   public static Holder<ContextIntProvider> avg(final Holder<ContextIntProvider>... inputs) {
      return Holder.<ContextIntProvider>direct(new Average(HolderSet.direct(inputs)));
   }

   public static Holder<ContextIntProvider> sub(final Holder<ContextIntProvider> left, final Holder<ContextIntProvider> right) {
      return Holder.<ContextIntProvider>direct(new Difference(left, right));
   }

   @SafeVarargs
   public static Holder<ContextIntProvider> max(final Holder<ContextIntProvider>... inputs) {
      return Holder.<ContextIntProvider>direct(new Maximum(HolderSet.direct(inputs)));
   }

   @SafeVarargs
   public static Holder<ContextIntProvider> min(final Holder<ContextIntProvider>... inputs) {
      return Holder.<ContextIntProvider>direct(new Minimum(HolderSet.direct(inputs)));
   }

   public static Holder<ContextIntProvider> mod(final Holder<ContextIntProvider> left, final Holder<ContextIntProvider> right) {
      return Holder.<ContextIntProvider>direct(new Modulus(left, right));
   }

   public static Holder<ContextIntProvider> floorMod(final Holder<ContextIntProvider> left, final Holder<ContextIntProvider> right) {
      return Holder.<ContextIntProvider>direct(new FloorModulus(left, right));
   }

   public static Holder<ContextIntProvider> negate(final Holder<ContextIntProvider> input) {
      return Holder.<ContextIntProvider>direct(new Negate(input));
   }

   @SafeVarargs
   public static Holder<ContextIntProvider> mul(final Holder<ContextIntProvider>... inputs) {
      return Holder.<ContextIntProvider>direct(new Product(HolderSet.direct(inputs)));
   }

   public static Holder<ContextIntProvider> div(final Holder<ContextIntProvider> left, final Holder<ContextIntProvider> right) {
      return Holder.<ContextIntProvider>direct(new Quotient(left, right));
   }

   public static Holder<ContextIntProvider> floorDiv(final Holder<ContextIntProvider> left, final Holder<ContextIntProvider> right) {
      return Holder.<ContextIntProvider>direct(new FloorQuotient(left, right));
   }

   @SafeVarargs
   public static Holder<ContextIntProvider> add(final Holder<ContextIntProvider>... inputs) {
      return Holder.<ContextIntProvider>direct(new Sum(HolderSet.direct(inputs)));
   }

   public static Holder<ContextIntProvider> between(final int min, final int max) {
      return Holder.<ContextIntProvider>direct(new UniformGenerator(exactly(min), exactly(max)));
   }

   public static Holder<ContextIntProvider> weighted(final WeightedList<Holder<ContextIntProvider>> cases) {
      return Holder.<ContextIntProvider>direct(new WeightedListValue(cases));
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.CONTEXT_INT_PROVIDER, DIRECT_CODEC);
      LIST_CODEC = RegistryCodecs.holderSet(Registries.CONTEXT_INT_PROVIDER, DIRECT_CODEC);
      COMPOSTABLE_LOW = createKey("compostable/low");
      COMPOSTABLE_LOW_MEDIUM = createKey("compostable/low_medium");
      COMPOSTABLE_MEDIUM = createKey("compostable/medium");
      COMPOSTABLE_MEDIUM_HIGH = createKey("compostable/medium_high");
      COMPOSTABLE_ALWAYS_ADD_ONE = createKey("compostable/always_add_one");
      COOKING_TIME_BAMBOO = createKey("cooking/time_bamboo");
      COOKING_TIME_WOOL_SLABS = createKey("cooking/time_wool_slabs");
      COOKING_TIME_WOOL_CARPETS = createKey("cooking/time_wool_carpets");
      COOKING_TIME_DRY_PLANTS = createKey("cooking/time_dry_plants");
      COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL = createKey("cooking/time_wood_items_extra_small");
      COOKING_TIME_WOOL = createKey("cooking/time_wool");
      COOKING_TIME_WOOD_SLABS = createKey("cooking/time_wood_slabs");
      COOKING_TIME_WOOD_ITEMS_LARGE = createKey("cooking/time_wood_items_large");
      COOKING_TIME_WOOD_ITEMS_SMALL = createKey("cooking/time_wood_items_small");
      COOKING_TIME_ROOTS = createKey("cooking/time_roots");
      COOKING_TIME_WOOD_BLOCKS = createKey("cooking/time_wood_blocks");
      COOKING_TIME_HANGING_SIGNS = createKey("cooking/time_hanging_signs");
      COOKING_TIME_BOATS = createKey("cooking/time_boats");
      COOKING_TIME_COAL = createKey("cooking/time_coal");
      COOKING_TIME_BLAZE_ROD = createKey("cooking/time_blaze_rod");
      COOKING_TIME_DRIED_KELP_BLOCK = createKey("cooking/time_dried_kelp_block");
      COOKING_TIME_COAL_BLOCK = createKey("cooking/time_coal_block");
      COOKING_TIME_LAVA_BUCKET = createKey("cooking/time_lava_bucket");
      COOKING_NORMAL_BURN_TIME_REDUCTION_FACTOR = createKey("cooking/normal_burn_time_reduction_factor");
      COOKING_FAST_BURN_TIME_REDUCTION_FACTOR = createKey("cooking/fast_burn_time_reduction_factor");
      BREWING_DEFAULT_USES = createKey("brewing/uses_default");
   }
}
