package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface ClientAsset {
   Identifier id();

   public static record ResourceTexture(Identifier id, Identifier texturePath) implements Texture {
      public static final Codec<ResourceTexture> CODEC;
      public static final MapCodec<ResourceTexture> DEFAULT_FIELD_CODEC;
      public static final StreamCodec<ByteBuf, ResourceTexture> STREAM_CODEC;

      public ResourceTexture(final Identifier texture) {
         this(texture, texture.withPath((UnaryOperator)((path) -> "textures/" + path + ".png")));
      }

      public ResourceTexture {
         super();
      }

      static {
         CODEC = Identifier.CODEC.xmap(ResourceTexture::new, ResourceTexture::id);
         DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
         STREAM_CODEC = Identifier.STREAM_CODEC.map(ResourceTexture::new, ResourceTexture::id);
      }
   }

   public static record DownloadedTexture(Identifier texturePath, String url) implements Texture {
      public DownloadedTexture {
         super();
      }

      public Identifier id() {
         return this.texturePath;
      }
   }

   public interface Texture extends ClientAsset {
      Identifier texturePath();
   }
}
