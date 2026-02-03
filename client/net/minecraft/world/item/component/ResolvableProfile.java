package net.minecraft.world.item.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public abstract sealed class ResolvableProfile implements TooltipProvider {
   private static final Codec<ResolvableProfile> FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.mapEither(ExtraCodecs.STORED_GAME_PROFILE, ResolvableProfile.Partial.MAP_CODEC).forGetter(ResolvableProfile::unpack), PlayerSkin.Patch.MAP_CODEC.forGetter(ResolvableProfile::skinPatch)).apply(i, ResolvableProfile::create));
   public static final Codec<ResolvableProfile> CODEC;
   public static final StreamCodec<ByteBuf, ResolvableProfile> STREAM_CODEC;
   protected final GameProfile partialProfile;
   protected final PlayerSkin.Patch skinPatch;

   private static ResolvableProfile create(final Either<GameProfile, Partial> value, final PlayerSkin.Patch patch) {
      return (ResolvableProfile)value.map((full) -> new Static(Either.left(full), patch), (partial) -> (ResolvableProfile)(partial.properties.isEmpty() && partial.id.isPresent() != partial.name.isPresent() ? (ResolvableProfile)partial.name.map((s) -> new Dynamic(Either.left(s), patch)).orElseGet(() -> new Dynamic(Either.right((UUID)partial.id.get()), patch)) : new Static(Either.right(partial), patch)));
   }

   public static ResolvableProfile createResolved(final GameProfile gameProfile) {
      return new Static(Either.left(gameProfile), PlayerSkin.Patch.EMPTY);
   }

   public static ResolvableProfile createUnresolved(final String name) {
      return new Dynamic(Either.left(name), PlayerSkin.Patch.EMPTY);
   }

   public static ResolvableProfile createUnresolved(final UUID id) {
      return new Dynamic(Either.right(id), PlayerSkin.Patch.EMPTY);
   }

   protected abstract Either<GameProfile, Partial> unpack();

   protected ResolvableProfile(final GameProfile partialProfile, final PlayerSkin.Patch skinPatch) {
      super();
      this.partialProfile = partialProfile;
      this.skinPatch = skinPatch;
   }

   public abstract CompletableFuture<GameProfile> resolveProfile(ProfileResolver profileResolver);

   public GameProfile partialProfile() {
      return this.partialProfile;
   }

   public PlayerSkin.Patch skinPatch() {
      return this.skinPatch;
   }

   private static GameProfile createPartialProfile(final Optional<String> maybeName, final Optional<UUID> maybeId, final PropertyMap properties) {
      String name = (String)maybeName.orElse("");
      UUID id = (UUID)maybeId.orElseGet(() -> (UUID)maybeName.map(UUIDUtil::createOfflinePlayerUUID).orElse(Util.NIL_UUID));
      return new GameProfile(id, name, properties);
   }

   public abstract Optional<String> name();

   static {
      CODEC = Codec.withAlternative(FULL_CODEC, ExtraCodecs.PLAYER_NAME, ResolvableProfile::createUnresolved);
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.either(ByteBufCodecs.GAME_PROFILE, ResolvableProfile.Partial.STREAM_CODEC), ResolvableProfile::unpack, PlayerSkin.Patch.STREAM_CODEC, ResolvableProfile::skinPatch, ResolvableProfile::create);
   }

   protected static record Partial(Optional<String> name, Optional<UUID> id, PropertyMap properties) {
      public static final Partial EMPTY;
      private static final MapCodec<Partial> MAP_CODEC;
      public static final StreamCodec<ByteBuf, Partial> STREAM_CODEC;

      protected Partial {
         super();
      }

      private GameProfile createProfile() {
         return ResolvableProfile.createPartialProfile(this.name, this.id, this.properties);
      }

      static {
         EMPTY = new Partial(Optional.empty(), Optional.empty(), PropertyMap.EMPTY);
         MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.PLAYER_NAME.optionalFieldOf("name").forGetter(Partial::name), UUIDUtil.CODEC.optionalFieldOf("id").forGetter(Partial::id), ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", PropertyMap.EMPTY).forGetter(Partial::properties)).apply(i, Partial::new));
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.PLAYER_NAME.apply(ByteBufCodecs::optional), Partial::name, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), Partial::id, ByteBufCodecs.GAME_PROFILE_PROPERTIES, Partial::properties, Partial::new);
      }
   }

   public static final class Static extends ResolvableProfile {
      public static final Static EMPTY;
      private final Either<GameProfile, Partial> contents;

      private Static(final Either<GameProfile, Partial> contents, final PlayerSkin.Patch skinPatch) {
         super((GameProfile)contents.map((gameProfile) -> gameProfile, Partial::createProfile), skinPatch);
         this.contents = contents;
      }

      public CompletableFuture<GameProfile> resolveProfile(final ProfileResolver profileResolver) {
         return CompletableFuture.completedFuture(this.partialProfile);
      }

      protected Either<GameProfile, Partial> unpack() {
         return this.contents;
      }

      public Optional<String> name() {
         return (Optional)this.contents.map((gameProfile) -> Optional.of(gameProfile.name()), (partial) -> partial.name);
      }

      public boolean equals(final Object o) {
         boolean var10000;
         if (this != o) {
            label28: {
               if (o instanceof Static) {
                  Static that = (Static)o;
                  if (this.contents.equals(that.contents) && this.skinPatch.equals(that.skinPatch)) {
                     break label28;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public int hashCode() {
         int result = 31 + this.contents.hashCode();
         result = 31 * result + this.skinPatch.hashCode();
         return result;
      }

      public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
      }

      static {
         EMPTY = new Static(Either.right(ResolvableProfile.Partial.EMPTY), PlayerSkin.Patch.EMPTY);
      }
   }

   public static final class Dynamic extends ResolvableProfile {
      private static final Component DYNAMIC_TOOLTIP;
      private final Either<String, UUID> nameOrId;

      private Dynamic(final Either<String, UUID> nameOrId, final PlayerSkin.Patch skinPatch) {
         super(ResolvableProfile.createPartialProfile(nameOrId.left(), nameOrId.right(), PropertyMap.EMPTY), skinPatch);
         this.nameOrId = nameOrId;
      }

      public Optional<String> name() {
         return this.nameOrId.left();
      }

      public boolean equals(final Object o) {
         boolean var10000;
         if (this != o) {
            label28: {
               if (o instanceof Dynamic) {
                  Dynamic that = (Dynamic)o;
                  if (this.nameOrId.equals(that.nameOrId) && this.skinPatch.equals(that.skinPatch)) {
                     break label28;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public int hashCode() {
         int result = 31 + this.nameOrId.hashCode();
         result = 31 * result + this.skinPatch.hashCode();
         return result;
      }

      protected Either<GameProfile, Partial> unpack() {
         return Either.right(new Partial(this.nameOrId.left(), this.nameOrId.right(), PropertyMap.EMPTY));
      }

      public CompletableFuture<GameProfile> resolveProfile(final ProfileResolver profileResolver) {
         return CompletableFuture.supplyAsync(() -> (GameProfile)profileResolver.fetchByNameOrId(this.nameOrId).orElse(this.partialProfile), Util.nonCriticalIoPool());
      }

      public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
         consumer.accept(DYNAMIC_TOOLTIP);
      }

      static {
         DYNAMIC_TOOLTIP = Component.translatable("component.profile.dynamic").withStyle(ChatFormatting.GRAY);
      }
   }
}
