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
import net.minecraft.server.packs.resources.ResourceProvider;

public class SoundBufferLibrary {
   private final ResourceProvider resourceManager;
   private final Map<ResourceLocation, CompletableFuture<SoundBuffer>> cache = Maps.newHashMap();

   public SoundBufferLibrary(ResourceProvider var1) {
      super();
      this.resourceManager = var1;
   }

   public CompletableFuture<SoundBuffer> getCompleteBuffer(ResourceLocation var1) {
      return this.cache.computeIfAbsent(var1, var1x -> CompletableFuture.supplyAsync(() -> {
            try {
               SoundBuffer var5;
               try (
                  InputStream var2 = this.resourceManager.open(var1x);
                  OggAudioStream var3 = new OggAudioStream(var2);
               ) {
                  ByteBuffer var4 = var3.readAll();
                  var5 = new SoundBuffer(var4, var3.getFormat());
               }

               return var5;
            } catch (IOException var10) {
               throw new CompletionException(var10);
            }
         }, Util.backgroundExecutor()));
   }

   public CompletableFuture<AudioStream> getStream(ResourceLocation var1, boolean var2) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            InputStream var3 = this.resourceManager.open(var1);
            return (AudioStream)(var2 ? new LoopingAudioStream(OggAudioStream::new, var3) : new OggAudioStream(var3));
         } catch (IOException var4) {
            throw new CompletionException(var4);
         }
      }, Util.backgroundExecutor());
   }

   public void clear() {
      this.cache.values().forEach(var0 -> var0.thenAccept(SoundBuffer::discardAlBuffer));
      this.cache.clear();
   }

   public CompletableFuture<?> preload(Collection<Sound> var1) {
      return CompletableFuture.allOf(var1.stream().map(var1x -> this.getCompleteBuffer(var1x.getPath())).toArray(var0 -> new CompletableFuture[var0]));
   }
}
