package net.minecraft.network.chat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

   public SelectorComponent(String var1) {
      super();
      this.pattern = var1;
      EntitySelector var2 = null;

      try {
         EntitySelectorParser var3 = new EntitySelectorParser(new StringReader(var1));
         var2 = var3.parse();
      } catch (CommandSyntaxException var4) {
         LOGGER.warn("Invalid selector component: {}", var1, var4.getMessage());
      }

      this.selector = var2;
   }

   public String getPattern() {
      return this.pattern;
   }

   public Component resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      return (Component)(var1 != null && this.selector != null ? EntitySelector.joinNames(this.selector.findEntities(var1)) : new TextComponent(""));
   }

   public String getContents() {
      return this.pattern;
   }

   public SelectorComponent copy() {
      return new SelectorComponent(this.pattern);
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
      return "SelectorComponent{pattern='" + this.pattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
