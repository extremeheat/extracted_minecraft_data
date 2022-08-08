package net.minecraft.network.chat;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(byte[] b) {
   private final byte[] bytes;
   public static final MessageSignature EMPTY;

   public MessageSignature(FriendlyByteBuf var1) {
      this(var1.readByteArray());
   }

   public MessageSignature(byte[] var1) {
      super();
      this.bytes = var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeByteArray(this.bytes);
   }

   public boolean verify(SignatureValidator var1, SignedMessageHeader var2, SignedMessageBody var3) {
      if (!this.isEmpty()) {
         byte[] var4 = var3.hash().asBytes();
         return var1.validate((var2x) -> {
            var2.updateSignature(var2x, var4);
         }, this.bytes);
      } else {
         return false;
      }
   }

   public boolean verify(SignatureValidator var1, SignedMessageHeader var2, byte[] var3) {
      return !this.isEmpty() ? var1.validate((var2x) -> {
         var2.updateSignature(var2x, var3);
      }, this.bytes) : false;
   }

   public boolean isEmpty() {
      return this.bytes.length == 0;
   }

   @Nullable
   public ByteBuffer asByteBuffer() {
      return !this.isEmpty() ? ByteBuffer.wrap(this.bytes) : null;
   }

   public boolean equals(Object var1) {
      boolean var10000;
      if (this != var1) {
         label26: {
            if (var1 instanceof MessageSignature) {
               MessageSignature var2 = (MessageSignature)var1;
               if (Arrays.equals(this.bytes, var2.bytes)) {
                  break label26;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public int hashCode() {
      return Arrays.hashCode(this.bytes);
   }

   public String toString() {
      return !this.isEmpty() ? Base64.getEncoder().encodeToString(this.bytes) : "empty";
   }

   public byte[] bytes() {
      return this.bytes;
   }

   static {
      EMPTY = new MessageSignature(ByteArrays.EMPTY_ARRAY);
   }
}
