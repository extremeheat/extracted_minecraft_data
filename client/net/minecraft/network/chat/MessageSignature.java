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
   public static final MessageSignature EMPTY = new MessageSignature(ByteArrays.EMPTY_ARRAY);

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
         return var1.validate(var2x -> var2.updateSignature(var2x, var4), this.bytes);
      } else {
         return false;
      }
   }

   public boolean verify(SignatureValidator var1, SignedMessageHeader var2, byte[] var3) {
      return !this.isEmpty() ? var1.validate(var2x -> var2.updateSignature(var2x, var3), this.bytes) : false;
   }

   public boolean isEmpty() {
      return this.bytes.length == 0;
   }

   @Nullable
   public ByteBuffer asByteBuffer() {
      return !this.isEmpty() ? ByteBuffer.wrap(this.bytes) : null;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof MessageSignature var2 && Arrays.equals(this.bytes, var2.bytes)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.bytes);
   }

   @Override
   public String toString() {
      return !this.isEmpty() ? Base64.getEncoder().encodeToString(this.bytes) : "empty";
   }
}
