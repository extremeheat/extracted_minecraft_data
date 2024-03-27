package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
   public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = (var0, var1) -> var0;
   public static final Codec<LootItemFunction> TYPED_CODEC = BuiltInRegistries.LOOT_FUNCTION_TYPE
      .byNameCodec()
      .dispatch("function", LootItemFunction::getType, LootItemFunctionType::codec);
   public static final Codec<LootItemFunction> ROOT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, SequenceFunction.INLINE_CODEC));
   public static final Codec<Holder<LootItemFunction>> CODEC = RegistryFileCodec.create(Registries.ITEM_MODIFIER, ROOT_CODEC);
   public static final LootItemFunctionType SET_COUNT = register("set_count", SetItemCountFunction.CODEC);
   public static final LootItemFunctionType ENCHANT_WITH_LEVELS = register("enchant_with_levels", EnchantWithLevelsFunction.CODEC);
   public static final LootItemFunctionType ENCHANT_RANDOMLY = register("enchant_randomly", EnchantRandomlyFunction.CODEC);
   public static final LootItemFunctionType SET_ENCHANTMENTS = register("set_enchantments", SetEnchantmentsFunction.CODEC);
   public static final LootItemFunctionType SET_CUSTOM_DATA = register("set_custom_data", SetCustomDataFunction.CODEC);
   public static final LootItemFunctionType SET_COMPONENTS = register("set_components", SetComponentsFunction.CODEC);
   public static final LootItemFunctionType FURNACE_SMELT = register("furnace_smelt", SmeltItemFunction.CODEC);
   public static final LootItemFunctionType LOOTING_ENCHANT = register("looting_enchant", LootingEnchantFunction.CODEC);
   public static final LootItemFunctionType SET_DAMAGE = register("set_damage", SetItemDamageFunction.CODEC);
   public static final LootItemFunctionType SET_ATTRIBUTES = register("set_attributes", SetAttributesFunction.CODEC);
   public static final LootItemFunctionType SET_NAME = register("set_name", SetNameFunction.CODEC);
   public static final LootItemFunctionType EXPLORATION_MAP = register("exploration_map", ExplorationMapFunction.CODEC);
   public static final LootItemFunctionType SET_STEW_EFFECT = register("set_stew_effect", SetStewEffectFunction.CODEC);
   public static final LootItemFunctionType COPY_NAME = register("copy_name", CopyNameFunction.CODEC);
   public static final LootItemFunctionType SET_CONTENTS = register("set_contents", SetContainerContents.CODEC);
   public static final LootItemFunctionType LIMIT_COUNT = register("limit_count", LimitCount.CODEC);
   public static final LootItemFunctionType APPLY_BONUS = register("apply_bonus", ApplyBonusCount.CODEC);
   public static final LootItemFunctionType SET_LOOT_TABLE = register("set_loot_table", SetContainerLootTable.CODEC);
   public static final LootItemFunctionType EXPLOSION_DECAY = register("explosion_decay", ApplyExplosionDecay.CODEC);
   public static final LootItemFunctionType SET_LORE = register("set_lore", SetLoreFunction.CODEC);
   public static final LootItemFunctionType FILL_PLAYER_HEAD = register("fill_player_head", FillPlayerHead.CODEC);
   public static final LootItemFunctionType COPY_CUSTOM_DATA = register("copy_custom_data", CopyCustomDataFunction.CODEC);
   public static final LootItemFunctionType COPY_STATE = register("copy_state", CopyBlockState.CODEC);
   public static final LootItemFunctionType SET_BANNER_PATTERN = register("set_banner_pattern", SetBannerPatternFunction.CODEC);
   public static final LootItemFunctionType SET_POTION = register("set_potion", SetPotionFunction.CODEC);
   public static final LootItemFunctionType SET_INSTRUMENT = register("set_instrument", SetInstrumentFunction.CODEC);
   public static final LootItemFunctionType REFERENCE = register("reference", FunctionReference.CODEC);
   public static final LootItemFunctionType SEQUENCE = register("sequence", SequenceFunction.CODEC);
   public static final LootItemFunctionType COPY_COMPONENTS = register("copy_components", CopyComponentsFunction.CODEC);
   public static final LootItemFunctionType SET_FIREWORKS = register("set_fireworks", SetFireworksFunction.CODEC);
   public static final LootItemFunctionType SET_FIREWORK_EXPLOSION = register("set_firework_explosion", SetFireworkExplosionFunction.CODEC);
   public static final LootItemFunctionType SET_BOOK_COVER = register("set_book_cover", SetBookCoverFunction.CODEC);
   public static final LootItemFunctionType SET_WRITTEN_BOOK_PAGES = register("set_written_book_pages", SetWrittenBookPagesFunction.CODEC);
   public static final LootItemFunctionType SET_WRITABLE_BOOK_PAGES = register("set_writable_book_pages", SetWritableBookPagesFunction.CODEC);
   public static final LootItemFunctionType TOGGLE_TOOLTIPS = register("toggle_tooltips", ToggleTooltips.CODEC);
   public static final LootItemFunctionType SET_OMINOUS_BOTTLE_AMPLIFIER = register("set_ominous_bottle_amplifier", SetOminousBottleAmplifierFunction.CODEC);

   public LootItemFunctions() {
      super();
   }

   private static LootItemFunctionType register(String var0, MapCodec<? extends LootItemFunction> var1) {
      return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, new ResourceLocation(var0), new LootItemFunctionType(var1));
   }

   public static BiFunction<ItemStack, LootContext, ItemStack> compose(List<? extends BiFunction<ItemStack, LootContext, ItemStack>> var0) {
      List var1 = List.copyOf(var0);

      return switch(var1.size()) {
         case 0 -> IDENTITY;
         case 1 -> (BiFunction)var1.get(0);
         case 2 -> {
            BiFunction var2 = (BiFunction)var1.get(0);
            BiFunction var3 = (BiFunction)var1.get(1);
            yield (var2x, var3x) -> (ItemStack)var3.apply((ItemStack)var2.apply(var2x, var3x), var3x);
         }
         default -> (var1x, var2x) -> {
         for(BiFunction var4 : var1) {
            var1x = (ItemStack)var4.apply(var1x, var2x);
         }

         return var1x;
      };
      };
   }
}
