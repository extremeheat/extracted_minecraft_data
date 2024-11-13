package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ChatType(ChatTypeDecoration chat, ChatTypeDecoration narration) {
   public static final Codec<ChatType> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ChatTypeDecoration.CODEC.fieldOf("chat").forGetter(ChatType::chat), ChatTypeDecoration.CODEC.fieldOf("narration").forGetter(ChatType::narration)).apply(var0, ChatType::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, ChatType> DIRECT_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ChatType>> STREAM_CODEC;
   public static final ChatTypeDecoration DEFAULT_CHAT_DECORATION;
   public static final ResourceKey<ChatType> CHAT;
   public static final ResourceKey<ChatType> SAY_COMMAND;
   public static final ResourceKey<ChatType> MSG_COMMAND_INCOMING;
   public static final ResourceKey<ChatType> MSG_COMMAND_OUTGOING;
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_INCOMING;
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_OUTGOING;
   public static final ResourceKey<ChatType> EMOTE_COMMAND;

   public ChatType(ChatTypeDecoration var1, ChatTypeDecoration var2) {
      super();
      this.chat = var1;
      this.narration = var2;
   }

   private static ResourceKey<ChatType> create(String var0) {
      return ResourceKey.create(Registries.CHAT_TYPE, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void bootstrap(BootstrapContext<ChatType> var0) {
      var0.register(CHAT, new ChatType(DEFAULT_CHAT_DECORATION, ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(SAY_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.announcement"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(MSG_COMMAND_INCOMING, new ChatType(ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(MSG_COMMAND_OUTGOING, new ChatType(ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(TEAM_MSG_COMMAND_INCOMING, new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.text"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(TEAM_MSG_COMMAND_OUTGOING, new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.sent"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(EMOTE_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.emote"), ChatTypeDecoration.withSender("chat.type.emote")));
   }

   public static Bound bind(ResourceKey<ChatType> var0, Entity var1) {
      return bind(var0, var1.level().registryAccess(), var1.getDisplayName());
   }

   public static Bound bind(ResourceKey<ChatType> var0, CommandSourceStack var1) {
      return bind(var0, var1.registryAccess(), var1.getDisplayName());
   }

   public static Bound bind(ResourceKey<ChatType> var0, RegistryAccess var1, Component var2) {
      Registry var3 = var1.lookupOrThrow(Registries.CHAT_TYPE);
      return new Bound(var3.getOrThrow(var0), var2);
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(ChatTypeDecoration.STREAM_CODEC, ChatType::chat, ChatTypeDecoration.STREAM_CODEC, ChatType::narration, ChatType::new);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.CHAT_TYPE, DIRECT_STREAM_CODEC);
      DEFAULT_CHAT_DECORATION = ChatTypeDecoration.withSender("chat.type.text");
      CHAT = create("chat");
      SAY_COMMAND = create("say_command");
      MSG_COMMAND_INCOMING = create("msg_command_incoming");
      MSG_COMMAND_OUTGOING = create("msg_command_outgoing");
      TEAM_MSG_COMMAND_INCOMING = create("team_msg_command_incoming");
      TEAM_MSG_COMMAND_OUTGOING = create("team_msg_command_outgoing");
      EMOTE_COMMAND = create("emote_command");
   }

   public static record Bound(Holder<ChatType> chatType, Component name, Optional<Component> targetName) {
      public static final StreamCodec<RegistryFriendlyByteBuf, Bound> STREAM_CODEC;

      Bound(Holder<ChatType> var1, Component var2) {
         this(var1, var2, Optional.empty());
      }

      public Bound(Holder<ChatType> var1, Component var2, Optional<Component> var3) {
         super();
         this.chatType = var1;
         this.name = var2;
         this.targetName = var3;
      }

      public Component decorate(Component var1) {
         return ((ChatType)this.chatType.value()).chat().decorate(var1, this);
      }

      public Component decorateNarration(Component var1) {
         return ((ChatType)this.chatType.value()).narration().decorate(var1, this);
      }

      public Bound withTargetName(Component var1) {
         return new Bound(this.chatType, this.name, Optional.of(var1));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ChatType.STREAM_CODEC, Bound::chatType, ComponentSerialization.TRUSTED_STREAM_CODEC, Bound::name, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, Bound::targetName, Bound::new);
      }
   }
}
