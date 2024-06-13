package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ToggleTooltips extends LootItemConditionalFunction {
   private static final Map<DataComponentType<?>, ToggleTooltips.ComponentToggle<?>> TOGGLES = Stream.of(
         new ToggleTooltips.ComponentToggle<>(DataComponents.TRIM, ArmorTrim::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.DYED_COLOR, DyedItemColor::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.ENCHANTMENTS, ItemEnchantments::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.UNBREAKABLE, Unbreakable::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.CAN_BREAK, AdventureModePredicate::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.CAN_PLACE_ON, AdventureModePredicate::withTooltip),
         new ToggleTooltips.ComponentToggle<>(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers::withTooltip)
      )
      .collect(Collectors.toMap(ToggleTooltips.ComponentToggle::type, var0 -> (ToggleTooltips.ComponentToggle<?>)var0));
   private static final Codec<ToggleTooltips.ComponentToggle<?>> TOGGLE_CODEC = BuiltInRegistries.DATA_COMPONENT_TYPE
      .byNameCodec()
      .comapFlatMap(
         var0 -> {
            ToggleTooltips.ComponentToggle var1 = TOGGLES.get(var0);
            return var1 != null
               ? DataResult.success(var1)
               : DataResult.error(() -> "Can't toggle tooltip visiblity for " + BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var0));
         },
         ToggleTooltips.ComponentToggle::type
      );
   public static final MapCodec<ToggleTooltips> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(Codec.unboundedMap(TOGGLE_CODEC, Codec.BOOL).fieldOf("toggles").forGetter(var0x -> var0x.values))
            .apply(var0, ToggleTooltips::new)
   );
   private final Map<ToggleTooltips.ComponentToggle<?>, Boolean> values;

   private ToggleTooltips(List<LootItemCondition> var1, Map<ToggleTooltips.ComponentToggle<?>, Boolean> var2) {
      super(var1);
      this.values = var2;
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      this.values.forEach((var1x, var2x) -> var1x.applyIfPresent(var1, var2x));
      return var1;
   }

   @Override
   public LootItemFunctionType<ToggleTooltips> getType() {
      return LootItemFunctions.TOGGLE_TOOLTIPS;
   }

   static record ComponentToggle<T>(DataComponentType<T> type, ToggleTooltips.TooltipWither<T> setter) {
      ComponentToggle(DataComponentType<T> type, ToggleTooltips.TooltipWither<T> setter) {
         super();
         this.type = type;
         this.setter = setter;
      }

      public void applyIfPresent(ItemStack var1, boolean var2) {
         Object var3 = var1.get(this.type);
         if (var3 != null) {
            var1.set(this.type, this.setter.withTooltip((T)var3, var2));
         }
      }
   }

   @FunctionalInterface
   interface TooltipWither<T> {
      T withTooltip(T var1, boolean var2);
   }
}