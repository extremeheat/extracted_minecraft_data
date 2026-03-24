package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;
import org.jspecify.annotations.Nullable;

public record MessageSignature(byte[] bytes) {
   public static final Codec<MessageSignature> CODEC;
   public static final int BYTES = 256;

   public MessageSignature {
      super();
      Preconditions.checkState(bytes.length == 256, "Invalid message signature size");
   }

   public static MessageSignature read(final FriendlyByteBuf input) {
      byte[] bytes = new byte[256];
      input.readBytes(bytes);
      return new MessageSignature(bytes);
   }

   public static void write(final FriendlyByteBuf output, final MessageSignature signature) {
      output.writeBytes(signature.bytes);
   }

   public boolean verify(final SignatureValidator signature, final SignatureUpdater updater) {
      return signature.validate(updater, this.bytes);
   }

   public ByteBuffer asByteBuffer() {
      return ByteBuffer.wrap(this.bytes);
   }

   public boolean equals(final Object o) {
      boolean var10000;
      if (this != o) {
         label26: {
            if (o instanceof MessageSignature) {
               MessageSignature that = (MessageSignature)o;
               if (Arrays.equals(this.bytes, that.bytes)) {
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
      return Base64.getEncoder().encodeToString(this.bytes);
   }

   public static String describe(final @Nullable MessageSignature signature) {
      return signature == null ? "<no signature>" : signature.toString();
   }

   public Packed pack(final MessageSignatureCache cache) {
      int packedId = cache.pack(this);
      return packedId != -1 ? new Packed(packedId) : new Packed(this);
   }

   public int checksum() {
      return Arrays.hashCode(this.bytes);
   }

   static {
      CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
   }

   public static record Packed(int id, @Nullable MessageSignature fullSignature) {
      public static final int FULL_SIGNATURE = -1;

      public Packed(final MessageSignature signature) {
         this(-1, signature);
      }

      public Packed(final int id) {
         this(id, (MessageSignature)null);
      }

      public Packed {
         super();
      }

      public static Packed read(final FriendlyByteBuf input) {
         int id = input.readVarInt() - 1;
         return id == -1 ? new Packed(MessageSignature.read(input)) : new Packed(id);
      }

      public static void write(final FriendlyByteBuf output, final Packed packed) {
         output.writeVarInt(packed.id() + 1);
         if (packed.fullSignature() != null) {
            MessageSignature.write(output, packed.fullSignature());
         }

      }

      public Optional<MessageSignature> unpack(final MessageSignatureCache cache) {
         return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(cache.unpack(this.id));
      }
   }
}
