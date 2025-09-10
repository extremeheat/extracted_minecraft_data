package net.minecraft.world.item.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public abstract sealed class ResolvableProfile implements TooltipProvider {
   private static final GameProfile NIL_PROFILE;
   public static final Codec<ResolvableProfile> CODEC;
   public static final StreamCodec<ByteBuf, ResolvableProfile> STREAM_CODEC;
   protected final GameProfile partialProfile;

   private static ResolvableProfile create(Either<GameProfile, Partial> var0) {
      return (ResolvableProfile)var0.map((var0x) -> new Static(Either.left(var0x)), (var0x) -> (ResolvableProfile)(var0x.properties.isEmpty() && var0x.id.isPresent() != var0x.name.isPresent() ? (ResolvableProfile)var0x.name.map((var0) -> new Dynamic(Either.left(var0))).orElseGet(() -> new Dynamic(Either.right((UUID)var0x.id.get()))) : new Static(Either.right(var0x))));
   }

   public static ResolvableProfile createResolved(GameProfile var0) {
      return new Static(Either.left(var0));
   }

   public static ResolvableProfile createUnresolved(UUID var0) {
      return new Dynamic(Either.right(var0));
   }

   protected abstract Either<GameProfile, Partial> unpack();

   protected ResolvableProfile(GameProfile var1) {
      super();
      this.partialProfile = var1;
   }

   public abstract CompletableFuture<GameProfile> resolveProfile(ProfileResolver var1);

   public GameProfile partialProfile() {
      return this.partialProfile;
   }

   static GameProfile createPartialProfile(Optional<String> var0, Optional<UUID> var1, PropertyMap var2) {
      if (var0.isEmpty() && var1.isEmpty()) {
         return NIL_PROFILE;
      } else {
         String var3 = (String)var0.orElse("");
         UUID var4 = (UUID)var1.orElseGet(() -> UUIDUtil.createOfflinePlayerUUID(var3));
         return new GameProfile(var4, var3, var2);
      }
   }

   public abstract Optional<String> name();

   static {
      NIL_PROFILE = new GameProfile(Util.NIL_UUID, "");
      CODEC = Codec.either(ExtraCodecs.STORED_GAME_PROFILE, ResolvableProfile.Partial.CODEC).xmap(ResolvableProfile::create, ResolvableProfile::unpack);
      STREAM_CODEC = ByteBufCodecs.either(ByteBufCodecs.GAME_PROFILE, ResolvableProfile.Partial.STREAM_CODEC).map(ResolvableProfile::create, ResolvableProfile::unpack);
   }

   protected static record Partial(Optional<String> name, Optional<UUID> id, PropertyMap properties) {
      final Optional<String> name;
      final Optional<UUID> id;
      final PropertyMap properties;
      private static final Codec<Partial> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.PLAYER_NAME.optionalFieldOf("name").forGetter(Partial::name), UUIDUtil.CODEC.optionalFieldOf("id").forGetter(Partial::id), ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", PropertyMap.EMPTY).forGetter(Partial::properties)).apply(var0, Partial::new));
      public static final Codec<Partial> CODEC;
      public static final StreamCodec<ByteBuf, Partial> STREAM_CODEC;

      protected Partial(Optional<String> var1, Optional<UUID> var2, PropertyMap var3) {
         super();
         this.name = var1;
         this.id = var2;
         this.properties = var3;
      }

      private GameProfile createProfile() {
         return ResolvableProfile.createPartialProfile(this.name, this.id, this.properties);
      }

      static {
         CODEC = Codec.withAlternative(FULL_CODEC, ExtraCodecs.PLAYER_NAME, (var0) -> new Partial(Optional.of(var0), Optional.empty(), PropertyMap.EMPTY));
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.PLAYER_NAME.apply(ByteBufCodecs::optional), Partial::name, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), Partial::id, ByteBufCodecs.GAME_PROFILE_PROPERTIES, Partial::properties, Partial::new);
      }
   }

   public static final class Static extends ResolvableProfile {
      private final Either<GameProfile, Partial> contents;

      Static(Either<GameProfile, Partial> var1) {
         super((GameProfile)var1.map((var0) -> var0, Partial::createProfile));
         this.contents = var1;
      }

      public CompletableFuture<GameProfile> resolveProfile(ProfileResolver var1) {
         return CompletableFuture.completedFuture(this.partialProfile);
      }

      protected Either<GameProfile, Partial> unpack() {
         return this.contents;
      }

      public Optional<String> name() {
         return (Optional)this.contents.map((var0) -> Optional.of(var0.name()), (var0) -> var0.name);
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (this != var1) {
            label26: {
               if (var1 instanceof Static) {
                  Static var2 = (Static)var1;
                  if (this.contents.equals(var2.contents)) {
                     break label26;
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
         return this.contents.hashCode();
      }

      public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      }
   }

   public static final class Dynamic extends ResolvableProfile {
      private static final Component DYNAMIC_TOOLTIP;
      private final Either<String, UUID> nameOrId;

      Dynamic(Either<String, UUID> var1) {
         super(ResolvableProfile.createPartialProfile(var1.left(), var1.right(), PropertyMap.EMPTY));
         this.nameOrId = var1;
      }

      public Optional<String> name() {
         return this.nameOrId.left();
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (this != var1) {
            label26: {
               if (var1 instanceof Dynamic) {
                  Dynamic var2 = (Dynamic)var1;
                  if (this.nameOrId.equals(var2.nameOrId)) {
                     break label26;
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
         return this.nameOrId.hashCode();
      }

      protected Either<GameProfile, Partial> unpack() {
         return Either.right(new Partial(this.nameOrId.left(), this.nameOrId.right(), PropertyMap.EMPTY));
      }

      public CompletableFuture<GameProfile> resolveProfile(ProfileResolver var1) {
         return CompletableFuture.supplyAsync(() -> (GameProfile)var1.fetchByNameOrId(this.nameOrId).orElse(this.partialProfile), Util.nonCriticalIoPool());
      }

      public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
         var2.accept(DYNAMIC_TOOLTIP);
      }

      static {
         DYNAMIC_TOOLTIP = Component.translatable("component.profile.dynamic").withStyle(ChatFormatting.GRAY);
      }
   }
}
