package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ToggleTooltips extends LootItemConditionalFunction {
   public static final MapCodec<ToggleTooltips> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(Codec.unboundedMap(DataComponentType.CODEC, Codec.BOOL).fieldOf("toggles").forGetter((var0x) -> var0x.values)).apply(var0, ToggleTooltips::new));
   private final Map<DataComponentType<?>, Boolean> values;

   private ToggleTooltips(List<LootItemCondition> var1, Map<DataComponentType<?>, Boolean> var2) {
      super(var1);
      this.values = var2;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT, (var1x) -> {
         for(Map.Entry var3 : this.values.entrySet()) {
            boolean var4 = (Boolean)var3.getValue();
            var1x = var1x.withHidden((DataComponentType)var3.getKey(), !var4);
         }

         return var1x;
      });
      return var1;
   }

   public LootItemFunctionType<ToggleTooltips> getType() {
      return LootItemFunctions.TOGGLE_TOOLTIPS;
   }
}
