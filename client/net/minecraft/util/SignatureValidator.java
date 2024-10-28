package net.minecraft.util;

import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.logging.LogUtils;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public interface SignatureValidator {
   SignatureValidator NO_VALIDATION = (var0, var1) -> {
      return true;
   };
   Logger LOGGER = LogUtils.getLogger();

   boolean validate(SignatureUpdater var1, byte[] var2);

   default boolean validate(byte[] var1, byte[] var2) {
      return this.validate((var1x) -> {
         var1x.update(var1);
      }, var2);
   }

   private static boolean verifySignature(SignatureUpdater var0, byte[] var1, Signature var2) throws SignatureException {
      Objects.requireNonNull(var2);
      var0.update(var2::update);
      return var2.verify(var1);
   }

   static SignatureValidator from(PublicKey var0, String var1) {
      return (var2, var3) -> {
         try {
            Signature var4 = Signature.getInstance(var1);
            var4.initVerify(var0);
            return verifySignature(var2, var3, var4);
         } catch (Exception var5) {
            LOGGER.error("Failed to verify signature", var5);
            return false;
         }
      };
   }

   @Nullable
   static SignatureValidator from(ServicesKeySet var0, ServicesKeyType var1) {
      Collection var2 = var0.keys(var1);
      return var2.isEmpty() ? null : (var1x, var2x) -> {
         return var2.stream().anyMatch((var2xx) -> {
            Signature var3 = var2xx.signature();

            try {
               return verifySignature(var1x, var2x, var3);
            } catch (SignatureException var5) {
               LOGGER.error("Failed to verify Services signature", var5);
               return false;
            }
         });
      };
   }
}
