package net.minecraft.server.players;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.datafixers.util.Either;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.StringUtil;

public interface ProfileResolver {
   Optional<GameProfile> fetchByName(String var1);

   Optional<GameProfile> fetchById(UUID var1);

   default Optional<GameProfile> fetchByNameOrId(Either<String, UUID> var1) {
      return (Optional)var1.map(this::fetchByName, this::fetchById);
   }

   public static class Cached implements ProfileResolver {
      private final LoadingCache<String, Optional<GameProfile>> profileCacheByName;
      final LoadingCache<UUID, Optional<GameProfile>> profileCacheById;

      public Cached(final MinecraftSessionService var1, final UserNameToIdResolver var2) {
         super();
         this.profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<UUID, Optional<GameProfile>>() {
            public Optional<GameProfile> load(UUID var1x) {
               ProfileResult var2 = var1.fetchProfile(var1x, true);
               return Optional.ofNullable(var2).map(ProfileResult::profile);
            }

            // $FF: synthetic method
            public Object load(final Object var1x) throws Exception {
               return this.load((UUID)var1x);
            }
         });
         this.profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<String, Optional<GameProfile>>() {
            public Optional<GameProfile> load(String var1) {
               return var2.get(var1).flatMap((var1x) -> (Optional)Cached.this.profileCacheById.getUnchecked(var1x.id()));
            }

            // $FF: synthetic method
            public Object load(final Object var1) throws Exception {
               return this.load((String)var1);
            }
         });
      }

      public Optional<GameProfile> fetchByName(String var1) {
         return StringUtil.isValidPlayerName(var1) ? (Optional)this.profileCacheByName.getUnchecked(var1) : Optional.empty();
      }

      public Optional<GameProfile> fetchById(UUID var1) {
         return (Optional)this.profileCacheById.getUnchecked(var1);
      }
   }
}
