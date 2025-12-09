package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public abstract class EnchantmentsPredicate implements SingleComponentItemPredicate<ItemEnchantments> {
   private final List<EnchantmentPredicate> enchantments;

   protected EnchantmentsPredicate(List<EnchantmentPredicate> var1) {
      super();
      this.enchantments = var1;
   }

   public static <T extends EnchantmentsPredicate> Codec<T> codec(Function<List<EnchantmentPredicate>, T> var0) {
      return EnchantmentPredicate.CODEC.listOf().xmap(var0, EnchantmentsPredicate::enchantments);
   }

   protected List<EnchantmentPredicate> enchantments() {
      return this.enchantments;
   }

   public boolean matches(ItemEnchantments var1) {
      for(EnchantmentPredicate var3 : this.enchantments) {
         if (!var3.containedIn(var1)) {
            return false;
         }
      }

      return true;
   }

   public static Enchantments enchantments(List<EnchantmentPredicate> var0) {
      return new Enchantments(var0);
   }

   public static StoredEnchantments storedEnchantments(List<EnchantmentPredicate> var0) {
      return new StoredEnchantments(var0);
   }

   public static class Enchantments extends EnchantmentsPredicate {
      public static final Codec<Enchantments> CODEC = codec(Enchantments::new);

      protected Enchantments(List<EnchantmentPredicate> var1) {
         super(var1);
      }

      public DataComponentType<ItemEnchantments> componentType() {
         return DataComponents.ENCHANTMENTS;
      }
   }

   public static class StoredEnchantments extends EnchantmentsPredicate {
      public static final Codec<StoredEnchantments> CODEC = codec(StoredEnchantments::new);

      protected StoredEnchantments(List<EnchantmentPredicate> var1) {
         super(var1);
      }

      public DataComponentType<ItemEnchantments> componentType() {
         return DataComponents.STORED_ENCHANTMENTS;
      }
   }
}
