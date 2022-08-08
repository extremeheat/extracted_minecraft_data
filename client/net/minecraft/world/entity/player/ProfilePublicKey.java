package net.minecraft.world.entity.player;

import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
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

   public static ProfilePublicKey createValidated(SignatureValidator var0, Data var1) throws InsecurePublicKeyException, CryptException {
      if (var1.hasExpired()) {
         throw new InsecurePublicKeyException.InvalidException("Expired profile public key");
      } else if (!var1.validateSignature(var0)) {
         throw new InsecurePublicKeyException.InvalidException("Invalid profile public key signature");
      } else {
         return createTrusted(var1);
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
         return var0.group(ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(Data::expiresAt), Crypt.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(Data::key), ExtraCodecs.BASE64_STRING.fieldOf("signature").forGetter(Data::keySignature)).apply(var0, Data::new);
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

      boolean validateSignature(SignatureValidator var1) {
         return var1.validate(this.signedPayload().getBytes(StandardCharsets.US_ASCII), this.keySignature);
      }

      private String signedPayload() {
         String var1 = Crypt.rsaPublicKeyToString(this.key);
         long var10000 = this.expiresAt.toEpochMilli();
         return "" + var10000 + var1;
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
