package net.minecraft.network.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;

public interface BundlerInfo {
   int BUNDLE_SIZE_LIMIT = 4096;

   static <T extends PacketListener, P extends BundlePacket<? super T>> BundlerInfo createForPacket(final PacketType<P> var0, final Function<Iterable<Packet<? super T>>, P> var1, final BundleDelimiterPacket<? super T> var2) {
      return new BundlerInfo() {
         public void unbundlePacket(Packet<?> var1x, Consumer<Packet<?>> var2x) {
            if (var1x.type() == var0) {
               BundlePacket var3 = (BundlePacket)var1x;
               var2x.accept(var2);
               var3.subPackets().forEach(var2x);
               var2x.accept(var2);
            } else {
               var2x.accept(var1x);
            }

         }

         @Nullable
         public Bundler startPacketBundling(Packet<?> var1x) {
            return var1x == var2 ? new Bundler() {
               private final List<Packet<? super T>> bundlePackets = new ArrayList();

               @Nullable
               public Packet<?> addPacket(Packet<?> var1x) {
                  if (var1x == var2) {
                     return (Packet)var1.apply(this.bundlePackets);
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
   Bundler startPacketBundling(Packet<?> var1);

   public interface Bundler {
      @Nullable
      Packet<?> addPacket(Packet<?> var1);
   }
}
