package io.netty.resolver.dns;

import java.net.InetAddress;

public interface DnsCacheEntry {
   InetAddress address();

   Throwable cause();
}
