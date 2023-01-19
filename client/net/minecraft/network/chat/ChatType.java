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

public record ChatType(Optional<ChatType.TextDisplay> j, Optional<ChatType.TextDisplay> k, Optional<ChatType.Narration> l) {
   private final Optional<ChatType.TextDisplay> chat;
   private final Optional<ChatType.TextDisplay> overlay;
   private final Optional<ChatType.Narration> narration;
   public static final Codec<ChatType> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ChatType.TextDisplay.CODEC.optionalFieldOf("chat").forGetter(ChatType::chat),
               ChatType.TextDisplay.CODEC.optionalFieldOf("overlay").forGetter(ChatType::overlay),
               ChatType.Narration.CODEC.optionalFieldOf("narration").forGetter(ChatType::narration)
            )
            .apply(var0, ChatType::new)
   );
   public static final ResourceKey<ChatType> CHAT = create("chat");
   public static final ResourceKey<ChatType> SYSTEM = create("system");
   public static final ResourceKey<ChatType> GAME_INFO = create("game_info");
   public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
   public static final ResourceKey<ChatType> MSG_COMMAND = create("msg_command");
   public static final ResourceKey<ChatType> TEAM_MSG_COMMAND = create("team_msg_command");
   public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");
   public static final ResourceKey<ChatType> TELLRAW_COMMAND = create("tellraw_command");

   public ChatType(Optional<ChatType.TextDisplay> var1, Optional<ChatType.TextDisplay> var2, Optional<ChatType.Narration> var3) {
      super();
      this.chat = var1;
      this.overlay = var2;
      this.narration = var3;
   }

   private static ResourceKey<ChatType> create(String var0) {
      return ResourceKey.create(Registry.CHAT_TYPE_REGISTRY, new ResourceLocation(var0));
   }

   public static Holder<ChatType> bootstrap(Registry<ChatType> var0) {
      BuiltinRegistries.register(
         var0,
         CHAT,
         new ChatType(
            Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.text"))),
            Optional.empty(),
            Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))
         )
      );
      BuiltinRegistries.register(
         var0,
         SYSTEM,
         new ChatType(
            Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty(), Optional.of(ChatType.Narration.undecorated(ChatType.Narration.Priority.SYSTEM))
         )
      );
      BuiltinRegistries.register(var0, GAME_INFO, new ChatType(Optional.empty(), Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty()));
      BuiltinRegistries.register(
         var0,
         SAY_COMMAND,
         new ChatType(
            Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.announcement"))),
            Optional.empty(),
            Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))
         )
      );
      BuiltinRegistries.register(
         var0,
         MSG_COMMAND,
         new ChatType(
            Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.directMessage("commands.message.display.incoming"))),
            Optional.empty(),
            Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))
         )
      );
      BuiltinRegistries.register(
         var0,
         TEAM_MSG_COMMAND,
         new ChatType(
            Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.teamMessage("chat.type.team.text"))),
            Optional.empty(),
            Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.text.narrate"), ChatType.Narration.Priority.CHAT))
         )
      );
      BuiltinRegistries.register(
         var0,
         EMOTE_COMMAND,
         new ChatType(
            Optional.of(ChatType.TextDisplay.decorated(ChatDecoration.withSender("chat.type.emote"))),
            Optional.empty(),
            Optional.of(ChatType.Narration.decorated(ChatDecoration.withSender("chat.type.emote"), ChatType.Narration.Priority.CHAT))
         )
      );
      return BuiltinRegistries.register(
         var0,
         TELLRAW_COMMAND,
         new ChatType(
            Optional.of(ChatType.TextDisplay.undecorated()), Optional.empty(), Optional.of(ChatType.Narration.undecorated(ChatType.Narration.Priority.CHAT))
         )
      );
   }

   public static record Narration(Optional<ChatDecoration> b, ChatType.Narration.Priority c) {
      private final Optional<ChatDecoration> decoration;
      private final ChatType.Narration.Priority priority;
      public static final Codec<ChatType.Narration> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(ChatType.Narration::decoration),
                  ChatType.Narration.Priority.CODEC.fieldOf("priority").forGetter(ChatType.Narration::priority)
               )
               .apply(var0, ChatType.Narration::new)
      );

      public Narration(Optional<ChatDecoration> var1, ChatType.Narration.Priority var2) {
         super();
         this.decoration = var1;
         this.priority = var2;
      }

      public static ChatType.Narration undecorated(ChatType.Narration.Priority var0) {
         return new ChatType.Narration(Optional.empty(), var0);
      }

      public static ChatType.Narration decorated(ChatDecoration var0, ChatType.Narration.Priority var1) {
         return new ChatType.Narration(Optional.of(var0), var1);
      }

      public Component decorate(Component var1, @Nullable ChatSender var2) {
         return this.decoration.<Component>map(var2x -> var2x.decorate(var1, var2)).orElse(var1);
      }

      public static enum Priority implements StringRepresentable {
         CHAT("chat", false),
         SYSTEM("system", true);

         public static final Codec<ChatType.Narration.Priority> CODEC = StringRepresentable.fromEnum(ChatType.Narration.Priority::values);
         private final String name;
         private final boolean interrupts;

         private Priority(String var3, boolean var4) {
            this.name = var3;
            this.interrupts = var4;
         }

         public boolean interrupts() {
            return this.interrupts;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }
   }

   public static record TextDisplay(Optional<ChatDecoration> b) {
      private final Optional<ChatDecoration> decoration;
      public static final Codec<ChatType.TextDisplay> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(ChatDecoration.CODEC.optionalFieldOf("decoration").forGetter(ChatType.TextDisplay::decoration))
               .apply(var0, ChatType.TextDisplay::new)
      );

      public TextDisplay(Optional<ChatDecoration> var1) {
         super();
         this.decoration = var1;
      }

      public static ChatType.TextDisplay undecorated() {
         return new ChatType.TextDisplay(Optional.empty());
      }

      public static ChatType.TextDisplay decorated(ChatDecoration var0) {
         return new ChatType.TextDisplay(Optional.of(var0));
      }

      public Component decorate(Component var1, @Nullable ChatSender var2) {
         return this.decoration.<Component>map(var2x -> var2x.decorate(var1, var2)).orElse(var1);
      }
   }
}
