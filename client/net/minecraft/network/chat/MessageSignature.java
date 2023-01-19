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

public record MessageSignature(byte[] c) {
   private final byte[] bytes;
   public static final Codec<MessageSignature> CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
   public static final int BYTES = 256;

   public MessageSignature(byte[] var1) {
      super();
      Preconditions.checkState(var1.length == 256, "Invalid message signature size");
      this.bytes = var1;
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
      return Base64.getEncoder().encodeToString(this.bytes);
   }

   public MessageSignature.Packed pack(MessageSignatureCache var1) {
      int var2 = var1.pack(this);
      return var2 != -1 ? new MessageSignature.Packed(var2) : new MessageSignature.Packed(this);
   }

   public static record Packed(int b, @Nullable MessageSignature c) {
      private final int id;
      @Nullable
      private final MessageSignature fullSignature;
      public static final int FULL_SIGNATURE = -1;

      public Packed(MessageSignature var1) {
         this(-1, var1);
      }

      public Packed(int var1) {
         this(var1, null);
      }

      public Packed(int var1, @Nullable MessageSignature var2) {
         super();
         this.id = var1;
         this.fullSignature = var2;
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
