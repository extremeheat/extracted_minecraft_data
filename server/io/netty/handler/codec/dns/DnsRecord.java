package io.netty.handler.codec.dns;

public interface DnsRecord {
   int CLASS_IN = 1;
   int CLASS_CSNET = 2;
   int CLASS_CHAOS = 3;
   int CLASS_HESIOD = 4;
   int CLASS_NONE = 254;
   int CLASS_ANY = 255;

   String name();

   DnsRecordType type();

   int dnsClass();

   long timeToLive();
}
