package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jspecify.annotations.Nullable;

public record TrimMaterialProperty() implements SelectItemModelProperty<ResourceKey<TrimMaterial>> {
   public static final Codec<ResourceKey<TrimMaterial>> VALUE_CODEC;
   public static final SelectItemModelProperty.Type<TrimMaterialProperty, ResourceKey<TrimMaterial>> TYPE;

   public TrimMaterialProperty() {
      super();
   }

   public @Nullable ResourceKey<TrimMaterial> get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      ArmorTrim var6 = (ArmorTrim)var1.get(DataComponents.TRIM);
      return var6 == null ? null : (ResourceKey)var6.material().unwrapKey().orElse((Object)null);
   }

   public SelectItemModelProperty.Type<TrimMaterialProperty, ResourceKey<TrimMaterial>> type() {
      return TYPE;
   }

   public Codec<ResourceKey<TrimMaterial>> valueCodec() {
      return VALUE_CODEC;
   }

   // $FF: synthetic method
   public @Nullable Object get(final ItemStack var1, final @Nullable ClientLevel var2, final @Nullable LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      VALUE_CODEC = ResourceKey.codec(Registries.TRIM_MATERIAL);
      TYPE = SelectItemModelProperty.Type.<TrimMaterialProperty, ResourceKey<TrimMaterial>>create(MapCodec.unit(new TrimMaterialProperty()), VALUE_CODEC);
   }
}
