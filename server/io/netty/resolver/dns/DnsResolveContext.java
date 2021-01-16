package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DefaultDnsRecordDecoder;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

abstract class DnsResolveContext<T> {
   private static final FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>> RELEASE_RESPONSE = new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
      public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> var1) {
         if (var1.isSuccess()) {
            ((AddressedEnvelope)var1.getNow()).release();
         }

      }
   };
   private static final RuntimeException NXDOMAIN_QUERY_FAILED_EXCEPTION = (RuntimeException)ThrowableUtil.unknownStackTrace(new RuntimeException("No answer found and NXDOMAIN response code returned"), DnsResolveContext.class, "onResponse(..)");
   private static final RuntimeException CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION = (RuntimeException)ThrowableUtil.unknownStackTrace(new RuntimeException("No matching CNAME record found"), DnsResolveContext.class, "onResponseCNAME(..)");
   private static final RuntimeException NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION = (RuntimeException)ThrowableUtil.unknownStackTrace(new RuntimeException("No matching record type found"), DnsResolveContext.class, "onResponseAorAAAA(..)");
   private static final RuntimeException UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION = (RuntimeException)ThrowableUtil.unknownStackTrace(new RuntimeException("Response type was unrecognized"), DnsResolveContext.class, "onResponse(..)");
   private static final RuntimeException NAME_SERVERS_EXHAUSTED_EXCEPTION = (RuntimeException)ThrowableUtil.unknownStackTrace(new RuntimeException("No name servers returned an answer"), DnsResolveContext.class, "tryToFinishResolve(..)");
   final DnsNameResolver parent;
   private final DnsServerAddressStream nameServerAddrs;
   private final String hostname;
   private final int dnsClass;
   private final DnsRecordType[] expectedTypes;
   private final int maxAllowedQueries;
   private final DnsRecord[] additionals;
   private final Set<Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> queriesInProgress = Collections.newSetFromMap(new IdentityHashMap());
   private List<T> finalResult;
   private int allowedQueries;
   private boolean triedCNAME;

   DnsResolveContext(DnsNameResolver var1, String var2, int var3, DnsRecordType[] var4, DnsRecord[] var5, DnsServerAddressStream var6) {
      super();

      assert var4.length > 0;

      this.parent = var1;
      this.hostname = var2;
      this.dnsClass = var3;
      this.expectedTypes = var4;
      this.additionals = var5;
      this.nameServerAddrs = (DnsServerAddressStream)ObjectUtil.checkNotNull(var6, "nameServerAddrs");
      this.maxAllowedQueries = var1.maxQueriesPerResolve();
      this.allowedQueries = this.maxAllowedQueries;
   }

   abstract DnsResolveContext<T> newResolverContext(DnsNameResolver var1, String var2, int var3, DnsRecordType[] var4, DnsRecord[] var5, DnsServerAddressStream var6);

   abstract T convertRecord(DnsRecord var1, String var2, DnsRecord[] var3, EventLoop var4);

   abstract List<T> filterResults(List<T> var1);

   abstract void cache(String var1, DnsRecord[] var2, DnsRecord var3, T var4);

   abstract void cache(String var1, DnsRecord[] var2, UnknownHostException var3);

   void resolve(final Promise<List<T>> var1) {
      final String[] var2 = this.parent.searchDomains();
      if (var2.length != 0 && this.parent.ndots() != 0 && !StringUtil.endsWith(this.hostname, '.')) {
         final boolean var3 = this.hasNDots();
         String var4 = var3 ? this.hostname : this.hostname + '.' + var2[0];
         final int var5 = var3 ? 0 : 1;
         this.doSearchDomainQuery(var4, new FutureListener<List<T>>() {
            private int searchDomainIdx = var5;

            public void operationComplete(Future<List<T>> var1x) {
               Throwable var2x = var1x.cause();
               if (var2x == null) {
                  var1.trySuccess(var1x.getNow());
               } else if (DnsNameResolver.isTransportOrTimeoutError(var2x)) {
                  var1.tryFailure(new DnsResolveContext.SearchDomainUnknownHostException(var2x, DnsResolveContext.this.hostname));
               } else if (this.searchDomainIdx < var2.length) {
                  DnsResolveContext.this.doSearchDomainQuery(DnsResolveContext.this.hostname + '.' + var2[this.searchDomainIdx++], this);
               } else if (!var3) {
                  DnsResolveContext.this.internalResolve(var1);
               } else {
                  var1.tryFailure(new DnsResolveContext.SearchDomainUnknownHostException(var2x, DnsResolveContext.this.hostname));
               }

            }
         });
      } else {
         this.internalResolve(var1);
      }

   }

   private boolean hasNDots() {
      int var1 = this.hostname.length() - 1;

      for(int var2 = 0; var1 >= 0; --var1) {
         if (this.hostname.charAt(var1) == '.') {
            ++var2;
            if (var2 >= this.parent.ndots()) {
               return true;
            }
         }
      }

      return false;
   }

   private void doSearchDomainQuery(String var1, FutureListener<List<T>> var2) {
      DnsResolveContext var3 = this.newResolverContext(this.parent, var1, this.dnsClass, this.expectedTypes, this.additionals, this.nameServerAddrs);
      Promise var4 = this.parent.executor().newPromise();
      var3.internalResolve(var4);
      var4.addListener(var2);
   }

   private void internalResolve(Promise<List<T>> var1) {
      DnsServerAddressStream var2 = this.getNameServers(this.hostname);
      int var3 = this.expectedTypes.length - 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         if (!this.query(this.hostname, this.expectedTypes[var4], var2.duplicate(), var1)) {
            return;
         }
      }

      this.query(this.hostname, this.expectedTypes[var3], var2, var1);
   }

   private void addNameServerToCache(DnsResolveContext.AuthoritativeNameServer var1, InetAddress var2, long var3) {
      if (!var1.isRootServer()) {
         this.parent.authoritativeDnsServerCache().cache(var1.domainName(), this.additionals, var2, var3, this.parent.ch.eventLoop());
      }

   }

   private DnsServerAddressStream getNameServersFromCache(String var1) {
      int var2 = var1.length();
      if (var2 == 0) {
         return null;
      } else {
         if (var1.charAt(var2 - 1) != '.') {
            var1 = var1 + ".";
         }

         int var3 = var1.indexOf(46);
         if (var3 == var1.length() - 1) {
            return null;
         } else {
            List var5;
            do {
               var1 = var1.substring(var3 + 1);
               int var4 = var1.indexOf(46);
               if (var4 <= 0 || var4 == var1.length() - 1) {
                  return null;
               }

               var3 = var4;
               var5 = this.parent.authoritativeDnsServerCache().get(var1, this.additionals);
            } while(var5 == null || var5.isEmpty());

            return DnsServerAddresses.sequential((Iterable)(new DnsResolveContext.DnsCacheIterable(var5))).stream();
         }
      }
   }

   private void query(DnsServerAddressStream var1, int var2, DnsQuestion var3, Promise<List<T>> var4, Throwable var5) {
      this.query(var1, var2, var3, this.parent.dnsQueryLifecycleObserverFactory().newDnsQueryLifecycleObserver(var3), var4, var5);
   }

   private void query(final DnsServerAddressStream var1, final int var2, final DnsQuestion var3, final DnsQueryLifecycleObserver var4, final Promise<List<T>> var5, Throwable var6) {
      if (var2 < var1.size() && this.allowedQueries != 0 && !var5.isCancelled()) {
         --this.allowedQueries;
         InetSocketAddress var7 = var1.next();
         ChannelPromise var8 = this.parent.ch.newPromise();
         Future var9 = this.parent.query0(var7, var3, this.additionals, var8, this.parent.ch.eventLoop().newPromise());
         this.queriesInProgress.add(var9);
         var4.queryWritten(var7, var8);
         var9.addListener(new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
            public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> var1x) {
               DnsResolveContext.this.queriesInProgress.remove(var1x);
               if (!var5.isDone() && !var1x.isCancelled()) {
                  Throwable var6 = var1x.cause();

                  try {
                     if (var6 == null) {
                        DnsResolveContext.this.onResponse(var1, var2, var3, (AddressedEnvelope)var1x.getNow(), var4, var5);
                     } else {
                        var4.queryFailed(var6);
                        DnsResolveContext.this.query(var1, var2 + 1, var3, var5, var6);
                     }
                  } finally {
                     DnsResolveContext.this.tryToFinishResolve(var1, var2, var3, NoopDnsQueryLifecycleObserver.INSTANCE, var5, var6);
                  }

               } else {
                  var4.queryCancelled(DnsResolveContext.this.allowedQueries);
                  AddressedEnvelope var2x = (AddressedEnvelope)var1x.getNow();
                  if (var2x != null) {
                     var2x.release();
                  }

               }
            }
         });
      } else {
         this.tryToFinishResolve(var1, var2, var3, var4, var5, var6);
      }
   }

   private void onResponse(DnsServerAddressStream var1, int var2, DnsQuestion var3, AddressedEnvelope<DnsResponse, InetSocketAddress> var4, DnsQueryLifecycleObserver var5, Promise<List<T>> var6) {
      try {
         DnsResponse var7 = (DnsResponse)var4.content();
         DnsResponseCode var8 = var7.code();
         if (var8 == DnsResponseCode.NOERROR) {
            if (this.handleRedirect(var3, var4, var5, var6)) {
               return;
            }

            DnsRecordType var9 = var3.type();
            if (var9 == DnsRecordType.CNAME) {
               this.onResponseCNAME(var3, buildAliasMap((DnsResponse)var4.content()), var5, var6);
               return;
            }

            DnsRecordType[] var10 = this.expectedTypes;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               DnsRecordType var13 = var10[var12];
               if (var9 == var13) {
                  this.onExpectedResponse(var3, var4, var5, var6);
                  return;
               }
            }

            var5.queryFailed(UNRECOGNIZED_TYPE_QUERY_FAILED_EXCEPTION);
            return;
         }

         if (var8 != DnsResponseCode.NXDOMAIN) {
            this.query(var1, var2 + 1, var3, var5.queryNoAnswer(var8), var6, (Throwable)null);
         } else {
            var5.queryFailed(NXDOMAIN_QUERY_FAILED_EXCEPTION);
         }
      } finally {
         ReferenceCountUtil.safeRelease(var4);
      }

   }

   private boolean handleRedirect(DnsQuestion var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4) {
      DnsResponse var5 = (DnsResponse)var2.content();
      if (var5.count(DnsSection.ANSWER) == 0) {
         DnsResolveContext.AuthoritativeNameServerList var6 = extractAuthoritativeNameServers(var1.name(), var5);
         if (var6 != null) {
            ArrayList var7 = new ArrayList(var6.size());
            int var8 = var5.count(DnsSection.ADDITIONAL);

            for(int var9 = 0; var9 < var8; ++var9) {
               DnsRecord var10 = var5.recordAt(DnsSection.ADDITIONAL, var9);
               if ((var10.type() != DnsRecordType.A || this.parent.supportsARecords()) && (var10.type() != DnsRecordType.AAAA || this.parent.supportsAAAARecords())) {
                  String var11 = var10.name();
                  DnsResolveContext.AuthoritativeNameServer var12 = var6.remove(var11);
                  if (var12 != null) {
                     InetAddress var13 = DnsAddressDecoder.decodeAddress(var10, var11, this.parent.isDecodeIdn());
                     if (var13 != null) {
                        var7.add(new InetSocketAddress(var13, this.parent.dnsRedirectPort(var13)));
                        this.addNameServerToCache(var12, var13, var10.timeToLive());
                     }
                  }
               }
            }

            if (!var7.isEmpty()) {
               this.query(this.parent.uncachedRedirectDnsServerStream(var7), 0, var1, var3.queryRedirected(Collections.unmodifiableList(var7)), var4, (Throwable)null);
               return true;
            }
         }
      }

      return false;
   }

   private static DnsResolveContext.AuthoritativeNameServerList extractAuthoritativeNameServers(String var0, DnsResponse var1) {
      int var2 = var1.count(DnsSection.AUTHORITY);
      if (var2 == 0) {
         return null;
      } else {
         DnsResolveContext.AuthoritativeNameServerList var3 = new DnsResolveContext.AuthoritativeNameServerList(var0);

         for(int var4 = 0; var4 < var2; ++var4) {
            var3.add(var1.recordAt(DnsSection.AUTHORITY, var4));
         }

         return var3;
      }
   }

   private void onExpectedResponse(DnsQuestion var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4) {
      DnsResponse var5 = (DnsResponse)var2.content();
      Map var6 = buildAliasMap(var5);
      int var7 = var5.count(DnsSection.ANSWER);
      boolean var8 = false;

      for(int var9 = 0; var9 < var7; ++var9) {
         DnsRecord var10 = var5.recordAt(DnsSection.ANSWER, var9);
         DnsRecordType var11 = var10.type();
         boolean var12 = false;
         DnsRecordType[] var13 = this.expectedTypes;
         int var14 = var13.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            DnsRecordType var16 = var13[var15];
            if (var11 == var16) {
               var12 = true;
               break;
            }
         }

         if (var12) {
            String var17 = var1.name().toLowerCase(Locale.US);
            String var18 = var10.name().toLowerCase(Locale.US);
            if (!var18.equals(var17)) {
               String var19 = var17;

               do {
                  var19 = (String)var6.get(var19);
               } while(!var18.equals(var19) && var19 != null);

               if (var19 == null) {
                  continue;
               }
            }

            Object var20 = this.convertRecord(var10, this.hostname, this.additionals, this.parent.ch.eventLoop());
            if (var20 != null) {
               if (this.finalResult == null) {
                  this.finalResult = new ArrayList(8);
               }

               this.finalResult.add(var20);
               this.cache(this.hostname, this.additionals, var10, var20);
               var8 = true;
            }
         }
      }

      if (var6.isEmpty()) {
         if (var8) {
            var3.querySucceed();
            return;
         }

         var3.queryFailed(NO_MATCHING_RECORD_QUERY_FAILED_EXCEPTION);
      } else {
         var3.querySucceed();
         this.onResponseCNAME(var1, var6, this.parent.dnsQueryLifecycleObserverFactory().newDnsQueryLifecycleObserver(var1), var4);
      }

   }

   private void onResponseCNAME(DnsQuestion var1, Map<String, String> var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4) {
      String var5 = var1.name().toLowerCase(Locale.US);

      boolean var6;
      String var7;
      for(var6 = false; !var2.isEmpty(); var5 = var7) {
         var7 = (String)var2.remove(var5);
         if (var7 == null) {
            break;
         }

         var6 = true;
      }

      if (var6) {
         this.followCname(var1, var5, var3, var4);
      } else {
         var3.queryFailed(CNAME_NOT_FOUND_QUERY_FAILED_EXCEPTION);
      }

   }

   private static Map<String, String> buildAliasMap(DnsResponse var0) {
      int var1 = var0.count(DnsSection.ANSWER);
      HashMap var2 = null;

      for(int var3 = 0; var3 < var1; ++var3) {
         DnsRecord var4 = var0.recordAt(DnsSection.ANSWER, var3);
         DnsRecordType var5 = var4.type();
         if (var5 == DnsRecordType.CNAME && var4 instanceof DnsRawRecord) {
            ByteBuf var6 = ((ByteBufHolder)var4).content();
            String var7 = decodeDomainName(var6);
            if (var7 != null) {
               if (var2 == null) {
                  var2 = new HashMap(Math.min(8, var1));
               }

               var2.put(var4.name().toLowerCase(Locale.US), var7.toLowerCase(Locale.US));
            }
         }
      }

      return (Map)(var2 != null ? var2 : Collections.emptyMap());
   }

   private void tryToFinishResolve(DnsServerAddressStream var1, int var2, DnsQuestion var3, DnsQueryLifecycleObserver var4, Promise<List<T>> var5, Throwable var6) {
      if (!this.queriesInProgress.isEmpty()) {
         var4.queryCancelled(this.allowedQueries);
      } else {
         if (this.finalResult == null) {
            if (var2 < var1.size()) {
               if (var4 == NoopDnsQueryLifecycleObserver.INSTANCE) {
                  this.query(var1, var2 + 1, var3, var5, var6);
               } else {
                  this.query(var1, var2 + 1, var3, var4, var5, var6);
               }

               return;
            }

            var4.queryFailed(NAME_SERVERS_EXHAUSTED_EXCEPTION);
            if (var6 == null && !this.triedCNAME) {
               this.triedCNAME = true;
               this.query(this.hostname, DnsRecordType.CNAME, this.getNameServers(this.hostname), var5);
               return;
            }
         } else {
            var4.queryCancelled(this.allowedQueries);
         }

         this.finishResolve(var5, var6);
      }
   }

   private void finishResolve(Promise<List<T>> var1, Throwable var2) {
      if (!this.queriesInProgress.isEmpty()) {
         Iterator var3 = this.queriesInProgress.iterator();

         while(var3.hasNext()) {
            Future var4 = (Future)var3.next();
            var3.remove();
            if (!var4.cancel(false)) {
               var4.addListener(RELEASE_RESPONSE);
            }
         }
      }

      if (this.finalResult != null) {
         DnsNameResolver.trySuccess(var1, this.filterResults(this.finalResult));
      } else {
         int var6 = this.maxAllowedQueries - this.allowedQueries;
         StringBuilder var7 = new StringBuilder(64);
         var7.append("failed to resolve '").append(this.hostname).append('\'');
         if (var6 > 1) {
            if (var6 < this.maxAllowedQueries) {
               var7.append(" after ").append(var6).append(" queries ");
            } else {
               var7.append(". Exceeded max queries per resolve ").append(this.maxAllowedQueries).append(' ');
            }
         }

         UnknownHostException var5 = new UnknownHostException(var7.toString());
         if (var2 == null) {
            this.cache(this.hostname, this.additionals, var5);
         } else {
            var5.initCause(var2);
         }

         var1.tryFailure(var5);
      }
   }

   static String decodeDomainName(ByteBuf var0) {
      var0.markReaderIndex();

      Object var2;
      try {
         String var1 = DefaultDnsRecordDecoder.decodeName(var0);
         return var1;
      } catch (CorruptedFrameException var6) {
         var2 = null;
      } finally {
         var0.resetReaderIndex();
      }

      return (String)var2;
   }

   private DnsServerAddressStream getNameServers(String var1) {
      DnsServerAddressStream var2 = this.getNameServersFromCache(var1);
      return var2 == null ? this.nameServerAddrs.duplicate() : var2;
   }

   private void followCname(DnsQuestion var1, String var2, DnsQueryLifecycleObserver var3, Promise<List<T>> var4) {
      DnsServerAddressStream var5 = this.getNameServers(var2);

      DnsQuestion var6;
      try {
         var6 = this.newQuestion(var2, var1.type());
      } catch (Throwable var8) {
         var3.queryFailed(var8);
         PlatformDependent.throwException(var8);
         return;
      }

      this.query(var5, 0, var6, var3.queryCNAMEd(var6), var4, (Throwable)null);
   }

   private boolean query(String var1, DnsRecordType var2, DnsServerAddressStream var3, Promise<List<T>> var4) {
      DnsQuestion var5 = this.newQuestion(var1, var2);
      if (var5 == null) {
         return false;
      } else {
         this.query(var3, 0, var5, var4, (Throwable)null);
         return true;
      }
   }

   private DnsQuestion newQuestion(String var1, DnsRecordType var2) {
      try {
         return new DefaultDnsQuestion(var1, var2, this.dnsClass);
      } catch (IllegalArgumentException var4) {
         return null;
      }
   }

   static final class AuthoritativeNameServer {
      final int dots;
      final String nsName;
      final String domainName;
      DnsResolveContext.AuthoritativeNameServer next;
      boolean removed;

      AuthoritativeNameServer(int var1, String var2, String var3) {
         super();
         this.dots = var1;
         this.nsName = var3;
         this.domainName = var2;
      }

      boolean isRootServer() {
         return this.dots == 1;
      }

      String domainName() {
         return this.domainName;
      }
   }

   private static final class AuthoritativeNameServerList {
      private final String questionName;
      private DnsResolveContext.AuthoritativeNameServer head;
      private int count;

      AuthoritativeNameServerList(String var1) {
         super();
         this.questionName = var1.toLowerCase(Locale.US);
      }

      void add(DnsRecord var1) {
         if (var1.type() == DnsRecordType.NS && var1 instanceof DnsRawRecord) {
            if (this.questionName.length() >= var1.name().length()) {
               String var2 = var1.name().toLowerCase(Locale.US);
               int var3 = 0;
               int var4 = var2.length() - 1;

               for(int var5 = this.questionName.length() - 1; var4 >= 0; --var5) {
                  char var6 = var2.charAt(var4);
                  if (this.questionName.charAt(var5) != var6) {
                     return;
                  }

                  if (var6 == '.') {
                     ++var3;
                  }

                  --var4;
               }

               if (this.head == null || this.head.dots <= var3) {
                  ByteBuf var7 = ((ByteBufHolder)var1).content();
                  String var8 = DnsResolveContext.decodeDomainName(var7);
                  if (var8 != null) {
                     if (this.head != null && this.head.dots >= var3) {
                        if (this.head.dots == var3) {
                           DnsResolveContext.AuthoritativeNameServer var9;
                           for(var9 = this.head; var9.next != null; var9 = var9.next) {
                           }

                           var9.next = new DnsResolveContext.AuthoritativeNameServer(var3, var2, var8);
                           ++this.count;
                        }
                     } else {
                        this.count = 1;
                        this.head = new DnsResolveContext.AuthoritativeNameServer(var3, var2, var8);
                     }

                  }
               }
            }
         }
      }

      DnsResolveContext.AuthoritativeNameServer remove(String var1) {
         for(DnsResolveContext.AuthoritativeNameServer var2 = this.head; var2 != null; var2 = var2.next) {
            if (!var2.removed && var2.nsName.equalsIgnoreCase(var1)) {
               var2.removed = true;
               return var2;
            }
         }

         return null;
      }

      int size() {
         return this.count;
      }
   }

   private final class DnsCacheIterable implements Iterable<InetSocketAddress> {
      private final List<? extends DnsCacheEntry> entries;

      DnsCacheIterable(List<? extends DnsCacheEntry> var2) {
         super();
         this.entries = var2;
      }

      public Iterator<InetSocketAddress> iterator() {
         return new Iterator<InetSocketAddress>() {
            Iterator<? extends DnsCacheEntry> entryIterator;

            {
               this.entryIterator = DnsCacheIterable.this.entries.iterator();
            }

            public boolean hasNext() {
               return this.entryIterator.hasNext();
            }

            public InetSocketAddress next() {
               InetAddress var1 = ((DnsCacheEntry)this.entryIterator.next()).address();
               return new InetSocketAddress(var1, DnsResolveContext.this.parent.dnsRedirectPort(var1));
            }

            public void remove() {
               this.entryIterator.remove();
            }
         };
      }
   }

   private static final class SearchDomainUnknownHostException extends UnknownHostException {
      private static final long serialVersionUID = -8573510133644997085L;

      SearchDomainUnknownHostException(Throwable var1, String var2) {
         super("Search domain query failed. Original hostname: '" + var2 + "' " + var1.getMessage());
         this.setStackTrace(var1.getStackTrace());
         this.initCause(var1.getCause());
      }

      public Throwable fillInStackTrace() {
         return this;
      }
   }
}
