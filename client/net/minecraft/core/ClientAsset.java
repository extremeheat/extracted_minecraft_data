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

      public ResourceTexture(Identifier var1) {
         this(var1, var1.withPath((UnaryOperator)((var0) -> "textures/" + var0 + ".png")));
      }

      public ResourceTexture(Identifier var1, Identifier var2) {
         super();
         this.id = var1;
         this.texturePath = var2;
      }

      static {
         CODEC = Identifier.CODEC.xmap(ResourceTexture::new, ResourceTexture::id);
         DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
         STREAM_CODEC = Identifier.STREAM_CODEC.map(ResourceTexture::new, ResourceTexture::id);
      }
   }

   public static record DownloadedTexture(Identifier texturePath, String url) implements Texture {
      public DownloadedTexture(Identifier var1, String var2) {
         super();
         this.texturePath = var1;
         this.url = var2;
      }

      public Identifier id() {
         return this.texturePath;
      }
   }

   public interface Texture extends ClientAsset {
      Identifier texturePath();
   }
}
