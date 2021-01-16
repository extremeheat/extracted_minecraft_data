package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.UnaryPromiseNotifier;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class DefaultHttp2Connection implements Http2Connection {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHttp2Connection.class);
   final IntObjectMap<Http2Stream> streamMap;
   final DefaultHttp2Connection.PropertyKeyRegistry propertyKeyRegistry;
   final DefaultHttp2Connection.ConnectionStream connectionStream;
   final DefaultHttp2Connection.DefaultEndpoint<Http2LocalFlowController> localEndpoint;
   final DefaultHttp2Connection.DefaultEndpoint<Http2RemoteFlowController> remoteEndpoint;
   final List<Http2Connection.Listener> listeners;
   final DefaultHttp2Connection.ActiveStreams activeStreams;
   Promise<Void> closePromise;

   public DefaultHttp2Connection(boolean var1) {
      this(var1, 100);
   }

   public DefaultHttp2Connection(boolean var1, int var2) {
      super();
      this.streamMap = new IntObjectHashMap();
      this.propertyKeyRegistry = new DefaultHttp2Connection.PropertyKeyRegistry();
      this.connectionStream = new DefaultHttp2Connection.ConnectionStream();
      this.listeners = new ArrayList(4);
      this.activeStreams = new DefaultHttp2Connection.ActiveStreams(this.listeners);
      this.localEndpoint = new DefaultHttp2Connection.DefaultEndpoint(var1, var1 ? 2147483647 : var2);
      this.remoteEndpoint = new DefaultHttp2Connection.DefaultEndpoint(!var1, var2);
      this.streamMap.put(this.connectionStream.id(), this.connectionStream);
   }

   final boolean isClosed() {
      return this.closePromise != null;
   }

   public Future<Void> close(Promise<Void> var1) {
      ObjectUtil.checkNotNull(var1, "promise");
      if (this.closePromise != null) {
         if (this.closePromise != var1) {
            if (var1 instanceof ChannelPromise && ((ChannelPromise)this.closePromise).isVoid()) {
               this.closePromise = var1;
            } else {
               this.closePromise.addListener(new UnaryPromiseNotifier(var1));
            }
         }
      } else {
         this.closePromise = var1;
      }

      if (this.isStreamMapEmpty()) {
         var1.trySuccess((Object)null);
         return var1;
      } else {
         Iterator var2 = this.streamMap.entries().iterator();
         if (this.activeStreams.allowModifications()) {
            this.activeStreams.incrementPendingIterations();

            try {
               while(var2.hasNext()) {
                  DefaultHttp2Connection.DefaultStream var7 = (DefaultHttp2Connection.DefaultStream)((IntObjectMap.PrimitiveEntry)var2.next()).value();
                  if (var7.id() != 0) {
                     var7.close(var2);
                  }
               }
            } finally {
               this.activeStreams.decrementPendingIterations();
            }
         } else {
            while(var2.hasNext()) {
               Http2Stream var3 = (Http2Stream)((IntObjectMap.PrimitiveEntry)var2.next()).value();
               if (var3.id() != 0) {
                  var3.close();
               }
            }
         }

         return this.closePromise;
      }
   }

   public void addListener(Http2Connection.Listener var1) {
      this.listeners.add(var1);
   }

   public void removeListener(Http2Connection.Listener var1) {
      this.listeners.remove(var1);
   }

   public boolean isServer() {
      return this.localEndpoint.isServer();
   }

   public Http2Stream connectionStream() {
      return this.connectionStream;
   }

   public Http2Stream stream(int var1) {
      return (Http2Stream)this.streamMap.get(var1);
   }

   public boolean streamMayHaveExisted(int var1) {
      return this.remoteEndpoint.mayHaveCreatedStream(var1) || this.localEndpoint.mayHaveCreatedStream(var1);
   }

   public int numActiveStreams() {
      return this.activeStreams.size();
   }

   public Http2Stream forEachActiveStream(Http2StreamVisitor var1) throws Http2Exception {
      return this.activeStreams.forEachActiveStream(var1);
   }

   public Http2Connection.Endpoint<Http2LocalFlowController> local() {
      return this.localEndpoint;
   }

   public Http2Connection.Endpoint<Http2RemoteFlowController> remote() {
      return this.remoteEndpoint;
   }

   public boolean goAwayReceived() {
      return this.localEndpoint.lastStreamKnownByPeer >= 0;
   }

   public void goAwayReceived(final int var1, long var2, ByteBuf var4) {
      this.localEndpoint.lastStreamKnownByPeer(var1);

      for(int var5 = 0; var5 < this.listeners.size(); ++var5) {
         try {
            ((Http2Connection.Listener)this.listeners.get(var5)).onGoAwayReceived(var1, var2, var4);
         } catch (Throwable var8) {
            logger.error("Caught Throwable from listener onGoAwayReceived.", var8);
         }
      }

      try {
         this.forEachActiveStream(new Http2StreamVisitor() {
            public boolean visit(Http2Stream var1x) {
               if (var1x.id() > var1 && DefaultHttp2Connection.this.localEndpoint.isValidStreamId(var1x.id())) {
                  var1x.close();
               }

               return true;
            }
         });
      } catch (Http2Exception var7) {
         PlatformDependent.throwException(var7);
      }

   }

   public boolean goAwaySent() {
      return this.remoteEndpoint.lastStreamKnownByPeer >= 0;
   }

   public void goAwaySent(final int var1, long var2, ByteBuf var4) {
      this.remoteEndpoint.lastStreamKnownByPeer(var1);

      for(int var5 = 0; var5 < this.listeners.size(); ++var5) {
         try {
            ((Http2Connection.Listener)this.listeners.get(var5)).onGoAwaySent(var1, var2, var4);
         } catch (Throwable var8) {
            logger.error("Caught Throwable from listener onGoAwaySent.", var8);
         }
      }

      try {
         this.forEachActiveStream(new Http2StreamVisitor() {
            public boolean visit(Http2Stream var1x) {
               if (var1x.id() > var1 && DefaultHttp2Connection.this.remoteEndpoint.isValidStreamId(var1x.id())) {
                  var1x.close();
               }

               return true;
            }
         });
      } catch (Http2Exception var7) {
         PlatformDependent.throwException(var7);
      }

   }

   private boolean isStreamMapEmpty() {
      return this.streamMap.size() == 1;
   }

   void removeStream(DefaultHttp2Connection.DefaultStream var1, Iterator<?> var2) {
      boolean var3;
      if (var2 == null) {
         var3 = this.streamMap.remove(var1.id()) != null;
      } else {
         var2.remove();
         var3 = true;
      }

      if (var3) {
         for(int var4 = 0; var4 < this.listeners.size(); ++var4) {
            try {
               ((Http2Connection.Listener)this.listeners.get(var4)).onStreamRemoved(var1);
            } catch (Throwable var6) {
               logger.error("Caught Throwable from listener onStreamRemoved.", var6);
            }
         }

         if (this.closePromise != null && this.isStreamMapEmpty()) {
            this.closePromise.trySuccess((Object)null);
         }
      }

   }

   static Http2Stream.State activeState(int var0, Http2Stream.State var1, boolean var2, boolean var3) throws Http2Exception {
      switch(var1) {
      case IDLE:
         return var3 ? (var2 ? Http2Stream.State.HALF_CLOSED_LOCAL : Http2Stream.State.HALF_CLOSED_REMOTE) : Http2Stream.State.OPEN;
      case RESERVED_LOCAL:
         return Http2Stream.State.HALF_CLOSED_REMOTE;
      case RESERVED_REMOTE:
         return Http2Stream.State.HALF_CLOSED_LOCAL;
      default:
         throw Http2Exception.streamError(var0, Http2Error.PROTOCOL_ERROR, "Attempting to open a stream in an invalid state: " + var1);
      }
   }

   void notifyHalfClosed(Http2Stream var1) {
      for(int var2 = 0; var2 < this.listeners.size(); ++var2) {
         try {
            ((Http2Connection.Listener)this.listeners.get(var2)).onStreamHalfClosed(var1);
         } catch (Throwable var4) {
            logger.error("Caught Throwable from listener onStreamHalfClosed.", var4);
         }
      }

   }

   void notifyClosed(Http2Stream var1) {
      for(int var2 = 0; var2 < this.listeners.size(); ++var2) {
         try {
            ((Http2Connection.Listener)this.listeners.get(var2)).onStreamClosed(var1);
         } catch (Throwable var4) {
            logger.error("Caught Throwable from listener onStreamClosed.", var4);
         }
      }

   }

   public Http2Connection.PropertyKey newKey() {
      return this.propertyKeyRegistry.newKey();
   }

   final DefaultHttp2Connection.DefaultPropertyKey verifyKey(Http2Connection.PropertyKey var1) {
      return ((DefaultHttp2Connection.DefaultPropertyKey)ObjectUtil.checkNotNull((DefaultHttp2Connection.DefaultPropertyKey)var1, "key")).verifyConnection(this);
   }

   private final class PropertyKeyRegistry {
      final List<DefaultHttp2Connection.DefaultPropertyKey> keys;

      private PropertyKeyRegistry() {
         super();
         this.keys = new ArrayList(4);
      }

      DefaultHttp2Connection.DefaultPropertyKey newKey() {
         DefaultHttp2Connection.DefaultPropertyKey var1 = DefaultHttp2Connection.this.new DefaultPropertyKey(this.keys.size());
         this.keys.add(var1);
         return var1;
      }

      int size() {
         return this.keys.size();
      }

      // $FF: synthetic method
      PropertyKeyRegistry(Object var2) {
         this();
      }
   }

   final class DefaultPropertyKey implements Http2Connection.PropertyKey {
      final int index;

      DefaultPropertyKey(int var2) {
         super();
         this.index = var2;
      }

      DefaultHttp2Connection.DefaultPropertyKey verifyConnection(Http2Connection var1) {
         if (var1 != DefaultHttp2Connection.this) {
            throw new IllegalArgumentException("Using a key that was not created by this connection");
         } else {
            return this;
         }
      }
   }

   private final class ActiveStreams {
      private final List<Http2Connection.Listener> listeners;
      private final Queue<DefaultHttp2Connection.Event> pendingEvents = new ArrayDeque(4);
      private final Set<Http2Stream> streams = new LinkedHashSet();
      private int pendingIterations;

      public ActiveStreams(List<Http2Connection.Listener> var2) {
         super();
         this.listeners = var2;
      }

      public int size() {
         return this.streams.size();
      }

      public void activate(final DefaultHttp2Connection.DefaultStream var1) {
         if (this.allowModifications()) {
            this.addToActiveStreams(var1);
         } else {
            this.pendingEvents.add(new DefaultHttp2Connection.Event() {
               public void process() {
                  ActiveStreams.this.addToActiveStreams(var1);
               }
            });
         }

      }

      public void deactivate(final DefaultHttp2Connection.DefaultStream var1, final Iterator<?> var2) {
         if (!this.allowModifications() && var2 == null) {
            this.pendingEvents.add(new DefaultHttp2Connection.Event() {
               public void process() {
                  ActiveStreams.this.removeFromActiveStreams(var1, var2);
               }
            });
         } else {
            this.removeFromActiveStreams(var1, var2);
         }

      }

      public Http2Stream forEachActiveStream(Http2StreamVisitor var1) throws Http2Exception {
         this.incrementPendingIterations();

         Iterator var2;
         try {
            var2 = this.streams.iterator();

            while(var2.hasNext()) {
               Http2Stream var3 = (Http2Stream)var2.next();
               if (!var1.visit(var3)) {
                  Http2Stream var4 = var3;
                  return var4;
               }
            }

            var2 = null;
         } finally {
            this.decrementPendingIterations();
         }

         return var2;
      }

      void addToActiveStreams(DefaultHttp2Connection.DefaultStream var1) {
         if (this.streams.add(var1)) {
            ++var1.createdBy().numActiveStreams;

            for(int var2 = 0; var2 < this.listeners.size(); ++var2) {
               try {
                  ((Http2Connection.Listener)this.listeners.get(var2)).onStreamActive(var1);
               } catch (Throwable var4) {
                  DefaultHttp2Connection.logger.error("Caught Throwable from listener onStreamActive.", var4);
               }
            }
         }

      }

      void removeFromActiveStreams(DefaultHttp2Connection.DefaultStream var1, Iterator<?> var2) {
         if (this.streams.remove(var1)) {
            --var1.createdBy().numActiveStreams;
            DefaultHttp2Connection.this.notifyClosed(var1);
         }

         DefaultHttp2Connection.this.removeStream(var1, var2);
      }

      boolean allowModifications() {
         return this.pendingIterations == 0;
      }

      void incrementPendingIterations() {
         ++this.pendingIterations;
      }

      void decrementPendingIterations() {
         --this.pendingIterations;
         if (this.allowModifications()) {
            while(true) {
               DefaultHttp2Connection.Event var1 = (DefaultHttp2Connection.Event)this.pendingEvents.poll();
               if (var1 == null) {
                  break;
               }

               try {
                  var1.process();
               } catch (Throwable var3) {
                  DefaultHttp2Connection.logger.error("Caught Throwable while processing pending ActiveStreams$Event.", var3);
               }
            }
         }

      }
   }

   interface Event {
      void process();
   }

   private final class DefaultEndpoint<F extends Http2FlowController> implements Http2Connection.Endpoint<F> {
      private final boolean server;
      private int nextStreamIdToCreate;
      private int nextReservationStreamId;
      private int lastStreamKnownByPeer = -1;
      private boolean pushToAllowed = true;
      private F flowController;
      private int maxStreams;
      private int maxActiveStreams;
      private final int maxReservedStreams;
      int numActiveStreams;
      int numStreams;

      DefaultEndpoint(boolean var2, int var3) {
         super();
         this.server = var2;
         if (var2) {
            this.nextStreamIdToCreate = 2;
            this.nextReservationStreamId = 0;
         } else {
            this.nextStreamIdToCreate = 1;
            this.nextReservationStreamId = 1;
         }

         this.pushToAllowed = !var2;
         this.maxActiveStreams = 2147483647;
         this.maxReservedStreams = ObjectUtil.checkPositiveOrZero(var3, "maxReservedStreams");
         this.updateMaxStreams();
      }

      public int incrementAndGetNextStreamId() {
         return this.nextReservationStreamId >= 0 ? (this.nextReservationStreamId += 2) : this.nextReservationStreamId;
      }

      private void incrementExpectedStreamId(int var1) {
         if (var1 > this.nextReservationStreamId && this.nextReservationStreamId >= 0) {
            this.nextReservationStreamId = var1;
         }

         this.nextStreamIdToCreate = var1 + 2;
         ++this.numStreams;
      }

      public boolean isValidStreamId(int var1) {
         return var1 > 0 && this.server == ((var1 & 1) == 0);
      }

      public boolean mayHaveCreatedStream(int var1) {
         return this.isValidStreamId(var1) && var1 <= this.lastStreamCreated();
      }

      public boolean canOpenStream() {
         return this.numActiveStreams < this.maxActiveStreams;
      }

      public DefaultHttp2Connection.DefaultStream createStream(int var1, boolean var2) throws Http2Exception {
         Http2Stream.State var3 = DefaultHttp2Connection.activeState(var1, Http2Stream.State.IDLE, this.isLocal(), var2);
         this.checkNewStreamAllowed(var1, var3);
         DefaultHttp2Connection.DefaultStream var4 = DefaultHttp2Connection.this.new DefaultStream(var1, var3);
         this.incrementExpectedStreamId(var1);
         this.addStream(var4);
         var4.activate();
         return var4;
      }

      public boolean created(Http2Stream var1) {
         return var1 instanceof DefaultHttp2Connection.DefaultStream && ((DefaultHttp2Connection.DefaultStream)var1).createdBy() == this;
      }

      public boolean isServer() {
         return this.server;
      }

      public DefaultHttp2Connection.DefaultStream reservePushStream(int var1, Http2Stream var2) throws Http2Exception {
         if (var2 == null) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Parent stream missing");
         } else {
            label27: {
               if (this.isLocal()) {
                  if (var2.state().localSideOpen()) {
                     break label27;
                  }
               } else if (var2.state().remoteSideOpen()) {
                  break label27;
               }

               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d is not open for sending push promise", var2.id());
            }

            if (!this.opposite().allowPushTo()) {
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server push not allowed to opposite endpoint");
            } else {
               Http2Stream.State var3 = this.isLocal() ? Http2Stream.State.RESERVED_LOCAL : Http2Stream.State.RESERVED_REMOTE;
               this.checkNewStreamAllowed(var1, var3);
               DefaultHttp2Connection.DefaultStream var4 = DefaultHttp2Connection.this.new DefaultStream(var1, var3);
               this.incrementExpectedStreamId(var1);
               this.addStream(var4);
               return var4;
            }
         }
      }

      private void addStream(DefaultHttp2Connection.DefaultStream var1) {
         DefaultHttp2Connection.this.streamMap.put(var1.id(), var1);

         for(int var2 = 0; var2 < DefaultHttp2Connection.this.listeners.size(); ++var2) {
            try {
               ((Http2Connection.Listener)DefaultHttp2Connection.this.listeners.get(var2)).onStreamAdded(var1);
            } catch (Throwable var4) {
               DefaultHttp2Connection.logger.error("Caught Throwable from listener onStreamAdded.", var4);
            }
         }

      }

      public void allowPushTo(boolean var1) {
         if (var1 && this.server) {
            throw new IllegalArgumentException("Servers do not allow push");
         } else {
            this.pushToAllowed = var1;
         }
      }

      public boolean allowPushTo() {
         return this.pushToAllowed;
      }

      public int numActiveStreams() {
         return this.numActiveStreams;
      }

      public int maxActiveStreams() {
         return this.maxActiveStreams;
      }

      public void maxActiveStreams(int var1) {
         this.maxActiveStreams = var1;
         this.updateMaxStreams();
      }

      public int lastStreamCreated() {
         return this.nextStreamIdToCreate > 1 ? this.nextStreamIdToCreate - 2 : 0;
      }

      public int lastStreamKnownByPeer() {
         return this.lastStreamKnownByPeer;
      }

      private void lastStreamKnownByPeer(int var1) {
         this.lastStreamKnownByPeer = var1;
      }

      public F flowController() {
         return this.flowController;
      }

      public void flowController(F var1) {
         this.flowController = (Http2FlowController)ObjectUtil.checkNotNull(var1, "flowController");
      }

      public Http2Connection.Endpoint<? extends Http2FlowController> opposite() {
         return this.isLocal() ? DefaultHttp2Connection.this.remoteEndpoint : DefaultHttp2Connection.this.localEndpoint;
      }

      private void updateMaxStreams() {
         this.maxStreams = (int)Math.min(2147483647L, (long)this.maxActiveStreams + (long)this.maxReservedStreams);
      }

      private void checkNewStreamAllowed(int var1, Http2Stream.State var2) throws Http2Exception {
         assert var2 != Http2Stream.State.IDLE;

         if (DefaultHttp2Connection.this.goAwayReceived() && var1 > DefaultHttp2Connection.this.localEndpoint.lastStreamKnownByPeer()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Cannot create stream %d since this endpoint has received a GOAWAY frame with last stream id %d.", var1, DefaultHttp2Connection.this.localEndpoint.lastStreamKnownByPeer());
         } else if (!this.isValidStreamId(var1)) {
            if (var1 < 0) {
               throw new Http2NoMoreStreamIdsException();
            } else {
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Request stream %d is not correct for %s connection", var1, this.server ? "server" : "client");
            }
         } else if (var1 < this.nextStreamIdToCreate) {
            throw Http2Exception.closedStreamError(Http2Error.PROTOCOL_ERROR, "Request stream %d is behind the next expected stream %d", var1, this.nextStreamIdToCreate);
         } else if (this.nextStreamIdToCreate <= 0) {
            throw Http2Exception.connectionError(Http2Error.REFUSED_STREAM, "Stream IDs are exhausted for this endpoint.");
         } else {
            boolean var3 = var2 == Http2Stream.State.RESERVED_LOCAL || var2 == Http2Stream.State.RESERVED_REMOTE;
            if (!var3 && !this.canOpenStream() || var3 && this.numStreams >= this.maxStreams) {
               throw Http2Exception.streamError(var1, Http2Error.REFUSED_STREAM, "Maximum active streams violated for this endpoint.");
            } else if (DefaultHttp2Connection.this.isClosed()) {
               throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Attempted to create stream id %d after connection was closed", var1);
            }
         }
      }

      private boolean isLocal() {
         return this == DefaultHttp2Connection.this.localEndpoint;
      }
   }

   private final class ConnectionStream extends DefaultHttp2Connection.DefaultStream {
      ConnectionStream() {
         super(0, Http2Stream.State.IDLE);
      }

      public boolean isResetSent() {
         return false;
      }

      DefaultHttp2Connection.DefaultEndpoint<? extends Http2FlowController> createdBy() {
         return null;
      }

      public Http2Stream resetSent() {
         throw new UnsupportedOperationException();
      }

      public Http2Stream open(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public Http2Stream close() {
         throw new UnsupportedOperationException();
      }

      public Http2Stream closeLocalSide() {
         throw new UnsupportedOperationException();
      }

      public Http2Stream closeRemoteSide() {
         throw new UnsupportedOperationException();
      }

      public Http2Stream headersSent(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean isHeadersSent() {
         throw new UnsupportedOperationException();
      }

      public Http2Stream pushPromiseSent() {
         throw new UnsupportedOperationException();
      }

      public boolean isPushPromiseSent() {
         throw new UnsupportedOperationException();
      }
   }

   private class DefaultStream implements Http2Stream {
      private static final byte META_STATE_SENT_RST = 1;
      private static final byte META_STATE_SENT_HEADERS = 2;
      private static final byte META_STATE_SENT_TRAILERS = 4;
      private static final byte META_STATE_SENT_PUSHPROMISE = 8;
      private static final byte META_STATE_RECV_HEADERS = 16;
      private static final byte META_STATE_RECV_TRAILERS = 32;
      private final int id;
      private final DefaultHttp2Connection.DefaultStream.PropertyMap properties = new DefaultHttp2Connection.DefaultStream.PropertyMap();
      private Http2Stream.State state;
      private byte metaState;

      DefaultStream(int var2, Http2Stream.State var3) {
         super();
         this.id = var2;
         this.state = var3;
      }

      public final int id() {
         return this.id;
      }

      public final Http2Stream.State state() {
         return this.state;
      }

      public boolean isResetSent() {
         return (this.metaState & 1) != 0;
      }

      public Http2Stream resetSent() {
         this.metaState = (byte)(this.metaState | 1);
         return this;
      }

      public Http2Stream headersSent(boolean var1) {
         if (!var1) {
            this.metaState = (byte)(this.metaState | (this.isHeadersSent() ? 4 : 2));
         }

         return this;
      }

      public boolean isHeadersSent() {
         return (this.metaState & 2) != 0;
      }

      public boolean isTrailersSent() {
         return (this.metaState & 4) != 0;
      }

      public Http2Stream headersReceived(boolean var1) {
         if (!var1) {
            this.metaState = (byte)(this.metaState | (this.isHeadersReceived() ? 32 : 16));
         }

         return this;
      }

      public boolean isHeadersReceived() {
         return (this.metaState & 16) != 0;
      }

      public boolean isTrailersReceived() {
         return (this.metaState & 32) != 0;
      }

      public Http2Stream pushPromiseSent() {
         this.metaState = (byte)(this.metaState | 8);
         return this;
      }

      public boolean isPushPromiseSent() {
         return (this.metaState & 8) != 0;
      }

      public final <V> V setProperty(Http2Connection.PropertyKey var1, V var2) {
         return this.properties.add(DefaultHttp2Connection.this.verifyKey(var1), var2);
      }

      public final <V> V getProperty(Http2Connection.PropertyKey var1) {
         return this.properties.get(DefaultHttp2Connection.this.verifyKey(var1));
      }

      public final <V> V removeProperty(Http2Connection.PropertyKey var1) {
         return this.properties.remove(DefaultHttp2Connection.this.verifyKey(var1));
      }

      public Http2Stream open(boolean var1) throws Http2Exception {
         this.state = DefaultHttp2Connection.activeState(this.id, this.state, this.isLocal(), var1);
         if (!this.createdBy().canOpenStream()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Maximum active streams violated for this endpoint.");
         } else {
            this.activate();
            return this;
         }
      }

      void activate() {
         if (this.state == Http2Stream.State.HALF_CLOSED_LOCAL) {
            this.headersSent(false);
         } else if (this.state == Http2Stream.State.HALF_CLOSED_REMOTE) {
            this.headersReceived(false);
         }

         DefaultHttp2Connection.this.activeStreams.activate(this);
      }

      Http2Stream close(Iterator<?> var1) {
         if (this.state == Http2Stream.State.CLOSED) {
            return this;
         } else {
            this.state = Http2Stream.State.CLOSED;
            --this.createdBy().numStreams;
            DefaultHttp2Connection.this.activeStreams.deactivate(this, var1);
            return this;
         }
      }

      public Http2Stream close() {
         return this.close((Iterator)null);
      }

      public Http2Stream closeLocalSide() {
         switch(this.state) {
         case OPEN:
            this.state = Http2Stream.State.HALF_CLOSED_LOCAL;
            DefaultHttp2Connection.this.notifyHalfClosed(this);
         case HALF_CLOSED_LOCAL:
            break;
         default:
            this.close();
         }

         return this;
      }

      public Http2Stream closeRemoteSide() {
         switch(this.state) {
         case OPEN:
            this.state = Http2Stream.State.HALF_CLOSED_REMOTE;
            DefaultHttp2Connection.this.notifyHalfClosed(this);
         case HALF_CLOSED_REMOTE:
            break;
         default:
            this.close();
         }

         return this;
      }

      DefaultHttp2Connection.DefaultEndpoint<? extends Http2FlowController> createdBy() {
         return DefaultHttp2Connection.this.localEndpoint.isValidStreamId(this.id) ? DefaultHttp2Connection.this.localEndpoint : DefaultHttp2Connection.this.remoteEndpoint;
      }

      final boolean isLocal() {
         return DefaultHttp2Connection.this.localEndpoint.isValidStreamId(this.id);
      }

      private class PropertyMap {
         Object[] values;

         private PropertyMap() {
            super();
            this.values = EmptyArrays.EMPTY_OBJECTS;
         }

         <V> V add(DefaultHttp2Connection.DefaultPropertyKey var1, V var2) {
            this.resizeIfNecessary(var1.index);
            Object var3 = this.values[var1.index];
            this.values[var1.index] = var2;
            return var3;
         }

         <V> V get(DefaultHttp2Connection.DefaultPropertyKey var1) {
            return var1.index >= this.values.length ? null : this.values[var1.index];
         }

         <V> V remove(DefaultHttp2Connection.DefaultPropertyKey var1) {
            Object var2 = null;
            if (var1.index < this.values.length) {
               var2 = this.values[var1.index];
               this.values[var1.index] = null;
            }

            return var2;
         }

         void resizeIfNecessary(int var1) {
            if (var1 >= this.values.length) {
               this.values = Arrays.copyOf(this.values, DefaultHttp2Connection.this.propertyKeyRegistry.size());
            }

         }

         // $FF: synthetic method
         PropertyMap(Object var2) {
            this();
         }
      }
   }
}
