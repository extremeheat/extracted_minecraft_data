package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ReferenceUtil {
   public static final Codec<URI> REFERENCE_CODEC;

   public ReferenceUtil() {
      super();
   }

   public static URI createLocalReference(final String typeId) {
      return URI.create("#/components/schemas/" + typeId);
   }

   static {
      REFERENCE_CODEC = Codec.STRING.comapFlatMap((string) -> {
         try {
            return DataResult.success(new URI(string));
         } catch (URISyntaxException e) {
            Objects.requireNonNull(e);
            return DataResult.error(e::getMessage);
         }
      }, URI::toString);
   }
}
