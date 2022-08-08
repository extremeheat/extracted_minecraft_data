package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public record ChatType(Optional<TextDisplay> j, Optional<TextDisplay> k, Optional<Narration> l) {
   private final Optional<TextDisplay> chat;
   private final Optional<TextDisplay> overlay;
   private final Optional<Narration> narration;
   public static final Codec<ChatType> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ChatType.TextDisplay.CODEC.optionalFieldOf("chat").forGetter(ChatType::chat), ChatType.TextDisplay.CODEC.optionalFieldOf("overlay").forGetter(ChatType::overlay), ChatType.Narration.CODEC.optionalFieldOf("narration").forGetter(ChatType::narration)).apply(var0, ChatType::new);
   });
   public static final ResourceKey<ChatType> CHAT = create("chat");
   public static final ResourceKey<ChatType> SYSTEM = create("system");
   public static final ResourceKey<ChatType> GAME_INFO = create("game_info");
   public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
   public static final ResourceKey<ChatType> MSG_COMMAND = create("msg_command");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND = create("team_msg_command");
   public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");
   public static final ResourceKey<ChatType> TELLRAW_COMMAND = create("tellraw_command");

   public ChatType(Optional<TextDisplay> var1, Optional<TextDisplay> var2, Optional<Narration> var3) {
      super();
      this.chat = var1;
      this.overlay = var2;
      this.narration = var3;
   }

   private static ResourceKey<ChatType> create(String var0) {
      return ResourceKey.create(Registry.CHAT_TYPE_REGISTRY, new ResourceLocation(var0));
   }

   public static Holder<ChatType> bootstrap(Registry<ChatType> var0) {
      BuiltinRegistries.register(var0, (ResourceKey)CHAT, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.text"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(var0, (ResourceKey)SYSTEM, new ChatType(Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty(), Optional.of(ChatType.Narration.undecorated(ChatType.Narration.Priority.SYSTEM))));
      BuiltinRegistries.register(var0, (ResourceKey)GAME_INFO, new ChatType(Optional.empty(), Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty()));
      BuiltinRegistries.register(var0, (ResourceKey)SAY_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.announcement"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(var0, (ResourceKey)MSG_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.directMessage("commands.message.display.incoming"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(var0, (ResourceKey)TEAM_MSG_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.teamMessage("chat.type.team.text"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))));
      BuiltinRegistries.register(var0, (ResourceKey)EMOTE_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.emote"))), Optional.empty(), Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.emote"), ChatType.Narration.Priority.CHAT))));
      return BuiltinRegistries.register(var0, (ResourceKey)TELLRAW_COMMAND, new ChatType(Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty(), Optional.of(ChatType.Narration.undecorated(ChatType.Narration.Priority.CHAT))));
   }

   public Optional<TextDisplay> chat() {
      return this.chat;
   }

   public Optional<TextDisplay> overlay() {
      return this.overlay;
   }

   public Optional<Narration> narration() {
      return this.narration;
   }

   public static record TextDisplay(Optional<ChatDecoration> b) {
      private final Optional<ChatDecoration> decoration;
      public static final Codec<TextDisplay> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(TextDisplay::decoration)).apply(var0, TextDisplay::new);
      });

      public TextDisplay(Optional<ChatDecoration> var1) {
         super();
         this.decoration = var1;
      }

      public static TextDisplay undecorated() {
         return new TextDisplay(Optional.empty());
      }

      public static TextDisplay decorated(ChatDecoration var0) {
         return new TextDisplay(Optional.of(var0));
      }

      public Component decorate(Component var1, @Nullable ChatSender var2) {
         return (Component)this.decoration.map((var2x) -> {
            return var2x.decorate(var1, var2);
         }).orElse(var1);
      }

      public Optional<ChatDecoration> decoration() {
         return this.decoration;
      }
   }

   public static record Narration(Optional<ChatDecoration> b, Priority c) {
      private final Optional<ChatDecoration> decoration;
      private final Priority priority;
      public static final Codec<Narration> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(Narration::decoration), ChatType.Narration.Priority.CODEC.fieldOf("priority").forGetter(Narration::priority)).apply(var0, Narration::new);
      });

      public Narration(Optional<ChatDecoration> var1, Priority var2) {
         super();
         this.decoration = var1;
         this.priority = var2;
      }

      public static Narration undecorated(Priority var0) {
         return new Narration(Optional.empty(), var0);
      }

      public static Narration decorated(ChatDecoration var0, Priority var1) {
         return new Narration(Optional.of(var0), var1);
      }

      public Component decorate(Component var1, @Nullable ChatSender var2) {
         return (Component)this.decoration.map((var2x) -> {
            return var2x.decorate(var1, var2);
         }).orElse(var1);
      }

      public Optional<ChatDecoration> decoration() {
         return this.decoration;
      }

      public Priority priority() {
         return this.priority;
      }

      public static enum Priority implements StringRepresentable {
         CHAT("chat", false),
         SYSTEM("system", true);

         public static final Codec<Priority> CODEC = StringRepresentable.fromEnum(Priority::values);
         private final String name;
         private final boolean interrupts;

         private Priority(String var3, boolean var4) {
            this.name = var3;
            this.interrupts = var4;
         }

         public boolean interrupts() {
            return this.interrupts;
         }

         public String getSerializedName() {
            return this.name;
         }

         // $FF: synthetic method
         private static Priority[] $values() {
            return new Priority[]{CHAT, SYSTEM};
         }
      }
   }
}
