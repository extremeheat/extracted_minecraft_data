package net.minecraft.client.network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.multiplayer.ThreadLanServerPing;

public class LanServerDetector$LanServerList {
   private ArrayList field_77555_b = new ArrayList();
   boolean field_77556_a;

   public LanServerDetector$LanServerList() {
      super();
   }

   public synchronized boolean func_77553_a() {
      return this.field_77556_a;
   }

   public synchronized void func_77552_b() {
      this.field_77556_a = false;
   }

   public synchronized List func_77554_c() {
      return Collections.unmodifiableList(this.field_77555_b);
   }

   public synchronized void func_77551_a(String var1, InetAddress var2) {
      String var3 = ThreadLanServerPing.func_77524_a(var1);
      String var4 = ThreadLanServerPing.func_77523_b(var1);
      if (var4 != null) {
         var4 = var2.getHostAddress() + ":" + var4;
         boolean var5 = false;

         for(LanServerDetector$LanServer var7 : this.field_77555_b) {
            if (var7.func_77488_b().equals(var4)) {
               var7.func_77489_c();
               var5 = true;
               break;
            }
         }

         if (!var5) {
            this.field_77555_b.add(new LanServerDetector$LanServer(var3, var4));
            this.field_77556_a = true;
         }
      }
   }
}
