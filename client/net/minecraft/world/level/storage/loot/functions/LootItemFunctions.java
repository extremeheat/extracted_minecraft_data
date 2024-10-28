package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootItemFunctions {
   public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = (var0, var1) -> {
      return var0;
   };
   public static final Codec<LootItemFunction> TYPED_CODEC;
   public static final Codec<LootItemFunction> ROOT_CODEC;
   public static final Codec<Holder<LootItemFunction>> CODEC;
   public static final LootItemFunctionType<SetItemCountFunction> SET_COUNT;
   public static final LootItemFunctionType<SetItemFunction> SET_ITEM;
   public static final LootItemFunctionType<EnchantWithLevelsFunction> ENCHANT_WITH_LEVELS;
   public static final LootItemFunctionType<EnchantRandomlyFunction> ENCHANT_RANDOMLY;
   public static final LootItemFunctionType<SetEnchantmentsFunction> SET_ENCHANTMENTS;
   public static final LootItemFunctionType<SetCustomDataFunction> SET_CUSTOM_DATA;
   public static final LootItemFunctionType<SetComponentsFunction> SET_COMPONENTS;
   public static final LootItemFunctionType<SmeltItemFunction> FURNACE_SMELT;
   public static final LootItemFunctionType<EnchantedCountIncreaseFunction> ENCHANTED_COUNT_INCREASE;
   public static final LootItemFunctionType<SetItemDamageFunction> SET_DAMAGE;
   public static final LootItemFunctionType<SetAttributesFunction> SET_ATTRIBUTES;
   public static final LootItemFunctionType<SetNameFunction> SET_NAME;
   public static final LootItemFunctionType<ExplorationMapFunction> EXPLORATION_MAP;
   public static final LootItemFunctionType<SetStewEffectFunction> SET_STEW_EFFECT;
   public static final LootItemFunctionType<CopyNameFunction> COPY_NAME;
   public static final LootItemFunctionType<SetContainerContents> SET_CONTENTS;
   public static final LootItemFunctionType<ModifyContainerContents> MODIFY_CONTENTS;
   public static final LootItemFunctionType<FilteredFunction> FILTERED;
   public static final LootItemFunctionType<LimitCount> LIMIT_COUNT;
   public static final LootItemFunctionType<ApplyBonusCount> APPLY_BONUS;
   public static final LootItemFunctionType<SetContainerLootTable> SET_LOOT_TABLE;
   public static final LootItemFunctionType<ApplyExplosionDecay> EXPLOSION_DECAY;
   public static final LootItemFunctionType<SetLoreFunction> SET_LORE;
   public static final LootItemFunctionType<FillPlayerHead> FILL_PLAYER_HEAD;
   public static final LootItemFunctionType<CopyCustomDataFunction> COPY_CUSTOM_DATA;
   public static final LootItemFunctionType<CopyBlockState> COPY_STATE;
   public static final LootItemFunctionType<SetBannerPatternFunction> SET_BANNER_PATTERN;
   public static final LootItemFunctionType<SetPotionFunction> SET_POTION;
   public static final LootItemFunctionType<SetInstrumentFunction> SET_INSTRUMENT;
   public static final LootItemFunctionType<FunctionReference> REFERENCE;
   public static final LootItemFunctionType<SequenceFunction> SEQUENCE;
   public static final LootItemFunctionType<CopyComponentsFunction> COPY_COMPONENTS;
   public static final LootItemFunctionType<SetFireworksFunction> SET_FIREWORKS;
   public static final LootItemFunctionType<SetFireworkExplosionFunction> SET_FIREWORK_EXPLOSION;
   public static final LootItemFunctionType<SetBookCoverFunction> SET_BOOK_COVER;
   public static final LootItemFunctionType<SetWrittenBookPagesFunction> SET_WRITTEN_BOOK_PAGES;
   public static final LootItemFunctionType<SetWritableBookPagesFunction> SET_WRITABLE_BOOK_PAGES;
   public static final LootItemFunctionType<ToggleTooltips> TOGGLE_TOOLTIPS;
   public static final LootItemFunctionType<SetOminousBottleAmplifierFunction> SET_OMINOUS_BOTTLE_AMPLIFIER;
   public static final LootItemFunctionType<SetCustomModelDataFunction> SET_CUSTOM_MODEL_DATA;

   public LootItemFunctions() {
      super();
   }

   private static <T extends LootItemFunction> LootItemFunctionType<T> register(String var0, MapCodec<T> var1) {
      return (LootItemFunctionType)Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new LootItemFunctionType(var1));
   }

   public static BiFunction<ItemStack, LootContext, ItemStack> compose(List<? extends BiFunction<ItemStack, LootContext, ItemStack>> var0) {
      List var1 = List.copyOf(var0);
      BiFunction var10000;
      switch (var1.size()) {
         case 0:
            var10000 = IDENTITY;
            break;
         case 1:
            var10000 = (BiFunction)var1.get(0);
            break;
         case 2:
            BiFunction var2 = (BiFunction)var1.get(0);
            BiFunction var3 = (BiFunction)var1.get(1);
            var10000 = (var2x, var3x) -> {
               return (ItemStack)var3.apply((ItemStack)var2.apply(var2x, var3x), var3x);
            };
            break;
         default:
            var10000 = (var1x, var2x) -> {
               BiFunction var4;
               for(Iterator var3 = var1.iterator(); var3.hasNext(); var1x = (ItemStack)var4.apply(var1x, var2x)) {
                  var4 = (BiFunction)var3.next();
               }

               return var1x;
            };
      }

      return var10000;
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_FUNCTION_TYPE.byNameCodec().dispatch("function", LootItemFunction::getType, LootItemFunctionType::codec);
      ROOT_CODEC = Codec.lazyInitialized(() -> {
         return Codec.withAlternative(TYPED_CODEC, SequenceFunction.INLINE_CODEC);
      });
      CODEC = RegistryFileCodec.create(Registries.ITEM_MODIFIER, ROOT_CODEC);
      SET_COUNT = register("set_count", SetItemCountFunction.CODEC);
      SET_ITEM = register("set_item", SetItemFunction.CODEC);
      ENCHANT_WITH_LEVELS = register("enchant_with_levels", EnchantWithLevelsFunction.CODEC);
      ENCHANT_RANDOMLY = register("enchant_randomly", EnchantRandomlyFunction.CODEC);
      SET_ENCHANTMENTS = register("set_enchantments", SetEnchantmentsFunction.CODEC);
      SET_CUSTOM_DATA = register("set_custom_data", SetCustomDataFunction.CODEC);
      SET_COMPONENTS = register("set_components", SetComponentsFunction.CODEC);
      FURNACE_SMELT = register("furnace_smelt", SmeltItemFunction.CODEC);
      ENCHANTED_COUNT_INCREASE = register("enchanted_count_increase", EnchantedCountIncreaseFunction.CODEC);
      SET_DAMAGE = register("set_damage", SetItemDamageFunction.CODEC);
      SET_ATTRIBUTES = register("set_attributes", SetAttributesFunction.CODEC);
      SET_NAME = register("set_name", SetNameFunction.CODEC);
      EXPLORATION_MAP = register("exploration_map", ExplorationMapFunction.CODEC);
      SET_STEW_EFFECT = register("set_stew_effect", SetStewEffectFunction.CODEC);
      COPY_NAME = register("copy_name", CopyNameFunction.CODEC);
      SET_CONTENTS = register("set_contents", SetContainerContents.CODEC);
      MODIFY_CONTENTS = register("modify_contents", ModifyContainerContents.CODEC);
      FILTERED = register("filtered", FilteredFunction.CODEC);
      LIMIT_COUNT = register("limit_count", LimitCount.CODEC);
      APPLY_BONUS = register("apply_bonus", ApplyBonusCount.CODEC);
      SET_LOOT_TABLE = register("set_loot_table", SetContainerLootTable.CODEC);
      EXPLOSION_DECAY = register("explosion_decay", ApplyExplosionDecay.CODEC);
      SET_LORE = register("set_lore", SetLoreFunction.CODEC);
      FILL_PLAYER_HEAD = register("fill_player_head", FillPlayerHead.CODEC);
      COPY_CUSTOM_DATA = register("copy_custom_data", CopyCustomDataFunction.CODEC);
      COPY_STATE = register("copy_state", CopyBlockState.CODEC);
      SET_BANNER_PATTERN = register("set_banner_pattern", SetBannerPatternFunction.CODEC);
      SET_POTION = register("set_potion", SetPotionFunction.CODEC);
      SET_INSTRUMENT = register("set_instrument", SetInstrumentFunction.CODEC);
      REFERENCE = register("reference", FunctionReference.CODEC);
      SEQUENCE = register("sequence", SequenceFunction.CODEC);
      COPY_COMPONENTS = register("copy_components", CopyComponentsFunction.CODEC);
      SET_FIREWORKS = register("set_fireworks", SetFireworksFunction.CODEC);
      SET_FIREWORK_EXPLOSION = register("set_firework_explosion", SetFireworkExplosionFunction.CODEC);
      SET_BOOK_COVER = register("set_book_cover", SetBookCoverFunction.CODEC);
      SET_WRITTEN_BOOK_PAGES = register("set_written_book_pages", SetWrittenBookPagesFunction.CODEC);
      SET_WRITABLE_BOOK_PAGES = register("set_writable_book_pages", SetWritableBookPagesFunction.CODEC);
      TOGGLE_TOOLTIPS = register("toggle_tooltips", ToggleTooltips.CODEC);
      SET_OMINOUS_BOTTLE_AMPLIFIER = register("set_ominous_bottle_amplifier", SetOminousBottleAmplifierFunction.CODEC);
      SET_CUSTOM_MODEL_DATA = register("set_custom_model_data", SetCustomModelDataFunction.CODEC);
   }
}
