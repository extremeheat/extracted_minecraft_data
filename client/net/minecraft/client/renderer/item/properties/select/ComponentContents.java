package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record ComponentContents<T>(DataComponentType<T> componentType) implements SelectItemModelProperty<T> {
   private static final SelectItemModelProperty.Type<? extends ComponentContents<?>, ?> TYPE = createType();

   public ComponentContents(DataComponentType<T> var1) {
      super();
      this.componentType = var1;
   }

   private static <T> SelectItemModelProperty.Type<ComponentContents<T>, T> createType() {
      Codec var0 = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().validate((var0x) -> var0x.isTransient() ? DataResult.error(() -> "Component can't be serialized") : DataResult.success(var0x));
      MapCodec var2 = var0.dispatchMap("component", (var0x) -> ((ComponentContents)var0x.property()).componentType, (var0x) -> SelectItemModelProperty.Type.createCasesFieldCodec(var0x.codecOrThrow()).xmap((var1) -> new SelectItemModel.UnbakedSwitch(new ComponentContents(var0x), var1), SelectItemModel.UnbakedSwitch::cases));
      return new SelectItemModelProperty.Type<ComponentContents<T>, T>(var2);
   }

   public static <T> SelectItemModelProperty.Type<ComponentContents<T>, T> castType() {
      return TYPE;
   }

   @Nullable
   public T get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return (T)var1.get(this.componentType);
   }

   public SelectItemModelProperty.Type<ComponentContents<T>, T> type() {
      return castType();
   }
}
