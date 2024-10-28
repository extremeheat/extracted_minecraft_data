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

public record Services(MinecraftSessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository, GameProfileCache profileCache) {
   private static final String USERID_CACHE_FILE = "usercache.json";

   public Services(MinecraftSessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository, GameProfileCache profileCache) {
      super();
      this.sessionService = sessionService;
      this.servicesKeySet = servicesKeySet;
      this.profileRepository = profileRepository;
      this.profileCache = profileCache;
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

   public boolean canValidateProfileKeys() {
      return !this.servicesKeySet.keys(ServicesKeyType.PROFILE_KEY).isEmpty();
   }

   public MinecraftSessionService sessionService() {
      return this.sessionService;
   }

   public ServicesKeySet servicesKeySet() {
      return this.servicesKeySet;
   }

   public GameProfileRepository profileRepository() {
      return this.profileRepository;
   }

   public GameProfileCache profileCache() {
      return this.profileCache;
   }
}
