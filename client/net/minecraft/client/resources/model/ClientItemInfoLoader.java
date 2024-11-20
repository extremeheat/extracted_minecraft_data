package net.minecraft.client.resources.model;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class ClientItemInfoLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter LISTER = FileToIdConverter.json("items");

   public ClientItemInfoLoader() {
      super();
   }

   public static CompletableFuture<LoadedClientInfos> scheduleLoad(ResourceManager var0, Executor var1) {
      return CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources(var0), var1).thenCompose((var1x) -> {
         ArrayList var2 = new ArrayList(var1x.size());
         var1x.forEach((var2x, var3) -> var2.add(CompletableFuture.supplyAsync(() -> {
               ResourceLocation var2 = LISTER.fileToId(var2x);

               try {
                  BufferedReader var3x = var3.openAsReader();

                  PendingLoad var5;
                  try {
                     ClientItem var4 = (ClientItem)ClientItem.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(var3x)).ifError((var2xx) -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", new Object[]{var2, var3.sourcePackId(), var2xx.message()})).result().orElse((Object)null);
                     var5 = new PendingLoad(var2, var4);
                  } catch (Throwable var7) {
                     if (var3x != null) {
                        try {
                           ((Reader)var3x).close();
                        } catch (Throwable var6) {
                           var7.addSuppressed(var6);
                        }
                     }

                     throw var7;
                  }

                  if (var3x != null) {
                     ((Reader)var3x).close();
                  }

                  return var5;
               } catch (Exception var8) {
                  LOGGER.error("Failed to open item model {} from pack '{}'", new Object[]{var2x, var3.sourcePackId(), var8});
                  return new PendingLoad(var2, (ClientItem)null);
               }
            }, var1)));
         return Util.sequence(var2).thenApply((var0) -> {
            HashMap var1 = new HashMap();

            for(PendingLoad var3 : var0) {
               if (var3.clientItemInfo != null) {
                  var1.put(var3.id, var3.clientItemInfo);
               }
            }

            return new LoadedClientInfos(var1);
         });
      });
   }

   static record PendingLoad(ResourceLocation id, @Nullable ClientItem clientItemInfo) {
      final ResourceLocation id;
      @Nullable
      final ClientItem clientItemInfo;

      PendingLoad(ResourceLocation var1, @Nullable ClientItem var2) {
         super();
         this.id = var1;
         this.clientItemInfo = var2;
      }
   }

   public static record LoadedClientInfos(Map<ResourceLocation, ClientItem> contents) {
      public LoadedClientInfos(Map<ResourceLocation, ClientItem> var1) {
         super();
         this.contents = var1;
      }
   }
}
