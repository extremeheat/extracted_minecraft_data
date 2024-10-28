package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.EncoderCache;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.LockCode;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DebugStickState;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.saveddata.maps.MapId;

public class DataComponents {
   static final EncoderCache ENCODER_CACHE = new EncoderCache(512);
   public static final DataComponentType<CustomData> CUSTOM_DATA = register("custom_data", (var0) -> {
      return var0.persistent(CustomData.CODEC);
   });
   public static final DataComponentType<Integer> MAX_STACK_SIZE = register("max_stack_size", (var0) -> {
      return var0.persistent(ExtraCodecs.intRange(1, 99)).networkSynchronized(ByteBufCodecs.VAR_INT);
   });
   public static final DataComponentType<Integer> MAX_DAMAGE = register("max_damage", (var0) -> {
      return var0.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT);
   });
   public static final DataComponentType<Integer> DAMAGE = register("damage", (var0) -> {
      return var0.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT);
   });
   public static final DataComponentType<Unbreakable> UNBREAKABLE = register("unbreakable", (var0) -> {
      return var0.persistent(Unbreakable.CODEC).networkSynchronized(Unbreakable.STREAM_CODEC);
   });
   public static final DataComponentType<Component> CUSTOM_NAME = register("custom_name", (var0) -> {
      return var0.persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<Component> ITEM_NAME = register("item_name", (var0) -> {
      return var0.persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ItemLore> LORE = register("lore", (var0) -> {
      return var0.persistent(ItemLore.CODEC).networkSynchronized(ItemLore.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<Rarity> RARITY = register("rarity", (var0) -> {
      return var0.persistent(Rarity.CODEC).networkSynchronized(Rarity.STREAM_CODEC);
   });
   public static final DataComponentType<ItemEnchantments> ENCHANTMENTS = register("enchantments", (var0) -> {
      return var0.persistent(ItemEnchantments.CODEC).networkSynchronized(ItemEnchantments.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<AdventureModePredicate> CAN_PLACE_ON = register("can_place_on", (var0) -> {
      return var0.persistent(AdventureModePredicate.CODEC).networkSynchronized(AdventureModePredicate.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<AdventureModePredicate> CAN_BREAK = register("can_break", (var0) -> {
      return var0.persistent(AdventureModePredicate.CODEC).networkSynchronized(AdventureModePredicate.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ItemAttributeModifiers> ATTRIBUTE_MODIFIERS = register("attribute_modifiers", (var0) -> {
      return var0.persistent(ItemAttributeModifiers.CODEC).networkSynchronized(ItemAttributeModifiers.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<CustomModelData> CUSTOM_MODEL_DATA = register("custom_model_data", (var0) -> {
      return var0.persistent(CustomModelData.CODEC).networkSynchronized(CustomModelData.STREAM_CODEC);
   });
   public static final DataComponentType<Unit> HIDE_ADDITIONAL_TOOLTIP = register("hide_additional_tooltip", (var0) -> {
      return var0.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE));
   });
   public static final DataComponentType<Unit> HIDE_TOOLTIP = register("hide_tooltip", (var0) -> {
      return var0.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE));
   });
   public static final DataComponentType<Integer> REPAIR_COST = register("repair_cost", (var0) -> {
      return var0.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT);
   });
   public static final DataComponentType<Unit> CREATIVE_SLOT_LOCK = register("creative_slot_lock", (var0) -> {
      return var0.networkSynchronized(StreamCodec.unit(Unit.INSTANCE));
   });
   public static final DataComponentType<Boolean> ENCHANTMENT_GLINT_OVERRIDE = register("enchantment_glint_override", (var0) -> {
      return var0.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL);
   });
   public static final DataComponentType<Unit> INTANGIBLE_PROJECTILE = register("intangible_projectile", (var0) -> {
      return var0.persistent(Unit.CODEC);
   });
   public static final DataComponentType<FoodProperties> FOOD = register("food", (var0) -> {
      return var0.persistent(FoodProperties.DIRECT_CODEC).networkSynchronized(FoodProperties.DIRECT_STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<Unit> FIRE_RESISTANT = register("fire_resistant", (var0) -> {
      return var0.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE));
   });
   public static final DataComponentType<Tool> TOOL = register("tool", (var0) -> {
      return var0.persistent(Tool.CODEC).networkSynchronized(Tool.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ItemEnchantments> STORED_ENCHANTMENTS = register("stored_enchantments", (var0) -> {
      return var0.persistent(ItemEnchantments.CODEC).networkSynchronized(ItemEnchantments.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<DyedItemColor> DYED_COLOR = register("dyed_color", (var0) -> {
      return var0.persistent(DyedItemColor.CODEC).networkSynchronized(DyedItemColor.STREAM_CODEC);
   });
   public static final DataComponentType<MapItemColor> MAP_COLOR = register("map_color", (var0) -> {
      return var0.persistent(MapItemColor.CODEC).networkSynchronized(MapItemColor.STREAM_CODEC);
   });
   public static final DataComponentType<MapId> MAP_ID = register("map_id", (var0) -> {
      return var0.persistent(MapId.CODEC).networkSynchronized(MapId.STREAM_CODEC);
   });
   public static final DataComponentType<MapDecorations> MAP_DECORATIONS = register("map_decorations", (var0) -> {
      return var0.persistent(MapDecorations.CODEC).cacheEncoding();
   });
   public static final DataComponentType<MapPostProcessing> MAP_POST_PROCESSING = register("map_post_processing", (var0) -> {
      return var0.networkSynchronized(MapPostProcessing.STREAM_CODEC);
   });
   public static final DataComponentType<ChargedProjectiles> CHARGED_PROJECTILES = register("charged_projectiles", (var0) -> {
      return var0.persistent(ChargedProjectiles.CODEC).networkSynchronized(ChargedProjectiles.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<BundleContents> BUNDLE_CONTENTS = register("bundle_contents", (var0) -> {
      return var0.persistent(BundleContents.CODEC).networkSynchronized(BundleContents.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<PotionContents> POTION_CONTENTS = register("potion_contents", (var0) -> {
      return var0.persistent(PotionContents.CODEC).networkSynchronized(PotionContents.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<SuspiciousStewEffects> SUSPICIOUS_STEW_EFFECTS = register("suspicious_stew_effects", (var0) -> {
      return var0.persistent(SuspiciousStewEffects.CODEC).networkSynchronized(SuspiciousStewEffects.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<WritableBookContent> WRITABLE_BOOK_CONTENT = register("writable_book_content", (var0) -> {
      return var0.persistent(WritableBookContent.CODEC).networkSynchronized(WritableBookContent.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<WrittenBookContent> WRITTEN_BOOK_CONTENT = register("written_book_content", (var0) -> {
      return var0.persistent(WrittenBookContent.CODEC).networkSynchronized(WrittenBookContent.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ArmorTrim> TRIM = register("trim", (var0) -> {
      return var0.persistent(ArmorTrim.CODEC).networkSynchronized(ArmorTrim.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<DebugStickState> DEBUG_STICK_STATE = register("debug_stick_state", (var0) -> {
      return var0.persistent(DebugStickState.CODEC).cacheEncoding();
   });
   public static final DataComponentType<CustomData> ENTITY_DATA = register("entity_data", (var0) -> {
      return var0.persistent(CustomData.CODEC_WITH_ID).networkSynchronized(CustomData.STREAM_CODEC);
   });
   public static final DataComponentType<CustomData> BUCKET_ENTITY_DATA = register("bucket_entity_data", (var0) -> {
      return var0.persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC);
   });
   public static final DataComponentType<CustomData> BLOCK_ENTITY_DATA = register("block_entity_data", (var0) -> {
      return var0.persistent(CustomData.CODEC_WITH_ID).networkSynchronized(CustomData.STREAM_CODEC);
   });
   public static final DataComponentType<Holder<Instrument>> INSTRUMENT = register("instrument", (var0) -> {
      return var0.persistent(Instrument.CODEC).networkSynchronized(Instrument.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<Integer> OMINOUS_BOTTLE_AMPLIFIER = register("ominous_bottle_amplifier", (var0) -> {
      return var0.persistent(ExtraCodecs.intRange(0, 4)).networkSynchronized(ByteBufCodecs.VAR_INT);
   });
   public static final DataComponentType<JukeboxPlayable> JUKEBOX_PLAYABLE = register("jukebox_playable", (var0) -> {
      return var0.persistent(JukeboxPlayable.CODEC).networkSynchronized(JukeboxPlayable.STREAM_CODEC);
   });
   public static final DataComponentType<List<ResourceLocation>> RECIPES = register("recipes", (var0) -> {
      return var0.persistent(ResourceLocation.CODEC.listOf()).cacheEncoding();
   });
   public static final DataComponentType<LodestoneTracker> LODESTONE_TRACKER = register("lodestone_tracker", (var0) -> {
      return var0.persistent(LodestoneTracker.CODEC).networkSynchronized(LodestoneTracker.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<FireworkExplosion> FIREWORK_EXPLOSION = register("firework_explosion", (var0) -> {
      return var0.persistent(FireworkExplosion.CODEC).networkSynchronized(FireworkExplosion.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<Fireworks> FIREWORKS = register("fireworks", (var0) -> {
      return var0.persistent(Fireworks.CODEC).networkSynchronized(Fireworks.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ResolvableProfile> PROFILE = register("profile", (var0) -> {
      return var0.persistent(ResolvableProfile.CODEC).networkSynchronized(ResolvableProfile.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ResourceLocation> NOTE_BLOCK_SOUND = register("note_block_sound", (var0) -> {
      return var0.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC);
   });
   public static final DataComponentType<BannerPatternLayers> BANNER_PATTERNS = register("banner_patterns", (var0) -> {
      return var0.persistent(BannerPatternLayers.CODEC).networkSynchronized(BannerPatternLayers.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<DyeColor> BASE_COLOR = register("base_color", (var0) -> {
      return var0.persistent(DyeColor.CODEC).networkSynchronized(DyeColor.STREAM_CODEC);
   });
   public static final DataComponentType<PotDecorations> POT_DECORATIONS = register("pot_decorations", (var0) -> {
      return var0.persistent(PotDecorations.CODEC).networkSynchronized(PotDecorations.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<ItemContainerContents> CONTAINER = register("container", (var0) -> {
      return var0.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<BlockItemStateProperties> BLOCK_STATE = register("block_state", (var0) -> {
      return var0.persistent(BlockItemStateProperties.CODEC).networkSynchronized(BlockItemStateProperties.STREAM_CODEC).cacheEncoding();
   });
   public static final DataComponentType<List<BeehiveBlockEntity.Occupant>> BEES = register("bees", (var0) -> {
      return var0.persistent(BeehiveBlockEntity.Occupant.LIST_CODEC).networkSynchronized(BeehiveBlockEntity.Occupant.STREAM_CODEC.apply(ByteBufCodecs.list())).cacheEncoding();
   });
   public static final DataComponentType<LockCode> LOCK = register("lock", (var0) -> {
      return var0.persistent(LockCode.CODEC);
   });
   public static final DataComponentType<SeededContainerLoot> CONTAINER_LOOT = register("container_loot", (var0) -> {
      return var0.persistent(SeededContainerLoot.CODEC);
   });
   public static final DataComponentMap COMMON_ITEM_COMPONENTS;

   public DataComponents() {
      super();
   }

   public static DataComponentType<?> bootstrap(Registry<DataComponentType<?>> var0) {
      return CUSTOM_DATA;
   }

   private static <T> DataComponentType<T> register(String var0, UnaryOperator<DataComponentType.Builder<T>> var1) {
      return (DataComponentType)Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, (String)var0, ((DataComponentType.Builder)var1.apply(DataComponentType.builder())).build());
   }

   static {
      COMMON_ITEM_COMPONENTS = DataComponentMap.builder().set(MAX_STACK_SIZE, 64).set(LORE, ItemLore.EMPTY).set(ENCHANTMENTS, ItemEnchantments.EMPTY).set(REPAIR_COST, 0).set(ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).set(RARITY, Rarity.COMMON).build();
   }
}
