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
   public static final Codec<ChatType> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ChatTypeDecoration.CODEC.fieldOf("chat").forGetter(ChatType::chat), ChatTypeDecoration.CODEC.fieldOf("narration").forGetter(ChatType::narration)
            )
            .apply(var0, ChatType::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ChatType> DIRECT_STREAM_CODEC = StreamCodec.composite(
      ChatTypeDecoration.STREAM_CODEC, ChatType::chat, ChatTypeDecoration.STREAM_CODEC, ChatType::narration, ChatType::new
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ChatType>> STREAM_CODEC = ByteBufCodecs.holder(Registries.CHAT_TYPE, DIRECT_STREAM_CODEC);
   public static final ChatTypeDecoration DEFAULT_CHAT_DECORATION = ChatTypeDecoration.withSender("chat.type.text");
   public static final ResourceKey<ChatType> CHAT = create("chat");
   public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
   public static final ResourceKey<ChatType> MSG_COMMAND_INCOMING = create("msg_command_incoming");
   public static final ResourceKey<ChatType> MSG_COMMAND_OUTGOING = create("msg_command_outgoing");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_INCOMING = create("team_msg_command_incoming");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_OUTGOING = create("team_msg_command_outgoing");
   public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");

   public ChatType(ChatTypeDecoration chat, ChatTypeDecoration narration) {
      super();
      this.chat = chat;
      this.narration = narration;
   }

   private static ResourceKey<ChatType> create(String var0) {
      return ResourceKey.create(Registries.CHAT_TYPE, new ResourceLocation(var0));
   }

   public static void bootstrap(BootstrapContext<ChatType> var0) {
      var0.register(CHAT, new ChatType(DEFAULT_CHAT_DECORATION, ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(SAY_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.announcement"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
      var0.register(
         MSG_COMMAND_INCOMING,
         new ChatType(ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      var0.register(
         MSG_COMMAND_OUTGOING,
         new ChatType(ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      var0.register(
         TEAM_MSG_COMMAND_INCOMING,
         new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.text"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      var0.register(
         TEAM_MSG_COMMAND_OUTGOING,
         new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.sent"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      var0.register(EMOTE_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.emote"), ChatTypeDecoration.withSender("chat.type.emote")));
   }

   public static ChatType.Bound bind(ResourceKey<ChatType> var0, Entity var1) {
      return bind(var0, var1.level().registryAccess(), var1.getDisplayName());
   }

   public static ChatType.Bound bind(ResourceKey<ChatType> var0, CommandSourceStack var1) {
      return bind(var0, var1.registryAccess(), var1.getDisplayName());
   }

   public static ChatType.Bound bind(ResourceKey<ChatType> var0, RegistryAccess var1, Component var2) {
      Registry var3 = var1.registryOrThrow(Registries.CHAT_TYPE);
      return new ChatType.Bound(var3.getHolderOrThrow(var0), var2);
   }

   public static record Bound(Holder<ChatType> chatType, Component name, Optional<Component> targetName) {
      public static final StreamCodec<RegistryFriendlyByteBuf, ChatType.Bound> STREAM_CODEC = StreamCodec.composite(
         ChatType.STREAM_CODEC,
         ChatType.Bound::chatType,
         ComponentSerialization.TRUSTED_STREAM_CODEC,
         ChatType.Bound::name,
         ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC,
         ChatType.Bound::targetName,
         ChatType.Bound::new
      );

      Bound(Holder<ChatType> var1, Component var2) {
         this(var1, var2, Optional.empty());
      }

      public Bound(Holder<ChatType> chatType, Component name, Optional<Component> targetName) {
         super();
         this.chatType = chatType;
         this.name = name;
         this.targetName = targetName;
      }

      public Component decorate(Component var1) {
         return this.chatType.value().chat().decorate(var1, this);
      }

      public Component decorateNarration(Component var1) {
         return this.chatType.value().narration().decorate(var1, this);
      }

      public ChatType.Bound withTargetName(Component var1) {
         return new ChatType.Bound(this.chatType, this.name, Optional.of(var1));
      }
   }
}
