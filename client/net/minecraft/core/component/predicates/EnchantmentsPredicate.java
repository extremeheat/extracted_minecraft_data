package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public abstract class EnchantmentsPredicate implements SingleComponentItemPredicate<ItemEnchantments> {
   private final List<EnchantmentPredicate> enchantments;

   protected EnchantmentsPredicate(final List<EnchantmentPredicate> enchantments) {
      super();
      this.enchantments = enchantments;
   }

   public static <T extends EnchantmentsPredicate> Codec<T> codec(final Function<List<EnchantmentPredicate>, T> constructor) {
      return EnchantmentPredicate.CODEC.listOf().xmap(constructor, EnchantmentsPredicate::enchantments);
   }

   protected List<EnchantmentPredicate> enchantments() {
      return this.enchantments;
   }

   public boolean matches(final ItemEnchantments appliedEnchantments) {
      for(EnchantmentPredicate enchantment : this.enchantments) {
         if (!enchantment.containedIn(appliedEnchantments)) {
            return false;
         }
      }

      return true;
   }

   public static Enchantments enchantments(final List<EnchantmentPredicate> predicates) {
      return new Enchantments(predicates);
   }

   public static StoredEnchantments storedEnchantments(final List<EnchantmentPredicate> predicates) {
      return new StoredEnchantments(predicates);
   }

   public static class Enchantments extends EnchantmentsPredicate {
      public static final Codec<Enchantments> CODEC = codec(Enchantments::new);

      protected Enchantments(final List<EnchantmentPredicate> enchantments) {
         super(enchantments);
      }

      public DataComponentType<ItemEnchantments> componentType() {
         return DataComponents.ENCHANTMENTS;
      }
   }

   public static class StoredEnchantments extends EnchantmentsPredicate {
      public static final Codec<StoredEnchantments> CODEC = codec(StoredEnchantments::new);

      protected StoredEnchantments(final List<EnchantmentPredicate> enchantments) {
         super(enchantments);
      }

      public DataComponentType<ItemEnchantments> componentType() {
         return DataComponents.STORED_ENCHANTMENTS;
      }
   }
}
