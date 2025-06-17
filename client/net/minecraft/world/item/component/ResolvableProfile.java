package net.minecraft.world.item.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public record ResolvableProfile(Optional<String> name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
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
         Optional var1;
         if (this.id.isPresent()) {
            var1 = (Optional)SkullBlockEntity.fetchGameProfile((UUID)this.id.get()).getNow((Object)null);
         } else {
            var1 = (Optional)SkullBlockEntity.fetchGameProfile((String)this.name.orElseThrow()).getNow((Object)null);
         }

         return var1 != null ? this.createProfile(var1) : null;
      }
   }

   public CompletableFuture<ResolvableProfile> resolve() {
      if (this.isResolved()) {
         return CompletableFuture.completedFuture(this);
      } else {
         return this.id.isPresent() ? SkullBlockEntity.fetchGameProfile((UUID)this.id.get()).thenApply(this::createProfile) : SkullBlockEntity.fetchGameProfile((String)this.name.orElseThrow()).thenApply(this::createProfile);
      }
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

   static {
      CODEC = Codec.withAlternative(FULL_CODEC, ExtraCodecs.PLAYER_NAME, (var0) -> new ResolvableProfile(Optional.of(var0), Optional.empty(), new PropertyMap()));
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(16).apply(ByteBufCodecs::optional), ResolvableProfile::name, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), ResolvableProfile::id, ByteBufCodecs.GAME_PROFILE_PROPERTIES, ResolvableProfile::properties, ResolvableProfile::new);
   }
}
