package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;
import java.security.PrivateKey;

public final class PemPrivateKey extends AbstractReferenceCounted implements PrivateKey, PemEncoded {
   private static final long serialVersionUID = 7978017465645018936L;
   private static final byte[] BEGIN_PRIVATE_KEY;
   private static final byte[] END_PRIVATE_KEY;
   private static final String PKCS8_FORMAT = "PKCS#8";
   private final ByteBuf content;

   static PemEncoded toPEM(ByteBufAllocator var0, boolean var1, PrivateKey var2) {
      if (var2 instanceof PemEncoded) {
         return ((PemEncoded)var2).retain();
      } else {
         ByteBuf var3 = Unpooled.wrappedBuffer(var2.getEncoded());

         PemValue var9;
         try {
            ByteBuf var4 = SslUtils.toBase64(var0, var3);

            try {
               int var5 = BEGIN_PRIVATE_KEY.length + var4.readableBytes() + END_PRIVATE_KEY.length;
               boolean var6 = false;
               ByteBuf var7 = var1 ? var0.directBuffer(var5) : var0.buffer(var5);

               try {
                  var7.writeBytes(BEGIN_PRIVATE_KEY);
                  var7.writeBytes(var4);
                  var7.writeBytes(END_PRIVATE_KEY);
                  PemValue var8 = new PemValue(var7, true);
                  var6 = true;
                  var9 = var8;
               } finally {
                  if (!var6) {
                     SslUtils.zerooutAndRelease(var7);
                  }

               }
            } finally {
               SslUtils.zerooutAndRelease(var4);
            }
         } finally {
            SslUtils.zerooutAndRelease(var3);
         }

         return var9;
      }
   }

   public static PemPrivateKey valueOf(byte[] var0) {
      return valueOf(Unpooled.wrappedBuffer(var0));
   }

   public static PemPrivateKey valueOf(ByteBuf var0) {
      return new PemPrivateKey(var0);
   }

   private PemPrivateKey(ByteBuf var1) {
      super();
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var1, "content");
   }

   public boolean isSensitive() {
      return true;
   }

   public ByteBuf content() {
      int var1 = this.refCnt();
      if (var1 <= 0) {
         throw new IllegalReferenceCountException(var1);
      } else {
         return this.content;
      }
   }

   public PemPrivateKey copy() {
      return this.replace(this.content.copy());
   }

   public PemPrivateKey duplicate() {
      return this.replace(this.content.duplicate());
   }

   public PemPrivateKey retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   public PemPrivateKey replace(ByteBuf var1) {
      return new PemPrivateKey(var1);
   }

   public PemPrivateKey touch() {
      this.content.touch();
      return this;
   }

   public PemPrivateKey touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   public PemPrivateKey retain() {
      return (PemPrivateKey)super.retain();
   }

   public PemPrivateKey retain(int var1) {
      return (PemPrivateKey)super.retain(var1);
   }

   protected void deallocate() {
      SslUtils.zerooutAndRelease(this.content);
   }

   public byte[] getEncoded() {
      throw new UnsupportedOperationException();
   }

   public String getAlgorithm() {
      throw new UnsupportedOperationException();
   }

   public String getFormat() {
      return "PKCS#8";
   }

   public void destroy() {
      this.release(this.refCnt());
   }

   public boolean isDestroyed() {
      return this.refCnt() == 0;
   }

   static {
      BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
      END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n".getBytes(CharsetUtil.US_ASCII);
   }
}
