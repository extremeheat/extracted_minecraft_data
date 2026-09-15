package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.SignatureUpdater;

public record LastSeenMessages(List<MessageSignature> entries) {
   public static final Codec<LastSeenMessages> CODEC;
   public static final LastSeenMessages EMPTY;
   public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 20;

   public LastSeenMessages {
      super();
   }

   public void updateSignature(final SignatureUpdater.Output output) throws SignatureException {
      output.update(Ints.toByteArray(this.entries.size()));

      for(MessageSignature entry : this.entries) {
         output.update(entry.bytes());
      }

   }

   public Packed pack(final MessageSignatureCache cache) {
      return new Packed(this.entries.stream().map((entry) -> entry.pack(cache)).toList());
   }

   public byte computeChecksum() {
      int checksum = 1;

      for(MessageSignature entry : this.entries) {
         checksum = 31 * checksum + entry.checksum();
      }

      byte checksumByte = (byte)checksum;
      return checksumByte == 0 ? 1 : checksumByte;
   }

   static {
      CODEC = MessageSignature.CODEC.listOf().xmap(LastSeenMessages::new, LastSeenMessages::entries);
      EMPTY = new LastSeenMessages(List.of());
   }

   public static record Packed(List<MessageSignature.Packed> entries) {
      public static final Packed EMPTY = new Packed(List.of());
      public static final StreamCodec<ByteBuf, Packed> STREAM_CODEC;

      public Packed {
         super();
      }

      public Optional<LastSeenMessages> unpack(final MessageSignatureCache cache) {
         List<MessageSignature> unpacked = new ArrayList(this.entries.size());

         for(MessageSignature.Packed packed : this.entries) {
            Optional<MessageSignature> entry = packed.unpack(cache);
            if (entry.isEmpty()) {
               return Optional.empty();
            }

            unpacked.add((MessageSignature)entry.get());
         }

         return Optional.of(new LastSeenMessages(unpacked));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(MessageSignature.Packed.STREAM_CODEC.apply(ByteBufCodecs.list(20)), Packed::entries, Packed::new);
      }
   }

   public static record Update(int offset, BitSet acknowledged, byte checksum) {
      public static final byte IGNORE_CHECKSUM = 0;
      public static final StreamCodec<ByteBuf, Update> STREAM_CODEC;

      public Update {
         super();
      }

      public boolean verifyChecksum(final LastSeenMessages lastSeen) {
         return this.checksum == 0 || this.checksum == lastSeen.computeChecksum();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Update::offset, ByteBufCodecs.fixedBitSet(20), Update::acknowledged, ByteBufCodecs.BYTE, Update::checksum, Update::new);
      }
   }
}
