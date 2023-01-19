package net.minecraft.network.chat;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;

public record LastSeenMessages(List<LastSeenMessages.Entry> c) {
   private final List<LastSeenMessages.Entry> entries;
   public static LastSeenMessages EMPTY = new LastSeenMessages(List.of());
   public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 5;

   public LastSeenMessages(FriendlyByteBuf var1) {
      this(var1.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 5), LastSeenMessages.Entry::new));
   }

   public LastSeenMessages(List<LastSeenMessages.Entry> var1) {
      super();
      this.entries = var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.entries, (var0, var1x) -> var1x.write(var0));
   }

   public void updateHash(DataOutput var1) throws IOException {
      for(LastSeenMessages.Entry var3 : this.entries) {
         UUID var4 = var3.profileId();
         MessageSignature var5 = var3.lastSignature();
         var1.writeByte(70);
         var1.writeLong(var4.getMostSignificantBits());
         var1.writeLong(var4.getLeastSignificantBits());
         var1.write(var5.bytes());
      }
   }

   public static record Entry(UUID a, MessageSignature b) {
      private final UUID profileId;
      private final MessageSignature lastSignature;

      public Entry(FriendlyByteBuf var1) {
         this(var1.readUUID(), new MessageSignature(var1));
      }

      public Entry(UUID var1, MessageSignature var2) {
         super();
         this.profileId = var1;
         this.lastSignature = var2;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUUID(this.profileId);
         this.lastSignature.write(var1);
      }
   }

   public static record Update(LastSeenMessages a, Optional<LastSeenMessages.Entry> b) {
      private final LastSeenMessages lastSeen;
      private final Optional<LastSeenMessages.Entry> lastReceived;

      public Update(FriendlyByteBuf var1) {
         this(new LastSeenMessages(var1), var1.readOptional(LastSeenMessages.Entry::new));
      }

      public Update(LastSeenMessages var1, Optional<LastSeenMessages.Entry> var2) {
         super();
         this.lastSeen = var1;
         this.lastReceived = var2;
      }

      public void write(FriendlyByteBuf var1) {
         this.lastSeen.write(var1);
         var1.writeOptional(this.lastReceived, (var0, var1x) -> var1x.write(var0));
      }
   }
}
