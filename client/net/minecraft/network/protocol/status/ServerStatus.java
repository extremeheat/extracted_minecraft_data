package net.minecraft.network.protocol.status;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

public record ServerStatus(Component b, Optional<ServerStatus.Players> c, Optional<ServerStatus.Version> d, Optional<ServerStatus.Favicon> e, boolean f) {
   private final Component description;
   private final Optional<ServerStatus.Players> players;
   private final Optional<ServerStatus.Version> version;
   private final Optional<ServerStatus.Favicon> favicon;
   private final boolean enforcesSecureChat;
   public static final Codec<ServerStatus> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.COMPONENT.optionalFieldOf("description", CommonComponents.EMPTY).forGetter(ServerStatus::description),
               ServerStatus.Players.CODEC.optionalFieldOf("players").forGetter(ServerStatus::players),
               ServerStatus.Version.CODEC.optionalFieldOf("version").forGetter(ServerStatus::version),
               ServerStatus.Favicon.CODEC.optionalFieldOf("favicon").forGetter(ServerStatus::favicon),
               Codec.BOOL.optionalFieldOf("enforcesSecureChat", false).forGetter(ServerStatus::enforcesSecureChat)
            )
            .apply(var0, ServerStatus::new)
   );

   public ServerStatus(
      Component var1, Optional<ServerStatus.Players> var2, Optional<ServerStatus.Version> var3, Optional<ServerStatus.Favicon> var4, boolean var5
   ) {
      super();
      this.description = var1;
      this.players = var2;
      this.version = var3;
      this.favicon = var4;
      this.enforcesSecureChat = var5;
   }

   public static record Favicon(byte[] b) {
      private final byte[] iconBytes;
      private static final String PREFIX = "data:image/png;base64,";
      public static final Codec<ServerStatus.Favicon> CODEC = Codec.STRING.comapFlatMap(var0 -> {
         if (!var0.startsWith("data:image/png;base64,")) {
            return DataResult.error(() -> "Unknown format");
         } else {
            try {
               String var1 = var0.substring("data:image/png;base64,".length()).replaceAll("\n", "");
               byte[] var2 = Base64.getDecoder().decode(var1.getBytes(StandardCharsets.UTF_8));
               return DataResult.success(new ServerStatus.Favicon(var2));
            } catch (IllegalArgumentException var3) {
               return DataResult.error(() -> "Malformed base64 server icon");
            }
         }
      }, var0 -> "data:image/png;base64," + new String(Base64.getEncoder().encode(var0.iconBytes), StandardCharsets.UTF_8));

      public Favicon(byte[] var1) {
         super();
         this.iconBytes = var1;
      }
   }

   public static record Players(int b, int c, List<GameProfile> d) {
      private final int max;
      private final int online;
      private final List<GameProfile> sample;
      private static final Codec<GameProfile> PROFILE_CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName))
               .apply(var0, GameProfile::new)
      );
      public static final Codec<ServerStatus.Players> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.INT.fieldOf("max").forGetter(ServerStatus.Players::max),
                  Codec.INT.fieldOf("online").forGetter(ServerStatus.Players::online),
                  PROFILE_CODEC.listOf().optionalFieldOf("sample", List.of()).forGetter(ServerStatus.Players::sample)
               )
               .apply(var0, ServerStatus.Players::new)
      );

      public Players(int var1, int var2, List<GameProfile> var3) {
         super();
         this.max = var1;
         this.online = var2;
         this.sample = var3;
      }
   }

   public static record Version(String b, int c) {
      private final String name;
      private final int protocol;
      public static final Codec<ServerStatus.Version> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.STRING.fieldOf("name").forGetter(ServerStatus.Version::name), Codec.INT.fieldOf("protocol").forGetter(ServerStatus.Version::protocol)
               )
               .apply(var0, ServerStatus.Version::new)
      );

      public Version(String var1, int var2) {
         super();
         this.name = var1;
         this.protocol = var2;
      }

      public static ServerStatus.Version current() {
         WorldVersion var0 = SharedConstants.getCurrentVersion();
         return new ServerStatus.Version(var0.getName(), var0.getProtocolVersion());
      }
   }
}
