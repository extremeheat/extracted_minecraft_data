package net.minecraft.network.chat;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Crypt;

public record MessageSigner(UUID a, Instant b, long c) {
   private final UUID profileId;
   private final Instant timeStamp;
   private final long salt;

   public MessageSigner(FriendlyByteBuf var1) {
      this(var1.readUUID(), var1.readInstant(), var1.readLong());
   }

   public MessageSigner(UUID var1, Instant var2, long var3) {
      super();
      this.profileId = var1;
      this.timeStamp = var2;
      this.salt = var3;
   }

   public static MessageSigner create(UUID var0) {
      return new MessageSigner(var0, Instant.now(), Crypt.SaltSupplier.getLong());
   }

   public static MessageSigner system() {
      return create(Util.NIL_UUID);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.profileId);
      var1.writeInstant(this.timeStamp);
      var1.writeLong(this.salt);
   }

   public boolean isSystem() {
      return this.profileId.equals(Util.NIL_UUID);
   }

   public UUID profileId() {
      return this.profileId;
   }

   public Instant timeStamp() {
      return this.timeStamp;
   }

   public long salt() {
      return this.salt;
   }
}
