package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record DisplayContext() implements SelectItemModelProperty<ItemDisplayContext> {
   public static final Codec<ItemDisplayContext> VALUE_CODEC;
   public static final SelectItemModelProperty.Type<DisplayContext, ItemDisplayContext> TYPE;

   public DisplayContext() {
      super();
   }

   public ItemDisplayContext get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      return displayContext;
   }

   public SelectItemModelProperty.Type<DisplayContext, ItemDisplayContext> type() {
      return TYPE;
   }

   public Codec<ItemDisplayContext> valueCodec() {
      return VALUE_CODEC;
   }

   static {
      VALUE_CODEC = ItemDisplayContext.CODEC;
      TYPE = SelectItemModelProperty.Type.<DisplayContext, ItemDisplayContext>create(MapCodec.unit(new DisplayContext()), VALUE_CODEC);
   }
}
