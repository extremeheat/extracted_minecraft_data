package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public interface ClickEvent {
   Codec<ClickEvent> CODEC = ClickEvent.Action.CODEC.dispatch("action", ClickEvent::action, (var0) -> var0.codec);

   Action action();

   public static record OpenUrl(URI uri) implements ClickEvent {
      public static final MapCodec<OpenUrl> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.UNTRUSTED_URI.fieldOf("url").forGetter(OpenUrl::uri)).apply(var0, OpenUrl::new));

      public OpenUrl(URI var1) {
         super();
         this.uri = var1;
      }

      public Action action() {
         return ClickEvent.Action.OPEN_URL;
      }
   }

   public static record OpenFile(String path) implements ClickEvent {
      public static final MapCodec<OpenFile> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("path").forGetter(OpenFile::path)).apply(var0, OpenFile::new));

      public OpenFile(File var1) {
         this(var1.toString());
      }

      public OpenFile(Path var1) {
         this(var1.toFile());
      }

      public OpenFile(String var1) {
         super();
         this.path = var1;
      }

      public File file() {
         return new File(this.path);
      }

      public Action action() {
         return ClickEvent.Action.OPEN_FILE;
      }
   }

   public static record RunCommand(String command) implements ClickEvent {
      public static final MapCodec<RunCommand> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.CHAT_STRING.fieldOf("command").forGetter(RunCommand::command)).apply(var0, RunCommand::new));

      public RunCommand(String var1) {
         super();
         this.command = var1;
      }

      public Action action() {
         return ClickEvent.Action.RUN_COMMAND;
      }
   }

   public static record SuggestCommand(String command) implements ClickEvent {
      public static final MapCodec<SuggestCommand> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.CHAT_STRING.fieldOf("command").forGetter(SuggestCommand::command)).apply(var0, SuggestCommand::new));

      public SuggestCommand(String var1) {
         super();
         this.command = var1;
      }

      public Action action() {
         return ClickEvent.Action.SUGGEST_COMMAND;
      }
   }

   public static record ShowDialog(Holder<Dialog> dialog) implements ClickEvent {
      public static final MapCodec<ShowDialog> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Dialog.CODEC.fieldOf("dialog").forGetter(ShowDialog::dialog)).apply(var0, ShowDialog::new));

      public ShowDialog(Holder<Dialog> var1) {
         super();
         this.dialog = var1;
      }

      public Action action() {
         return ClickEvent.Action.SHOW_DIALOG;
      }
   }

   public static record ChangePage(int page) implements ClickEvent {
      public static final MapCodec<ChangePage> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("page").forGetter(ChangePage::page)).apply(var0, ChangePage::new));

      public ChangePage(int var1) {
         super();
         this.page = var1;
      }

      public Action action() {
         return ClickEvent.Action.CHANGE_PAGE;
      }
   }

   public static record CopyToClipboard(String value) implements ClickEvent {
      public static final MapCodec<CopyToClipboard> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("value").forGetter(CopyToClipboard::value)).apply(var0, CopyToClipboard::new));

      public CopyToClipboard(String var1) {
         super();
         this.value = var1;
      }

      public Action action() {
         return ClickEvent.Action.COPY_TO_CLIPBOARD;
      }
   }

   public static record Custom(ResourceLocation id, Optional<String> payload) implements ClickEvent {
      public static final MapCodec<Custom> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(Custom::id), Codec.STRING.optionalFieldOf("payload").forGetter(Custom::payload)).apply(var0, Custom::new));

      public Custom(ResourceLocation var1, Optional<String> var2) {
         super();
         this.id = var1;
         this.payload = var2;
      }

      public Action action() {
         return ClickEvent.Action.CUSTOM;
      }
   }

   public static enum Action implements StringRepresentable {
      OPEN_URL("open_url", true, ClickEvent.OpenUrl.CODEC),
      OPEN_FILE("open_file", false, ClickEvent.OpenFile.CODEC),
      RUN_COMMAND("run_command", true, ClickEvent.RunCommand.CODEC),
      SUGGEST_COMMAND("suggest_command", true, ClickEvent.SuggestCommand.CODEC),
      SHOW_DIALOG("show_dialog", true, ClickEvent.ShowDialog.CODEC),
      CHANGE_PAGE("change_page", true, ClickEvent.ChangePage.CODEC),
      COPY_TO_CLIPBOARD("copy_to_clipboard", true, ClickEvent.CopyToClipboard.CODEC),
      CUSTOM("custom", true, ClickEvent.Custom.CODEC);

      public static final Codec<Action> UNSAFE_CODEC = StringRepresentable.<Action>fromEnum(Action::values);
      public static final Codec<Action> CODEC = UNSAFE_CODEC.validate(Action::filterForSerialization);
      private final boolean allowFromServer;
      private final String name;
      final MapCodec<? extends ClickEvent> codec;

      private Action(final String var3, final boolean var4, final MapCodec<? extends ClickEvent> var5) {
         this.name = var3;
         this.allowFromServer = var4;
         this.codec = var5;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static DataResult<Action> filterForSerialization(Action var0) {
         return !var0.isAllowedFromServer() ? DataResult.error(() -> "Click event type not allowed: " + String.valueOf(var0)) : DataResult.success(var0, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, SHOW_DIALOG, CHANGE_PAGE, COPY_TO_CLIPBOARD, CUSTOM};
      }
   }
}
