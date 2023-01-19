package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraft.util.Crypt;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureValidator;

public record ProfilePublicKey(ProfilePublicKey.Data d) {
   private final ProfilePublicKey.Data data;
   public static final Component EXPIRED_PROFILE_PUBLIC_KEY = Component.translatable("multiplayer.disconnect.expired_public_key");
   private static final Component INVALID_SIGNATURE = Component.translatable("multiplayer.disconnect.invalid_public_key_signature");
   public static final Duration EXPIRY_GRACE_PERIOD = Duration.ofHours(8L);
   public static final Codec<ProfilePublicKey> TRUSTED_CODEC = ProfilePublicKey.Data.CODEC.xmap(ProfilePublicKey::new, ProfilePublicKey::data);

   public ProfilePublicKey(ProfilePublicKey.Data var1) {
      super();
      this.data = var1;
   }

   public static ProfilePublicKey createValidated(SignatureValidator var0, UUID var1, ProfilePublicKey.Data var2, Duration var3) throws ProfilePublicKey.ValidationException {
      if (var2.hasExpired(var3)) {
         throw new ProfilePublicKey.ValidationException(EXPIRED_PROFILE_PUBLIC_KEY);
      } else if (!var2.validateSignature(var0, var1)) {
         throw new ProfilePublicKey.ValidationException(INVALID_SIGNATURE);
      } else {
         return new ProfilePublicKey(var2);
      }
   }

   public SignatureValidator createSignatureValidator() {
      return SignatureValidator.from(this.data.key, "SHA256withRSA");
   }

   public static record Data(Instant b, PublicKey c, byte[] d) {
      private final Instant expiresAt;
      final PublicKey key;
      private final byte[] keySignature;
      private static final int MAX_KEY_SIGNATURE_SIZE = 4096;
      public static final Codec<ProfilePublicKey.Data> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(ProfilePublicKey.Data::expiresAt),
                  Crypt.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(ProfilePublicKey.Data::key),
                  ExtraCodecs.BASE64_STRING.fieldOf("signature_v2").forGetter(ProfilePublicKey.Data::keySignature)
               )
               .apply(var0, ProfilePublicKey.Data::new)
      );

      public Data(FriendlyByteBuf var1) {
         this(var1.readInstant(), var1.readPublicKey(), var1.readByteArray(4096));
      }

      public Data(Instant var1, PublicKey var2, byte[] var3) {
         super();
         this.expiresAt = var1;
         this.key = var2;
         this.keySignature = var3;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeInstant(this.expiresAt);
         var1.writePublicKey(this.key);
         var1.writeByteArray(this.keySignature);
      }

      boolean validateSignature(SignatureValidator var1, UUID var2) {
         return var1.validate(this.signedPayload(var2), this.keySignature);
      }

      private byte[] signedPayload(UUID var1) {
         byte[] var2 = this.key.getEncoded();
         byte[] var3 = new byte[24 + var2.length];
         ByteBuffer var4 = ByteBuffer.wrap(var3).order(ByteOrder.BIG_ENDIAN);
         var4.putLong(var1.getMostSignificantBits()).putLong(var1.getLeastSignificantBits()).putLong(this.expiresAt.toEpochMilli()).put(var2);
         return var3;
      }

      public boolean hasExpired() {
         return this.expiresAt.isBefore(Instant.now());
      }

      public boolean hasExpired(Duration var1) {
         return this.expiresAt.plus(var1).isBefore(Instant.now());
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof ProfilePublicKey.Data)) {
            return false;
         } else {
            ProfilePublicKey.Data var2 = (ProfilePublicKey.Data)var1;
            return this.expiresAt.equals(var2.expiresAt) && this.key.equals(var2.key) && Arrays.equals(this.keySignature, var2.keySignature);
         }
      }
   }

   public static class ValidationException extends ThrowingComponent {
      public ValidationException(Component var1) {
         super(var1);
      }
   }
}
