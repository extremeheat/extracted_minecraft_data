package net.minecraft.client.multiplayer.resolver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public interface AddressCheck {
   boolean isAllowed(ResolvedServerAddress var1);

   boolean isAllowed(ServerAddress var1);

   static AddressCheck createFromService() {
      final ImmutableList var0 = Streams.stream(ServiceLoader.load(BlockListSupplier.class))
         .<Predicate>map(BlockListSupplier::createBlockList)
         .filter(Objects::nonNull)
         .collect(ImmutableList.toImmutableList());
      return new AddressCheck() {
         @Override
         public boolean isAllowed(ResolvedServerAddress var1) {
            String var2 = var1.getHostName();
            String var3 = var1.getHostIp();
            return var0.stream().noneMatch(var2x -> var2x.test(var2) || var2x.test(var3));
         }

         @Override
         public boolean isAllowed(ServerAddress var1) {
            String var2 = var1.getHost();
            return var0.stream().noneMatch(var1x -> var1x.test(var2));
         }
      };
   }
}
