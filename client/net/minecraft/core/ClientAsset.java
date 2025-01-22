package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ClientAsset(ResourceLocation id, ResourceLocation texturePath) {
   public static final Codec<ClientAsset> CODEC;
   public static final MapCodec<ClientAsset> DEFAULT_FIELD_CODEC;
   public static final StreamCodec<ByteBuf, ClientAsset> STREAM_CODEC;

   public ClientAsset(ResourceLocation var1) {
      this(var1, var1.withPath((UnaryOperator)((var0) -> "textures/" + var0 + ".png")));
   }

   public ClientAsset(ResourceLocation var1, ResourceLocation var2) {
      super();
      this.id = var1;
      this.texturePath = var2;
   }

   static {
      CODEC = ResourceLocation.CODEC.xmap(ClientAsset::new, ClientAsset::id);
      DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, ClientAsset::id, ClientAsset::new);
   }
}
