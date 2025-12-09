package net.minecraft.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import org.jspecify.annotations.Nullable;

public record ItemBlockState(String property) implements SelectItemModelProperty<String> {
   public static final PrimitiveCodec<String> VALUE_CODEC;
   public static final SelectItemModelProperty.Type<ItemBlockState, String> TYPE;

   public ItemBlockState(String var1) {
      super();
      this.property = var1;
   }

   public @Nullable String get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      BlockItemStateProperties var6 = (BlockItemStateProperties)var1.get(DataComponents.BLOCK_STATE);
      return var6 == null ? null : (String)var6.properties().get(this.property);
   }

   public SelectItemModelProperty.Type<ItemBlockState, String> type() {
      return TYPE;
   }

   public Codec<String> valueCodec() {
      return VALUE_CODEC;
   }

   // $FF: synthetic method
   public @Nullable Object get(final ItemStack var1, final @Nullable ClientLevel var2, final @Nullable LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      VALUE_CODEC = Codec.STRING;
      TYPE = SelectItemModelProperty.Type.<ItemBlockState, String>create(RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("block_state_property").forGetter(ItemBlockState::property)).apply(var0, ItemBlockState::new)), VALUE_CODEC);
   }
}
