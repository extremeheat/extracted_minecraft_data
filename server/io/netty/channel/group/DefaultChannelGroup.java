package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.ServerChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup extends AbstractSet<Channel> implements ChannelGroup {
   private static final AtomicInteger nextId = new AtomicInteger();
   private final String name;
   private final EventExecutor executor;
   private final ConcurrentMap<ChannelId, Channel> serverChannels;
   private final ConcurrentMap<ChannelId, Channel> nonServerChannels;
   private final ChannelFutureListener remover;
   private final VoidChannelGroupFuture voidFuture;
   private final boolean stayClosed;
   private volatile boolean closed;

   public DefaultChannelGroup(EventExecutor var1) {
      this(var1, false);
   }

   public DefaultChannelGroup(String var1, EventExecutor var2) {
      this(var1, var2, false);
   }

   public DefaultChannelGroup(EventExecutor var1, boolean var2) {
      this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), var1, var2);
   }

   public DefaultChannelGroup(String var1, EventExecutor var2, boolean var3) {
      super();
      this.serverChannels = PlatformDependent.newConcurrentHashMap();
      this.nonServerChannels = PlatformDependent.newConcurrentHashMap();
      this.remover = new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            DefaultChannelGroup.this.remove(var1.channel());
         }
      };
      this.voidFuture = new VoidChannelGroupFuture(this);
      if (var1 == null) {
         throw new NullPointerException("name");
      } else {
         this.name = var1;
         this.executor = var2;
         this.stayClosed = var3;
      }
   }

   public String name() {
      return this.name;
   }

   public Channel find(ChannelId var1) {
      Channel var2 = (Channel)this.nonServerChannels.get(var1);
      return var2 != null ? var2 : (Channel)this.serverChannels.get(var1);
   }

   public boolean isEmpty() {
      return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
   }

   public int size() {
      return this.nonServerChannels.size() + this.serverChannels.size();
   }

   public boolean contains(Object var1) {
      if (var1 instanceof ServerChannel) {
         return this.serverChannels.containsValue(var1);
      } else {
         return var1 instanceof Channel ? this.nonServerChannels.containsValue(var1) : false;
      }
   }

   public boolean add(Channel var1) {
      ConcurrentMap var2 = var1 instanceof ServerChannel ? this.serverChannels : this.nonServerChannels;
      boolean var3 = var2.putIfAbsent(var1.id(), var1) == null;
      if (var3) {
         var1.closeFuture().addListener(this.remover);
      }

      if (this.stayClosed && this.closed) {
         var1.close();
      }

      return var3;
   }

   public boolean remove(Object var1) {
      Channel var2 = null;
      if (var1 instanceof ChannelId) {
         var2 = (Channel)this.nonServerChannels.remove(var1);
         if (var2 == null) {
            var2 = (Channel)this.serverChannels.remove(var1);
         }
      } else if (var1 instanceof Channel) {
         var2 = (Channel)var1;
         if (var2 instanceof ServerChannel) {
            var2 = (Channel)this.serverChannels.remove(var2.id());
         } else {
            var2 = (Channel)this.nonServerChannels.remove(var2.id());
         }
      }

      if (var2 == null) {
         return false;
      } else {
         var2.closeFuture().removeListener(this.remover);
         return true;
      }
   }

   public void clear() {
      this.nonServerChannels.clear();
      this.serverChannels.clear();
   }

   public Iterator<Channel> iterator() {
      return new CombinedIterator(this.serverChannels.values().iterator(), this.nonServerChannels.values().iterator());
   }

   public Object[] toArray() {
      ArrayList var1 = new ArrayList(this.size());
      var1.addAll(this.serverChannels.values());
      var1.addAll(this.nonServerChannels.values());
      return var1.toArray();
   }

   public <T> T[] toArray(T[] var1) {
      ArrayList var2 = new ArrayList(this.size());
      var2.addAll(this.serverChannels.values());
      var2.addAll(this.nonServerChannels.values());
      return var2.toArray(var1);
   }

   public ChannelGroupFuture close() {
      return this.close(ChannelMatchers.all());
   }

   public ChannelGroupFuture disconnect() {
      return this.disconnect(ChannelMatchers.all());
   }

   public ChannelGroupFuture deregister() {
      return this.deregister(ChannelMatchers.all());
   }

   public ChannelGroupFuture write(Object var1) {
      return this.write(var1, ChannelMatchers.all());
   }

   private static Object safeDuplicate(Object var0) {
      if (var0 instanceof ByteBuf) {
         return ((ByteBuf)var0).retainedDuplicate();
      } else {
         return var0 instanceof ByteBufHolder ? ((ByteBufHolder)var0).retainedDuplicate() : ReferenceCountUtil.retain(var0);
      }
   }

   public ChannelGroupFuture write(Object var1, ChannelMatcher var2) {
      return this.write(var1, var2, false);
   }

   public ChannelGroupFuture write(Object var1, ChannelMatcher var2, boolean var3) {
      if (var1 == null) {
         throw new NullPointerException("message");
      } else if (var2 == null) {
         throw new NullPointerException("matcher");
      } else {
         Object var4;
         if (var3) {
            Iterator var5 = this.nonServerChannels.values().iterator();

            while(var5.hasNext()) {
               Channel var6 = (Channel)var5.next();
               if (var2.matches(var6)) {
                  var6.write(safeDuplicate(var1), var6.voidPromise());
               }
            }

            var4 = this.voidFuture;
         } else {
            LinkedHashMap var8 = new LinkedHashMap(this.size());
            Iterator var9 = this.nonServerChannels.values().iterator();

            while(var9.hasNext()) {
               Channel var7 = (Channel)var9.next();
               if (var2.matches(var7)) {
                  var8.put(var7, var7.write(safeDuplicate(var1)));
               }
            }

            var4 = new DefaultChannelGroupFuture(this, var8, this.executor);
         }

         ReferenceCountUtil.release(var1);
         return (ChannelGroupFuture)var4;
      }
   }

   public ChannelGroup flush() {
      return this.flush(ChannelMatchers.all());
   }

   public ChannelGroupFuture flushAndWrite(Object var1) {
      return this.writeAndFlush(var1);
   }

   public ChannelGroupFuture writeAndFlush(Object var1) {
      return this.writeAndFlush(var1, ChannelMatchers.all());
   }

   public ChannelGroupFuture disconnect(ChannelMatcher var1) {
      if (var1 == null) {
         throw new NullPointerException("matcher");
      } else {
         LinkedHashMap var2 = new LinkedHashMap(this.size());
         Iterator var3 = this.serverChannels.values().iterator();

         Channel var4;
         while(var3.hasNext()) {
            var4 = (Channel)var3.next();
            if (var1.matches(var4)) {
               var2.put(var4, var4.disconnect());
            }
         }

         var3 = this.nonServerChannels.values().iterator();

         while(var3.hasNext()) {
            var4 = (Channel)var3.next();
            if (var1.matches(var4)) {
               var2.put(var4, var4.disconnect());
            }
         }

         return new DefaultChannelGroupFuture(this, var2, this.executor);
      }
   }

   public ChannelGroupFuture close(ChannelMatcher var1) {
      if (var1 == null) {
         throw new NullPointerException("matcher");
      } else {
         LinkedHashMap var2 = new LinkedHashMap(this.size());
         if (this.stayClosed) {
            this.closed = true;
         }

         Iterator var3 = this.serverChannels.values().iterator();

         Channel var4;
         while(var3.hasNext()) {
            var4 = (Channel)var3.next();
            if (var1.matches(var4)) {
               var2.put(var4, var4.close());
            }
         }

         var3 = this.nonServerChannels.values().iterator();

         while(var3.hasNext()) {
            var4 = (Channel)var3.next();
            if (var1.matches(var4)) {
               var2.put(var4, var4.close());
            }
         }

         return new DefaultChannelGroupFuture(this, var2, this.executor);
      }
   }

   public ChannelGroupFuture deregister(ChannelMatcher var1) {
      if (var1 == null) {
         throw new NullPointerException("matcher");
      } else {
         LinkedHashMap var2 = new LinkedHashMap(this.size());
         Iterator var3 = this.serverChannels.values().iterator();

         Channel var4;
         while(var3.hasNext()) {
            var4 = (Channel)var3.next();
            if (var1.matches(var4)) {
               var2.put(var4, var4.deregister());
            }
         }

         var3 = this.nonServerChannels.values().iterator();

         while(var3.hasNext()) {
            var4 = (Channel)var3.next();
            if (var1.matches(var4)) {
               var2.put(var4, var4.deregister());
            }
         }

         return new DefaultChannelGroupFuture(this, var2, this.executor);
      }
   }

   public ChannelGroup flush(ChannelMatcher var1) {
      Iterator var2 = this.nonServerChannels.values().iterator();

      while(var2.hasNext()) {
         Channel var3 = (Channel)var2.next();
         if (var1.matches(var3)) {
            var3.flush();
         }
      }

      return this;
   }

   public ChannelGroupFuture flushAndWrite(Object var1, ChannelMatcher var2) {
      return this.writeAndFlush(var1, var2);
   }

   public ChannelGroupFuture writeAndFlush(Object var1, ChannelMatcher var2) {
      return this.writeAndFlush(var1, var2, false);
   }

   public ChannelGroupFuture writeAndFlush(Object var1, ChannelMatcher var2, boolean var3) {
      if (var1 == null) {
         throw new NullPointerException("message");
      } else {
         Object var4;
         if (var3) {
            Iterator var5 = this.nonServerChannels.values().iterator();

            while(var5.hasNext()) {
               Channel var6 = (Channel)var5.next();
               if (var2.matches(var6)) {
                  var6.writeAndFlush(safeDuplicate(var1), var6.voidPromise());
               }
            }

            var4 = this.voidFuture;
         } else {
            LinkedHashMap var8 = new LinkedHashMap(this.size());
            Iterator var9 = this.nonServerChannels.values().iterator();

            while(var9.hasNext()) {
               Channel var7 = (Channel)var9.next();
               if (var2.matches(var7)) {
                  var8.put(var7, var7.writeAndFlush(safeDuplicate(var1)));
               }
            }

            var4 = new DefaultChannelGroupFuture(this, var8, this.executor);
         }

         ReferenceCountUtil.release(var1);
         return (ChannelGroupFuture)var4;
      }
   }

   public ChannelGroupFuture newCloseFuture() {
      return this.newCloseFuture(ChannelMatchers.all());
   }

   public ChannelGroupFuture newCloseFuture(ChannelMatcher var1) {
      LinkedHashMap var2 = new LinkedHashMap(this.size());
      Iterator var3 = this.serverChannels.values().iterator();

      Channel var4;
      while(var3.hasNext()) {
         var4 = (Channel)var3.next();
         if (var1.matches(var4)) {
            var2.put(var4, var4.closeFuture());
         }
      }

      var3 = this.nonServerChannels.values().iterator();

      while(var3.hasNext()) {
         var4 = (Channel)var3.next();
         if (var1.matches(var4)) {
            var2.put(var4, var4.closeFuture());
         }
      }

      return new DefaultChannelGroupFuture(this, var2, this.executor);
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int compareTo(ChannelGroup var1) {
      int var2 = this.name().compareTo(var1.name());
      return var2 != 0 ? var2 : System.identityHashCode(this) - System.identityHashCode(var1);
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(name: " + this.name() + ", size: " + this.size() + ')';
   }
}
