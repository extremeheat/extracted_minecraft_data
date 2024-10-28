package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureUpdater;

public record LastSeenMessages(List<MessageSignature> entries) {
   public static final Codec<LastSeenMessages> CODEC;
   public static LastSeenMessages EMPTY;
   public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 20;

   public LastSeenMessages(List<MessageSignature> entries) {
      super();
      this.entries = entries;
   }

   public void updateSignature(SignatureUpdater.Output var1) throws SignatureException {
      var1.update(Ints.toByteArray(this.entries.size()));
      Iterator var2 = this.entries.iterator();

      while(var2.hasNext()) {
         MessageSignature var3 = (MessageSignature)var2.next();
         var1.update(var3.bytes());
      }

   }

   public Packed pack(MessageSignatureCache var1) {
      return new Packed(this.entries.stream().map((var1x) -> {
         return var1x.pack(var1);
      }).toList());
   }

   public List<MessageSignature> entries() {
      return this.entries;
   }

   static {
      CODEC = MessageSignature.CODEC.listOf().xmap(LastSeenMessages::new, LastSeenMessages::entries);
      EMPTY = new LastSeenMessages(List.of());
   }

   public static record Packed(List<MessageSignature.Packed> entries) {
      public static final Packed EMPTY = new Packed(List.of());

      public Packed(FriendlyByteBuf var1) {
         this((List)var1.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 20), MessageSignature.Packed::read));
      }

      public Packed(List<MessageSignature.Packed> entries) {
         super();
         this.entries = entries;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeCollection(this.entries, MessageSignature.Packed::write);
      }

      public Optional<LastSeenMessages> unpack(MessageSignatureCache var1) {
         ArrayList var2 = new ArrayList(this.entries.size());
         Iterator var3 = this.entries.iterator();

         while(var3.hasNext()) {
            MessageSignature.Packed var4 = (MessageSignature.Packed)var3.next();
            Optional var5 = var4.unpack(var1);
            if (var5.isEmpty()) {
               return Optional.empty();
            }

            var2.add((MessageSignature)var5.get());
         }

         return Optional.of(new LastSeenMessages(var2));
      }

      public List<MessageSignature.Packed> entries() {
         return this.entries;
      }
   }

   public static record Update(int offset, BitSet acknowledged) {
      public Update(FriendlyByteBuf var1) {
         this(var1.readVarInt(), var1.readFixedBitSet(20));
      }

      public Update(int offset, BitSet acknowledged) {
         super();
         this.offset = offset;
         this.acknowledged = acknowledged;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.offset);
         var1.writeFixedBitSet(this.acknowledged, 20);
      }

      public int offset() {
         return this.offset;
      }

      public BitSet acknowledged() {
         return this.acknowledged;
      }
   }
}
