package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ContextDimension() implements SelectItemModelProperty<ResourceKey<Level>> {
   public static final SelectItemModelProperty.Type<ContextDimension, ResourceKey<Level>> TYPE;

   public ContextDimension() {
      super();
   }

   @Nullable
   public ResourceKey<Level> get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var2 != null ? var2.dimension() : null;
   }

   public SelectItemModelProperty.Type<ContextDimension, ResourceKey<Level>> type() {
      return TYPE;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.<ContextDimension, ResourceKey<Level>>create(MapCodec.unit(new ContextDimension()), ResourceKey.codec(Registries.DIMENSION));
   }
}
