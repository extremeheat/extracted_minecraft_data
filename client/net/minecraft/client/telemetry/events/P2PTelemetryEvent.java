package net.minecraft.client.telemetry.events;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public class P2PTelemetryEvent {
   public static final P2PTelemetryEvent INSTANCE = new P2PTelemetryEvent();

   public P2PTelemetryEvent() {
      super();
   }

   public void send(final boolean successful, final State state, final @Nullable Instant connectionStartTime, final @Nullable Instant signalingDoneTime, final @Nullable Instant connectionEstablishedTime) {
      State.Snapshot snapshot = state.snapshot();
      Long totalTimeMs = millisBetween(connectionStartTime, connectionEstablishedTime);
      Long signalingTimeMs = millisBetween(connectionStartTime, signalingDoneTime);
      Long iceConnectTimeMs = millisBetween(signalingDoneTime, connectionEstablishedTime);
      IcePath icePath = snapshot.localCandidateType() != null && snapshot.remoteCandidateType() != null ? P2PTelemetryEvent.IcePath.classify(snapshot.localCandidateType(), snapshot.remoteCandidateType()) : null;
      Minecraft.getInstance().getTelemetryManager().getOutsideSessionSender().send(TelemetryEventType.P2P_CONNECTION, (properties) -> {
         properties.put(TelemetryProperty.P2P_CONNECTION_SUCCESSFUL, successful);
         if (icePath != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_ICE_PATH, icePath);
         }

         if (snapshot.localCandidateType() != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_LOCAL_CANDIDATE_TYPE, snapshot.localCandidateType());
         }

         if (snapshot.remoteCandidateType() != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_REMOTE_CANDIDATE_TYPE, snapshot.remoteCandidateType());
         }

         if (totalTimeMs != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_TOTAL_TIME_MS, totalTimeMs);
         }

         if (signalingTimeMs != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_SIGNALING_TIME_MS, signalingTimeMs);
         }

         if (iceConnectTimeMs != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_ICE_CONNECT_TIME_MS, iceConnectTimeMs);
         }

         if (!successful && snapshot.failureStage() != null) {
            properties.put(TelemetryProperty.P2P_CONNECTION_FAILURE_STAGE, snapshot.failureStage());
         }

      });
   }

   private static @Nullable Long millisBetween(final @Nullable Instant from, final @Nullable Instant to) {
      return from != null && to != null ? from.until(to, ChronoUnit.MILLIS) : null;
   }

   public static final class State {
      private @Nullable IceCandidateType localCandidateType;
      private @Nullable IceCandidateType remoteCandidateType;
      private @Nullable FailureStage failureStage;

      public State() {
         super();
      }

      public synchronized Snapshot snapshot() {
         return new Snapshot(this.localCandidateType, this.remoteCandidateType, this.failureStage);
      }

      public synchronized void setIceInfo(final IceCandidateType local, final IceCandidateType remote) {
         this.localCandidateType = local;
         this.remoteCandidateType = remote;
      }

      public synchronized void setFailureStage(final FailureStage failureStage) {
         if (this.failureStage == null) {
            this.failureStage = failureStage;
         }

      }

      public static record Snapshot(@Nullable IceCandidateType localCandidateType, @Nullable IceCandidateType remoteCandidateType, @Nullable FailureStage failureStage) {
         public Snapshot {
            super();
         }
      }
   }

   public static enum IcePath implements StringRepresentable {
      LOCAL("LOCAL"),
      DIRECT("DIRECT"),
      RELAY("RELAY"),
      UNKNOWN("UNKNOWN");

      public static final Codec<IcePath> CODEC = StringRepresentable.<IcePath>fromEnum(IcePath::values);
      private final String name;

      private IcePath(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static IcePath classify(final IceCandidateType local, final IceCandidateType remote) {
         if (local != P2PTelemetryEvent.IceCandidateType.RELAY && remote != P2PTelemetryEvent.IceCandidateType.RELAY) {
            if (local != P2PTelemetryEvent.IceCandidateType.SRFLX && local != P2PTelemetryEvent.IceCandidateType.PRFLX && remote != P2PTelemetryEvent.IceCandidateType.SRFLX && remote != P2PTelemetryEvent.IceCandidateType.PRFLX) {
               return local == P2PTelemetryEvent.IceCandidateType.HOST && remote == P2PTelemetryEvent.IceCandidateType.HOST ? LOCAL : UNKNOWN;
            } else {
               return DIRECT;
            }
         } else {
            return RELAY;
         }
      }

      // $FF: synthetic method
      private static IcePath[] $values() {
         return new IcePath[]{LOCAL, DIRECT, RELAY, UNKNOWN};
      }
   }

   public static enum FailureStage implements StringRepresentable {
      SIGNALING("SIGNALING"),
      ICE_CONNECT("ICE_CONNECT"),
      TIMEOUT("TIMEOUT");

      public static final Codec<FailureStage> CODEC = StringRepresentable.<FailureStage>fromEnum(FailureStage::values);
      private final String name;

      private FailureStage(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static FailureStage[] $values() {
         return new FailureStage[]{SIGNALING, ICE_CONNECT, TIMEOUT};
      }
   }

   public static enum IceCandidateType implements StringRepresentable {
      HOST("host"),
      SRFLX("srflx"),
      PRFLX("prflx"),
      RELAY("relay");

      public static final Codec<IceCandidateType> CODEC = StringRepresentable.<IceCandidateType>fromEnum(IceCandidateType::values);
      private static final Map<String, IceCandidateType> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(IceCandidateType::getSerializedName, Function.identity()));
      private final String name;

      private IceCandidateType(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Optional<IceCandidateType> byName(final String name) {
         return Optional.ofNullable((IceCandidateType)BY_NAME.get(name));
      }

      // $FF: synthetic method
      private static IceCandidateType[] $values() {
         return new IceCandidateType[]{HOST, SRFLX, PRFLX, RELAY};
      }
   }
}
