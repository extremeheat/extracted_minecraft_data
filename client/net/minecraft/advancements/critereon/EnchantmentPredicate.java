package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentPredicate(Optional<HolderSet<Enchantment>> enchantments, MinMaxBounds.Ints level) {
   public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("enchantments").forGetter(EnchantmentPredicate::enchantments), MinMaxBounds.Ints.CODEC.optionalFieldOf("levels", MinMaxBounds.Ints.ANY).forGetter(EnchantmentPredicate::level)).apply(var0, EnchantmentPredicate::new));

   public EnchantmentPredicate(Holder<Enchantment> var1, MinMaxBounds.Ints var2) {
      this(Optional.of(HolderSet.direct(var1)), var2);
   }

   public EnchantmentPredicate(HolderSet<Enchantment> var1, MinMaxBounds.Ints var2) {
      this(Optional.of(var1), var2);
   }

   public EnchantmentPredicate(Optional<HolderSet<Enchantment>> var1, MinMaxBounds.Ints var2) {
      super();
      this.enchantments = var1;
      this.level = var2;
   }

   public boolean containedIn(ItemEnchantments var1) {
      if (this.enchantments.isPresent()) {
         for(Holder var5 : (HolderSet)this.enchantments.get()) {
            if (this.matchesEnchantment(var1, var5)) {
               return true;
            }
         }

         return false;
      } else if (this.level != MinMaxBounds.Ints.ANY) {
         for(Object2IntMap.Entry var3 : var1.entrySet()) {
            if (this.level.matches(var3.getIntValue())) {
               return true;
            }
         }

         return false;
      } else {
         return !var1.isEmpty();
      }
   }

   private boolean matchesEnchantment(ItemEnchantments var1, Holder<Enchantment> var2) {
      int var3 = var1.getLevel(var2);
      if (var3 == 0) {
         return false;
      } else {
         return this.level == MinMaxBounds.Ints.ANY ? true : this.level.matches(var3);
      }
   }
}
