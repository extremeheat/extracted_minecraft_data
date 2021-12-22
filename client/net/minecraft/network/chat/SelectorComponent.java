package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorComponent extends BaseComponent implements ContextAwareComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String pattern;
   @Nullable
   private final EntitySelector selector;
   protected final Optional<Component> separator;

   public SelectorComponent(String var1, Optional<Component> var2) {
      super();
      this.pattern = var1;
      this.separator = var2;
      EntitySelector var3 = null;

      try {
         EntitySelectorParser var4 = new EntitySelectorParser(new StringReader(var1));
         var3 = var4.parse();
      } catch (CommandSyntaxException var5) {
         LOGGER.warn("Invalid selector component: {}: {}", var1, var5.getMessage());
      }

      this.selector = var3;
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

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 != null && this.selector != null) {
         Optional var4 = ComponentUtils.updateForEntity(var1, this.separator, var2, var3);
         return ComponentUtils.formatList(this.selector.findEntities(var1), (Optional)var4, Entity::getDisplayName);
      } else {
         return new TextComponent("");
      }
   }

   public String getContents() {
      return this.pattern;
   }

   public SelectorComponent plainCopy() {
      return new SelectorComponent(this.pattern, this.separator);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SelectorComponent)) {
         return false;
      } else {
         SelectorComponent var2 = (SelectorComponent)var1;
         return this.pattern.equals(var2.pattern) && super.equals(var1);
      }
   }

   public String toString() {
      String var10000 = this.pattern;
      return "SelectorComponent{pattern='" + var10000 + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }

   // $FF: synthetic method
   public BaseComponent plainCopy() {
      return this.plainCopy();
   }

   // $FF: synthetic method
   public MutableComponent plainCopy() {
      return this.plainCopy();
   }
}
