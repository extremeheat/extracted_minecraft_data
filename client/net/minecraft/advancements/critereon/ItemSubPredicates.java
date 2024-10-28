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
   }
}
