package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.SignatureValidator;

public record Services(MinecraftSessionService a, ServicesKeySet b, GameProfileRepository c, GameProfileCache d) {
   private final MinecraftSessionService sessionService;
   private final ServicesKeySet servicesKeySet;
   private final GameProfileRepository profileRepository;
   private final GameProfileCache profileCache;
   private static final String USERID_CACHE_FILE = "usercache.json";

   public Services(MinecraftSessionService var1, ServicesKeySet var2, GameProfileRepository var3, GameProfileCache var4) {
      super();
      this.sessionService = var1;
      this.servicesKeySet = var2;
      this.profileRepository = var3;
      this.profileCache = var4;
   }

   public static Services create(YggdrasilAuthenticationService var0, File var1) {
      MinecraftSessionService var2 = var0.createMinecraftSessionService();
      GameProfileRepository var3 = var0.createProfileRepository();
      GameProfileCache var4 = new GameProfileCache(var3, new File(var1, "usercache.json"));
      return new Services(var2, var0.getServicesKeySet(), var3, var4);
   }

   @Nullable
   public SignatureValidator profileKeySignatureValidator() {
      return SignatureValidator.from(this.servicesKeySet, ServicesKeyType.PROFILE_KEY);
   }
}
