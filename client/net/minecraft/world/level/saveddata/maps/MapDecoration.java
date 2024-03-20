package net.minecraft.world.level.saveddata.maps;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record MapDecoration(Holder<MapDecorationType> b, byte c, byte d, byte e, Optional<Component> f) {
   private final Holder<MapDecorationType> type;
   private final byte x;
   private final byte y;
   private final byte rot;
   private final Optional<Component> name;
   public static final StreamCodec<RegistryFriendlyByteBuf, MapDecoration> STREAM_CODEC = StreamCodec.composite(
      MapDecorationType.STREAM_CODEC,
      MapDecoration::type,
      ByteBufCodecs.BYTE,
      MapDecoration::x,
      ByteBufCodecs.BYTE,
      MapDecoration::y,
      ByteBufCodecs.BYTE,
      MapDecoration::rot,
      ComponentSerialization.OPTIONAL_STREAM_CODEC,
      MapDecoration::name,
      MapDecoration::new
   );

   public MapDecoration(Holder<MapDecorationType> var1, byte var2, byte var3, byte var4, Optional<Component> var5) {
      super();
      var4 = (byte)(var4 & 15);
      this.type = var1;
      this.x = var2;
      this.y = var3;
      this.rot = var4;
      this.name = var5;
   }

   public ResourceLocation getSpriteLocation() {
      return ((MapDecorationType)this.type.value()).assetId();
   }

   public boolean renderOnFrame() {
      return ((MapDecorationType)this.type.value()).showOnItemFrame();
   }
}
