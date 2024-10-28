package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.SelectorPattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;

public record SelectorContents(SelectorPattern selector, Optional<Component> separator) implements ComponentContents {
   public static final MapCodec<SelectorContents> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SelectorPattern.CODEC.fieldOf("selector").forGetter(SelectorContents::selector), ComponentSerialization.CODEC.optionalFieldOf("separator").forGetter(SelectorContents::separator)).apply(var0, SelectorContents::new);
   });
   public static final ComponentContents.Type<SelectorContents> TYPE;

   public SelectorContents(SelectorPattern var1, Optional<Component> var2) {
      super();
      this.selector = var1;
      this.separator = var2;
   }

   public ComponentContents.Type<?> type() {
      return TYPE;
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 == null) {
         return Component.empty();
      } else {
         Optional var4 = ComponentUtils.updateForEntity(var1, this.separator, var2, var3);
         return ComponentUtils.formatList(this.selector.resolved().findEntities(var1), (Optional)var4, Entity::getDisplayName);
      }
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return var1.accept(var2, this.selector.pattern());
   }

   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return var1.accept(this.selector.pattern());
   }

   public String toString() {
      return "pattern{" + String.valueOf(this.selector) + "}";
   }

   public SelectorPattern selector() {
      return this.selector;
   }

   public Optional<Component> separator() {
      return this.separator;
   }

   static {
      TYPE = new ComponentContents.Type(CODEC, "selector");
   }
}
