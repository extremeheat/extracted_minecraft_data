package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.CertificateRequestedCallback.KeyMaterial;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.net.ssl.SSLException;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

class OpenSslKeyMaterialManager {
   static final String KEY_TYPE_RSA = "RSA";
   static final String KEY_TYPE_DH_RSA = "DH_RSA";
   static final String KEY_TYPE_EC = "EC";
   static final String KEY_TYPE_EC_EC = "EC_EC";
   static final String KEY_TYPE_EC_RSA = "EC_RSA";
   private static final Map<String, String> KEY_TYPES = new HashMap();
   private final X509KeyManager keyManager;
   private final String password;

   OpenSslKeyMaterialManager(X509KeyManager var1, String var2) {
      super();
      this.keyManager = var1;
      this.password = var2;
   }

   void setKeyMaterial(ReferenceCountedOpenSslEngine var1) throws SSLException {
      long var2 = var1.sslPointer();
      String[] var4 = SSL.authenticationMethods(var2);
      HashSet var5 = new HashSet(var4.length);
      String[] var6 = var4;
      int var7 = var4.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         String var10 = (String)KEY_TYPES.get(var9);
         if (var10 != null) {
            String var11 = this.chooseServerAlias(var1, var10);
            if (var11 != null && var5.add(var11)) {
               this.setKeyMaterial(var2, var11);
            }
         }
      }

   }

   KeyMaterial keyMaterial(ReferenceCountedOpenSslEngine var1, String[] var2, X500Principal[] var3) throws SSLException {
      String var4 = this.chooseClientAlias(var1, var2, var3);
      long var5 = 0L;
      long var7 = 0L;
      long var9 = 0L;
      long var11 = 0L;

      KeyMaterial var16;
      try {
         X509Certificate[] var13 = this.keyManager.getCertificateChain(var4);
         PrivateKey var14;
         if (var13 == null || var13.length == 0) {
            var14 = null;
            return var14;
         }

         var14 = this.keyManager.getPrivateKey(var4);
         var7 = ReferenceCountedOpenSslContext.toBIO(var13);
         var11 = SSL.parseX509Chain(var7);
         if (var14 != null) {
            var5 = ReferenceCountedOpenSslContext.toBIO(var14);
            var9 = SSL.parsePrivateKey(var5, this.password);
         }

         KeyMaterial var15 = new KeyMaterial(var11, var9);
         var9 = 0L;
         var11 = 0L;
         var16 = var15;
      } catch (SSLException var21) {
         throw var21;
      } catch (Exception var22) {
         throw new SSLException(var22);
      } finally {
         ReferenceCountedOpenSslContext.freeBio(var5);
         ReferenceCountedOpenSslContext.freeBio(var7);
         SSL.freePrivateKey(var9);
         SSL.freeX509Chain(var11);
      }

      return var16;
   }

   private void setKeyMaterial(long var1, String var3) throws SSLException {
      long var4 = 0L;
      long var6 = 0L;
      long var8 = 0L;

      try {
         X509Certificate[] var10 = this.keyManager.getCertificateChain(var3);
         if (var10 != null && var10.length != 0) {
            PrivateKey var11 = this.keyManager.getPrivateKey(var3);
            PemEncoded var12 = PemX509Certificate.toPEM(ByteBufAllocator.DEFAULT, true, var10);

            try {
               var6 = ReferenceCountedOpenSslContext.toBIO(ByteBufAllocator.DEFAULT, var12.retain());
               var8 = ReferenceCountedOpenSslContext.toBIO(ByteBufAllocator.DEFAULT, var12.retain());
               if (var11 != null) {
                  var4 = ReferenceCountedOpenSslContext.toBIO(var11);
               }

               SSL.setCertificateBio(var1, var6, var4, this.password);
               SSL.setCertificateChainBio(var1, var8, true);
               return;
            } finally {
               var12.release();
            }
         }
      } catch (SSLException var24) {
         throw var24;
      } catch (Exception var25) {
         throw new SSLException(var25);
      } finally {
         ReferenceCountedOpenSslContext.freeBio(var4);
         ReferenceCountedOpenSslContext.freeBio(var6);
         ReferenceCountedOpenSslContext.freeBio(var8);
      }

   }

   protected String chooseClientAlias(ReferenceCountedOpenSslEngine var1, String[] var2, X500Principal[] var3) {
      return this.keyManager.chooseClientAlias(var2, var3, (Socket)null);
   }

   protected String chooseServerAlias(ReferenceCountedOpenSslEngine var1, String var2) {
      return this.keyManager.chooseServerAlias(var2, (Principal[])null, (Socket)null);
   }

   static {
      KEY_TYPES.put("RSA", "RSA");
      KEY_TYPES.put("DHE_RSA", "RSA");
      KEY_TYPES.put("ECDHE_RSA", "RSA");
      KEY_TYPES.put("ECDHE_ECDSA", "EC");
      KEY_TYPES.put("ECDH_RSA", "EC_RSA");
      KEY_TYPES.put("ECDH_ECDSA", "EC_EC");
      KEY_TYPES.put("DH_RSA", "DH_RSA");
   }
}
