package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;

public record ItemTrimPredicate(Optional<HolderSet<TrimMaterial>> material, Optional<HolderSet<TrimPattern>> pattern) implements SingleComponentItemPredicate<ArmorTrim> {
   public static final Codec<ItemTrimPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.TRIM_MATERIAL).optionalFieldOf("material").forGetter(ItemTrimPredicate::material), RegistryCodecs.homogeneousList(Registries.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(ItemTrimPredicate::pattern)).apply(var0, ItemTrimPredicate::new);
   });

   public ItemTrimPredicate(Optional<HolderSet<TrimMaterial>> material, Optional<HolderSet<TrimPattern>> pattern) {
      super();
      this.material = material;
      this.pattern = pattern;
   }

   public DataComponentType<ArmorTrim> componentType() {
      return DataComponents.TRIM;
   }

   public boolean matches(ItemStack var1, ArmorTrim var2) {
      if (this.material.isPresent() && !((HolderSet)this.material.get()).contains(var2.material())) {
         return false;
      } else {
         return !this.pattern.isPresent() || ((HolderSet)this.pattern.get()).contains(var2.pattern());
      }
   }

   public Optional<HolderSet<TrimMaterial>> material() {
      return this.material;
   }

   public Optional<HolderSet<TrimPattern>> pattern() {
      return this.pattern;
   }
}
