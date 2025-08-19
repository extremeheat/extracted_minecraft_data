package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.server.players.CachedUserNameToIdResolver;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.server.players.UserNameToIdResolver;
import net.minecraft.util.SignatureValidator;

public record Services(MinecraftSessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository, UserNameToIdResolver nameToIdCache, ProfileResolver profileResolver) {
   private static final String USERID_CACHE_FILE = "usercache.json";

   public Services(MinecraftSessionService var1, ServicesKeySet var2, GameProfileRepository var3, UserNameToIdResolver var4, ProfileResolver var5) {
      super();
      this.sessionService = var1;
      this.servicesKeySet = var2;
      this.profileRepository = var3;
      this.nameToIdCache = var4;
      this.profileResolver = var5;
   }

   public static Services create(YggdrasilAuthenticationService var0, File var1) {
      MinecraftSessionService var2 = var0.createMinecraftSessionService();
      GameProfileRepository var3 = var0.createProfileRepository();
      CachedUserNameToIdResolver var4 = new CachedUserNameToIdResolver(var3, new File(var1, "usercache.json"));
      ProfileResolver.Cached var5 = new ProfileResolver.Cached(var2, var4);
      return new Services(var2, var0.getServicesKeySet(), var3, var4, var5);
   }

   @Nullable
   public SignatureValidator profileKeySignatureValidator() {
      return SignatureValidator.from(this.servicesKeySet, ServicesKeyType.PROFILE_KEY);
   }

   public boolean canValidateProfileKeys() {
      return !this.servicesKeySet.keys(ServicesKeyType.PROFILE_KEY).isEmpty();
   }
}
