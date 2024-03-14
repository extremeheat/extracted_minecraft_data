package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public abstract class ItemEnchantmentsPredicate implements SingleComponentItemPredicate<ItemEnchantments> {
   private final List<EnchantmentPredicate> enchantments;

   protected ItemEnchantmentsPredicate(List<EnchantmentPredicate> var1) {
      super();
      this.enchantments = var1;
   }

   public static <T extends ItemEnchantmentsPredicate> Codec<T> codec(Function<List<EnchantmentPredicate>, T> var0) {
      return EnchantmentPredicate.CODEC.listOf().xmap(var0, ItemEnchantmentsPredicate::enchantments);
   }

   protected List<EnchantmentPredicate> enchantments() {
      return this.enchantments;
   }

   public boolean matches(ItemStack var1, ItemEnchantments var2) {
      for(EnchantmentPredicate var4 : this.enchantments) {
         if (!var4.containedIn(var2)) {
            return false;
         }
      }

      return true;
   }

   public static ItemEnchantmentsPredicate.Enchantments enchantments(List<EnchantmentPredicate> var0) {
      return new ItemEnchantmentsPredicate.Enchantments(var0);
   }

   public static ItemEnchantmentsPredicate.StoredEnchantments storedEnchantments(List<EnchantmentPredicate> var0) {
      return new ItemEnchantmentsPredicate.StoredEnchantments(var0);
   }

   public static class Enchantments extends ItemEnchantmentsPredicate {
      public static final Codec<ItemEnchantmentsPredicate.Enchantments> CODEC = codec(ItemEnchantmentsPredicate.Enchantments::new);

      protected Enchantments(List<EnchantmentPredicate> var1) {
         super(var1);
      }

      @Override
      public DataComponentType<ItemEnchantments> componentType() {
         return DataComponents.ENCHANTMENTS;
      }
   }

   public static class StoredEnchantments extends ItemEnchantmentsPredicate {
      public static final Codec<ItemEnchantmentsPredicate.StoredEnchantments> CODEC = codec(ItemEnchantmentsPredicate.StoredEnchantments::new);

      protected StoredEnchantments(List<EnchantmentPredicate> var1) {
         super(var1);
      }

      @Override
      public DataComponentType<ItemEnchantments> componentType() {
         return DataComponents.STORED_ENCHANTMENTS;
      }
   }
}
