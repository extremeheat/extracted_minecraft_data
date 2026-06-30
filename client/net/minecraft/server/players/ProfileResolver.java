package net.minecraft.server.players;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SessionService;
import com.mojang.authlib.services.ProfileResult;
import com.mojang.datafixers.util.Either;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.StringUtil;

public interface ProfileResolver {
   Optional<GameProfile> fetchByName(String name);

   Optional<GameProfile> fetchById(UUID id);

   default Optional<GameProfile> fetchByNameOrId(final Either<String, UUID> nameOrId) {
      return (Optional)nameOrId.map(this::fetchByName, this::fetchById);
   }

   public static class Cached implements ProfileResolver {
      private final LoadingCache<String, Optional<GameProfile>> profileCacheByName;
      private final LoadingCache<UUID, Optional<GameProfile>> profileCacheById;

      public Cached(final SessionService sessionService, final UserNameToIdResolver nameToIdCache) {
         super();
         this.profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<UUID, Optional<GameProfile>>() {
            {
               Objects.requireNonNull(Cached.this);
            }

            public Optional<GameProfile> load(final UUID profileId) {
               ProfileResult result = sessionService.fetchProfile(profileId, true);
               return Optional.ofNullable(result).map(ProfileResult::profile);
            }
         });
         this.profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<String, Optional<GameProfile>>() {
            {
               Objects.requireNonNull(Cached.this);
            }

            public Optional<GameProfile> load(final String name) {
               return nameToIdCache.get(name).flatMap((nameAndId) -> (Optional)Cached.this.profileCacheById.getUnchecked(nameAndId.id()));
            }
         });
      }

      public Optional<GameProfile> fetchByName(final String name) {
         return StringUtil.isValidPlayerName(name) ? (Optional)this.profileCacheByName.getUnchecked(name) : Optional.empty();
      }

      public Optional<GameProfile> fetchById(final UUID id) {
         return (Optional)this.profileCacheById.getUnchecked(id);
      }
   }
}
