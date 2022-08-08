package net.minecraft.network.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.util.Crypt;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(UUID a, Instant b, Crypt.SaltSignaturePair c) {
   private final UUID sender;
   private final Instant timeStamp;
   private final Crypt.SaltSignaturePair saltSignature;

   public MessageSignature(UUID var1, Instant var2, Crypt.SaltSignaturePair var3) {
      super();
      this.sender = var1;
      this.timeStamp = var2;
      this.saltSignature = var3;
   }

   public static MessageSignature unsigned() {
      return new MessageSignature(Util.NIL_UUID, Instant.now(), Crypt.SaltSignaturePair.EMPTY);
   }

   public boolean verify(SignatureValidator var1, Component var2) {
      return this.isValid() ? var1.validate((var2x) -> {
         updateSignature(var2x, var2, this.sender, this.timeStamp, this.saltSignature.salt());
      }, this.saltSignature.signature()) : false;
   }

   public boolean verify(SignatureValidator var1, String var2) throws SignatureException {
      return this.verify(var1, (Component)Component.literal(var2));
   }

   public static void updateSignature(SignatureUpdater.Output var0, Component var1, UUID var2, Instant var3, long var4) throws SignatureException {
      byte[] var6 = new byte[32];
      ByteBuffer var7 = ByteBuffer.wrap(var6).order(ByteOrder.BIG_ENDIAN);
      var7.putLong(var4);
      var7.putLong(var2.getMostSignificantBits()).putLong(var2.getLeastSignificantBits());
      var7.putLong(var3.getEpochSecond());
      var0.update(var6);
      var0.update(encodeContent(var1));
   }

   private static byte[] encodeContent(Component var0) {
      String var1 = Component.Serializer.toStableJson(var0);
      return var1.getBytes(StandardCharsets.UTF_8);
   }

   public boolean isValid() {
      return this.sender != Util.NIL_UUID && this.saltSignature.isValid();
   }

   public boolean isValid(UUID var1) {
      return this.isValid() && var1.equals(this.sender);
   }

   public UUID sender() {
      return this.sender;
   }

   public Instant timeStamp() {
      return this.timeStamp;
   }

   public Crypt.SaltSignaturePair saltSignature() {
      return this.saltSignature;
   }
}
