package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject {
   private static final Ordering<NetworkPlayerInfo> field_178674_a = Ordering.from(new Comparator<NetworkPlayerInfo>() {
      public int compare(NetworkPlayerInfo var1, NetworkPlayerInfo var2) {
         return ComparisonChain.start().compare(var1.func_178845_a().getId(), var2.func_178845_a().getId()).result();
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((NetworkPlayerInfo)var1, (NetworkPlayerInfo)var2);
      }
   });
   private final List<ISpectatorMenuObject> field_178673_b;

   public TeleportToPlayer() {
      this(field_178674_a.sortedCopy(Minecraft.func_71410_x().func_147114_u().func_175106_d()));
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> var1) {
      super();
      this.field_178673_b = Lists.newArrayList();
      Iterator var2 = field_178674_a.sortedCopy(var1).iterator();

      while(var2.hasNext()) {
         NetworkPlayerInfo var3 = (NetworkPlayerInfo)var2.next();
         if (var3.func_178848_b() != WorldSettings.GameType.SPECTATOR) {
            this.field_178673_b.add(new PlayerMenuObject(var3.func_178845_a()));
         }
      }

   }

   public List<ISpectatorMenuObject> func_178669_a() {
      return this.field_178673_b;
   }

   public IChatComponent func_178670_b() {
      return new ChatComponentText("Select a player to teleport to");
   }

   public void func_178661_a(SpectatorMenu var1) {
      var1.func_178647_a(this);
   }

   public IChatComponent func_178664_z_() {
      return new ChatComponentText("Teleport to player");
   }

   public void func_178663_a(float var1, int var2) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiSpectator.field_175269_a);
      Gui.func_146110_a(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   public boolean func_178662_A_() {
      return !this.field_178673_b.isEmpty();
   }
}
