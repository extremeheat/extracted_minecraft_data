package net.minecraft.client.multiplayer.p2p;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.onvoid.webrtc.RTCIceCandidate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public record SignalingMessage(Type type, String sessionId, @Nullable String sdp, WebRtc.@Nullable Candidate iceCandidate) {
   public static final Codec<SignalingMessage> CODEC = RecordCodecBuilder.create((i) -> i.group(SignalingMessage.Type.CODEC.fieldOf("type").forGetter(SignalingMessage::type), Codec.STRING.fieldOf("sessionId").forGetter(SignalingMessage::sessionId), Codec.STRING.optionalFieldOf("sdp").forGetter((m) -> Optional.ofNullable(m.sdp())), SignalingMessage.WebRtc.Candidate.CODEC.optionalFieldOf("iceCandidate").forGetter((m) -> Optional.ofNullable(m.iceCandidate()))).apply(i, (type, sessionId, sdp, iceCandidate) -> new SignalingMessage(type, sessionId, (String)sdp.orElse((Object)null), (WebRtc.Candidate)iceCandidate.orElse((Object)null))));

   public SignalingMessage {
      super();
   }

   public static SignalingMessage joinRequest(final String sessionId) {
      return from(new FriendJoin.Request(sessionId));
   }

   public static SignalingMessage joinAccepted(final String sessionId) {
      return from(new FriendJoin.Accepted(sessionId));
   }

   public static SignalingMessage joinRejected(final String sessionId) {
      return from(new FriendJoin.Rejected(sessionId));
   }

   public static SignalingMessage inviteDeclined() {
      return from(SignalingMessage.FriendJoin.InviteDeclined.INSTANCE);
   }

   public static SignalingMessage offer(final String sessionId, final String sdp) {
      return from(new WebRtc.Offer(sessionId, sdp));
   }

   public static SignalingMessage answer(final String sessionId, final String sdp) {
      return from(new WebRtc.Answer(sessionId, sdp));
   }

   public static SignalingMessage iceCandidate(final String sessionId, final RTCIceCandidate candidate) {
      return from(new WebRtc.IceCandidate(sessionId, SignalingMessage.WebRtc.Candidate.from(candidate)));
   }

   private static SignalingMessage from(final Payload payload) {
      Objects.requireNonNull(payload);
      byte var2 = 0;
      SignalingMessage var10000;
      //$FF: var2->value
      //0->net/minecraft/client/multiplayer/p2p/SignalingMessage$FriendJoin$Request
      //1->net/minecraft/client/multiplayer/p2p/SignalingMessage$FriendJoin$Accepted
      //2->net/minecraft/client/multiplayer/p2p/SignalingMessage$FriendJoin$Rejected
      //3->net/minecraft/client/multiplayer/p2p/SignalingMessage$FriendJoin$InviteDeclined
      //4->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc$Offer
      //5->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc$Answer
      //6->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc$IceCandidate
      switch (payload.typeSwitch<invokedynamic>(payload, var2)) {
         case 0:
            FriendJoin.Request m = (FriendJoin.Request)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.JOIN_REQUEST, m.sessionId(), (String)null, (WebRtc.Candidate)null);
            break;
         case 1:
            FriendJoin.Accepted m = (FriendJoin.Accepted)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.JOIN_ACCEPTED, m.sessionId(), (String)null, (WebRtc.Candidate)null);
            break;
         case 2:
            FriendJoin.Rejected m = (FriendJoin.Rejected)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.JOIN_REJECTED, m.sessionId(), (String)null, (WebRtc.Candidate)null);
            break;
         case 3:
            FriendJoin.InviteDeclined ignored = (FriendJoin.InviteDeclined)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.INVITE_DECLINED, UUID.randomUUID().toString(), (String)null, (WebRtc.Candidate)null);
            break;
         case 4:
            WebRtc.Offer m = (WebRtc.Offer)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.OFFER, m.sessionId(), m.sdp(), (WebRtc.Candidate)null);
            break;
         case 5:
            WebRtc.Answer m = (WebRtc.Answer)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.ANSWER, m.sessionId(), m.sdp(), (WebRtc.Candidate)null);
            break;
         case 6:
            WebRtc.IceCandidate m = (WebRtc.IceCandidate)payload;
            var10000 = new SignalingMessage(SignalingMessage.Type.ICE_CANDIDATE, m.sessionId(), (String)null, m.candidate());
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @Nullable Payload decode() {
      Object var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = new FriendJoin.Request(this.sessionId);
         case 1 -> var10000 = new FriendJoin.Accepted(this.sessionId);
         case 2 -> var10000 = new FriendJoin.Rejected(this.sessionId);
         case 3 -> var10000 = SignalingMessage.FriendJoin.InviteDeclined.INSTANCE;
         case 4 -> var10000 = this.sdp != null ? new WebRtc.Offer(this.sessionId, this.sdp) : null;
         case 5 -> var10000 = this.sdp != null ? new WebRtc.Answer(this.sessionId, this.sdp) : null;
         case 6 -> var10000 = this.iceCandidate != null ? new WebRtc.IceCandidate(this.sessionId, this.iceCandidate) : (this.sdp != null ? new WebRtc.IceCandidate(this.sessionId, new WebRtc.Candidate(this.sdp, "0", 0)) : null);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (Payload)var10000;
   }

   private static enum Type implements StringRepresentable {
      JOIN_REQUEST,
      JOIN_ACCEPTED,
      JOIN_REJECTED,
      INVITE_DECLINED,
      OFFER,
      ANSWER,
      ICE_CANDIDATE;

      private static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);

      private Type() {
      }

      public String getSerializedName() {
         return this.name();
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{JOIN_REQUEST, JOIN_ACCEPTED, JOIN_REJECTED, INVITE_DECLINED, OFFER, ANSWER, ICE_CANDIDATE};
      }
   }

   public sealed interface FriendJoin extends Payload {
      public static record Request(String sessionId) implements FriendJoin {
         public Request {
            super();
         }
      }

      public static record Accepted(String sessionId) implements FriendJoin {
         public Accepted {
            super();
         }
      }

      public static record Rejected(String sessionId) implements FriendJoin {
         public Rejected {
            super();
         }
      }

      public static record InviteDeclined() implements FriendJoin {
         private static final InviteDeclined INSTANCE = new InviteDeclined();

         public InviteDeclined() {
            super();
         }
      }
   }

   public sealed interface Payload permits SignalingMessage.FriendJoin, SignalingMessage.WebRtc {
   }

   public sealed interface WebRtc extends Payload {
      String sessionId();

      public static record Offer(String sessionId, String sdp) implements WebRtc {
         public Offer {
            super();
         }
      }

      public static record Answer(String sessionId, String sdp) implements WebRtc {
         public Answer {
            super();
         }
      }

      public static record IceCandidate(String sessionId, Candidate candidate) implements WebRtc {
         public IceCandidate {
            super();
         }
      }

      public static record Candidate(String candidate, @Nullable String sdpMid, int sdpMLineIndex) {
         private static final Codec<Candidate> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("candidate").forGetter(Candidate::candidate), Codec.STRING.optionalFieldOf("sdpMid").forGetter((c) -> Optional.ofNullable(c.sdpMid())), Codec.INT.fieldOf("sdpMLineIndex").forGetter(Candidate::sdpMLineIndex)).apply(i, (candidate, sdpMid, sdpMLineIndex) -> new Candidate(candidate, (String)sdpMid.orElse((Object)null), sdpMLineIndex)));

         public Candidate {
            super();
         }

         private static Candidate from(final RTCIceCandidate candidate) {
            return new Candidate(candidate.sdp, candidate.sdpMid, candidate.sdpMLineIndex);
         }

         public RTCIceCandidate toRtcIceCandidate() {
            return new RTCIceCandidate(this.sdpMid != null ? this.sdpMid : "0", this.sdpMLineIndex, this.candidate);
         }
      }
   }
}
