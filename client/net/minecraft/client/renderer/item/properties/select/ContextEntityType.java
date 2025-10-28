package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ContextEntityType() implements SelectItemModelProperty<ResourceKey<EntityType<?>>> {
   public static final Codec<ResourceKey<EntityType<?>>> VALUE_CODEC;
   public static final SelectItemModelProperty.Type<ContextEntityType, ResourceKey<EntityType<?>>> TYPE;

   public ContextEntityType() {
      super();
   }

   public @Nullable ResourceKey<EntityType<?>> get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var3 == null ? null : var3.getType().builtInRegistryHolder().key();
   }

   public SelectItemModelProperty.Type<ContextEntityType, ResourceKey<EntityType<?>>> type() {
      return TYPE;
   }

   public Codec<ResourceKey<EntityType<?>>> valueCodec() {
      return VALUE_CODEC;
   }

   // $FF: synthetic method
   public @Nullable Object get(final ItemStack var1, final @Nullable ClientLevel var2, final @Nullable LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      VALUE_CODEC = ResourceKey.codec(Registries.ENTITY_TYPE);
      TYPE = SelectItemModelProperty.Type.<ContextEntityType, ResourceKey<EntityType<?>>>create(MapCodec.unit(new ContextEntityType()), VALUE_CODEC);
   }
}
