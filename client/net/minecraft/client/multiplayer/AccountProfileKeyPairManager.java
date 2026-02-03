package net.minecraft.client.multiplayer;

import com.google.common.base.Strings;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class AccountProfileKeyPairManager implements ProfileKeyPairManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Duration MINIMUM_PROFILE_KEY_REFRESH_INTERVAL = Duration.ofHours(1L);
   private static final Path PROFILE_KEY_PAIR_DIR = Path.of("profilekeys");
   private final UserApiService userApiService;
   private final Path profileKeyPairPath;
   private CompletableFuture<Optional<ProfileKeyPair>> keyPair = CompletableFuture.completedFuture(Optional.empty());
   private Instant nextProfileKeyRefreshTime;

   public AccountProfileKeyPairManager(final UserApiService userApiService, final UUID profileId, final Path gameDirectory) {
      super();
      this.nextProfileKeyRefreshTime = Instant.EPOCH;
      this.userApiService = userApiService;
      this.profileKeyPairPath = gameDirectory.resolve(PROFILE_KEY_PAIR_DIR).resolve(String.valueOf(profileId) + ".json");
   }

   public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair() {
      this.nextProfileKeyRefreshTime = Instant.now().plus(MINIMUM_PROFILE_KEY_REFRESH_INTERVAL);
      this.keyPair = this.keyPair.thenCompose(this::readOrFetchProfileKeyPair);
      return this.keyPair;
   }

   public boolean shouldRefreshKeyPair() {
      return this.keyPair.isDone() && Instant.now().isAfter(this.nextProfileKeyRefreshTime) ? (Boolean)((Optional)this.keyPair.join()).map(ProfileKeyPair::dueRefresh).orElse(true) : false;
   }

   private CompletableFuture<Optional<ProfileKeyPair>> readOrFetchProfileKeyPair(final Optional<ProfileKeyPair> cachedKeyPair) {
      return CompletableFuture.supplyAsync(() -> {
         if (cachedKeyPair.isPresent() && !((ProfileKeyPair)cachedKeyPair.get()).dueRefresh()) {
            if (!SharedConstants.IS_RUNNING_IN_IDE) {
               this.writeProfileKeyPair((ProfileKeyPair)null);
            }

            return cachedKeyPair;
         } else {
            try {
               ProfileKeyPair fetchedKeyPair = this.fetchProfileKeyPair(this.userApiService);
               this.writeProfileKeyPair(fetchedKeyPair);
               return Optional.ofNullable(fetchedKeyPair);
            } catch (CryptException | MinecraftClientException | IOException e) {
               LOGGER.error("Failed to retrieve profile key pair", e);
               this.writeProfileKeyPair((ProfileKeyPair)null);
               return cachedKeyPair;
            }
         }
      }, Util.nonCriticalIoPool());
   }

   private Optional<ProfileKeyPair> readProfileKeyPair() {
      if (Files.notExists(this.profileKeyPairPath, new LinkOption[0])) {
         return Optional.empty();
      } else {
         try {
            BufferedReader bufferedReader = Files.newBufferedReader(this.profileKeyPairPath);

            Optional var2;
            try {
               var2 = ProfileKeyPair.CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse((Reader)bufferedReader)).result();
            } catch (Throwable var5) {
               if (bufferedReader != null) {
                  try {
                     bufferedReader.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (bufferedReader != null) {
               bufferedReader.close();
            }

            return var2;
         } catch (Exception e) {
            LOGGER.error("Failed to read profile key pair file {}", this.profileKeyPairPath, e);
            return Optional.empty();
         }
      }
   }

   private void writeProfileKeyPair(final @Nullable ProfileKeyPair profileKeyPair) {
      try {
         Files.deleteIfExists(this.profileKeyPairPath);
      } catch (IOException e) {
         LOGGER.error("Failed to delete profile key pair file {}", this.profileKeyPairPath, e);
      }

      if (profileKeyPair != null) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            ProfileKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, profileKeyPair).ifSuccess((jsonStr) -> {
               try {
                  Files.createDirectories(this.profileKeyPairPath.getParent());
                  Files.writeString(this.profileKeyPairPath, jsonStr.toString());
               } catch (Exception e) {
                  LOGGER.error("Failed to write profile key pair file {}", this.profileKeyPairPath, e);
               }

            });
         }
      }
   }

   private @Nullable ProfileKeyPair fetchProfileKeyPair(final UserApiService userApiService) throws CryptException, IOException {
      KeyPairResponse keyPair = userApiService.getKeyPair();
      if (keyPair != null) {
         ProfilePublicKey.Data publicKeyData = parsePublicKey(keyPair);
         return new ProfileKeyPair(Crypt.stringToPemRsaPrivateKey(keyPair.keyPair().privateKey()), new ProfilePublicKey(publicKeyData), Instant.parse(keyPair.refreshedAfter()));
      } else {
         return null;
      }
   }

   private static ProfilePublicKey.Data parsePublicKey(final KeyPairResponse response) throws CryptException {
      KeyPairResponse.KeyPair keyPair = response.keyPair();
      if (keyPair != null && !Strings.isNullOrEmpty(keyPair.publicKey()) && response.publicKeySignature() != null && response.publicKeySignature().array().length != 0) {
         try {
            Instant expiresAt = Instant.parse(response.expiresAt());
            PublicKey key = Crypt.stringToRsaPublicKey(keyPair.publicKey());
            ByteBuffer signature = response.publicKeySignature();
            return new ProfilePublicKey.Data(expiresAt, key, signature.array());
         } catch (IllegalArgumentException | DateTimeException e) {
            throw new CryptException(e);
         }
      } else {
         throw new CryptException(new InsecurePublicKeyException.MissingException("Missing public key"));
      }
   }
}
