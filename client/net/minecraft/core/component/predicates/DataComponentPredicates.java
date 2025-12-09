package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class DataComponentPredicates {
   public static final DataComponentPredicate.Type<DamagePredicate> DAMAGE;
   public static final DataComponentPredicate.Type<EnchantmentsPredicate.Enchantments> ENCHANTMENTS;
   public static final DataComponentPredicate.Type<EnchantmentsPredicate.StoredEnchantments> STORED_ENCHANTMENTS;
   public static final DataComponentPredicate.Type<PotionsPredicate> POTIONS;
   public static final DataComponentPredicate.Type<CustomDataPredicate> CUSTOM_DATA;
   public static final DataComponentPredicate.Type<ContainerPredicate> CONTAINER;
   public static final DataComponentPredicate.Type<BundlePredicate> BUNDLE_CONTENTS;
   public static final DataComponentPredicate.Type<FireworkExplosionPredicate> FIREWORK_EXPLOSION;
   public static final DataComponentPredicate.Type<FireworksPredicate> FIREWORKS;
   public static final DataComponentPredicate.Type<WritableBookPredicate> WRITABLE_BOOK;
   public static final DataComponentPredicate.Type<WrittenBookPredicate> WRITTEN_BOOK;
   public static final DataComponentPredicate.Type<AttributeModifiersPredicate> ATTRIBUTE_MODIFIERS;
   public static final DataComponentPredicate.Type<TrimPredicate> ARMOR_TRIM;
   public static final DataComponentPredicate.Type<JukeboxPlayablePredicate> JUKEBOX_PLAYABLE;

   public DataComponentPredicates() {
      super();
   }

   private static <T extends DataComponentPredicate> DataComponentPredicate.Type<T> register(String var0, Codec<T> var1) {
      return (DataComponentPredicate.Type)Registry.register(BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE, (String)var0, new DataComponentPredicate.ConcreteType(var1));
   }

   public static DataComponentPredicate.Type<?> bootstrap(Registry<DataComponentPredicate.Type<?>> var0) {
      return DAMAGE;
   }

   static {
      DAMAGE = register("damage", DamagePredicate.CODEC);
      ENCHANTMENTS = register("enchantments", EnchantmentsPredicate.Enchantments.CODEC);
      STORED_ENCHANTMENTS = register("stored_enchantments", EnchantmentsPredicate.StoredEnchantments.CODEC);
      POTIONS = register("potion_contents", PotionsPredicate.CODEC);
      CUSTOM_DATA = register("custom_data", CustomDataPredicate.CODEC);
      CONTAINER = register("container", ContainerPredicate.CODEC);
      BUNDLE_CONTENTS = register("bundle_contents", BundlePredicate.CODEC);
      FIREWORK_EXPLOSION = register("firework_explosion", FireworkExplosionPredicate.CODEC);
      FIREWORKS = register("fireworks", FireworksPredicate.CODEC);
      WRITABLE_BOOK = register("writable_book_content", WritableBookPredicate.CODEC);
      WRITTEN_BOOK = register("written_book_content", WrittenBookPredicate.CODEC);
      ATTRIBUTE_MODIFIERS = register("attribute_modifiers", AttributeModifiersPredicate.CODEC);
      ARMOR_TRIM = register("trim", TrimPredicate.CODEC);
      JUKEBOX_PLAYABLE = register("jukebox_playable", JukeboxPlayablePredicate.CODEC);
   }
}
