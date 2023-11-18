package net.minecraft.network.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;

public interface BundlerInfo {
   int BUNDLE_SIZE_LIMIT = 4096;
   BundlerInfo EMPTY = new BundlerInfo() {
      @Override
      public void unbundlePacket(Packet<?> var1, Consumer<Packet<?>> var2) {
         var2.accept(var1);
      }

      @Nullable
      @Override
      public BundlerInfo.Bundler startPacketBundling(Packet<?> var1) {
         return null;
      }
   };

   static <T extends PacketListener, P extends BundlePacket<T>> BundlerInfo createForPacket(
      final Class<P> var0, final Function<Iterable<Packet<T>>, P> var1, final BundleDelimiterPacket<T> var2
   ) {
      return new BundlerInfo() {
         @Override
         public void unbundlePacket(Packet<?> var1x, Consumer<Packet<?>> var2x) {
            if (var1x.getClass() == var0) {
               BundlePacket var3 = (BundlePacket)var1x;
               var2x.accept(var2);
               var3.subPackets().forEach(var2x);
               var2x.accept(var2);
            } else {
               var2x.accept(var1x);
            }
         }

         @Nullable
         @Override
         public BundlerInfo.Bundler startPacketBundling(Packet<?> var1x) {
            return var1x == var2 ? new BundlerInfo.Bundler() {
               private final List<Packet<T>> bundlePackets = new ArrayList<>();

               @Nullable
               @Override
               public Packet<?> addPacket(Packet<?> var1x) {
                  if (var1x == var2) {
                     return (Packet<?>)var1.apply(this.bundlePackets);
                  } else if (this.bundlePackets.size() >= 4096) {
                     throw new IllegalStateException("Too many packets in a bundle");
                  } else {
                     this.bundlePackets.add(var1x);
                     return null;
                  }
               }
            } : null;
         }
      };
   }

   void unbundlePacket(Packet<?> var1, Consumer<Packet<?>> var2);

   @Nullable
   BundlerInfo.Bundler startPacketBundling(Packet<?> var1);

   public interface Bundler {
      @Nullable
      Packet<?> addPacket(Packet<?> var1);
   }

   public interface Provider {
      BundlerInfo bundlerInfo();
   }
}
