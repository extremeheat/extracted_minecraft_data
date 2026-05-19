package net.minecraft.client.multiplayer.p2p;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.onvoid.webrtc.RTCIceCandidate;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;

public sealed interface SignalingMessage {
   Codec<SignalingMessage> CODEC = SignalingMessage.Type.CODEC.dispatch(SignalingMessage::type, Type::codec);

   static SignalingMessage joinRequest(final String sessionId) {
      return new FriendJoin.Request(sessionId);
   }

   static SignalingMessage joinAccepted(final String sessionId) {
      return new FriendJoin.Accepted(sessionId);
   }

   static SignalingMessage joinRejected(final String sessionId) {
      return new FriendJoin.Rejected(sessionId);
   }

   static SignalingMessage inviteDeclined() {
      return new FriendJoin.InviteDeclined(UUID.randomUUID().toString());
   }

   static SignalingMessage offer(final String sessionId, final String sdp) {
      return new WebRtc.Offer(sessionId, sdp);
   }

   static SignalingMessage answer(final String sessionId, final String sdp) {
      return new WebRtc.Answer(sessionId, sdp);
   }

   static SignalingMessage iceCandidate(final String sessionId, final RTCIceCandidate candidate) {
      return new WebRtc.IceCandidate(sessionId, candidate.sdp, candidate.sdpMid, candidate.sdpMLineIndex);
   }

   Type type();

   public static enum Type implements StringRepresentable {
      JOIN_REQUEST(() -> SignalingMessage.FriendJoin.Request.CODEC),
      JOIN_ACCEPTED(() -> SignalingMessage.FriendJoin.Accepted.CODEC),
      JOIN_REJECTED(() -> SignalingMessage.FriendJoin.Rejected.CODEC),
      INVITE_DECLINED(() -> SignalingMessage.FriendJoin.InviteDeclined.CODEC),
      OFFER(() -> SignalingMessage.WebRtc.Offer.CODEC),
      ANSWER(() -> SignalingMessage.WebRtc.Answer.CODEC),
      ICE_CANDIDATE(() -> SignalingMessage.WebRtc.IceCandidate.CODEC);

      private static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final Supplier<MapCodec<? extends SignalingMessage>> codec;

      private Type(final Supplier<MapCodec<? extends SignalingMessage>> codec) {
         this.codec = codec;
      }

      private MapCodec<? extends SignalingMessage> codec() {
         return (MapCodec)this.codec.get();
      }

      public String getSerializedName() {
         return this.name();
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{JOIN_REQUEST, JOIN_ACCEPTED, JOIN_REJECTED, INVITE_DECLINED, OFFER, ANSWER, ICE_CANDIDATE};
      }
   }

   public sealed interface FriendJoin extends SignalingMessage {
      public static record Request(String sessionId) implements FriendJoin {
         private static final MapCodec<Request> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(Request::sessionId)).apply(i, Request::new));

         public Request {
            super();
         }

         public Type type() {
            return SignalingMessage.Type.JOIN_REQUEST;
         }
      }

      public static record Accepted(String sessionId) implements FriendJoin {
         private static final MapCodec<Accepted> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(Accepted::sessionId)).apply(i, Accepted::new));

         public Accepted {
            super();
         }

         public Type type() {
            return SignalingMessage.Type.JOIN_ACCEPTED;
         }
      }

      public static record Rejected(String sessionId) implements FriendJoin {
         private static final MapCodec<Rejected> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(Rejected::sessionId)).apply(i, Rejected::new));

         public Rejected {
            super();
         }

         public Type type() {
            return SignalingMessage.Type.JOIN_REJECTED;
         }
      }

      public static record InviteDeclined(String sessionId) implements FriendJoin {
         private static final MapCodec<InviteDeclined> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(InviteDeclined::sessionId)).apply(i, InviteDeclined::new));

         public InviteDeclined {
            super();
         }

         public Type type() {
            return SignalingMessage.Type.INVITE_DECLINED;
         }
      }
   }

   public sealed interface WebRtc extends SignalingMessage {
      String sessionId();

      public static record Offer(String sessionId, String sdp) implements WebRtc {
         private static final MapCodec<Offer> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(Offer::sessionId), Codec.STRING.fieldOf("sdp").forGetter(Offer::sdp)).apply(i, Offer::new));

         public Offer {
            super();
         }

         public Type type() {
            return SignalingMessage.Type.OFFER;
         }
      }

      public static record Answer(String sessionId, String sdp) implements WebRtc {
         private static final MapCodec<Answer> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(Answer::sessionId), Codec.STRING.fieldOf("sdp").forGetter(Answer::sdp)).apply(i, Answer::new));

         public Answer {
            super();
         }

         public Type type() {
            return SignalingMessage.Type.ANSWER;
         }
      }

      public static record IceCandidate(String sessionId, String candidate, String sdpMid, int sdpMLineIndex) implements WebRtc {
         private static final MapCodec<IceCandidate> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("sessionId").forGetter(IceCandidate::sessionId), Codec.STRING.fieldOf("candidate").forGetter(IceCandidate::candidate), Codec.STRING.fieldOf("sdpMid").forGetter(IceCandidate::sdpMid), Codec.INT.fieldOf("sdpMLineIndex").forGetter(IceCandidate::sdpMLineIndex)).apply(i, IceCandidate::new));

         public IceCandidate {
            super();
         }

         public RTCIceCandidate toRtcIceCandidate() {
            return new RTCIceCandidate(this.sdpMid, this.sdpMLineIndex, this.candidate);
         }

         public Type type() {
            return SignalingMessage.Type.ICE_CANDIDATE;
         }
      }
   }
}
