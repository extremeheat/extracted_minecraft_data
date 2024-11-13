package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsKeybindDown(KeyMapping keybind) implements ConditionalItemModelProperty {
   private static final Codec<KeyMapping> KEYBIND_CODEC;
   public static final MapCodec<IsKeybindDown> MAP_CODEC;

   public IsKeybindDown(KeyMapping var1) {
      super();
      this.keybind = var1;
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return this.keybind.isDown();
   }

   public MapCodec<IsKeybindDown> type() {
      return MAP_CODEC;
   }

   static {
      KEYBIND_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         KeyMapping var1 = KeyMapping.get(var0);
         return var1 != null ? DataResult.success(var1) : DataResult.error(() -> "Invalid keybind: " + var0);
      }, KeyMapping::getName);
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(KEYBIND_CODEC.fieldOf("keybind").forGetter(IsKeybindDown::keybind)).apply(var0, IsKeybindDown::new));
   }
}
