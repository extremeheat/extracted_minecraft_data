package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public record ContextDimension() implements SelectItemModelProperty<ResourceKey<Level>> {
   public static final Codec<ResourceKey<Level>> VALUE_CODEC;
   public static final SelectItemModelProperty.Type<ContextDimension, ResourceKey<Level>> TYPE;

   public ContextDimension() {
      super();
   }

   public @Nullable ResourceKey<Level> get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var2 != null ? var2.dimension() : null;
   }

   public SelectItemModelProperty.Type<ContextDimension, ResourceKey<Level>> type() {
      return TYPE;
   }

   public Codec<ResourceKey<Level>> valueCodec() {
      return VALUE_CODEC;
   }

   // $FF: synthetic method
   public @Nullable Object get(final ItemStack var1, final @Nullable ClientLevel var2, final @Nullable LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      VALUE_CODEC = ResourceKey.codec(Registries.DIMENSION);
      TYPE = SelectItemModelProperty.Type.<ContextDimension, ResourceKey<Level>>create(MapCodec.unit(new ContextDimension()), VALUE_CODEC);
   }
}
