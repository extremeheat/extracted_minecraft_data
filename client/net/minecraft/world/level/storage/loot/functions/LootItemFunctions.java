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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootItemFunctions {
   public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = (stack, context) -> stack;
   public static final Codec<LootItemFunction> TYPED_CODEC;
   public static final Codec<LootItemFunction> ROOT_CODEC;
   public static final Codec<Holder<LootItemFunction>> CODEC;

   public LootItemFunctions() {
      super();
   }

   public static MapCodec<? extends LootItemFunction> bootstrap(final Registry<MapCodec<? extends LootItemFunction>> registry) {
      Registry.register(registry, (String)"set_count", SetItemCountFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_item", SetItemFunction.MAP_CODEC);
      Registry.register(registry, (String)"enchant_with_levels", EnchantWithLevelsFunction.MAP_CODEC);
      Registry.register(registry, (String)"enchant_randomly", EnchantRandomlyFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_enchantments", SetEnchantmentsFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_custom_data", SetCustomDataFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_components", SetComponentsFunction.MAP_CODEC);
      Registry.register(registry, (String)"furnace_smelt", SmeltItemFunction.MAP_CODEC);
      Registry.register(registry, (String)"enchanted_count_increase", EnchantedCountIncreaseFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_damage", SetItemDamageFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_attributes", SetAttributesFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_name", SetNameFunction.MAP_CODEC);
      Registry.register(registry, (String)"exploration_map", ExplorationMapFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_stew_effect", SetStewEffectFunction.MAP_CODEC);
      Registry.register(registry, (String)"copy_name", CopyNameFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_contents", SetContainerContents.MAP_CODEC);
      Registry.register(registry, (String)"modify_contents", ModifyContainerContents.MAP_CODEC);
      Registry.register(registry, (String)"filtered", FilteredFunction.MAP_CODEC);
      Registry.register(registry, (String)"limit_count", LimitCount.MAP_CODEC);
      Registry.register(registry, (String)"apply_bonus", ApplyBonusCount.MAP_CODEC);
      Registry.register(registry, (String)"set_loot_table", SetContainerLootTable.MAP_CODEC);
      Registry.register(registry, (String)"explosion_decay", ApplyExplosionDecay.MAP_CODEC);
      Registry.register(registry, (String)"set_lore", SetLoreFunction.MAP_CODEC);
      Registry.register(registry, (String)"fill_player_head", FillPlayerHead.MAP_CODEC);
      Registry.register(registry, (String)"copy_custom_data", CopyCustomDataFunction.MAP_CODEC);
      Registry.register(registry, (String)"copy_state", CopyBlockState.MAP_CODEC);
      Registry.register(registry, (String)"set_banner_pattern", SetBannerPatternFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_potion", SetPotionFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_random_dyes", SetRandomDyesFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_random_potion", SetRandomPotionFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_instrument", SetInstrumentFunction.MAP_CODEC);
      Registry.register(registry, (String)"reference", FunctionReference.MAP_CODEC);
      Registry.register(registry, (String)"sequence", SequenceFunction.MAP_CODEC);
      Registry.register(registry, (String)"copy_components", CopyComponentsFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_fireworks", SetFireworksFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_firework_explosion", SetFireworkExplosionFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_book_cover", SetBookCoverFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_written_book_pages", SetWrittenBookPagesFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_writable_book_pages", SetWritableBookPagesFunction.MAP_CODEC);
      Registry.register(registry, (String)"toggle_tooltips", ToggleTooltips.MAP_CODEC);
      Registry.register(registry, (String)"set_ominous_bottle_amplifier", SetOminousBottleAmplifierFunction.MAP_CODEC);
      Registry.register(registry, (String)"set_custom_model_data", SetCustomModelDataFunction.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"discard", DiscardItem.MAP_CODEC);
   }

   public static BiFunction<ItemStack, LootContext, ItemStack> compose(final List<? extends BiFunction<ItemStack, LootContext, ItemStack>> functions) {
      List<BiFunction<ItemStack, LootContext, ItemStack>> terms = List.copyOf(functions);
      BiFunction var10000;
      switch (terms.size()) {
         case 0:
            var10000 = IDENTITY;
            break;
         case 1:
            var10000 = (BiFunction)terms.get(0);
            break;
         case 2:
            BiFunction<ItemStack, LootContext, ItemStack> first = (BiFunction)terms.get(0);
            BiFunction<ItemStack, LootContext, ItemStack> second = (BiFunction)terms.get(1);
            var10000 = (itemStack, context) -> (ItemStack)second.apply((ItemStack)first.apply(itemStack, context), context);
            break;
         default:
            var10000 = (itemStack, context) -> {
               for(BiFunction<ItemStack, LootContext, ItemStack> function : terms) {
                  itemStack = (ItemStack)function.apply(itemStack, context);
               }

               return itemStack;
            };
      }

      return var10000;
   }

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_FUNCTION_TYPE.byNameCodec().dispatch("function", LootItemFunction::codec, (c) -> c);
      ROOT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, SequenceFunction.INLINE_CODEC));
      CODEC = RegistryFileCodec.<Holder<LootItemFunction>>create(Registries.ITEM_MODIFIER, ROOT_CODEC);
   }
}
