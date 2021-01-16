package io.netty.handler.ssl;

import java.security.Principal;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslExtendedKeyMaterialManager extends OpenSslKeyMaterialManager {
   private final X509ExtendedKeyManager keyManager;

   OpenSslExtendedKeyMaterialManager(X509ExtendedKeyManager var1, String var2) {
      super(var1, var2);
      this.keyManager = var1;
   }

   protected String chooseClientAlias(ReferenceCountedOpenSslEngine var1, String[] var2, X500Principal[] var3) {
      return this.keyManager.chooseEngineClientAlias(var2, var3, var1);
   }

   protected String chooseServerAlias(ReferenceCountedOpenSslEngine var1, String var2) {
      return this.keyManager.chooseEngineServerAlias(var2, (Principal[])null, var1);
   }
}
