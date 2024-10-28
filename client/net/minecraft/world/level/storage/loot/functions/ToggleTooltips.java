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
   private static final Map<DataComponentType<?>, ComponentToggle<?>> TOGGLES;
   private static final Codec<ComponentToggle<?>> TOGGLE_CODEC;
   public static final MapCodec<ToggleTooltips> CODEC;
   private final Map<ComponentToggle<?>, Boolean> values;

   private ToggleTooltips(List<LootItemCondition> var1, Map<ComponentToggle<?>, Boolean> var2) {
      super(var1);
      this.values = var2;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      this.values.forEach((var1x, var2x) -> {
         var1x.applyIfPresent(var1, var2x);
      });
      return var1;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.TOGGLE_TOOLTIPS;
   }

   static {
      TOGGLES = (Map)Stream.of(new ComponentToggle(DataComponents.TRIM, ArmorTrim::withTooltip), new ComponentToggle(DataComponents.DYED_COLOR, DyedItemColor::withTooltip), new ComponentToggle(DataComponents.ENCHANTMENTS, ItemEnchantments::withTooltip), new ComponentToggle(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments::withTooltip), new ComponentToggle(DataComponents.UNBREAKABLE, Unbreakable::withTooltip), new ComponentToggle(DataComponents.CAN_BREAK, AdventureModePredicate::withTooltip), new ComponentToggle(DataComponents.CAN_PLACE_ON, AdventureModePredicate::withTooltip), new ComponentToggle(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers::withTooltip)).collect(Collectors.toMap(ComponentToggle::type, (var0) -> {
         return var0;
      }));
      TOGGLE_CODEC = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().comapFlatMap((var0) -> {
         ComponentToggle var1 = (ComponentToggle)TOGGLES.get(var0);
         return var1 != null ? DataResult.success(var1) : DataResult.error(() -> {
            return "Can't toggle tooltip visiblity for " + String.valueOf(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var0));
         });
      }, ComponentToggle::type);
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return commonFields(var0).and(Codec.unboundedMap(TOGGLE_CODEC, Codec.BOOL).fieldOf("toggles").forGetter((var0x) -> {
            return var0x.values;
         })).apply(var0, ToggleTooltips::new);
      });
   }

   private static record ComponentToggle<T>(DataComponentType<T> type, TooltipWither<T> setter) {
      ComponentToggle(DataComponentType<T> var1, TooltipWither<T> var2) {
         super();
         this.type = var1;
         this.setter = var2;
      }

      public void applyIfPresent(ItemStack var1, boolean var2) {
         Object var3 = var1.get(this.type);
         if (var3 != null) {
            var1.set(this.type, this.setter.withTooltip(var3, var2));
         }

      }

      public DataComponentType<T> type() {
         return this.type;
      }

      public TooltipWither<T> setter() {
         return this.setter;
      }
   }

   @FunctionalInterface
   private interface TooltipWither<T> {
      T withTooltip(T var1, boolean var2);
   }
}
