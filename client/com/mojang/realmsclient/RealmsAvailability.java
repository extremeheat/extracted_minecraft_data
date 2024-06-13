package com.mojang.realmsclient;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.slf4j.Logger;

public class RealmsAvailability {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private static CompletableFuture<RealmsAvailability.Result> future;

   public RealmsAvailability() {
      super();
   }

   public static CompletableFuture<RealmsAvailability.Result> get() {
      if (future == null || shouldRefresh(future)) {
         future = check();
      }

      return future;
   }

   private static boolean shouldRefresh(CompletableFuture<RealmsAvailability.Result> var0) {
      RealmsAvailability.Result var1 = (RealmsAvailability.Result)var0.getNow(null);
      return var1 != null && var1.exception() != null;
   }

   private static CompletableFuture<RealmsAvailability.Result> check() {
      User var0 = Minecraft.getInstance().getUser();
      return var0.getType() != User.Type.MSA
         ? CompletableFuture.completedFuture(new RealmsAvailability.Result(RealmsAvailability.Type.AUTHENTICATION_ERROR))
         : CompletableFuture.supplyAsync(
            () -> {
               RealmsClient var0x = RealmsClient.create();

               try {
                  if (var0x.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                     return new RealmsAvailability.Result(RealmsAvailability.Type.INCOMPATIBLE_CLIENT);
                  } else {
                     return !var0x.hasParentalConsent()
                        ? new RealmsAvailability.Result(RealmsAvailability.Type.NEEDS_PARENTAL_CONSENT)
                        : new RealmsAvailability.Result(RealmsAvailability.Type.SUCCESS);
                  }
               } catch (RealmsServiceException var2) {
                  LOGGER.error("Couldn't connect to realms", var2);
                  return var2.realmsError.errorCode() == 401
                     ? new RealmsAvailability.Result(RealmsAvailability.Type.AUTHENTICATION_ERROR)
                     : new RealmsAvailability.Result(var2);
               }
            },
            Util.ioPool()
         );
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static enum Type {
      SUCCESS,
      INCOMPATIBLE_CLIENT,
      NEEDS_PARENTAL_CONSENT,
      AUTHENTICATION_ERROR,
      UNEXPECTED_ERROR;

      private Type() {
      }
   }
}
