package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record ContextEntityType() implements SelectItemModelProperty<ResourceKey<EntityType<?>>> {
   public static final SelectItemModelProperty.Type<ContextEntityType, ResourceKey<EntityType<?>>> TYPE;

   public ContextEntityType() {
      super();
   }

   @Nullable
   public ResourceKey<EntityType<?>> get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var3 == null ? null : var3.getType().builtInRegistryHolder().key();
   }

   public SelectItemModelProperty.Type<ContextEntityType, ResourceKey<EntityType<?>>> type() {
      return TYPE;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.<ContextEntityType, ResourceKey<EntityType<?>>>create(MapCodec.unit(new ContextEntityType()), ResourceKey.codec(Registries.ENTITY_TYPE));
   }
}
