package net.minecraft.network.chat;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import net.minecraft.network.FriendlyByteBuf;

public record SignedMessageBody(ChatMessageContent b, Instant c, long d, LastSeenMessages e) {
   private final ChatMessageContent content;
   private final Instant timeStamp;
   private final long salt;
   private final LastSeenMessages lastSeen;
   public static final byte HASH_SEPARATOR_BYTE = 70;

   public SignedMessageBody(FriendlyByteBuf var1) {
      this(ChatMessageContent.read(var1), var1.readInstant(), var1.readLong(), new LastSeenMessages(var1));
   }

   public SignedMessageBody(ChatMessageContent var1, Instant var2, long var3, LastSeenMessages var5) {
      super();
      this.content = var1;
      this.timeStamp = var2;
      this.salt = var3;
      this.lastSeen = var5;
   }

   public void write(FriendlyByteBuf var1) {
      ChatMessageContent.write(var1, this.content);
      var1.writeInstant(this.timeStamp);
      var1.writeLong(this.salt);
      this.lastSeen.write(var1);
   }

   public HashCode hash() {
      HashingOutputStream var1 = new HashingOutputStream(Hashing.sha256(), OutputStream.nullOutputStream());

      try {
         DataOutputStream var2 = new DataOutputStream(var1);
         var2.writeLong(this.salt);
         var2.writeLong(this.timeStamp.getEpochSecond());
         OutputStreamWriter var3 = new OutputStreamWriter(var2, StandardCharsets.UTF_8);
         var3.write(this.content.plain());
         var3.flush();
         var2.write(70);
         if (this.content.isDecorated()) {
            var3.write(Component.Serializer.toStableJson(this.content.decorated()));
            var3.flush();
         }

         this.lastSeen.updateHash(var2);
      } catch (IOException var4) {
      }

      return var1.hash();
   }

   public SignedMessageBody withContent(ChatMessageContent var1) {
      return new SignedMessageBody(var1, this.timeStamp, this.salt, this.lastSeen);
   }
}
