package net.minecraft.client.multiplayer;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class ProfileKeyPairManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Path PROFILE_KEY_PAIR_DIR = Path.of("profilekeys");
   private final Path profileKeyPairPath;
   private final CompletableFuture<Optional<ProfilePublicKey>> publicKey;
   private final CompletableFuture<Optional<Signer>> signer;

   public ProfileKeyPairManager(UserApiService var1, UUID var2, Path var3) {
      super();
      this.profileKeyPairPath = var3.resolve(PROFILE_KEY_PAIR_DIR).resolve("" + var2 + ".json");
      CompletableFuture var4 = this.readOrFetchProfileKeyPair(var1);
      this.publicKey = var4.thenApply((var0) -> {
         return var0.map(ProfileKeyPair::publicKey);
      });
      this.signer = var4.thenApply((var0) -> {
         return var0.map((var0x) -> {
            return Signer.from(var0x.privateKey(), "SHA256withRSA");
         });
      });
   }

   private CompletableFuture<Optional<ProfileKeyPair>> readOrFetchProfileKeyPair(UserApiService var1) {
      return CompletableFuture.supplyAsync(() -> {
         Optional var2 = this.readProfileKeyPair().filter((var0) -> {
            return !var0.publicKey().data().hasExpired();
         });
         if (var2.isPresent() && !((ProfileKeyPair)var2.get()).dueRefresh()) {
            return var2;
         } else {
            try {
               ProfileKeyPair var3 = this.fetchProfileKeyPair(var1);
               this.writeProfileKeyPair(var3);
               return Optional.of(var3);
            } catch (CryptException | MinecraftClientException | IOException var4) {
               LOGGER.error("Failed to retrieve profile key pair", var4);
               this.writeProfileKeyPair((ProfileKeyPair)null);
               return var2;
            }
         }
      }, Util.backgroundExecutor());
   }

   private Optional<ProfileKeyPair> readProfileKeyPair() {
      if (Files.notExists(this.profileKeyPairPath, new LinkOption[0])) {
         return Optional.empty();
      } else {
         try {
            BufferedReader var1 = Files.newBufferedReader(this.profileKeyPairPath);

            Optional var2;
            try {
               var2 = ProfileKeyPair.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(var1)).result();
            } catch (Throwable var5) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (var1 != null) {
               var1.close();
            }

            return var2;
         } catch (Exception var6) {
            LOGGER.error("Failed to read profile key pair file {}", this.profileKeyPairPath, var6);
            return Optional.empty();
         }
      }
   }

   private void writeProfileKeyPair(@Nullable ProfileKeyPair var1) {
      try {
         Files.deleteIfExists(this.profileKeyPairPath);
      } catch (IOException var3) {
         LOGGER.error("Failed to delete profile key pair file {}", this.profileKeyPairPath, var3);
      }

      if (var1 != null) {
         ProfileKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, var1).result().ifPresent((var1x) -> {
            try {
               Files.createDirectories(this.profileKeyPairPath.getParent());
               Files.writeString(this.profileKeyPairPath, var1x.toString());
            } catch (Exception var3) {
               LOGGER.error("Failed to write profile key pair file {}", this.profileKeyPairPath, var3);
            }

         });
      }
   }

   private ProfileKeyPair fetchProfileKeyPair(UserApiService var1) throws CryptException, IOException {
      KeyPairResponse var2 = var1.getKeyPair();
      if (var2 != null) {
         ProfilePublicKey.Data var3 = parsePublicKey(var2);
         return new ProfileKeyPair(Crypt.stringToPemRsaPrivateKey(var2.getPrivateKey()), ProfilePublicKey.createTrusted(var3), Instant.parse(var2.getRefreshedAfter()));
      } else {
         throw new IOException("Could not retrieve profile key pair");
      }
   }

   private static ProfilePublicKey.Data parsePublicKey(KeyPairResponse var0) throws CryptException {
      if (!Strings.isNullOrEmpty(var0.getPublicKey()) && !Strings.isNullOrEmpty(var0.getPublicKeySignature())) {
         try {
            Instant var1 = Instant.parse(var0.getExpiresAt());
            PublicKey var2 = Crypt.stringToRsaPublicKey(var0.getPublicKey());
            byte[] var3 = Base64.getDecoder().decode(var0.getPublicKeySignature());
            return new ProfilePublicKey.Data(var1, var2, var3);
         } catch (IllegalArgumentException | DateTimeException var4) {
            throw new CryptException(var4);
         }
      } else {
         throw new CryptException(new InsecurePublicKeyException.MissingException());
      }
   }

   @Nullable
   public Signer signer() {
      return (Signer)((Optional)this.signer.join()).orElse((Object)null);
   }

   public Optional<ProfilePublicKey> profilePublicKey() {
      return (Optional)this.publicKey.join();
   }

   public Optional<ProfilePublicKey.Data> profilePublicKeyData() {
      return this.profilePublicKey().map(ProfilePublicKey::data);
   }
}
