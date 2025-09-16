package net.minecraft.world.entity.player;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PlayerSkin(ClientAsset.Texture body, @Nullable ClientAsset.Texture cape, @Nullable ClientAsset.Texture elytra, PlayerModelType model, boolean secure) {
   public PlayerSkin(ClientAsset.Texture var1, @Nullable ClientAsset.Texture var2, @Nullable ClientAsset.Texture var3, PlayerModelType var4, boolean var5) {
      super();
      this.body = var1;
      this.cape = var2;
      this.elytra = var3;
      this.model = var4;
      this.secure = var5;
   }

   public static PlayerSkin insecure(ClientAsset.Texture var0, @Nullable ClientAsset.Texture var1, @Nullable ClientAsset.Texture var2, PlayerModelType var3) {
      return new PlayerSkin(var0, var1, var2, var3, false);
   }

   public PlayerSkin with(Patch var1) {
      return var1.equals(PlayerSkin.Patch.EMPTY) ? this : insecure((ClientAsset.Texture)DataFixUtils.orElse(var1.body, this.body), (ClientAsset.Texture)DataFixUtils.orElse(var1.cape, this.cape), (ClientAsset.Texture)DataFixUtils.orElse(var1.elytra, this.elytra), (PlayerModelType)var1.model.orElse(this.model));
   }

   public static record Patch(Optional<ClientAsset.ResourceTexture> body, Optional<ClientAsset.ResourceTexture> cape, Optional<ClientAsset.ResourceTexture> elytra, Optional<PlayerModelType> model) {
      final Optional<ClientAsset.ResourceTexture> body;
      final Optional<ClientAsset.ResourceTexture> cape;
      final Optional<ClientAsset.ResourceTexture> elytra;
      final Optional<PlayerModelType> model;
      public static final Patch EMPTY = new Patch(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
      public static final MapCodec<Patch> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ClientAsset.ResourceTexture.CODEC.optionalFieldOf("texture").forGetter(Patch::body), ClientAsset.ResourceTexture.CODEC.optionalFieldOf("cape").forGetter(Patch::cape), ClientAsset.ResourceTexture.CODEC.optionalFieldOf("elytra").forGetter(Patch::elytra), PlayerModelType.CODEC.optionalFieldOf("model").forGetter(Patch::model)).apply(var0, Patch::create));
      public static final StreamCodec<ByteBuf, Patch> STREAM_CODEC;

      public Patch(Optional<ClientAsset.ResourceTexture> var1, Optional<ClientAsset.ResourceTexture> var2, Optional<ClientAsset.ResourceTexture> var3, Optional<PlayerModelType> var4) {
         super();
         this.body = var1;
         this.cape = var2;
         this.elytra = var3;
         this.model = var4;
      }

      public static Patch create(Optional<ClientAsset.ResourceTexture> var0, Optional<ClientAsset.ResourceTexture> var1, Optional<ClientAsset.ResourceTexture> var2, Optional<PlayerModelType> var3) {
         return var0.isEmpty() && var1.isEmpty() && var2.isEmpty() && var3.isEmpty() ? EMPTY : new Patch(var0, var1, var2, var3);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional), Patch::body, ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional), Patch::cape, ClientAsset.ResourceTexture.STREAM_CODEC.apply(ByteBufCodecs::optional), Patch::elytra, PlayerModelType.STREAM_CODEC.apply(ByteBufCodecs::optional), Patch::model, Patch::create);
      }
   }
}
