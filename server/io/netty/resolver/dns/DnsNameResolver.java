package io.netty.resolver.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponse;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.codec.dns.DefaultDnsRawRecord;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.InetNameResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DnsNameResolver extends InetNameResolver {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);
   private static final String LOCALHOST = "localhost";
   private static final InetAddress LOCALHOST_ADDRESS;
   private static final DnsRecord[] EMPTY_ADDITIONALS = new DnsRecord[0];
   private static final DnsRecordType[] IPV4_ONLY_RESOLVED_RECORD_TYPES;
   private static final InternetProtocolFamily[] IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
   private static final DnsRecordType[] IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
   private static final InternetProtocolFamily[] IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
   private static final DnsRecordType[] IPV6_ONLY_RESOLVED_RECORD_TYPES;
   private static final InternetProtocolFamily[] IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
   private static final DnsRecordType[] IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
   private static final InternetProtocolFamily[] IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
   static final ResolvedAddressTypes DEFAULT_RESOLVE_ADDRESS_TYPES;
   static final String[] DEFAULT_SEARCH_DOMAINS;
   private static final int DEFAULT_NDOTS;
   private static final DatagramDnsResponseDecoder DECODER;
   private static final DatagramDnsQueryEncoder ENCODER;
   final Future<Channel> channelFuture;
   final DatagramChannel ch;
   final DnsQueryContextManager queryContextManager = new DnsQueryContextManager();
   private final DnsCache resolveCache;
   private final DnsCache authoritativeDnsServerCache;
   private final FastThreadLocal<DnsServerAddressStream> nameServerAddrStream = new FastThreadLocal<DnsServerAddressStream>() {
      protected DnsServerAddressStream initialValue() throws Exception {
         return DnsNameResolver.this.dnsServerAddressStreamProvider.nameServerAddressStream("");
      }
   };
   private final long queryTimeoutMillis;
   private final int maxQueriesPerResolve;
   private final ResolvedAddressTypes resolvedAddressTypes;
   private final InternetProtocolFamily[] resolvedInternetProtocolFamilies;
   private final boolean recursionDesired;
   private final int maxPayloadSize;
   private final boolean optResourceEnabled;
   private final HostsFileEntriesResolver hostsFileEntriesResolver;
   private final DnsServerAddressStreamProvider dnsServerAddressStreamProvider;
   private final String[] searchDomains;
   private final int ndots;
   private final boolean supportsAAAARecords;
   private final boolean supportsARecords;
   private final InternetProtocolFamily preferredAddressType;
   private final DnsRecordType[] resolveRecordTypes;
   private final boolean decodeIdn;
   private final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory;

   public DnsNameResolver(EventLoop var1, ChannelFactory<? extends DatagramChannel> var2, final DnsCache var3, DnsCache var4, DnsQueryLifecycleObserverFactory var5, long var6, ResolvedAddressTypes var8, boolean var9, int var10, boolean var11, int var12, boolean var13, HostsFileEntriesResolver var14, DnsServerAddressStreamProvider var15, String[] var16, int var17, boolean var18) {
      super(var1);
      this.queryTimeoutMillis = ObjectUtil.checkPositive(var6, "queryTimeoutMillis");
      this.resolvedAddressTypes = var8 != null ? var8 : DEFAULT_RESOLVE_ADDRESS_TYPES;
      this.recursionDesired = var9;
      this.maxQueriesPerResolve = ObjectUtil.checkPositive(var10, "maxQueriesPerResolve");
      this.maxPayloadSize = ObjectUtil.checkPositive(var12, "maxPayloadSize");
      this.optResourceEnabled = var13;
      this.hostsFileEntriesResolver = (HostsFileEntriesResolver)ObjectUtil.checkNotNull(var14, "hostsFileEntriesResolver");
      this.dnsServerAddressStreamProvider = (DnsServerAddressStreamProvider)ObjectUtil.checkNotNull(var15, "dnsServerAddressStreamProvider");
      this.resolveCache = (DnsCache)ObjectUtil.checkNotNull(var3, "resolveCache");
      this.authoritativeDnsServerCache = (DnsCache)ObjectUtil.checkNotNull(var4, "authoritativeDnsServerCache");
      this.dnsQueryLifecycleObserverFactory = (DnsQueryLifecycleObserverFactory)(var11 ? (var5 instanceof NoopDnsQueryLifecycleObserverFactory ? new TraceDnsQueryLifeCycleObserverFactory() : new BiDnsQueryLifecycleObserverFactory(new TraceDnsQueryLifeCycleObserverFactory(), var5)) : (DnsQueryLifecycleObserverFactory)ObjectUtil.checkNotNull(var5, "dnsQueryLifecycleObserverFactory"));
      this.searchDomains = var16 != null ? (String[])var16.clone() : DEFAULT_SEARCH_DOMAINS;
      this.ndots = var17 >= 0 ? var17 : DEFAULT_NDOTS;
      this.decodeIdn = var18;
      switch(this.resolvedAddressTypes) {
      case IPV4_ONLY:
         this.supportsAAAARecords = false;
         this.supportsARecords = true;
         this.resolveRecordTypes = IPV4_ONLY_RESOLVED_RECORD_TYPES;
         this.resolvedInternetProtocolFamilies = IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES;
         this.preferredAddressType = InternetProtocolFamily.IPv4;
         break;
      case IPV4_PREFERRED:
         this.supportsAAAARecords = true;
         this.supportsARecords = true;
         this.resolveRecordTypes = IPV4_PREFERRED_RESOLVED_RECORD_TYPES;
         this.resolvedInternetProtocolFamilies = IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
         this.preferredAddressType = InternetProtocolFamily.IPv4;
         break;
      case IPV6_ONLY:
         this.supportsAAAARecords = true;
         this.supportsARecords = false;
         this.resolveRecordTypes = IPV6_ONLY_RESOLVED_RECORD_TYPES;
         this.resolvedInternetProtocolFamilies = IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES;
         this.preferredAddressType = InternetProtocolFamily.IPv6;
         break;
      case IPV6_PREFERRED:
         this.supportsAAAARecords = true;
         this.supportsARecords = true;
         this.resolveRecordTypes = IPV6_PREFERRED_RESOLVED_RECORD_TYPES;
         this.resolvedInternetProtocolFamilies = IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES;
         this.preferredAddressType = InternetProtocolFamily.IPv6;
         break;
      default:
         throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + var8);
      }

      Bootstrap var19 = new Bootstrap();
      var19.group(this.executor());
      var19.channelFactory(var2);
      var19.option(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, true);
      final DnsNameResolver.DnsResponseHandler var20 = new DnsNameResolver.DnsResponseHandler(this.executor().newPromise());
      var19.handler(new ChannelInitializer<DatagramChannel>() {
         protected void initChannel(DatagramChannel var1) throws Exception {
            var1.pipeline().addLast(DnsNameResolver.DECODER, DnsNameResolver.ENCODER, var20);
         }
      });
      this.channelFuture = var20.channelActivePromise;
      this.ch = (DatagramChannel)var19.register().channel();
      this.ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(var12));
      this.ch.closeFuture().addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            var3.clear();
         }
      });
   }

   int dnsRedirectPort(InetAddress var1) {
      return 53;
   }

   final DnsQueryLifecycleObserverFactory dnsQueryLifecycleObserverFactory() {
      return this.dnsQueryLifecycleObserverFactory;
   }

   protected DnsServerAddressStream uncachedRedirectDnsServerStream(List<InetSocketAddress> var1) {
      return DnsServerAddresses.sequential((Iterable)var1).stream();
   }

   public DnsCache resolveCache() {
      return this.resolveCache;
   }

   public DnsCache authoritativeDnsServerCache() {
      return this.authoritativeDnsServerCache;
   }

   public long queryTimeoutMillis() {
      return this.queryTimeoutMillis;
   }

   public ResolvedAddressTypes resolvedAddressTypes() {
      return this.resolvedAddressTypes;
   }

   InternetProtocolFamily[] resolvedInternetProtocolFamiliesUnsafe() {
      return this.resolvedInternetProtocolFamilies;
   }

   final String[] searchDomains() {
      return this.searchDomains;
   }

   final int ndots() {
      return this.ndots;
   }

   final boolean supportsAAAARecords() {
      return this.supportsAAAARecords;
   }

   final boolean supportsARecords() {
      return this.supportsARecords;
   }

   final InternetProtocolFamily preferredAddressType() {
      return this.preferredAddressType;
   }

   final DnsRecordType[] resolveRecordTypes() {
      return this.resolveRecordTypes;
   }

   final boolean isDecodeIdn() {
      return this.decodeIdn;
   }

   public boolean isRecursionDesired() {
      return this.recursionDesired;
   }

   public int maxQueriesPerResolve() {
      return this.maxQueriesPerResolve;
   }

   public int maxPayloadSize() {
      return this.maxPayloadSize;
   }

   public boolean isOptResourceEnabled() {
      return this.optResourceEnabled;
   }

   public HostsFileEntriesResolver hostsFileEntriesResolver() {
      return this.hostsFileEntriesResolver;
   }

   public void close() {
      if (this.ch.isOpen()) {
         this.ch.close();
      }

   }

   protected EventLoop executor() {
      return (EventLoop)super.executor();
   }

   private InetAddress resolveHostsFileEntry(String var1) {
      if (this.hostsFileEntriesResolver == null) {
         return null;
      } else {
         InetAddress var2 = this.hostsFileEntriesResolver.address(var1, this.resolvedAddressTypes);
         return var2 == null && PlatformDependent.isWindows() && "localhost".equalsIgnoreCase(var1) ? LOCALHOST_ADDRESS : var2;
      }
   }

   public final Future<InetAddress> resolve(String var1, Iterable<DnsRecord> var2) {
      return this.resolve(var1, var2, this.executor().newPromise());
   }

   public final Future<InetAddress> resolve(String var1, Iterable<DnsRecord> var2, Promise<InetAddress> var3) {
      ObjectUtil.checkNotNull(var3, "promise");
      DnsRecord[] var4 = toArray(var2, true);

      try {
         this.doResolve(var1, var4, var3, this.resolveCache);
         return var3;
      } catch (Exception var6) {
         return var3.setFailure(var6);
      }
   }

   public final Future<List<InetAddress>> resolveAll(String var1, Iterable<DnsRecord> var2) {
      return this.resolveAll(var1, var2, this.executor().newPromise());
   }

   public final Future<List<InetAddress>> resolveAll(String var1, Iterable<DnsRecord> var2, Promise<List<InetAddress>> var3) {
      ObjectUtil.checkNotNull(var3, "promise");
      DnsRecord[] var4 = toArray(var2, true);

      try {
         this.doResolveAll(var1, var4, var3, this.resolveCache);
         return var3;
      } catch (Exception var6) {
         return var3.setFailure(var6);
      }
   }

   protected void doResolve(String var1, Promise<InetAddress> var2) throws Exception {
      this.doResolve(var1, EMPTY_ADDITIONALS, var2, this.resolveCache);
   }

   public final Future<List<DnsRecord>> resolveAll(DnsQuestion var1) {
      return this.resolveAll(var1, EMPTY_ADDITIONALS, this.executor().newPromise());
   }

   public final Future<List<DnsRecord>> resolveAll(DnsQuestion var1, Iterable<DnsRecord> var2) {
      return this.resolveAll(var1, var2, this.executor().newPromise());
   }

   public final Future<List<DnsRecord>> resolveAll(DnsQuestion var1, Iterable<DnsRecord> var2, Promise<List<DnsRecord>> var3) {
      DnsRecord[] var4 = toArray(var2, true);
      return this.resolveAll(var1, var4, var3);
   }

   private Future<List<DnsRecord>> resolveAll(DnsQuestion var1, DnsRecord[] var2, Promise<List<DnsRecord>> var3) {
      ObjectUtil.checkNotNull(var1, "question");
      ObjectUtil.checkNotNull(var3, "promise");
      DnsRecordType var4 = var1.type();
      String var5 = var1.name();
      if (var4 == DnsRecordType.A || var4 == DnsRecordType.AAAA) {
         InetAddress var6 = this.resolveHostsFileEntry(var5);
         if (var6 != null) {
            ByteBuf var7 = null;
            if (var6 instanceof Inet4Address) {
               if (var4 == DnsRecordType.A) {
                  var7 = Unpooled.wrappedBuffer(var6.getAddress());
               }
            } else if (var6 instanceof Inet6Address && var4 == DnsRecordType.AAAA) {
               var7 = Unpooled.wrappedBuffer(var6.getAddress());
            }

            if (var7 != null) {
               trySuccess(var3, Collections.singletonList(new DefaultDnsRawRecord(var5, var4, 86400L, var7)));
               return var3;
            }
         }
      }

      DnsServerAddressStream var8 = this.dnsServerAddressStreamProvider.nameServerAddressStream(var5);
      (new DnsRecordResolveContext(this, var1, var2, var8)).resolve(var3);
      return var3;
   }

   private static DnsRecord[] toArray(Iterable<DnsRecord> var0, boolean var1) {
      ObjectUtil.checkNotNull(var0, "additionals");
      DnsRecord var4;
      if (var0 instanceof Collection) {
         Collection var5 = (Collection)var0;
         Iterator var6 = var0.iterator();

         while(var6.hasNext()) {
            var4 = (DnsRecord)var6.next();
            validateAdditional(var4, var1);
         }

         return (DnsRecord[])var5.toArray(new DnsRecord[var5.size()]);
      } else {
         Iterator var2 = var0.iterator();
         if (!var2.hasNext()) {
            return EMPTY_ADDITIONALS;
         } else {
            ArrayList var3 = new ArrayList();

            do {
               var4 = (DnsRecord)var2.next();
               validateAdditional(var4, var1);
               var3.add(var4);
            } while(var2.hasNext());

            return (DnsRecord[])var3.toArray(new DnsRecord[var3.size()]);
         }
      }
   }

   private static void validateAdditional(DnsRecord var0, boolean var1) {
      ObjectUtil.checkNotNull(var0, "record");
      if (var1 && var0 instanceof DnsRawRecord) {
         throw new IllegalArgumentException("DnsRawRecord implementations not allowed: " + var0);
      }
   }

   private InetAddress loopbackAddress() {
      return this.preferredAddressType().localhost();
   }

   protected void doResolve(String var1, DnsRecord[] var2, Promise<InetAddress> var3, DnsCache var4) throws Exception {
      if (var1 != null && !var1.isEmpty()) {
         byte[] var5 = NetUtil.createByteArrayFromIpAddressString(var1);
         if (var5 != null) {
            var3.setSuccess(InetAddress.getByAddress(var5));
         } else {
            String var6 = hostname(var1);
            InetAddress var7 = this.resolveHostsFileEntry(var6);
            if (var7 != null) {
               var3.setSuccess(var7);
            } else {
               if (!this.doResolveCached(var6, var2, var3, var4)) {
                  this.doResolveUncached(var6, var2, var3, var4);
               }

            }
         }
      } else {
         var3.setSuccess(this.loopbackAddress());
      }
   }

   private boolean doResolveCached(String var1, DnsRecord[] var2, Promise<InetAddress> var3, DnsCache var4) {
      List var5 = var4.get(var1, var2);
      if (var5 != null && !var5.isEmpty()) {
         Throwable var6 = ((DnsCacheEntry)var5.get(0)).cause();
         if (var6 != null) {
            tryFailure(var3, var6);
            return true;
         } else {
            int var7 = var5.size();
            InternetProtocolFamily[] var8 = this.resolvedInternetProtocolFamilies;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               InternetProtocolFamily var11 = var8[var10];

               for(int var12 = 0; var12 < var7; ++var12) {
                  DnsCacheEntry var13 = (DnsCacheEntry)var5.get(var12);
                  if (var11.addressType().isInstance(var13.address())) {
                     trySuccess(var3, var13.address());
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   static <T> void trySuccess(Promise<T> var0, T var1) {
      if (!var0.trySuccess(var1)) {
         logger.warn("Failed to notify success ({}) to a promise: {}", var1, var0);
      }

   }

   private static void tryFailure(Promise<?> var0, Throwable var1) {
      if (!var0.tryFailure(var1)) {
         logger.warn("Failed to notify failure to a promise: {}", var0, var1);
      }

   }

   private void doResolveUncached(String var1, DnsRecord[] var2, final Promise<InetAddress> var3, DnsCache var4) {
      Promise var5 = this.executor().newPromise();
      this.doResolveAllUncached(var1, var2, var5, var4);
      var5.addListener(new FutureListener<List<InetAddress>>() {
         public void operationComplete(Future<List<InetAddress>> var1) {
            if (var1.isSuccess()) {
               DnsNameResolver.trySuccess(var3, ((List)var1.getNow()).get(0));
            } else {
               DnsNameResolver.tryFailure(var3, var1.cause());
            }

         }
      });
   }

   protected void doResolveAll(String var1, Promise<List<InetAddress>> var2) throws Exception {
      this.doResolveAll(var1, EMPTY_ADDITIONALS, var2, this.resolveCache);
   }

   protected void doResolveAll(String var1, DnsRecord[] var2, Promise<List<InetAddress>> var3, DnsCache var4) throws Exception {
      if (var1 != null && !var1.isEmpty()) {
         byte[] var5 = NetUtil.createByteArrayFromIpAddressString(var1);
         if (var5 != null) {
            var3.setSuccess(Collections.singletonList(InetAddress.getByAddress(var5)));
         } else {
            String var6 = hostname(var1);
            InetAddress var7 = this.resolveHostsFileEntry(var6);
            if (var7 != null) {
               var3.setSuccess(Collections.singletonList(var7));
            } else {
               if (!this.doResolveAllCached(var6, var2, var3, var4)) {
                  this.doResolveAllUncached(var6, var2, var3, var4);
               }

            }
         }
      } else {
         var3.setSuccess(Collections.singletonList(this.loopbackAddress()));
      }
   }

   private boolean doResolveAllCached(String var1, DnsRecord[] var2, Promise<List<InetAddress>> var3, DnsCache var4) {
      List var5 = var4.get(var1, var2);
      if (var5 != null && !var5.isEmpty()) {
         Throwable var6 = ((DnsCacheEntry)var5.get(0)).cause();
         if (var6 != null) {
            tryFailure(var3, var6);
            return true;
         } else {
            ArrayList var7 = null;
            int var8 = var5.size();
            InternetProtocolFamily[] var9 = this.resolvedInternetProtocolFamilies;
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               InternetProtocolFamily var12 = var9[var11];

               for(int var13 = 0; var13 < var8; ++var13) {
                  DnsCacheEntry var14 = (DnsCacheEntry)var5.get(var13);
                  if (var12.addressType().isInstance(var14.address())) {
                     if (var7 == null) {
                        var7 = new ArrayList(var8);
                     }

                     var7.add(var14.address());
                  }
               }
            }

            if (var7 != null) {
               trySuccess(var3, var7);
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void doResolveAllUncached(String var1, DnsRecord[] var2, Promise<List<InetAddress>> var3, DnsCache var4) {
      DnsServerAddressStream var5 = this.dnsServerAddressStreamProvider.nameServerAddressStream(var1);
      (new DnsAddressResolveContext(this, var1, var2, var5, var4)).resolve(var3);
   }

   private static String hostname(String var0) {
      String var1 = IDN.toASCII(var0);
      if (StringUtil.endsWith(var0, '.') && !StringUtil.endsWith(var1, '.')) {
         var1 = var1 + ".";
      }

      return var1;
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion var1) {
      return this.query(this.nextNameServerAddress(), var1);
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion var1, Iterable<DnsRecord> var2) {
      return this.query(this.nextNameServerAddress(), var1, var2);
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(DnsQuestion var1, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var2) {
      return this.query(this.nextNameServerAddress(), var1, Collections.emptyList(), var2);
   }

   private InetSocketAddress nextNameServerAddress() {
      return ((DnsServerAddressStream)this.nameServerAddrStream.get()).next();
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress var1, DnsQuestion var2) {
      return this.query0(var1, var2, EMPTY_ADDITIONALS, this.ch.eventLoop().newPromise());
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress var1, DnsQuestion var2, Iterable<DnsRecord> var3) {
      return this.query0(var1, var2, toArray(var3, false), this.ch.eventLoop().newPromise());
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress var1, DnsQuestion var2, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var3) {
      return this.query0(var1, var2, EMPTY_ADDITIONALS, var3);
   }

   public Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query(InetSocketAddress var1, DnsQuestion var2, Iterable<DnsRecord> var3, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var4) {
      return this.query0(var1, var2, toArray(var3, false), var4);
   }

   public static boolean isTransportOrTimeoutError(Throwable var0) {
      return var0 != null && var0.getCause() instanceof DnsNameResolverException;
   }

   public static boolean isTimeoutError(Throwable var0) {
      return var0 != null && var0.getCause() instanceof DnsNameResolverTimeoutException;
   }

   final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(InetSocketAddress var1, DnsQuestion var2, DnsRecord[] var3, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var4) {
      return this.query0(var1, var2, var3, this.ch.newPromise(), var4);
   }

   final Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> query0(InetSocketAddress var1, DnsQuestion var2, DnsRecord[] var3, ChannelPromise var4, Promise<AddressedEnvelope<? extends DnsResponse, InetSocketAddress>> var5) {
      assert !var4.isVoid();

      Promise var6 = cast((Promise)ObjectUtil.checkNotNull(var5, "promise"));

      try {
         (new DnsQueryContext(this, var1, var2, var3, var6)).query(var4);
         return var6;
      } catch (Exception var8) {
         return var6.setFailure(var8);
      }
   }

   private static Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> cast(Promise<?> var0) {
      return var0;
   }

   static {
      IPV4_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.A};
      IPV4_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv4};
      IPV4_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.A, DnsRecordType.AAAA};
      IPV4_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv4, InternetProtocolFamily.IPv6};
      IPV6_ONLY_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.AAAA};
      IPV6_ONLY_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv6};
      IPV6_PREFERRED_RESOLVED_RECORD_TYPES = new DnsRecordType[]{DnsRecordType.AAAA, DnsRecordType.A};
      IPV6_PREFERRED_RESOLVED_PROTOCOL_FAMILIES = new InternetProtocolFamily[]{InternetProtocolFamily.IPv6, InternetProtocolFamily.IPv4};
      if (NetUtil.isIpV4StackPreferred()) {
         DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_ONLY;
         LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
      } else if (NetUtil.isIpV6AddressesPreferred()) {
         DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV6_PREFERRED;
         LOCALHOST_ADDRESS = NetUtil.LOCALHOST6;
      } else {
         DEFAULT_RESOLVE_ADDRESS_TYPES = ResolvedAddressTypes.IPV4_PREFERRED;
         LOCALHOST_ADDRESS = NetUtil.LOCALHOST4;
      }

      String[] var0;
      try {
         Class var1 = Class.forName("sun.net.dns.ResolverConfiguration");
         Method var2 = var1.getMethod("open");
         Method var3 = var1.getMethod("searchlist");
         Object var4 = var2.invoke((Object)null);
         List var5 = (List)var3.invoke(var4);
         var0 = (String[])var5.toArray(new String[var5.size()]);
      } catch (Exception var7) {
         var0 = EmptyArrays.EMPTY_STRINGS;
      }

      DEFAULT_SEARCH_DOMAINS = var0;

      int var8;
      try {
         var8 = UnixResolverDnsServerAddressStreamProvider.parseEtcResolverFirstNdots();
      } catch (Exception var6) {
         var8 = 1;
      }

      DEFAULT_NDOTS = var8;
      DECODER = new DatagramDnsResponseDecoder();
      ENCODER = new DatagramDnsQueryEncoder();
   }

   private final class DnsResponseHandler extends ChannelInboundHandlerAdapter {
      private final Promise<Channel> channelActivePromise;

      DnsResponseHandler(Promise<Channel> var2) {
         super();
         this.channelActivePromise = var2;
      }

      public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
         try {
            DatagramDnsResponse var3 = (DatagramDnsResponse)var2;
            int var4 = var3.id();
            if (DnsNameResolver.logger.isDebugEnabled()) {
               DnsNameResolver.logger.debug("{} RECEIVED: [{}: {}], {}", DnsNameResolver.this.ch, var4, var3.sender(), var3);
            }

            DnsQueryContext var5 = DnsNameResolver.this.queryContextManager.get(var3.sender(), var4);
            if (var5 == null) {
               DnsNameResolver.logger.warn("{} Received a DNS response with an unknown ID: {}", DnsNameResolver.this.ch, var4);
               return;
            }

            var5.finish(var3);
         } finally {
            ReferenceCountUtil.safeRelease(var2);
         }

      }

      public void channelActive(ChannelHandlerContext var1) throws Exception {
         super.channelActive(var1);
         this.channelActivePromise.setSuccess(var1.channel());
      }

      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
         DnsNameResolver.logger.warn("{} Unexpected exception: ", DnsNameResolver.this.ch, var2);
      }
   }
}
