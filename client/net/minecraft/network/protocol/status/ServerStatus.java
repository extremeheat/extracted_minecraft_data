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

public record ServerStatus(Component description, Optional<Players> players, Optional<Version> version, Optional<Favicon> favicon, boolean enforcesSecureChat) {
   public static final Codec<ServerStatus> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ComponentSerialization.CODEC.lenientOptionalFieldOf("description", CommonComponents.EMPTY).forGetter(ServerStatus::description), ServerStatus.Players.CODEC.lenientOptionalFieldOf("players").forGetter(ServerStatus::players), ServerStatus.Version.CODEC.lenientOptionalFieldOf("version").forGetter(ServerStatus::version), ServerStatus.Favicon.CODEC.lenientOptionalFieldOf("favicon").forGetter(ServerStatus::favicon), Codec.BOOL.lenientOptionalFieldOf("enforcesSecureChat", false).forGetter(ServerStatus::enforcesSecureChat)).apply(var0, ServerStatus::new);
   });

   public ServerStatus(Component description, Optional<Players> players, Optional<Version> version, Optional<Favicon> favicon, boolean enforcesSecureChat) {
      super();
      this.description = description;
      this.players = players;
      this.version = version;
      this.favicon = favicon;
      this.enforcesSecureChat = enforcesSecureChat;
   }

   public Component description() {
      return this.description;
   }

   public Optional<Players> players() {
      return this.players;
   }

   public Optional<Version> version() {
      return this.version;
   }

   public Optional<Favicon> favicon() {
      return this.favicon;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }

   public static record Players(int max, int online, List<GameProfile> sample) {
      private static final Codec<GameProfile> PROFILE_CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)).apply(var0, GameProfile::new);
      });
      public static final Codec<Players> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("max").forGetter(Players::max), Codec.INT.fieldOf("online").forGetter(Players::online), PROFILE_CODEC.listOf().lenientOptionalFieldOf("sample", List.of()).forGetter(Players::sample)).apply(var0, Players::new);
      });

      public Players(int max, int online, List<GameProfile> sample) {
         super();
         this.max = max;
         this.online = online;
         this.sample = sample;
      }

      public int max() {
         return this.max;
      }

      public int online() {
         return this.online;
      }

      public List<GameProfile> sample() {
         return this.sample;
      }
   }

   public static record Version(String name, int protocol) {
      public static final Codec<Version> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("name").forGetter(Version::name), Codec.INT.fieldOf("protocol").forGetter(Version::protocol)).apply(var0, Version::new);
      });

      public Version(String name, int protocol) {
         super();
         this.name = name;
         this.protocol = protocol;
      }

      public static Version current() {
         WorldVersion var0 = SharedConstants.getCurrentVersion();
         return new Version(var0.getName(), var0.getProtocolVersion());
      }

      public String name() {
         return this.name;
      }

      public int protocol() {
         return this.protocol;
      }
   }

   public static record Favicon(byte[] iconBytes) {
      private static final String PREFIX = "data:image/png;base64,";
      public static final Codec<Favicon> CODEC;

      public Favicon(byte[] iconBytes) {
         super();
         this.iconBytes = iconBytes;
      }

      public byte[] iconBytes() {
         return this.iconBytes;
      }

      static {
         CODEC = Codec.STRING.comapFlatMap((var0) -> {
            if (!var0.startsWith("data:image/png;base64,")) {
               return DataResult.error(() -> {
                  return "Unknown format";
               });
            } else {
               try {
                  String var1 = var0.substring("data:image/png;base64,".length()).replaceAll("\n", "");
                  byte[] var2 = Base64.getDecoder().decode(var1.getBytes(StandardCharsets.UTF_8));
                  return DataResult.success(new Favicon(var2));
               } catch (IllegalArgumentException var3) {
                  return DataResult.error(() -> {
                     return "Malformed base64 server icon";
                  });
               }
            }
         }, (var0) -> {
            String var10000 = new String(Base64.getEncoder().encode(var0.iconBytes), StandardCharsets.UTF_8);
            return "data:image/png;base64," + var10000;
         });
      }
   }
}
