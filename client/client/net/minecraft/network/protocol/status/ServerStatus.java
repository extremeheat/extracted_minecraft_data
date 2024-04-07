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
import net.minecraft.network.chat.ComponentSerialization;

public record ServerStatus(
   Component description,
   Optional<ServerStatus.Players> players,
   Optional<ServerStatus.Version> version,
   Optional<ServerStatus.Favicon> favicon,
   boolean enforcesSecureChat
) {
   public static final Codec<ServerStatus> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ComponentSerialization.CODEC.lenientOptionalFieldOf("description", CommonComponents.EMPTY).forGetter(ServerStatus::description),
               ServerStatus.Players.CODEC.lenientOptionalFieldOf("players").forGetter(ServerStatus::players),
               ServerStatus.Version.CODEC.lenientOptionalFieldOf("version").forGetter(ServerStatus::version),
               ServerStatus.Favicon.CODEC.lenientOptionalFieldOf("favicon").forGetter(ServerStatus::favicon),
               Codec.BOOL.lenientOptionalFieldOf("enforcesSecureChat", false).forGetter(ServerStatus::enforcesSecureChat)
            )
            .apply(var0, ServerStatus::new)
   );

   public ServerStatus(
      Component description,
      Optional<ServerStatus.Players> players,
      Optional<ServerStatus.Version> version,
      Optional<ServerStatus.Favicon> favicon,
      boolean enforcesSecureChat
   ) {
      super();
      this.description = description;
      this.players = players;
      this.version = version;
      this.favicon = favicon;
      this.enforcesSecureChat = enforcesSecureChat;
   }

   public static record Favicon(byte[] iconBytes) {
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

      public Favicon(byte[] iconBytes) {
         super();
         this.iconBytes = iconBytes;
      }
   }

   public static record Players(int max, int online, List<GameProfile> sample) {
      private static final Codec<GameProfile> PROFILE_CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName))
               .apply(var0, GameProfile::new)
      );
      public static final Codec<ServerStatus.Players> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.INT.fieldOf("max").forGetter(ServerStatus.Players::max),
                  Codec.INT.fieldOf("online").forGetter(ServerStatus.Players::online),
                  PROFILE_CODEC.listOf().lenientOptionalFieldOf("sample", List.of()).forGetter(ServerStatus.Players::sample)
               )
               .apply(var0, ServerStatus.Players::new)
      );

      public Players(int max, int online, List<GameProfile> sample) {
         super();
         this.max = max;
         this.online = online;
         this.sample = sample;
      }
   }

   public static record Version(String name, int protocol) {
      public static final Codec<ServerStatus.Version> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.STRING.fieldOf("name").forGetter(ServerStatus.Version::name), Codec.INT.fieldOf("protocol").forGetter(ServerStatus.Version::protocol)
               )
               .apply(var0, ServerStatus.Version::new)
      );

      public Version(String name, int protocol) {
         super();
         this.name = name;
         this.protocol = protocol;
      }

      public static ServerStatus.Version current() {
         WorldVersion var0 = SharedConstants.getCurrentVersion();
         return new ServerStatus.Version(var0.getName(), var0.getProtocolVersion());
      }
   }
}
