package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentPredicate(Optional<Holder<Enchantment>> enchantment, MinMaxBounds.Ints level) {
   public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BuiltInRegistries.ENCHANTMENT.holderByNameCodec().optionalFieldOf("enchantment").forGetter(EnchantmentPredicate::enchantment), MinMaxBounds.Ints.CODEC.optionalFieldOf("levels", MinMaxBounds.Ints.ANY).forGetter(EnchantmentPredicate::level)).apply(var0, EnchantmentPredicate::new);
   });

   public EnchantmentPredicate(Enchantment var1, MinMaxBounds.Ints var2) {
      this(Optional.of(var1.builtInRegistryHolder()), var2);
   }

   public EnchantmentPredicate(Optional<Holder<Enchantment>> var1, MinMaxBounds.Ints var2) {
      super();
      this.enchantment = var1;
      this.level = var2;
   }

   public boolean containedIn(ItemEnchantments var1) {
      if (this.enchantment.isPresent()) {
         Enchantment var2 = (Enchantment)((Holder)this.enchantment.get()).value();
         int var3 = var1.getLevel(var2);
         if (var3 == 0) {
            return false;
         }

         if (this.level != MinMaxBounds.Ints.ANY && !this.level.matches(var3)) {
            return false;
         }
      } else if (this.level != MinMaxBounds.Ints.ANY) {
         Iterator var4 = var1.entrySet().iterator();

         Object2IntMap.Entry var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (Object2IntMap.Entry)var4.next();
         } while(!this.level.matches(var5.getIntValue()));

         return true;
      }

      return true;
   }

   public Optional<Holder<Enchantment>> enchantment() {
      return this.enchantment;
   }

   public MinMaxBounds.Ints level() {
      return this.level;
   }
}
