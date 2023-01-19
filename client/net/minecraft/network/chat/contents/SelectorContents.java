package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class SelectorContents implements ComponentContents {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String pattern;
   @Nullable
   private final EntitySelector selector;
   protected final Optional<Component> separator;

   public SelectorContents(String var1, Optional<Component> var2) {
      super();
      this.pattern = var1;
      this.separator = var2;
      this.selector = parseSelector(var1);
   }

   @Nullable
   private static EntitySelector parseSelector(String var0) {
      EntitySelector var1 = null;

      try {
         EntitySelectorParser var2 = new EntitySelectorParser(new StringReader(var0));
         var1 = var2.parse();
      } catch (CommandSyntaxException var3) {
         LOGGER.warn("Invalid selector component: {}: {}", var0, var3.getMessage());
      }

      return var1;
   }

   public String getPattern() {
      return this.pattern;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   public Optional<Component> getSeparator() {
      return this.separator;
   }

   @Override
   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 != null && this.selector != null) {
         Optional var4 = ComponentUtils.updateForEntity(var1, this.separator, var2, var3);
         return ComponentUtils.formatList(this.selector.findEntities(var1), var4, Entity::getDisplayName);
      } else {
         return Component.empty();
      }
   }

   @Override
   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return var1.accept(var2, this.pattern);
   }

   @Override
   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return var1.accept(this.pattern);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof SelectorContents var2 && this.pattern.equals(var2.pattern) && this.separator.equals(var2.separator)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.pattern.hashCode();
      return 31 * var1 + this.separator.hashCode();
   }

   @Override
   public String toString() {
      return "pattern{" + this.pattern + "}";
   }
}
