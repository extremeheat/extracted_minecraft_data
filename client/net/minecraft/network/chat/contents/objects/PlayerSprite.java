package net.minecraft.network.chat.contents.objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.world.item.component.ResolvableProfile;

public record PlayerSprite(ResolvableProfile player, boolean hat) implements ObjectInfo {
   public static final MapCodec<PlayerSprite> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResolvableProfile.CODEC.fieldOf("player").forGetter(PlayerSprite::player), Codec.BOOL.optionalFieldOf("hat", true).forGetter(PlayerSprite::hat)).apply(var0, PlayerSprite::new));

   public PlayerSprite(ResolvableProfile var1, boolean var2) {
      super();
      this.player = var1;
      this.hat = var2;
   }

   public FontDescription fontDescription() {
      return new FontDescription.PlayerSprite(this.player, this.hat);
   }

   public String description() {
      return (String)this.player.name().map((var0) -> "[" + var0 + " head]").orElse("[unknown player head]");
   }

   public MapCodec<PlayerSprite> codec() {
      return MAP_CODEC;
   }
}
