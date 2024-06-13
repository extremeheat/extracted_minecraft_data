package net.minecraft.world.item.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public record ResolvableProfile(Optional<String> name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
   private static final Codec<ResolvableProfile> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ResolvableProfile::name),
               UUIDUtil.CODEC.optionalFieldOf("id").forGetter(ResolvableProfile::id),
               ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(ResolvableProfile::properties)
            )
            .apply(var0, ResolvableProfile::new)
   );
   public static final Codec<ResolvableProfile> CODEC = Codec.withAlternative(
      FULL_CODEC, ExtraCodecs.PLAYER_NAME, var0 -> new ResolvableProfile(Optional.of(var0), Optional.empty(), new PropertyMap())
   );
   public static final StreamCodec<ByteBuf, ResolvableProfile> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.stringUtf8(16).apply(ByteBufCodecs::optional),
      ResolvableProfile::name,
      UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional),
      ResolvableProfile::id,
      ByteBufCodecs.GAME_PROFILE_PROPERTIES,
      ResolvableProfile::properties,
      ResolvableProfile::new
   );

   public ResolvableProfile(Optional<String> var1, Optional<UUID> var2, PropertyMap var3) {
      this(var1, var2, var3, createProfile(var1, var2, var3));
   }

   public ResolvableProfile(GameProfile var1) {
      this(Optional.of(var1.getName()), Optional.of(var1.getId()), var1.getProperties(), var1);
   }

   public ResolvableProfile(Optional<String> name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
      super();
      this.name = name;
      this.id = id;
      this.properties = properties;
      this.gameProfile = gameProfile;
   }

   public CompletableFuture<ResolvableProfile> resolve() {
      if (this.isResolved()) {
         return CompletableFuture.completedFuture(this);
      } else {
         return this.id.isPresent() ? SkullBlockEntity.fetchGameProfile(this.id.get()).thenApply(var1 -> {
            GameProfile var2 = var1.orElseGet(() -> new GameProfile(this.id.get(), this.name.orElse("")));
            return new ResolvableProfile(var2);
         }) : SkullBlockEntity.fetchGameProfile(this.name.orElseThrow()).thenApply(var1 -> {
            GameProfile var2 = var1.orElseGet(() -> new GameProfile(Util.NIL_UUID, this.name.get()));
            return new ResolvableProfile(var2);
         });
      }
   }

   private static GameProfile createProfile(Optional<String> var0, Optional<UUID> var1, PropertyMap var2) {
      GameProfile var3 = new GameProfile(var1.orElse(Util.NIL_UUID), var0.orElse(""));
      var3.getProperties().putAll(var2);
      return var3;
   }

   public boolean isResolved() {
      return !this.properties.isEmpty() ? true : this.id.isPresent() == this.name.isPresent();
   }
}
