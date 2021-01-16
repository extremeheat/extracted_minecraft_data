package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public final class PemX509Certificate extends X509Certificate implements PemEncoded {
   private static final byte[] BEGIN_CERT;
   private static final byte[] END_CERT;
   private final ByteBuf content;

   static PemEncoded toPEM(ByteBufAllocator var0, boolean var1, X509Certificate... var2) throws CertificateEncodingException {
      if (var2 != null && var2.length != 0) {
         if (var2.length == 1) {
            X509Certificate var3 = var2[0];
            if (var3 instanceof PemEncoded) {
               return ((PemEncoded)var3).retain();
            }
         }

         boolean var12 = false;
         ByteBuf var4 = null;

         PemValue var14;
         try {
            X509Certificate[] var5 = var2;
            int var6 = var2.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               X509Certificate var8 = var5[var7];
               if (var8 == null) {
                  throw new IllegalArgumentException("Null element in chain: " + Arrays.toString(var2));
               }

               if (var8 instanceof PemEncoded) {
                  var4 = append(var0, var1, (PemEncoded)var8, var2.length, var4);
               } else {
                  var4 = append(var0, var1, var8, var2.length, var4);
               }
            }

            PemValue var13 = new PemValue(var4, false);
            var12 = true;
            var14 = var13;
         } finally {
            if (!var12 && var4 != null) {
               var4.release();
            }

         }

         return var14;
      } else {
         throw new IllegalArgumentException("X.509 certificate chain can't be null or empty");
      }
   }

   private static ByteBuf append(ByteBufAllocator var0, boolean var1, PemEncoded var2, int var3, ByteBuf var4) {
      ByteBuf var5 = var2.content();
      if (var4 == null) {
         var4 = newBuffer(var0, var1, var5.readableBytes() * var3);
      }

      var4.writeBytes(var5.slice());
      return var4;
   }

   private static ByteBuf append(ByteBufAllocator var0, boolean var1, X509Certificate var2, int var3, ByteBuf var4) throws CertificateEncodingException {
      ByteBuf var5 = Unpooled.wrappedBuffer(var2.getEncoded());

      try {
         ByteBuf var6 = SslUtils.toBase64(var0, var5);

         try {
            if (var4 == null) {
               var4 = newBuffer(var0, var1, (BEGIN_CERT.length + var6.readableBytes() + END_CERT.length) * var3);
            }

            var4.writeBytes(BEGIN_CERT);
            var4.writeBytes(var6);
            var4.writeBytes(END_CERT);
         } finally {
            var6.release();
         }
      } finally {
         var5.release();
      }

      return var4;
   }

   private static ByteBuf newBuffer(ByteBufAllocator var0, boolean var1, int var2) {
      return var1 ? var0.directBuffer(var2) : var0.buffer(var2);
   }

   public static PemX509Certificate valueOf(byte[] var0) {
      return valueOf(Unpooled.wrappedBuffer(var0));
   }

   public static PemX509Certificate valueOf(ByteBuf var0) {
      return new PemX509Certificate(var0);
   }

   private PemX509Certificate(ByteBuf var1) {
      super();
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var1, "content");
   }

   public boolean isSensitive() {
      return false;
   }

   public int refCnt() {
      return this.content.refCnt();
   }

   public ByteBuf content() {
      int var1 = this.refCnt();
      if (var1 <= 0) {
         throw new IllegalReferenceCountException(var1);
      } else {
         return this.content;
      }
   }

   public PemX509Certificate copy() {
      return this.replace(this.content.copy());
   }

   public PemX509Certificate duplicate() {
      return this.replace(this.content.duplicate());
   }

   public PemX509Certificate retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public PemX509Certificate replace(ByteBuf var1) {
      return new PemX509Certificate(var1);
   }

   public PemX509Certificate retain() {
      this.content.retain();
      return this;
   }

   public PemX509Certificate retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   public PemX509Certificate touch() {
      this.content.touch();
      return this;
   }

   public PemX509Certificate touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   public boolean release() {
      return this.content.release();
   }

   public boolean release(int var1) {
      return this.content.release(var1);
   }

   public byte[] getEncoded() {
      throw new UnsupportedOperationException();
   }

   public boolean hasUnsupportedCriticalExtension() {
      throw new UnsupportedOperationException();
   }

   public Set<String> getCriticalExtensionOIDs() {
      throw new UnsupportedOperationException();
   }

   public Set<String> getNonCriticalExtensionOIDs() {
      throw new UnsupportedOperationException();
   }

   public byte[] getExtensionValue(String var1) {
      throw new UnsupportedOperationException();
   }

   public void checkValidity() {
      throw new UnsupportedOperationException();
   }

   public void checkValidity(Date var1) {
      throw new UnsupportedOperationException();
   }

   public int getVersion() {
      throw new UnsupportedOperationException();
   }

   public BigInteger getSerialNumber() {
      throw new UnsupportedOperationException();
   }

   public Principal getIssuerDN() {
      throw new UnsupportedOperationException();
   }

   public Principal getSubjectDN() {
      throw new UnsupportedOperationException();
   }

   public Date getNotBefore() {
      throw new UnsupportedOperationException();
   }

   public Date getNotAfter() {
      throw new UnsupportedOperationException();
   }

   public byte[] getTBSCertificate() {
      throw new UnsupportedOperationException();
   }

   public byte[] getSignature() {
      throw new UnsupportedOperationException();
   }

   public String getSigAlgName() {
      throw new UnsupportedOperationException();
   }

   public String getSigAlgOID() {
      throw new UnsupportedOperationException();
   }

   public byte[] getSigAlgParams() {
      throw new UnsupportedOperationException();
   }

   public boolean[] getIssuerUniqueID() {
      throw new UnsupportedOperationException();
   }

   public boolean[] getSubjectUniqueID() {
      throw new UnsupportedOperationException();
   }

   public boolean[] getKeyUsage() {
      throw new UnsupportedOperationException();
   }

   public int getBasicConstraints() {
      throw new UnsupportedOperationException();
   }

   public void verify(PublicKey var1) {
      throw new UnsupportedOperationException();
   }

   public void verify(PublicKey var1, String var2) {
      throw new UnsupportedOperationException();
   }

   public PublicKey getPublicKey() {
      throw new UnsupportedOperationException();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PemX509Certificate)) {
         return false;
      } else {
         PemX509Certificate var2 = (PemX509Certificate)var1;
         return this.content.equals(var2.content);
      }
   }

   public int hashCode() {
      return this.content.hashCode();
   }

   public String toString() {
      return this.content.toString(CharsetUtil.UTF_8);
   }

   static {
      BEGIN_CERT = "-----BEGIN CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
      END_CERT = "\n-----END CERTIFICATE-----\n".getBytes(CharsetUtil.US_ASCII);
   }
}
