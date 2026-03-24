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
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public interface ClickEvent {
   Codec<ClickEvent> CODEC = ClickEvent.Action.CODEC.dispatch("action", ClickEvent::action, (action) -> action.codec);

   Action action();

   public static record OpenUrl(URI uri) implements ClickEvent {
      public static final MapCodec<OpenUrl> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.UNTRUSTED_URI.fieldOf("url").forGetter(OpenUrl::uri)).apply(i, OpenUrl::new));

      public OpenUrl {
         super();
      }

      public Action action() {
         return ClickEvent.Action.OPEN_URL;
      }
   }

   public static record OpenFile(String path) implements ClickEvent {
      public static final MapCodec<OpenFile> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("path").forGetter(OpenFile::path)).apply(i, OpenFile::new));

      public OpenFile(final File file) {
         this(file.toString());
      }

      public OpenFile(final Path path) {
         this(path.toFile());
      }

      public OpenFile {
         super();
      }

      public File file() {
         return new File(this.path);
      }

      public Action action() {
         return ClickEvent.Action.OPEN_FILE;
      }
   }

   public static record RunCommand(String command) implements ClickEvent {
      public static final MapCodec<RunCommand> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.CHAT_STRING.fieldOf("command").forGetter(RunCommand::command)).apply(i, RunCommand::new));

      public RunCommand {
         super();
      }

      public Action action() {
         return ClickEvent.Action.RUN_COMMAND;
      }
   }

   public static record SuggestCommand(String command) implements ClickEvent {
      public static final MapCodec<SuggestCommand> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.CHAT_STRING.fieldOf("command").forGetter(SuggestCommand::command)).apply(i, SuggestCommand::new));

      public SuggestCommand {
         super();
      }

      public Action action() {
         return ClickEvent.Action.SUGGEST_COMMAND;
      }
   }

   public static record ShowDialog(Holder<Dialog> dialog) implements ClickEvent {
      public static final MapCodec<ShowDialog> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Dialog.CODEC.fieldOf("dialog").forGetter(ShowDialog::dialog)).apply(i, ShowDialog::new));

      public ShowDialog {
         super();
      }

      public Action action() {
         return ClickEvent.Action.SHOW_DIALOG;
      }
   }

   public static record ChangePage(int page) implements ClickEvent {
      public static final MapCodec<ChangePage> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.POSITIVE_INT.fieldOf("page").forGetter(ChangePage::page)).apply(i, ChangePage::new));

      public ChangePage {
         super();
      }

      public Action action() {
         return ClickEvent.Action.CHANGE_PAGE;
      }
   }

   public static record CopyToClipboard(String value) implements ClickEvent {
      public static final MapCodec<CopyToClipboard> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("value").forGetter(CopyToClipboard::value)).apply(i, CopyToClipboard::new));

      public CopyToClipboard {
         super();
      }

      public Action action() {
         return ClickEvent.Action.COPY_TO_CLIPBOARD;
      }
   }

   public static record Custom(Identifier id, Optional<Tag> payload) implements ClickEvent {
      public static final MapCodec<Custom> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(Custom::id), ExtraCodecs.NBT.optionalFieldOf("payload").forGetter(Custom::payload)).apply(i, Custom::new));

      public Custom {
         super();
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
      private final MapCodec<? extends ClickEvent> codec;

      private Action(final String name, final boolean allowFromServer, final MapCodec<? extends ClickEvent> codec) {
         this.name = name;
         this.allowFromServer = allowFromServer;
         this.codec = codec;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getSerializedName() {
         return this.name;
      }

      public MapCodec<? extends ClickEvent> valueCodec() {
         return this.codec;
      }

      public static DataResult<Action> filterForSerialization(final Action action) {
         return !action.isAllowedFromServer() ? DataResult.error(() -> "Click event type not allowed: " + String.valueOf(action)) : DataResult.success(action, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, SHOW_DIALOG, CHANGE_PAGE, COPY_TO_CLIPBOARD, CUSTOM};
      }
   }
}
