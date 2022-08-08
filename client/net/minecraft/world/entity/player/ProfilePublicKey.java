package net.minecraft.world.entity.player;

import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureValidator;

public record ProfilePublicKey(Data b) {
   private final Data data;
   public static final Codec<ProfilePublicKey> TRUSTED_CODEC;

   public ProfilePublicKey(Data var1) {
      super();
      this.data = var1;
   }

   public static ProfilePublicKey createTrusted(Data var0) throws CryptException {
      return new ProfilePublicKey(var0);
   }

   public static ProfilePublicKey createValidated(SignatureValidator var0, UUID var1, Data var2) throws InsecurePublicKeyException, CryptException {
      if (var2.hasExpired()) {
         throw new InsecurePublicKeyException.InvalidException("Expired profile public key");
      } else if (!var2.validateSignature(var0, var1)) {
         throw new InsecurePublicKeyException.InvalidException("Invalid profile public key signature");
      } else {
         return createTrusted(var2);
      }
   }

   public SignatureValidator createSignatureValidator() {
      return SignatureValidator.from(this.data.key, "SHA256withRSA");
   }

   public Data data() {
      return this.data;
   }

   static {
      TRUSTED_CODEC = ProfilePublicKey.Data.CODEC.comapFlatMap((var0) -> {
         try {
            return DataResult.success(createTrusted(var0));
         } catch (CryptException var2) {
            return DataResult.error("Malformed public key");
         }
      }, ProfilePublicKey::data);
   }

   public static record Data(Instant b, PublicKey c, byte[] d) {
      private final Instant expiresAt;
      final PublicKey key;
      private final byte[] keySignature;
      private static final int MAX_KEY_SIGNATURE_SIZE = 4096;
      public static final Codec<Data> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(Data::expiresAt), Crypt.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(Data::key), ExtraCodecs.BASE64_STRING.fieldOf("signature_v2").forGetter(Data::keySignature)).apply(var0, Data::new);
      });

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

      public Instant expiresAt() {
         return this.expiresAt;
      }

      public PublicKey key() {
         return this.key;
      }

      public byte[] keySignature() {
         return this.keySignature;
      }
   }
}
