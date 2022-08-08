package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Objects;
import org.slf4j.Logger;

public interface Signer {
   Logger LOGGER = LogUtils.getLogger();

   byte[] sign(SignatureUpdater var1);

   default byte[] sign(byte[] var1) {
      return this.sign((var1x) -> {
         var1x.update(var1);
      });
   }

   static Signer from(PrivateKey var0, String var1) {
      return (var2) -> {
         try {
            Signature var3 = Signature.getInstance(var1);
            var3.initSign(var0);
            Objects.requireNonNull(var3);
            var2.update(var3::update);
            return var3.sign();
         } catch (Exception var4) {
            throw new IllegalStateException("Failed to sign message", var4);
         }
      };
   }
}
