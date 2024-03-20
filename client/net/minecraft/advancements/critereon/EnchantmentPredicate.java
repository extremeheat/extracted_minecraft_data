package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentPredicate(Optional<Holder<Enchantment>> b, MinMaxBounds.Ints c) {
   private final Optional<Holder<Enchantment>> enchantment;
   private final MinMaxBounds.Ints level;
   public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(BuiltInRegistries.ENCHANTMENT.holderByNameCodec(), "enchantment").forGetter(EnchantmentPredicate::enchantment),
               ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "levels", MinMaxBounds.Ints.ANY).forGetter(EnchantmentPredicate::level)
            )
            .apply(var0, EnchantmentPredicate::new)
   );

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
         Enchantment var2 = this.enchantment.get().value();
         int var3 = var1.getLevel(var2);
         if (var3 == 0) {
            return false;
         }

         if (this.level != MinMaxBounds.Ints.ANY && !this.level.matches(var3)) {
            return false;
         }
      } else if (this.level != MinMaxBounds.Ints.ANY) {
         for(Entry var5 : var1.entrySet()) {
            if (this.level.matches(var5.getIntValue())) {
               return true;
            }
         }

         return false;
      }

      return true;
   }
}