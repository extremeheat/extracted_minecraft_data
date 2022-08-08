package net.minecraft.client.multiplayer.resolver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;

public interface AddressCheck {
   boolean isAllowed(ResolvedServerAddress var1);

   boolean isAllowed(ServerAddress var1);

   static AddressCheck createFromService() {
      final ImmutableList var0 = (ImmutableList)Streams.stream(ServiceLoader.load(BlockListSupplier.class)).map(BlockListSupplier::createBlockList).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
      return new AddressCheck() {
         public boolean isAllowed(ResolvedServerAddress var1) {
            String var2 = var1.getHostName();
            String var3 = var1.getHostIp();
            return var0.stream().noneMatch((var2x) -> {
               return var2x.test(var2) || var2x.test(var3);
            });
         }

         public boolean isAllowed(ServerAddress var1) {
            String var2 = var1.getHost();
            return var0.stream().noneMatch((var1x) -> {
               return var1x.test(var2);
            });
         }
      };
   }
}
