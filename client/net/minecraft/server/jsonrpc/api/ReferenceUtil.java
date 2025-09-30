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

   public static URI createLocalReference(String var0) {
      return URI.create("#/components/schemas/" + var0);
   }

   static {
      REFERENCE_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(new URI(var0));
         } catch (URISyntaxException var2) {
            Objects.requireNonNull(var2);
            return DataResult.error(var2::getMessage);
         }
      }, URI::toString);
   }
}
