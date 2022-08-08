package net.minecraft.network.chat;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;

public record LastSeenMessages(List<Entry> c) {
   private final List<Entry> entries;
   public static LastSeenMessages EMPTY = new LastSeenMessages(List.of());
   public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 5;

   public LastSeenMessages(FriendlyByteBuf var1) {
      this((List)var1.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 5), Entry::new));
   }

   public LastSeenMessages(List<Entry> var1) {
      super();
      this.entries = var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.entries, (var0, var1x) -> {
         var1x.write(var0);
      });
   }

   public void updateHash(DataOutput var1) throws IOException {
      Iterator var2 = this.entries.iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         UUID var4 = var3.profileId();
         MessageSignature var5 = var3.lastSignature();
         var1.writeByte(70);
         var1.writeLong(var4.getMostSignificantBits());
         var1.writeLong(var4.getLeastSignificantBits());
         var1.write(var5.bytes());
      }

   }

   public List<Entry> entries() {
      return this.entries;
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

      public UUID profileId() {
         return this.profileId;
      }

      public MessageSignature lastSignature() {
         return this.lastSignature;
      }
   }

   public static record Update(LastSeenMessages a, Optional<Entry> b) {
      private final LastSeenMessages lastSeen;
      private final Optional<Entry> lastReceived;

      public Update(FriendlyByteBuf var1) {
         this(new LastSeenMessages(var1), var1.readOptional(Entry::new));
      }

      public Update(LastSeenMessages var1, Optional<Entry> var2) {
         super();
         this.lastSeen = var1;
         this.lastReceived = var2;
      }

      public void write(FriendlyByteBuf var1) {
         this.lastSeen.write(var1);
         var1.writeOptional(this.lastReceived, (var0, var1x) -> {
            var1x.write(var0);
         });
      }

      public LastSeenMessages lastSeen() {
         return this.lastSeen;
      }

      public Optional<Entry> lastReceived() {
         return this.lastReceived;
      }
   }
}
