package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public record TrimMaterialProperty() implements SelectItemModelProperty<ResourceKey<TrimMaterial>> {
   public static final SelectItemModelProperty.Type<TrimMaterialProperty, ResourceKey<TrimMaterial>> TYPE;

   public TrimMaterialProperty() {
      super();
   }

   @Nullable
   public ResourceKey<TrimMaterial> get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      ArmorTrim var6 = (ArmorTrim)var1.get(DataComponents.TRIM);
      return var6 == null ? null : (ResourceKey)var6.material().unwrapKey().orElse((Object)null);
   }

   public SelectItemModelProperty.Type<TrimMaterialProperty, ResourceKey<TrimMaterial>> type() {
      return TYPE;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.create(MapCodec.unit(new TrimMaterialProperty()), ResourceKey.codec(Registries.TRIM_MATERIAL));
   }
}
