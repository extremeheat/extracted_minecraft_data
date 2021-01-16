package io.netty.resolver;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.net.SocketAddress;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AddressResolverGroup<T extends SocketAddress> implements Closeable {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
   private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap();

   protected AddressResolverGroup() {
      super();
   }

   public AddressResolver<T> getResolver(final EventExecutor var1) {
      if (var1 == null) {
         throw new NullPointerException("executor");
      } else if (var1.isShuttingDown()) {
         throw new IllegalStateException("executor not accepting a task");
      } else {
         synchronized(this.resolvers) {
            AddressResolver var2 = (AddressResolver)this.resolvers.get(var1);
            if (var2 == null) {
               final AddressResolver var4;
               try {
                  var4 = this.newResolver(var1);
               } catch (Exception var7) {
                  throw new IllegalStateException("failed to create a new resolver", var7);
               }

               this.resolvers.put(var1, var4);
               var1.terminationFuture().addListener(new FutureListener<Object>() {
                  public void operationComplete(Future<Object> var1x) throws Exception {
                     synchronized(AddressResolverGroup.this.resolvers) {
                        AddressResolverGroup.this.resolvers.remove(var1);
                     }

                     var4.close();
                  }
               });
               var2 = var4;
            }

            return var2;
         }
      }
   }

   protected abstract AddressResolver<T> newResolver(EventExecutor var1) throws Exception;

   public void close() {
      AddressResolver[] var1;
      synchronized(this.resolvers) {
         var1 = (AddressResolver[])((AddressResolver[])this.resolvers.values().toArray(new AddressResolver[this.resolvers.size()]));
         this.resolvers.clear();
      }

      AddressResolver[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AddressResolver var5 = var2[var4];

         try {
            var5.close();
         } catch (Throwable var7) {
            logger.warn("Failed to close a resolver:", var7);
         }
      }

   }
}
