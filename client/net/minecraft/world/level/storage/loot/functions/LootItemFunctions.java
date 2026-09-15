package net.minecraft.world.level.storage.loot.functions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;

public class LootItemFunctions {
   public static final Codec<LootItemFunction> TYPED_CODEC;
   public static final Codec<LootItemFunction> DIRECT_CODEC;
   public static final Codec<Holder<LootItemFunction>> CODEC;
   public static final Codec<HolderSet<LootItemFunction>> LIST_CODEC;

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

   static {
      TYPED_CODEC = BuiltInRegistries.LOOT_FUNCTION_TYPE.byNameCodec().dispatch(LootItemFunction::codec, (c) -> c);
      DIRECT_CODEC = Codec.lazyInitialized(() -> Codec.either(TYPED_CODEC, SequenceFunction.INLINE_CODEC).xmap((typedOrList) -> (LootItemFunction)typedOrList.map((f) -> f, (f) -> f), (function) -> {
            Either var10000;
            if (function instanceof SequenceFunction sequence) {
               if (sequence.canUseInlineCodec()) {
                  var10000 = Either.right(sequence);
                  return var10000;
               }
            }

            var10000 = Either.left(function);
            return var10000;
         }));
      CODEC = RegistryCodecs.holder(Registries.ITEM_MODIFIER, DIRECT_CODEC);
      LIST_CODEC = RegistryCodecs.holderSet(Registries.ITEM_MODIFIER, TYPED_CODEC);
   }
}
