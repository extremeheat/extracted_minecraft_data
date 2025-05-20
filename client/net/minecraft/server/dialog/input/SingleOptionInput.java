package net.minecraft.server.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;

public record SingleOptionInput(int width, List<Entry> entries, Component label, boolean labelVisible) implements InputControl {
   public static final MapCodec<SingleOptionInput> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(SingleOptionInput::width), ExtraCodecs.nonEmptyList(SingleOptionInput.Entry.CODEC.listOf()).fieldOf("options").forGetter(SingleOptionInput::entries), ComponentSerialization.CODEC.fieldOf("label").forGetter(SingleOptionInput::label), Codec.BOOL.optionalFieldOf("label_visible", true).forGetter(SingleOptionInput::labelVisible)).apply(var0, SingleOptionInput::new)).validate((var0) -> {
      long var1 = var0.entries.stream().filter(Entry::initial).count();
      return var1 > 1L ? DataResult.error(() -> "Multiple initial values") : DataResult.success(var0);
   });

   public SingleOptionInput(int var1, List<Entry> var2, Component var3, boolean var4) {
      super();
      this.width = var1;
      this.entries = var2;
      this.label = var3;
      this.labelVisible = var4;
   }

   public MapCodec<SingleOptionInput> mapCodec() {
      return MAP_CODEC;
   }

   public Optional<Entry> initial() {
      return this.entries.stream().filter(Entry::initial).findFirst();
   }

   public static record Entry(String id, Optional<Component> display, boolean initial) {
      public static final Codec<Entry> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("id").forGetter(Entry::id), ComponentSerialization.CODEC.optionalFieldOf("display").forGetter(Entry::display), Codec.BOOL.optionalFieldOf("initial", false).forGetter(Entry::initial)).apply(var0, Entry::new));
      public static final Codec<Entry> CODEC;

      public Entry(String var1, Optional<Component> var2, boolean var3) {
         super();
         this.id = var1;
         this.display = var2;
         this.initial = var3;
      }

      public Component displayOrDefault() {
         return (Component)this.display.orElseGet(() -> Component.literal(this.id));
      }

      static {
         CODEC = Codec.withAlternative(FULL_CODEC, Codec.STRING, (var0) -> new Entry(var0, Optional.empty(), false));
      }
   }
}
