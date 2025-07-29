package net.minecraft.world.item.component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.Services;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringUtil;

public record ResolvableProfile(Optional<String> name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
   @Nullable
   private static volatile Resolver resolver;
   @Nullable
   private static volatile Executor mainThreadExecutor;
   public static final Executor CHECKED_MAIN_THREAD_EXECUTOR = (var0) -> {
      Executor var1 = mainThreadExecutor;
      if (var1 != null) {
         var1.execute(var0);
      }

   };
   private static final Codec<ResolvableProfile> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ResolvableProfile::name), UUIDUtil.CODEC.optionalFieldOf("id").forGetter(ResolvableProfile::id), ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(ResolvableProfile::properties)).apply(var0, ResolvableProfile::new));
   public static final Codec<ResolvableProfile> CODEC;
   public static final StreamCodec<ByteBuf, ResolvableProfile> STREAM_CODEC;

   public ResolvableProfile(Optional<String> var1, Optional<UUID> var2, PropertyMap var3) {
      this(var1, var2, var3, createGameProfile(var2, var1, var3));
   }

   public ResolvableProfile(GameProfile var1) {
      this(Optional.of(var1.getName()), Optional.of(var1.getId()), var1.getProperties(), var1);
   }

   public ResolvableProfile(Optional<String> var1, Optional<UUID> var2, PropertyMap var3, GameProfile var4) {
      super();
      this.name = var1;
      this.id = var2;
      this.properties = var3;
      this.gameProfile = var4;
   }

   @Nullable
   public ResolvableProfile pollResolve() {
      if (this.isResolved()) {
         return this;
      } else {
         Optional var1 = (Optional)this.fetchProfile().getNow((Object)null);
         return var1 != null ? this.createProfile(var1) : null;
      }
   }

   public CompletableFuture<ResolvableProfile> resolve() {
      return this.isResolved() ? CompletableFuture.completedFuture(this) : this.fetchProfile().thenApply(this::createProfile);
   }

   private CompletableFuture<Optional<GameProfile>> fetchProfile() {
      Resolver var1 = resolver;
      return var1 == null ? CompletableFuture.completedFuture(Optional.empty()) : var1.fetchProfile(this);
   }

   private ResolvableProfile createProfile(Optional<GameProfile> var1) {
      return new ResolvableProfile((GameProfile)var1.orElseGet(() -> createGameProfile(this.id, this.name)));
   }

   private static GameProfile createGameProfile(Optional<UUID> var0, Optional<String> var1) {
      return new GameProfile((UUID)var0.orElse(Util.NIL_UUID), (String)var1.orElse(""));
   }

   private static GameProfile createGameProfile(Optional<UUID> var0, Optional<String> var1, PropertyMap var2) {
      GameProfile var3 = createGameProfile(var0, var1);
      var3.getProperties().putAll(var2);
      return var3;
   }

   public boolean isResolved() {
      if (!this.properties.isEmpty()) {
         return true;
      } else {
         return this.id.isPresent() == this.name.isPresent();
      }
   }

   public static void setupResolver(Services var0, Executor var1) {
      resolver = new Resolver(var0);
      mainThreadExecutor = var1;
   }

   public static void clearResolver() {
      resolver = null;
      mainThreadExecutor = null;
   }

   static {
      CODEC = Codec.withAlternative(FULL_CODEC, ExtraCodecs.PLAYER_NAME, (var0) -> new ResolvableProfile(Optional.of(var0), Optional.empty(), new PropertyMap()));
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(16).apply(ByteBufCodecs::optional), ResolvableProfile::name, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), ResolvableProfile::id, ByteBufCodecs.GAME_PROFILE_PROPERTIES, ResolvableProfile::properties, ResolvableProfile::new);
   }

   public static class Resolver {
      private final LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCacheByName;
      final LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> profileCacheById;

      Resolver(final Services var1) {
         super();
         this.profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>() {
            public CompletableFuture<Optional<GameProfile>> load(UUID var1x) {
               return CompletableFuture.supplyAsync(() -> {
                  ProfileResult var2 = var1.sessionService().fetchProfile(var1x, true);
                  return Optional.ofNullable(var2).map(ProfileResult::profile);
               }, Util.backgroundExecutor().forName("fetchProfile"));
            }

            // $FF: synthetic method
            public Object load(final Object var1x) throws Exception {
               return this.load((UUID)var1x);
            }
         });
         this.profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>() {
            public CompletableFuture<Optional<GameProfile>> load(String var1x) {
               return var1.nameToIdCache().getAsync(var1x).thenCompose((var1xx) -> (CompletionStage)var1xx.map((var1x) -> (CompletableFuture)Resolver.this.profileCacheById.getUnchecked(var1x.id())).orElseGet(() -> CompletableFuture.completedFuture(Optional.empty())));
            }

            // $FF: synthetic method
            public Object load(final Object var1x) throws Exception {
               return this.load((String)var1x);
            }
         });
      }

      CompletableFuture<Optional<GameProfile>> fetchProfile(ResolvableProfile var1) {
         if (var1.id().isPresent()) {
            return (CompletableFuture)this.profileCacheById.getUnchecked((UUID)var1.id().get());
         } else {
            String var2 = (String)var1.name().orElseThrow();
            return StringUtil.isValidPlayerName(var2) ? (CompletableFuture)this.profileCacheByName.getUnchecked(var2) : CompletableFuture.completedFuture(Optional.empty());
         }
      }
   }
}
