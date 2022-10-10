package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;

public class ServerSelectionList extends GuiListExtended<ServerSelectionList.Entry> {
   private final GuiMultiplayer field_148200_k;
   private final List<ServerListEntryNormal> field_148198_l = Lists.newArrayList();
   private final ServerSelectionList.Entry field_148196_n = new ServerListEntryLanScan();
   private final List<ServerListEntryLanDetected> field_148199_m = Lists.newArrayList();
   private int field_148197_o = -1;

   private void func_195094_h() {
      this.func_195086_c();
      this.field_148198_l.forEach(this::func_195085_a);
      this.func_195085_a(this.field_148196_n);
      this.field_148199_m.forEach(this::func_195085_a);
   }

   public ServerSelectionList(GuiMultiplayer var1, Minecraft var2, int var3, int var4, int var5, int var6, int var7) {
      super(var2, var3, var4, var5, var6, var7);
      this.field_148200_k = var1;
   }

   public void func_148192_c(int var1) {
      this.field_148197_o = var1;
   }

   protected boolean func_148131_a(int var1) {
      return var1 == this.field_148197_o;
   }

   public int func_148193_k() {
      return this.field_148197_o;
   }

   public void func_148195_a(ServerList var1) {
      this.field_148198_l.clear();

      for(int var2 = 0; var2 < var1.func_78856_c(); ++var2) {
         this.field_148198_l.add(new ServerListEntryNormal(this.field_148200_k, var1.func_78850_a(var2)));
      }

      this.func_195094_h();
   }

   public void func_148194_a(List<LanServerInfo> var1) {
      this.field_148199_m.clear();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         LanServerInfo var3 = (LanServerInfo)var2.next();
         this.field_148199_m.add(new ServerListEntryLanDetected(this.field_148200_k, var3));
      }

      this.func_195094_h();
   }

   protected int func_148137_d() {
      return super.func_148137_d() + 30;
   }

   public int func_148139_c() {
      return super.func_148139_c() + 85;
   }

   public abstract static class Entry extends GuiListExtended.IGuiListEntry<ServerSelectionList.Entry> {
      public Entry() {
         super();
      }
   }
}
