package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;

public class PlayerListComponent extends JList<String> implements ITickable {
   private final MinecraftServer field_120015_a;
   private int field_120014_b;

   public PlayerListComponent(MinecraftServer var1) {
      super();
      this.field_120015_a = var1;
      var1.func_82010_a(this);
   }

   public void func_73660_a() {
      if (this.field_120014_b++ % 20 == 0) {
         Vector var1 = new Vector();

         for(int var2 = 0; var2 < this.field_120015_a.func_184103_al().func_181057_v().size(); ++var2) {
            var1.add(((EntityPlayerMP)this.field_120015_a.func_184103_al().func_181057_v().get(var2)).func_146103_bH().getName());
         }

         this.setListData(var1);
      }

   }
}
