package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;

public record ItemBlockState(String property) implements SelectItemModelProperty<String> {
   public static final SelectItemModelProperty.Type<ItemBlockState, String> TYPE;

   public ItemBlockState(String var1) {
      super();
      this.property = var1;
   }

   @Nullable
   public String get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      BlockItemStateProperties var6 = (BlockItemStateProperties)var1.get(DataComponents.BLOCK_STATE);
      return var6 == null ? null : (String)var6.properties().get(this.property);
   }

   public SelectItemModelProperty.Type<ItemBlockState, String> type() {
      return TYPE;
   }

   public String property() {
      return this.property;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      TYPE = SelectItemModelProperty.Type.create(RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.STRING.fieldOf("block_state_property").forGetter(ItemBlockState::property)).apply(var0, ItemBlockState::new);
      }), Codec.STRING);
   }
}
