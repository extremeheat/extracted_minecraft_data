package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemSubPredicates {
   public static final ItemSubPredicate.Type<ItemDamagePredicate> DAMAGE;
   public static final ItemSubPredicate.Type<ItemEnchantmentsPredicate.Enchantments> ENCHANTMENTS;
   public static final ItemSubPredicate.Type<ItemEnchantmentsPredicate.StoredEnchantments> STORED_ENCHANTMENTS;
   public static final ItemSubPredicate.Type<ItemPotionsPredicate> POTIONS;
   public static final ItemSubPredicate.Type<ItemCustomDataPredicate> CUSTOM_DATA;
   public static final ItemSubPredicate.Type<ItemContainerPredicate> CONTAINER;
   public static final ItemSubPredicate.Type<ItemBundlePredicate> BUNDLE_CONTENTS;
   public static final ItemSubPredicate.Type<ItemFireworkExplosionPredicate> FIREWORK_EXPLOSION;
   public static final ItemSubPredicate.Type<ItemFireworksPredicate> FIREWORKS;
   public static final ItemSubPredicate.Type<ItemWritableBookPredicate> WRITABLE_BOOK;
   public static final ItemSubPredicate.Type<ItemWrittenBookPredicate> WRITTEN_BOOK;
   public static final ItemSubPredicate.Type<ItemAttributeModifiersPredicate> ATTRIBUTE_MODIFIERS;
   public static final ItemSubPredicate.Type<ItemTrimPredicate> ARMOR_TRIM;
   public static final ItemSubPredicate.Type<ItemJukeboxPlayablePredicate> JUKEBOX_PLAYABLE;

   public ItemSubPredicates() {
      super();
   }

   private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> register(String var0, Codec<T> var1) {
      return (ItemSubPredicate.Type)Registry.register(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE, (String)var0, new ItemSubPredicate.Type(var1));
   }

   public static ItemSubPredicate.Type<?> bootstrap(Registry<ItemSubPredicate.Type<?>> var0) {
      return DAMAGE;
   }

   static {
      DAMAGE = register("damage", ItemDamagePredicate.CODEC);
      ENCHANTMENTS = register("enchantments", ItemEnchantmentsPredicate.Enchantments.CODEC);
      STORED_ENCHANTMENTS = register("stored_enchantments", ItemEnchantmentsPredicate.StoredEnchantments.CODEC);
      POTIONS = register("potion_contents", ItemPotionsPredicate.CODEC);
      CUSTOM_DATA = register("custom_data", ItemCustomDataPredicate.CODEC);
      CONTAINER = register("container", ItemContainerPredicate.CODEC);
      BUNDLE_CONTENTS = register("bundle_contents", ItemBundlePredicate.CODEC);
      FIREWORK_EXPLOSION = register("firework_explosion", ItemFireworkExplosionPredicate.CODEC);
      FIREWORKS = register("fireworks", ItemFireworksPredicate.CODEC);
      WRITABLE_BOOK = register("writable_book_content", ItemWritableBookPredicate.CODEC);
      WRITTEN_BOOK = register("written_book_content", ItemWrittenBookPredicate.CODEC);
      ATTRIBUTE_MODIFIERS = register("attribute_modifiers", ItemAttributeModifiersPredicate.CODEC);
      ARMOR_TRIM = register("trim", ItemTrimPredicate.CODEC);
      JUKEBOX_PLAYABLE = register("jukebox_playable", ItemJukeboxPlayablePredicate.CODEC);
   }
}
