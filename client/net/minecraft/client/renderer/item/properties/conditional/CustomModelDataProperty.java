package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

public record CustomModelDataProperty(int index) implements ConditionalItemModelProperty {
   public static final MapCodec<CustomModelDataProperty> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataProperty::index)).apply(var0, CustomModelDataProperty::new));

   public CustomModelDataProperty(int var1) {
      super();
      this.index = var1;
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      CustomModelData var6 = (CustomModelData)var1.get(DataComponents.CUSTOM_MODEL_DATA);
      if (var6 != null) {
         return var6.getBoolean(this.index) == Boolean.TRUE;
      } else {
         return false;
      }
   }

   public MapCodec<CustomModelDataProperty> type() {
      return MAP_CODEC;
   }
}
