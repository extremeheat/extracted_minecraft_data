package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.audio.OggAudioStream;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class SoundBufferLibrary {
   private final ResourceManager resourceManager;
   private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Maps.newHashMap();

   public SoundBufferLibrary(ResourceManager var1) {
      super();
      this.resourceManager = var1;
   }

   public CompletableFuture<SoundBuffer> getCompleteBuffer(ResourceLocation var1) {
      return (CompletableFuture)this.cache.computeIfAbsent(var1, (var1x) -> {
         return CompletableFuture.supplyAsync(() -> {
            try {
               Resource var2 = this.resourceManager.getResource(var1x);

               SoundBuffer var6;
               try {
                  InputStream var3 = var2.getInputStream();

                  try {
                     OggAudioStream var4 = new OggAudioStream(var3);

                     try {
                        ByteBuffer var5 = var4.readAll();
                        var6 = new SoundBuffer(var5, var4.getFormat());
                     } catch (Throwable var10) {
                        try {
                           var4.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }

                        throw var10;
                     }

                     var4.close();
                  } catch (Throwable var11) {
                     if (var3 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var8) {
                           var11.addSuppressed(var8);
                        }
                     }

                     throw var11;
                  }

                  if (var3 != null) {
                     var3.close();
                  }
               } catch (Throwable var12) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var7) {
                        var12.addSuppressed(var7);
                     }
                  }

                  throw var12;
               }

               if (var2 != null) {
                  var2.close();
               }

               return var6;
            } catch (IOException var13) {
               throw new CompletionException(var13);
            }
         }, Util.backgroundExecutor());
      });
   }

   public CompletableFuture<AudioStream> getStream(ResourceLocation var1, boolean var2) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            Resource var3 = this.resourceManager.getResource(var1);
            InputStream var4 = var3.getInputStream();
            return (AudioStream)(var2 ? new LoopingAudioStream(OggAudioStream::new, var4) : new OggAudioStream(var4));
         } catch (IOException var5) {
            throw new CompletionException(var5);
         }
      }, Util.backgroundExecutor());
   }

   public void clear() {
      this.cache.values().forEach((var0) -> {
         var0.thenAccept(SoundBuffer::discardAlBuffer);
      });
      this.cache.clear();
   }

   public CompletableFuture<?> preload(Collection<Sound> var1) {
      return CompletableFuture.allOf((CompletableFuture[])var1.stream().map((var1x) -> {
         return this.getCompleteBuffer(var1x.getPath());
      }).toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
   }
}
