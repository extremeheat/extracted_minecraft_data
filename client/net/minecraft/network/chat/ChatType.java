package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ChatType(ChatTypeDecoration j, ChatTypeDecoration k) {
   private final ChatTypeDecoration chat;
   private final ChatTypeDecoration narration;
   public static final Codec<ChatType> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ChatTypeDecoration.CODEC.fieldOf("chat").forGetter(ChatType::chat),
               ChatTypeDecoration.CODEC.fieldOf("narration").forGetter(ChatType::narration)
            )
            .apply(var0, ChatType::new)
   );
   public static final ChatTypeDecoration DEFAULT_CHAT_DECORATION = ChatTypeDecoration.withSender("chat.type.text");
   public static final ResourceKey<ChatType> CHAT = create("chat");
   public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
   public static final ResourceKey<ChatType> MSG_COMMAND_INCOMING = create("msg_command_incoming");
   public static final ResourceKey<ChatType> MSG_COMMAND_OUTGOING = create("msg_command_outgoing");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_INCOMING = create("team_msg_command_incoming");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_OUTGOING = create("team_msg_command_outgoing");
   public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");

   public ChatType(ChatTypeDecoration var1, ChatTypeDecoration var2) {
      super();
      this.chat = var1;
      this.narration = var2;
   }

   private static ResourceKey<ChatType> create(String var0) {
      return ResourceKey.create(Registry.CHAT_TYPE_REGISTRY, new ResourceLocation(var0));
   }

   public static Holder<ChatType> bootstrap(Registry<ChatType> var0) {
      BuiltinRegistries.register(var0, CHAT, new ChatType(DEFAULT_CHAT_DECORATION, ChatTypeDecoration.withSender("chat.type.text.narrate")));
      BuiltinRegistries.register(
         var0, SAY_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.announcement"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      BuiltinRegistries.register(
         var0,
         MSG_COMMAND_INCOMING,
         new ChatType(ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      BuiltinRegistries.register(
         var0,
         MSG_COMMAND_OUTGOING,
         new ChatType(ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      BuiltinRegistries.register(
         var0,
         TEAM_MSG_COMMAND_INCOMING,
         new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.text"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      BuiltinRegistries.register(
         var0,
         TEAM_MSG_COMMAND_OUTGOING,
         new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.sent"), ChatTypeDecoration.withSender("chat.type.text.narrate"))
      );
      return BuiltinRegistries.register(
         var0, EMOTE_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.emote"), ChatTypeDecoration.withSender("chat.type.emote"))
      );
   }

   public static ChatType.Bound bind(ResourceKey<ChatType> var0, Entity var1) {
      return bind(var0, var1.level.registryAccess(), var1.getDisplayName());
   }

   public static ChatType.Bound bind(ResourceKey<ChatType> var0, CommandSourceStack var1) {
      return bind(var0, var1.registryAccess(), var1.getDisplayName());
   }

   public static ChatType.Bound bind(ResourceKey<ChatType> var0, RegistryAccess var1, Component var2) {
      Registry var3 = var1.registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
      return ((ChatType)var3.getOrThrow(var0)).bind(var2);
   }

   public ChatType.Bound bind(Component var1) {
      return new ChatType.Bound(this, var1);
   }

   public static record Bound(ChatType a, Component b, @Nullable Component c) {
      private final ChatType chatType;
      private final Component name;
      @Nullable
      private final Component targetName;

      Bound(ChatType var1, Component var2) {
         this(var1, var2, null);
      }

      public Bound(ChatType var1, Component var2, @Nullable Component var3) {
         super();
         this.chatType = var1;
         this.name = var2;
         this.targetName = var3;
      }

      public Component decorate(Component var1) {
         return this.chatType.chat().decorate(var1, this);
      }

      public Component decorateNarration(Component var1) {
         return this.chatType.narration().decorate(var1, this);
      }

      public ChatType.Bound withTargetName(Component var1) {
         return new ChatType.Bound(this.chatType, this.name, var1);
      }

      public ChatType.BoundNetwork toNetwork(RegistryAccess var1) {
         Registry var2 = var1.registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
         return new ChatType.BoundNetwork(var2.getId(this.chatType), this.name, this.targetName);
      }
   }

   public static record BoundNetwork(int a, Component b, @Nullable Component c) {
      private final int chatType;
      private final Component name;
      @Nullable
      private final Component targetName;

      public BoundNetwork(FriendlyByteBuf var1) {
         this(var1.readVarInt(), var1.readComponent(), var1.readNullable(FriendlyByteBuf::readComponent));
      }

      public BoundNetwork(int var1, Component var2, @Nullable Component var3) {
         super();
         this.chatType = var1;
         this.name = var2;
         this.targetName = var3;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.chatType);
         var1.writeComponent(this.name);
         var1.writeNullable(this.targetName, FriendlyByteBuf::writeComponent);
      }

      public Optional<ChatType.Bound> resolve(RegistryAccess var1) {
         Registry var2 = var1.registryOrThrow(Registry.CHAT_TYPE_REGISTRY);
         ChatType var3 = (ChatType)var2.byId(this.chatType);
         return Optional.ofNullable(var3).map(var1x -> new ChatType.Bound(var1x, this.name, this.targetName));
      }
   }
}
