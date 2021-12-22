package net.minecraft.client.multiplayer.resolver;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;

public class ServerNameResolver {
   public static final ServerNameResolver DEFAULT;
   private final ServerAddressResolver resolver;
   private final ServerRedirectHandler redirectHandler;
   private final AddressCheck addressCheck;

   @VisibleForTesting
   ServerNameResolver(ServerAddressResolver var1, ServerRedirectHandler var2, AddressCheck var3) {
      super();
      this.resolver = var1;
      this.redirectHandler = var2;
      this.addressCheck = var3;
   }

   public Optional<ResolvedServerAddress> resolveAddress(ServerAddress var1) {
      Optional var2 = this.resolver.resolve(var1);
      if ((!var2.isPresent() || this.addressCheck.isAllowed((ResolvedServerAddress)var2.get())) && this.addressCheck.isAllowed(var1)) {
         Optional var3 = this.redirectHandler.lookupRedirect(var1);
         if (var3.isPresent()) {
            Optional var10000 = this.resolver.resolve((ServerAddress)var3.get());
            AddressCheck var10001 = this.addressCheck;
            Objects.requireNonNull(var10001);
            var2 = var10000.filter(var10001::isAllowed);
         }

         return var2;
      } else {
         return Optional.empty();
      }
   }

   static {
      DEFAULT = new ServerNameResolver(ServerAddressResolver.SYSTEM, ServerRedirectHandler.createDnsSrvRedirectHandler(), AddressCheck.createFromService());
   }
}
