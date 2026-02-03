package net.minecraft.server;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public record ServerLinks(List<Entry> entries) {
   public static final ServerLinks EMPTY = new ServerLinks(List.of());
   public static final StreamCodec<ByteBuf, Either<KnownLinkType, Component>> TYPE_STREAM_CODEC;
   public static final StreamCodec<ByteBuf, List<UntrustedEntry>> UNTRUSTED_LINKS_STREAM_CODEC;

   public ServerLinks {
      super();
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public Optional<Entry> findKnownType(final KnownLinkType type) {
      return this.entries.stream().filter((e) -> (Boolean)e.type.map((l) -> l == type, (r) -> false)).findFirst();
   }

   public List<UntrustedEntry> untrust() {
      return this.entries.stream().map((e) -> new UntrustedEntry(e.type, e.link.toString())).toList();
   }

   static {
      TYPE_STREAM_CODEC = ByteBufCodecs.either(ServerLinks.KnownLinkType.STREAM_CODEC, ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC);
      UNTRUSTED_LINKS_STREAM_CODEC = ServerLinks.UntrustedEntry.STREAM_CODEC.apply(ByteBufCodecs.list());
   }

   public static record UntrustedEntry(Either<KnownLinkType, Component> type, String link) {
      public static final StreamCodec<ByteBuf, UntrustedEntry> STREAM_CODEC;

      public UntrustedEntry {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ServerLinks.TYPE_STREAM_CODEC, UntrustedEntry::type, ByteBufCodecs.STRING_UTF8, UntrustedEntry::link, UntrustedEntry::new);
      }
   }

   public static record Entry(Either<KnownLinkType, Component> type, URI link) {
      public Entry {
         super();
      }

      public static Entry knownType(final KnownLinkType type, final URI link) {
         return new Entry(Either.left(type), link);
      }

      public static Entry custom(final Component displayName, final URI link) {
         return new Entry(Either.right(displayName), link);
      }

      public Component displayName() {
         return (Component)this.type.map(KnownLinkType::displayName, (r) -> r);
      }
   }

   public static enum KnownLinkType {
      BUG_REPORT(0, "report_bug"),
      COMMUNITY_GUIDELINES(1, "community_guidelines"),
      SUPPORT(2, "support"),
      STATUS(3, "status"),
      FEEDBACK(4, "feedback"),
      COMMUNITY(5, "community"),
      WEBSITE(6, "website"),
      FORUMS(7, "forums"),
      NEWS(8, "news"),
      ANNOUNCEMENTS(9, "announcements");

      private static final IntFunction<KnownLinkType> BY_ID = ByIdMap.<KnownLinkType>continuous((e) -> e.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, KnownLinkType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (e) -> e.id);
      private final int id;
      private final String name;

      private KnownLinkType(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      private Component displayName() {
         return Component.translatable("known_server_link." + this.name);
      }

      public Entry create(final URI link) {
         return ServerLinks.Entry.knownType(this, link);
      }

      // $FF: synthetic method
      private static KnownLinkType[] $values() {
         return new KnownLinkType[]{BUG_REPORT, COMMUNITY_GUIDELINES, SUPPORT, STATUS, FEEDBACK, COMMUNITY, WEBSITE, FORUMS, NEWS, ANNOUNCEMENTS};
      }
   }
}
