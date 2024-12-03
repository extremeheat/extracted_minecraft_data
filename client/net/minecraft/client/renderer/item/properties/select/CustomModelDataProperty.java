package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

public record CustomModelDataProperty(int index) implements SelectItemModelProperty<String> {
   public static final SelectItemModelProperty.Type<CustomModelDataProperty, String> TYPE;

   public CustomModelDataProperty(int var1) {
      super();
      this.index = var1;
   }

   @Nullable
   public String get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      CustomModelData var6 = (CustomModelData)var1.get(DataComponents.CUSTOM_MODEL_DATA);
      return var6 != null ? var6.getString(this.index) : null;
   }

   public SelectItemModelProperty.Type<CustomModelDataProperty, String> type() {
      return TYPE;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.<CustomModelDataProperty, String>create(RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataProperty::index)).apply(var0, CustomModelDataProperty::new)), Codec.STRING);
   }
}
