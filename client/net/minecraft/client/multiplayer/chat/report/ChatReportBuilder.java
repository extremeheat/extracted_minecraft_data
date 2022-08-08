package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportChatMessageBody;
import com.mojang.authlib.minecraft.report.ReportChatMessageContent;
import com.mojang.authlib.minecraft.report.ReportChatMessageHeader;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.client.multiplayer.chat.LoggedChatMessageLink;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageBody;

public class ChatReportBuilder {
   private final UUID reportId;
   private final Instant createdAt;
   private final UUID reportedProfileId;
   private final AbuseReportLimits limits;
   private final IntSet reportedMessages;
   private String comments;
   @Nullable
   private ReportReason reason;

   private ChatReportBuilder(UUID var1, Instant var2, UUID var3, AbuseReportLimits var4) {
      super();
      this.reportedMessages = new IntOpenHashSet();
      this.comments = "";
      this.reportId = var1;
      this.createdAt = var2;
      this.reportedProfileId = var3;
      this.limits = var4;
   }

   public ChatReportBuilder(UUID var1, AbuseReportLimits var2) {
      this(UUID.randomUUID(), Instant.now(), var1, var2);
   }

   public void setComments(String var1) {
      this.comments = var1;
   }

   public void setReason(ReportReason var1) {
      this.reason = var1;
   }

   public void toggleReported(int var1) {
      if (this.reportedMessages.contains(var1)) {
         this.reportedMessages.remove(var1);
      } else if (this.reportedMessages.size() < this.limits.maxReportedMessageCount()) {
         this.reportedMessages.add(var1);
      }

   }

   public UUID reportedProfileId() {
      return this.reportedProfileId;
   }

   public IntSet reportedMessages() {
      return this.reportedMessages;
   }

   public String comments() {
      return this.comments;
   }

   @Nullable
   public ReportReason reason() {
      return this.reason;
   }

   public boolean isReported(int var1) {
      return this.reportedMessages.contains(var1);
   }

   @Nullable
   public CannotBuildReason checkBuildable() {
      if (this.reportedMessages.isEmpty()) {
         return ChatReportBuilder.CannotBuildReason.NO_REPORTED_MESSAGES;
      } else if (this.reportedMessages.size() > this.limits.maxReportedMessageCount()) {
         return ChatReportBuilder.CannotBuildReason.TOO_MANY_MESSAGES;
      } else if (this.reason == null) {
         return ChatReportBuilder.CannotBuildReason.NO_REASON;
      } else {
         return this.comments.length() > this.limits.maxOpinionCommentsLength() ? ChatReportBuilder.CannotBuildReason.COMMENTS_TOO_LONG : null;
      }
   }

   public Either<Result, CannotBuildReason> build(ReportingContext var1) {
      CannotBuildReason var2 = this.checkBuildable();
      if (var2 != null) {
         return Either.right(var2);
      } else {
         String var3 = ((ReportReason)Objects.requireNonNull(this.reason)).backendName();
         ReportEvidence var4 = this.buildEvidence(var1.chatLog());
         ReportedEntity var5 = new ReportedEntity(this.reportedProfileId);
         AbuseReport var6 = new AbuseReport(this.comments, var3, var4, var5, this.createdAt);
         return Either.left(new Result(this.reportId, var6));
      }
   }

   private ReportEvidence buildEvidence(ChatLog var1) {
      Int2ObjectRBTreeMap var2 = new Int2ObjectRBTreeMap();
      this.reportedMessages.forEach((var3) -> {
         Int2ObjectMap var4 = collectReferencedContext(var1, var3, this.limits);
         ObjectOpenHashSet var5 = new ObjectOpenHashSet();
         ObjectIterator var6 = Int2ObjectMaps.fastIterable(var4).iterator();

         while(var6.hasNext()) {
            Int2ObjectMap.Entry var7 = (Int2ObjectMap.Entry)var6.next();
            int var8 = var7.getIntKey();
            LoggedChatMessage.Player var9 = (LoggedChatMessage.Player)var7.getValue();
            var2.put(var8, this.buildReportedChatMessage(var8, var9));
            var5.add(var9.profileId());
         }

         Iterator var10 = var5.iterator();

         while(var10.hasNext()) {
            UUID var11 = (UUID)var10.next();
            this.chainForPlayer(var1, var4, var11).forEach((var2x) -> {
               LoggedChatMessageLink var3 = (LoggedChatMessageLink)var2x.event();
               if (var3 instanceof LoggedChatMessage.Player var4) {
                  var2.putIfAbsent(var2x.id(), this.buildReportedChatMessage(var2x.id(), var4));
               } else {
                  var2.putIfAbsent(var2x.id(), this.buildReportedChatHeader(var3));
               }

            });
         }

      });
      return new ReportEvidence(new ArrayList(var2.values()));
   }

   private Stream<ChatLog.Entry<LoggedChatMessageLink>> chainForPlayer(ChatLog var1, Int2ObjectMap<LoggedChatMessage.Player> var2, UUID var3) {
      int var4 = 2147483647;
      int var5 = -2147483648;
      ObjectIterator var6 = Int2ObjectMaps.fastIterable(var2).iterator();

      while(var6.hasNext()) {
         Int2ObjectMap.Entry var7 = (Int2ObjectMap.Entry)var6.next();
         LoggedChatMessage.Player var8 = (LoggedChatMessage.Player)var7.getValue();
         if (var8.profileId().equals(var3)) {
            int var9 = var7.getIntKey();
            var4 = Math.min(var4, var9);
            var5 = Math.max(var5, var9);
         }
      }

      return var1.selectBetween(var4, var5).entries().map((var0) -> {
         return var0.tryCast(LoggedChatMessageLink.class);
      }).filter(Objects::nonNull).filter((var1x) -> {
         return ((LoggedChatMessageLink)var1x.event()).header().sender().equals(var3);
      });
   }

   private static Int2ObjectMap<LoggedChatMessage.Player> collectReferencedContext(ChatLog var0, int var1, AbuseReportLimits var2) {
      int var3 = var2.leadingContextMessageCount() + 1;
      Int2ObjectOpenHashMap var4 = new Int2ObjectOpenHashMap();
      walkMessageReferenceGraph(var0, var1, (var2x, var3x) -> {
         var4.put(var2x, var3x);
         return var4.size() < var3;
      });
      trailingContext(var0, var1, var2.trailingContextMessageCount()).forEach((var1x) -> {
         var4.put(var1x.id(), (LoggedChatMessage.Player)var1x.event());
      });
      return var4;
   }

   private static Stream<ChatLog.Entry<LoggedChatMessage.Player>> trailingContext(ChatLog var0, int var1, int var2) {
      return var0.selectAfter(var0.after(var1)).entries().map((var0x) -> {
         return var0x.tryCast(LoggedChatMessage.Player.class);
      }).filter(Objects::nonNull).limit((long)var2);
   }

   private static void walkMessageReferenceGraph(ChatLog var0, int var1, ReferencedMessageVisitor var2) {
      IntArrayPriorityQueue var3 = new IntArrayPriorityQueue(IntComparators.OPPOSITE_COMPARATOR);
      var3.enqueue(var1);
      IntOpenHashSet var4 = new IntOpenHashSet();
      var4.add(var1);

      while(!var3.isEmpty()) {
         int var5 = var3.dequeueInt();
         LoggedChatEvent var7 = var0.lookup(var5);
         if (var7 instanceof LoggedChatMessage.Player var6) {
            if (!var2.accept(var5, var6)) {
               break;
            }

            IntIterator var9 = messageReferences(var0, var5, var6.message()).iterator();

            while(var9.hasNext()) {
               int var8 = (Integer)var9.next();
               if (var4.add(var8)) {
                  var3.enqueue(var8);
               }
            }
         }
      }

   }

   private static IntCollection messageReferences(ChatLog var0, int var1, PlayerChatMessage var2) {
      Set var3 = (Set)var2.signedBody().lastSeen().entries().stream().map(LastSeenMessages.Entry::lastSignature).collect(Collectors.toCollection(ObjectOpenHashSet::new));
      MessageSignature var4 = var2.signedHeader().previousSignature();
      if (var4 != null) {
         var3.add(var4);
      }

      IntArrayList var5 = new IntArrayList();
      Iterator var6 = var0.selectBefore(var1).entries().iterator();

      while(var6.hasNext() && !var3.isEmpty()) {
         ChatLog.Entry var7 = (ChatLog.Entry)var6.next();
         LoggedChatEvent var9 = var7.event();
         if (var9 instanceof LoggedChatMessage.Player var8) {
            if (var3.remove(var8.headerSignature())) {
               var5.add(var7.id());
            }
         }
      }

      return var5;
   }

   private ReportChatMessage buildReportedChatMessage(int var1, LoggedChatMessage.Player var2) {
      PlayerChatMessage var3 = var2.message();
      SignedMessageBody var4 = var3.signedBody();
      Instant var5 = var3.timeStamp();
      long var6 = var3.salt();
      ByteBuffer var8 = var3.headerSignature().asByteBuffer();
      ByteBuffer var9 = (ByteBuffer)Util.mapNullable(var3.signedHeader().previousSignature(), MessageSignature::asByteBuffer);
      ByteBuffer var10 = ByteBuffer.wrap(var4.hash().asBytes());
      ReportChatMessageContent var11 = new ReportChatMessageContent(var3.signedContent().plain(), var3.signedContent().isDecorated() ? encodeComponent(var3.signedContent().decorated()) : null);
      String var12 = (String)var3.unsignedContent().map(ChatReportBuilder::encodeComponent).orElse((Object)null);
      List var13 = var4.lastSeen().entries().stream().map((var0) -> {
         return new ReportChatMessageBody.LastSeenSignature(var0.profileId(), var0.lastSignature().asByteBuffer());
      }).toList();
      return new ReportChatMessage(new ReportChatMessageHeader(var9, var2.profileId(), var10, var8), new ReportChatMessageBody(var5, var6, var13, var11), var12, this.isReported(var1));
   }

   private ReportChatMessage buildReportedChatHeader(LoggedChatMessageLink var1) {
      ByteBuffer var2 = var1.headerSignature().asByteBuffer();
      ByteBuffer var3 = (ByteBuffer)Util.mapNullable(var1.header().previousSignature(), MessageSignature::asByteBuffer);
      return new ReportChatMessage(new ReportChatMessageHeader(var3, var1.header().sender(), ByteBuffer.wrap(var1.bodyDigest()), var2), (ReportChatMessageBody)null, (String)null, false);
   }

   private static String encodeComponent(Component var0) {
      return Component.Serializer.toStableJson(var0);
   }

   public ChatReportBuilder copy() {
      ChatReportBuilder var1 = new ChatReportBuilder(this.reportId, this.createdAt, this.reportedProfileId, this.limits);
      var1.reportedMessages.addAll(this.reportedMessages);
      var1.comments = this.comments;
      var1.reason = this.reason;
      return var1;
   }

   public static record CannotBuildReason(Component e) {
      private final Component message;
      public static final CannotBuildReason NO_REASON = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reason"));
      public static final CannotBuildReason NO_REPORTED_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.no_reported_messages"));
      public static final CannotBuildReason TOO_MANY_MESSAGES = new CannotBuildReason(Component.translatable("gui.chatReport.send.too_many_messages"));
      public static final CannotBuildReason COMMENTS_TOO_LONG = new CannotBuildReason(Component.translatable("gui.chatReport.send.comments_too_long"));

      public CannotBuildReason(Component var1) {
         super();
         this.message = var1;
      }

      public Component message() {
         return this.message;
      }
   }

   public static record Result(UUID a, AbuseReport b) {
      private final UUID id;
      private final AbuseReport report;

      public Result(UUID var1, AbuseReport var2) {
         super();
         this.id = var1;
         this.report = var2;
      }

      public UUID id() {
         return this.id;
      }

      public AbuseReport report() {
         return this.report;
      }
   }

   private interface ReferencedMessageVisitor {
      boolean accept(int var1, LoggedChatMessage.Player var2);
   }
}
