package net.minecraft.client.multiplayer;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse.KeyPair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class AccountProfileKeyPairManager implements ProfileKeyPairManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Duration MINIMUM_PROFILE_KEY_REFRESH_INTERVAL = Duration.ofHours(1L);
   private static final Path PROFILE_KEY_PAIR_DIR = Path.of("profilekeys");
   private final UserApiService userApiService;
   private final Path profileKeyPairPath;
   private CompletableFuture<Optional<ProfileKeyPair>> keyPair;
   private Instant nextProfileKeyRefreshTime = Instant.EPOCH;

   public AccountProfileKeyPairManager(UserApiService var1, UUID var2, Path var3) {
      super();
      this.userApiService = var1;
      this.profileKeyPairPath = var3.resolve(PROFILE_KEY_PAIR_DIR).resolve(var2 + ".json");
      this.keyPair = CompletableFuture.<Optional>supplyAsync(
            () -> this.readProfileKeyPair().filter(var0 -> !var0.publicKey().data().hasExpired()), Util.backgroundExecutor()
         )
         .thenCompose(this::readOrFetchProfileKeyPair);
   }

   @Override
   public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair() {
      this.nextProfileKeyRefreshTime = Instant.now().plus(MINIMUM_PROFILE_KEY_REFRESH_INTERVAL);
      this.keyPair = this.keyPair.thenCompose(this::readOrFetchProfileKeyPair);
      return this.keyPair;
   }

   @Override
   public boolean shouldRefreshKeyPair() {
      return this.keyPair.isDone() && Instant.now().isAfter(this.nextProfileKeyRefreshTime)
         ? this.keyPair.join().map(ProfileKeyPair::dueRefresh).orElse(true)
         : false;
   }

   private CompletableFuture<Optional<ProfileKeyPair>> readOrFetchProfileKeyPair(Optional<ProfileKeyPair> var1) {
      return CompletableFuture.supplyAsync(() -> {
         if (var1.isPresent() && !((ProfileKeyPair)var1.get()).dueRefresh()) {
            if (!SharedConstants.IS_RUNNING_IN_IDE) {
               this.writeProfileKeyPair(null);
            }

            return var1;
         } else {
            try {
               ProfileKeyPair var2 = this.fetchProfileKeyPair(this.userApiService);
               this.writeProfileKeyPair(var2);
               return Optional.of(var2);
            } catch (CryptException | MinecraftClientException | IOException var3) {
               LOGGER.error("Failed to retrieve profile key pair", var3);
               this.writeProfileKeyPair(null);
               return var1;
            }
         }
      }, Util.backgroundExecutor());
   }

   private Optional<ProfileKeyPair> readProfileKeyPair() {
      if (Files.notExists(this.profileKeyPairPath)) {
         return Optional.empty();
      } else {
         try {
            Optional var2;
            try (BufferedReader var1 = Files.newBufferedReader(this.profileKeyPairPath)) {
               var2 = ProfileKeyPair.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(var1)).result();
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
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            ProfileKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, var1).result().ifPresent(var1x -> {
               try {
                  Files.createDirectories(this.profileKeyPairPath.getParent());
                  Files.writeString(this.profileKeyPairPath, var1x.toString());
               } catch (Exception var3xx) {
                  LOGGER.error("Failed to write profile key pair file {}", this.profileKeyPairPath, var3xx);
               }
            });
         }
      }
   }

   private ProfileKeyPair fetchProfileKeyPair(UserApiService var1) throws CryptException, IOException {
      KeyPairResponse var2 = var1.getKeyPair();
      if (var2 != null) {
         ProfilePublicKey.Data var3 = parsePublicKey(var2);
         return new ProfileKeyPair(
            Crypt.stringToPemRsaPrivateKey(var2.keyPair().privateKey()), new ProfilePublicKey(var3), Instant.parse(var2.refreshedAfter())
         );
      } else {
         throw new IOException("Could not retrieve profile key pair");
      }
   }

   private static ProfilePublicKey.Data parsePublicKey(KeyPairResponse var0) throws CryptException {
      KeyPair var1 = var0.keyPair();
      if (!Strings.isNullOrEmpty(var1.publicKey()) && var0.publicKeySignature() != null && var0.publicKeySignature().array().length != 0) {
         try {
            Instant var2 = Instant.parse(var0.expiresAt());
            PublicKey var3 = Crypt.stringToRsaPublicKey(var1.publicKey());
            ByteBuffer var4 = var0.publicKeySignature();
            return new ProfilePublicKey.Data(var2, var3, var4.array());
         } catch (IllegalArgumentException | DateTimeException var5) {
            throw new CryptException(var5);
         }
      } else {
         throw new CryptException(new MissingException("Missing public key"));
      }
   }
}
