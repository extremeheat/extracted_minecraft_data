package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;

public record SignedMessageBody(String content, Instant timeStamp, long salt, LastSeenMessages lastSeen) {
   public static final MapCodec<SignedMessageBody> MAP_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.STRING.fieldOf("content").forGetter(SignedMessageBody::content),
               ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(SignedMessageBody::timeStamp),
               Codec.LONG.fieldOf("salt").forGetter(SignedMessageBody::salt),
               LastSeenMessages.CODEC.optionalFieldOf("last_seen", LastSeenMessages.EMPTY).forGetter(SignedMessageBody::lastSeen)
            )
            .apply(var0, SignedMessageBody::new)
   );

   public SignedMessageBody(String content, Instant timeStamp, long salt, LastSeenMessages lastSeen) {
      super();
      this.content = content;
      this.timeStamp = timeStamp;
      this.salt = salt;
      this.lastSeen = lastSeen;
   }

   public static SignedMessageBody unsigned(String var0) {
      return new SignedMessageBody(var0, Instant.now(), 0L, LastSeenMessages.EMPTY);
   }

   public void updateSignature(SignatureUpdater.Output var1) throws SignatureException {
      var1.update(Longs.toByteArray(this.salt));
      var1.update(Longs.toByteArray(this.timeStamp.getEpochSecond()));
      byte[] var2 = this.content.getBytes(StandardCharsets.UTF_8);
      var1.update(Ints.toByteArray(var2.length));
      var1.update(var2);
      this.lastSeen.updateSignature(var1);
   }

   public SignedMessageBody.Packed pack(MessageSignatureCache var1) {
      return new SignedMessageBody.Packed(this.content, this.timeStamp, this.salt, this.lastSeen.pack(var1));
   }

   public static record Packed(String content, Instant timeStamp, long salt, LastSeenMessages.Packed lastSeen) {
      public Packed(FriendlyByteBuf var1) {
         this(var1.readUtf(256), var1.readInstant(), var1.readLong(), new LastSeenMessages.Packed(var1));
      }

      public Packed(String content, Instant timeStamp, long salt, LastSeenMessages.Packed lastSeen) {
         super();
         this.content = content;
         this.timeStamp = timeStamp;
         this.salt = salt;
         this.lastSeen = lastSeen;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUtf(this.content, 256);
         var1.writeInstant(this.timeStamp);
         var1.writeLong(this.salt);
         this.lastSeen.write(var1);
      }

      public Optional<SignedMessageBody> unpack(MessageSignatureCache var1) {
         return this.lastSeen.unpack(var1).map(var1x -> new SignedMessageBody(this.content, this.timeStamp, this.salt, var1x));
      }
   }
}
