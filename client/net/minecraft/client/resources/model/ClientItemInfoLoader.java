package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.PlaceholderLookupProvider;
import net.minecraft.util.StrictJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ClientItemInfoLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter LISTER = FileToIdConverter.json("items");

   public ClientItemInfoLoader() {
      super();
   }

   public static CompletableFuture<LoadedClientInfos> scheduleLoad(ResourceManager var0, Executor var1) {
      RegistryAccess.Frozen var2 = ClientRegistryLayer.createRegistryAccess().compositeAccess();
      return CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources(var0), var1).thenCompose((var2x) -> {
         ArrayList var3 = new ArrayList(var2x.size());
         var2x.forEach((var3x, var4) -> var3.add(CompletableFuture.supplyAsync(() -> {
               ResourceLocation var3 = LISTER.fileToId(var3x);

               try {
                  BufferedReader var4x = var4.openAsReader();

                  PendingLoad var8;
                  try {
                     PlaceholderLookupProvider var5 = new PlaceholderLookupProvider(var2);
                     RegistryOps var6 = var5.createSerializationContext(JsonOps.INSTANCE);
                     ClientItem var7 = (ClientItem)ClientItem.CODEC.parse(var6, StrictJsonParser.parse((Reader)var4x)).ifError((var2x) -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", new Object[]{var3, var4.sourcePackId(), var2x.message()})).result().map((var1) -> var5.hasRegisteredPlaceholders() ? var1.withRegistrySwapper(var5.createSwapper()) : var1).orElse((Object)null);
                     var8 = new PendingLoad(var3, var7);
                  } catch (Throwable var10) {
                     if (var4x != null) {
                        try {
                           ((Reader)var4x).close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (var4x != null) {
                     ((Reader)var4x).close();
                  }

                  return var8;
               } catch (Exception var11) {
                  LOGGER.error("Failed to open item model {} from pack '{}'", new Object[]{var3x, var4.sourcePackId(), var11});
                  return new PendingLoad(var3, (ClientItem)null);
               }
            }, var1)));
         return Util.sequence(var3).thenApply((var0) -> {
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
      final @Nullable ClientItem clientItemInfo;

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
