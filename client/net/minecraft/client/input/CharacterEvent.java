package net.minecraft.client.input;

import net.minecraft.util.StringUtil;

public record CharacterEvent(int codepoint, @InputWithModifiers.Modifiers int modifiers) {
   public CharacterEvent(int var1, @InputWithModifiers.Modifiers int var2) {
      super();
      this.codepoint = var1;
      this.modifiers = var2;
   }

   public String codepointAsString() {
      return Character.toString(this.codepoint);
   }

   public boolean isAllowedChatCharacter() {
      return StringUtil.isAllowedChatCharacter(this.codepoint);
   }
}
