package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(byte[] bytes) {
   public static final Codec<MessageSignature> CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
   public static final int BYTES = 256;

   public MessageSignature(byte[] bytes) {
      super();
      Preconditions.checkState(bytes.length == 256, "Invalid message signature size");
      this.bytes = bytes;
   }

   public static MessageSignature read(FriendlyByteBuf var0) {
      byte[] var1 = new byte[256];
      var0.readBytes(var1);
      return new MessageSignature(var1);
   }

   public static void write(FriendlyByteBuf var0, MessageSignature var1) {
      var0.writeBytes(var1.bytes);
   }

   public boolean verify(SignatureValidator var1, SignatureUpdater var2) {
      return var1.validate(var2, this.bytes);
   }

   public ByteBuffer asByteBuffer() {
      return ByteBuffer.wrap(this.bytes);
   }

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

   public int hashCode() {
      return Arrays.hashCode(this.bytes);
   }

   public String toString() {
      return Base64.getEncoder().encodeToString(this.bytes);
   }

   public MessageSignature.Packed pack(MessageSignatureCache var1) {
      int var2 = var1.pack(this);
      return var2 != -1 ? new MessageSignature.Packed(var2) : new MessageSignature.Packed(this);
   }

   public static record Packed(int id, @Nullable MessageSignature fullSignature) {
      public static final int FULL_SIGNATURE = -1;

      public Packed(MessageSignature var1) {
         this(-1, var1);
      }

      public Packed(int var1) {
         this(var1, null);
      }

      public Packed(int id, @Nullable MessageSignature fullSignature) {
         super();
         this.id = id;
         this.fullSignature = fullSignature;
      }

      public static MessageSignature.Packed read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt() - 1;
         return var1 == -1 ? new MessageSignature.Packed(MessageSignature.read(var0)) : new MessageSignature.Packed(var1);
      }

      public static void write(FriendlyByteBuf var0, MessageSignature.Packed var1) {
         var0.writeVarInt(var1.id() + 1);
         if (var1.fullSignature() != null) {
            MessageSignature.write(var0, var1.fullSignature());
         }
      }

      public Optional<MessageSignature> unpack(MessageSignatureCache var1) {
         return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(var1.unpack(this.id));
      }
   }
}
