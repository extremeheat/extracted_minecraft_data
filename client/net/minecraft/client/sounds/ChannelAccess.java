package net.minecraft.client.sounds;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ChannelAccess {
   private final Set<ChannelAccess.ChannelHandle> channels = Sets.newIdentityHashSet();
   final Library library;
   final Executor executor;

   public ChannelAccess(Library var1, Executor var2) {
      super();
      this.library = var1;
      this.executor = var2;
   }

   public CompletableFuture<ChannelAccess.ChannelHandle> createHandle(Library.Pool var1) {
      CompletableFuture var2 = new CompletableFuture();
      this.executor.execute(() -> {
         Channel var3 = this.library.acquireChannel(var1);
         if (var3 != null) {
            ChannelAccess.ChannelHandle var4 = new ChannelAccess.ChannelHandle(var3);
            this.channels.add(var4);
            var2.complete(var4);
         } else {
            var2.complete((Object)null);
         }

      });
      return var2;
   }

   public void executeOnChannels(Consumer<Stream<Channel>> var1) {
      this.executor.execute(() -> {
         var1.accept(this.channels.stream().map((var0) -> {
            return var0.channel;
         }).filter(Objects::nonNull));
      });
   }

   public void scheduleTick() {
      this.executor.execute(() -> {
         Iterator var1 = this.channels.iterator();

         while(var1.hasNext()) {
            ChannelAccess.ChannelHandle var2 = (ChannelAccess.ChannelHandle)var1.next();
            var2.channel.updateStream();
            if (var2.channel.stopped()) {
               var2.release();
               var1.remove();
            }
         }

      });
   }

   public void clear() {
      this.channels.forEach(ChannelAccess.ChannelHandle::release);
      this.channels.clear();
   }

   public class ChannelHandle {
      @Nullable
      Channel channel;
      private boolean stopped;

      public boolean isStopped() {
         return this.stopped;
      }

      public ChannelHandle(Channel var2) {
         super();
         this.channel = var2;
      }

      public void execute(Consumer<Channel> var1) {
         ChannelAccess.this.executor.execute(() -> {
            if (this.channel != null) {
               var1.accept(this.channel);
            }

         });
      }

      public void release() {
         this.stopped = true;
         ChannelAccess.this.library.releaseChannel(this.channel);
         this.channel = null;
      }
   }
}
