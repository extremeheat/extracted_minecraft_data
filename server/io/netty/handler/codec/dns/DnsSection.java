package io.netty.handler.codec.dns;

public enum DnsSection {
   QUESTION,
   ANSWER,
   AUTHORITY,
   ADDITIONAL;

   private DnsSection() {
   }
}
