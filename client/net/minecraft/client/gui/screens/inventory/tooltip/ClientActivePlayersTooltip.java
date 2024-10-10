package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;

public class ClientActivePlayersTooltip implements ClientTooltipComponent {
   private static final int SKIN_SIZE = 10;
   private static final int PADDING = 2;
   private final List<ProfileResult> activePlayers;

   public ClientActivePlayersTooltip(ClientActivePlayersTooltip.ActivePlayersTooltip var1) {
      super();
      this.activePlayers = var1.profiles();
   }

   @Override
   public int getHeight(Font var1) {
      return this.activePlayers.size() * 12 + 2;
   }

   @Override
   public int getWidth(Font var1) {
      int var2 = 0;

      for (ProfileResult var4 : this.activePlayers) {
         int var5 = var1.width(var4.profile().getName());
         if (var5 > var2) {
            var2 = var5;
         }
      }

      return var2 + 10 + 6;
   }

   @Override
   public void renderImage(Font var1, int var2, int var3, int var4, int var5, GuiGraphics var6) {
      for (int var7 = 0; var7 < this.activePlayers.size(); var7++) {
         ProfileResult var8 = this.activePlayers.get(var7);
         int var9 = var3 + 2 + var7 * 12;
         PlayerFaceRenderer.draw(var6, Minecraft.getInstance().getSkinManager().getInsecureSkin(var8.profile()), var2 + 2, var9, 10);
         var6.drawString(var1, var8.profile().getName(), var2 + 10 + 4, var9 + 2, -1);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
