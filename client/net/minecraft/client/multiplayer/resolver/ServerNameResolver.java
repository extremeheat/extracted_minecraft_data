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
   ServerNameResolver(final ServerAddressResolver resolver, final ServerRedirectHandler redirectHandler, final AddressCheck addressCheck) {
      super();
      this.resolver = resolver;
      this.redirectHandler = redirectHandler;
      this.addressCheck = addressCheck;
   }

   public Optional<ResolvedServerAddress> resolveAddress(final ServerAddress address) {
      Optional<ResolvedServerAddress> resolvedAddress = this.resolver.resolve(address);
      if ((!resolvedAddress.isPresent() || this.addressCheck.isAllowed((ResolvedServerAddress)resolvedAddress.get())) && this.addressCheck.isAllowed(address)) {
         Optional<ServerAddress> redirectedAddress = this.redirectHandler.lookupRedirect(address);
         if (redirectedAddress.isPresent()) {
            Optional var10000 = this.resolver.resolve((ServerAddress)redirectedAddress.get());
            AddressCheck var10001 = this.addressCheck;
            Objects.requireNonNull(var10001);
            resolvedAddress = var10000.filter(var10001::isAllowed);
         }

         return resolvedAddress;
      } else {
         return Optional.empty();
      }
   }

   static {
      DEFAULT = new ServerNameResolver(ServerAddressResolver.SYSTEM, ServerRedirectHandler.createDnsSrvRedirectHandler(), AddressCheck.createFromService());
   }
}
