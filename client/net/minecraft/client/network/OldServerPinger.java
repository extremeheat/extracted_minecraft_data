package net.minecraft.client.network;

import com.google.common.base.Splitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
   private static final Splitter field_147230_a = Splitter.on('\u0000').limit(6);
   private static final Logger field_147228_b = LogManager.getLogger();
   private final List field_147229_c = Collections.synchronizedList(new ArrayList());

   public OldServerPinger() {
      super();
   }

   public void func_147224_a(ServerData var1) {
      ServerAddress var2 = ServerAddress.func_78860_a(var1.field_78845_b);
      NetworkManager var3 = NetworkManager.func_150726_a(InetAddress.getByName(var2.func_78861_a()), var2.func_78864_b());
      this.field_147229_c.add(var3);
      var1.field_78843_d = "Pinging...";
      var1.field_78844_e = -1L;
      var1.field_147412_i = null;
      var3.func_150719_a(new OldServerPinger$1(this, var1, var3));

      try {
         var3.func_150725_a(new C00Handshake(5, var2.func_78861_a(), var2.func_78864_b(), EnumConnectionState.STATUS));
         var3.func_150725_a(new C00PacketServerQuery());
      } catch (Throwable var5) {
         field_147228_b.error(var5);
      }
   }

   private void func_147225_b(ServerData var1) {
      ServerAddress var2 = ServerAddress.func_78860_a(var1.field_78845_b);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(NetworkManager.field_150734_f)).handler(new OldServerPinger$2(this, var2, var1)))
            .channel(NioSocketChannel.class))
         .connect(var2.func_78861_a(), var2.func_78864_b());
   }

   public void func_147223_a() {
      synchronized(this.field_147229_c) {
         Iterator var2 = this.field_147229_c.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.func_150724_d()) {
               var3.func_74428_b();
            } else {
               var2.remove();
               if (var3.func_150730_f() != null) {
                  var3.func_150729_e().func_147231_a(var3.func_150730_f());
               }
            }
         }
      }
   }

   public void func_147226_b() {
      synchronized(this.field_147229_c) {
         Iterator var2 = this.field_147229_c.iterator();

         while(var2.hasNext()) {
            NetworkManager var3 = (NetworkManager)var2.next();
            if (var3.func_150724_d()) {
               var2.remove();
               var3.func_150718_a(new ChatComponentText("Cancelled"));
            }
         }
      }
   }
}
