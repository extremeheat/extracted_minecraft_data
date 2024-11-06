package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record DisplayContext() implements SelectItemModelProperty<ItemDisplayContext> {
   public static final SelectItemModelProperty.Type<DisplayContext, ItemDisplayContext> TYPE;

   public DisplayContext() {
      super();
   }

   public ItemDisplayContext get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var5;
   }

   public SelectItemModelProperty.Type<DisplayContext, ItemDisplayContext> type() {
      return TYPE;
   }

   // $FF: synthetic method
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.create(MapCodec.unit(new DisplayContext()), ItemDisplayContext.CODEC);
   }
}
