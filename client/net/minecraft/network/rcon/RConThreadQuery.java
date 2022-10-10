package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.Util;

public class RConThreadQuery extends RConThreadBase {
   private long field_72629_g;
   private int field_72636_h;
   private final int field_72637_i;
   private final int field_72634_j;
   private final String field_72635_k;
   private final String field_72632_l;
   private DatagramSocket field_72633_m;
   private final byte[] field_72630_n = new byte[1460];
   private DatagramPacket field_72631_o;
   private final Map<SocketAddress, String> field_72644_p;
   private String field_72643_q;
   private String field_72642_r;
   private final Map<SocketAddress, RConThreadQuery.Auth> field_72641_s;
   private final long field_72640_t;
   private final RConOutputStream field_72639_u;
   private long field_72638_v;

   public RConThreadQuery(IServer var1) {
      super(var1, "Query Listener");
      this.field_72636_h = var1.func_71327_a("query.port", 0);
      this.field_72642_r = var1.func_71277_t();
      this.field_72637_i = var1.func_71234_u();
      this.field_72635_k = var1.func_71274_v();
      this.field_72634_j = var1.func_71275_y();
      this.field_72632_l = var1.func_71270_I();
      this.field_72638_v = 0L;
      this.field_72643_q = "0.0.0.0";
      if (!this.field_72642_r.isEmpty() && !this.field_72643_q.equals(this.field_72642_r)) {
         this.field_72643_q = this.field_72642_r;
      } else {
         this.field_72642_r = "0.0.0.0";

         try {
            InetAddress var2 = InetAddress.getLocalHost();
            this.field_72643_q = var2.getHostAddress();
         } catch (UnknownHostException var3) {
            this.func_72606_c("Unable to determine local host IP, please set server-ip in '" + var1.func_71329_c() + "' : " + var3.getMessage());
         }
      }

      if (0 == this.field_72636_h) {
         this.field_72636_h = this.field_72637_i;
         this.func_72609_b("Setting default query port to " + this.field_72636_h);
         var1.func_71328_a("query.port", this.field_72636_h);
         var1.func_71328_a("debug", false);
         var1.func_71326_a();
      }

      this.field_72644_p = Maps.newHashMap();
      this.field_72639_u = new RConOutputStream(1460);
      this.field_72641_s = Maps.newHashMap();
      this.field_72640_t = (new Date()).getTime();
   }

   private void func_72620_a(byte[] var1, DatagramPacket var2) throws IOException {
      this.field_72633_m.send(new DatagramPacket(var1, var1.length, var2.getSocketAddress()));
   }

   private boolean func_72621_a(DatagramPacket var1) throws IOException {
      byte[] var2 = var1.getData();
      int var3 = var1.getLength();
      SocketAddress var4 = var1.getSocketAddress();
      this.func_72607_a("Packet len " + var3 + " [" + var4 + "]");
      if (3 <= var3 && -2 == var2[0] && -3 == var2[1]) {
         this.func_72607_a("Packet '" + RConUtils.func_72663_a(var2[2]) + "' [" + var4 + "]");
         switch(var2[2]) {
         case 0:
            if (!this.func_72627_c(var1)) {
               this.func_72607_a("Invalid challenge [" + var4 + "]");
               return false;
            } else if (15 == var3) {
               this.func_72620_a(this.func_72624_b(var1), var1);
               this.func_72607_a("Rules [" + var4 + "]");
            } else {
               RConOutputStream var5 = new RConOutputStream(1460);
               var5.func_72667_a(0);
               var5.func_72670_a(this.func_72625_a(var1.getSocketAddress()));
               var5.func_72671_a(this.field_72635_k);
               var5.func_72671_a("SMP");
               var5.func_72671_a(this.field_72632_l);
               var5.func_72671_a(Integer.toString(this.func_72603_d()));
               var5.func_72671_a(Integer.toString(this.field_72634_j));
               var5.func_72668_a((short)this.field_72637_i);
               var5.func_72671_a(this.field_72643_q);
               this.func_72620_a(var5.func_72672_a(), var1);
               this.func_72607_a("Status [" + var4 + "]");
            }
         default:
            return true;
         case 9:
            this.func_72622_d(var1);
            this.func_72607_a("Challenge [" + var4 + "]");
            return true;
         }
      } else {
         this.func_72607_a("Invalid packet [" + var4 + "]");
         return false;
      }
   }

   private byte[] func_72624_b(DatagramPacket var1) throws IOException {
      long var2 = Util.func_211177_b();
      if (var2 < this.field_72638_v + 5000L) {
         byte[] var9 = this.field_72639_u.func_72672_a();
         byte[] var10 = this.func_72625_a(var1.getSocketAddress());
         var9[1] = var10[0];
         var9[2] = var10[1];
         var9[3] = var10[2];
         var9[4] = var10[3];
         return var9;
      } else {
         this.field_72638_v = var2;
         this.field_72639_u.func_72669_b();
         this.field_72639_u.func_72667_a(0);
         this.field_72639_u.func_72670_a(this.func_72625_a(var1.getSocketAddress()));
         this.field_72639_u.func_72671_a("splitnum");
         this.field_72639_u.func_72667_a(128);
         this.field_72639_u.func_72667_a(0);
         this.field_72639_u.func_72671_a("hostname");
         this.field_72639_u.func_72671_a(this.field_72635_k);
         this.field_72639_u.func_72671_a("gametype");
         this.field_72639_u.func_72671_a("SMP");
         this.field_72639_u.func_72671_a("game_id");
         this.field_72639_u.func_72671_a("MINECRAFT");
         this.field_72639_u.func_72671_a("version");
         this.field_72639_u.func_72671_a(this.field_72617_b.func_71249_w());
         this.field_72639_u.func_72671_a("plugins");
         this.field_72639_u.func_72671_a(this.field_72617_b.func_71258_A());
         this.field_72639_u.func_72671_a("map");
         this.field_72639_u.func_72671_a(this.field_72632_l);
         this.field_72639_u.func_72671_a("numplayers");
         this.field_72639_u.func_72671_a("" + this.func_72603_d());
         this.field_72639_u.func_72671_a("maxplayers");
         this.field_72639_u.func_72671_a("" + this.field_72634_j);
         this.field_72639_u.func_72671_a("hostport");
         this.field_72639_u.func_72671_a("" + this.field_72637_i);
         this.field_72639_u.func_72671_a("hostip");
         this.field_72639_u.func_72671_a(this.field_72643_q);
         this.field_72639_u.func_72667_a(0);
         this.field_72639_u.func_72667_a(1);
         this.field_72639_u.func_72671_a("player_");
         this.field_72639_u.func_72667_a(0);
         String[] var4 = this.field_72617_b.func_71213_z();
         String[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            this.field_72639_u.func_72671_a(var8);
         }

         this.field_72639_u.func_72667_a(0);
         return this.field_72639_u.func_72672_a();
      }
   }

   private byte[] func_72625_a(SocketAddress var1) {
      return ((RConThreadQuery.Auth)this.field_72641_s.get(var1)).func_72591_c();
   }

   private Boolean func_72627_c(DatagramPacket var1) {
      SocketAddress var2 = var1.getSocketAddress();
      if (!this.field_72641_s.containsKey(var2)) {
         return false;
      } else {
         byte[] var3 = var1.getData();
         return ((RConThreadQuery.Auth)this.field_72641_s.get(var2)).func_72592_a() != RConUtils.func_72664_c(var3, 7, var1.getLength()) ? false : true;
      }
   }

   private void func_72622_d(DatagramPacket var1) throws IOException {
      RConThreadQuery.Auth var2 = new RConThreadQuery.Auth(var1);
      this.field_72641_s.put(var1.getSocketAddress(), var2);
      this.func_72620_a(var2.func_72594_b(), var1);
   }

   private void func_72628_f() {
      if (this.field_72619_a) {
         long var1 = Util.func_211177_b();
         if (var1 >= this.field_72629_g + 30000L) {
            this.field_72629_g = var1;
            Iterator var3 = this.field_72641_s.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               if (((RConThreadQuery.Auth)var4.getValue()).func_72593_a(var1)) {
                  var3.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.func_72609_b("Query running on " + this.field_72642_r + ":" + this.field_72636_h);
      this.field_72629_g = Util.func_211177_b();
      this.field_72631_o = new DatagramPacket(this.field_72630_n, this.field_72630_n.length);

      try {
         while(this.field_72619_a) {
            try {
               this.field_72633_m.receive(this.field_72631_o);
               this.func_72628_f();
               this.func_72621_a(this.field_72631_o);
            } catch (SocketTimeoutException var7) {
               this.func_72628_f();
            } catch (PortUnreachableException var8) {
            } catch (IOException var9) {
               this.func_72623_a(var9);
            }
         }
      } finally {
         this.func_72611_e();
      }

   }

   public void func_72602_a() {
      if (!this.field_72619_a) {
         if (0 < this.field_72636_h && 65535 >= this.field_72636_h) {
            if (this.func_72626_g()) {
               super.func_72602_a();
            }

         } else {
            this.func_72606_c("Invalid query port " + this.field_72636_h + " found in '" + this.field_72617_b.func_71329_c() + "' (queries disabled)");
         }
      }
   }

   private void func_72623_a(Exception var1) {
      if (this.field_72619_a) {
         this.func_72606_c("Unexpected exception, buggy JRE? (" + var1 + ")");
         if (!this.func_72626_g()) {
            this.func_72610_d("Failed to recover from buggy JRE, shutting down!");
            this.field_72619_a = false;
         }

      }
   }

   private boolean func_72626_g() {
      try {
         this.field_72633_m = new DatagramSocket(this.field_72636_h, InetAddress.getByName(this.field_72642_r));
         this.func_72601_a(this.field_72633_m);
         this.field_72633_m.setSoTimeout(500);
         return true;
      } catch (SocketException var2) {
         this.func_72606_c("Unable to initialise query system on " + this.field_72642_r + ":" + this.field_72636_h + " (Socket): " + var2.getMessage());
      } catch (UnknownHostException var3) {
         this.func_72606_c("Unable to initialise query system on " + this.field_72642_r + ":" + this.field_72636_h + " (Unknown Host): " + var3.getMessage());
      } catch (Exception var4) {
         this.func_72606_c("Unable to initialise query system on " + this.field_72642_r + ":" + this.field_72636_h + " (E): " + var4.getMessage());
      }

      return false;
   }

   class Auth {
      private final long field_72598_b = (new Date()).getTime();
      private final int field_72599_c;
      private final byte[] field_72596_d;
      private final byte[] field_72597_e;
      private final String field_72595_f;

      public Auth(DatagramPacket var2) {
         super();
         byte[] var3 = var2.getData();
         this.field_72596_d = new byte[4];
         this.field_72596_d[0] = var3[3];
         this.field_72596_d[1] = var3[4];
         this.field_72596_d[2] = var3[5];
         this.field_72596_d[3] = var3[6];
         this.field_72595_f = new String(this.field_72596_d, StandardCharsets.UTF_8);
         this.field_72599_c = (new Random()).nextInt(16777216);
         this.field_72597_e = String.format("\t%s%d\u0000", this.field_72595_f, this.field_72599_c).getBytes(StandardCharsets.UTF_8);
      }

      public Boolean func_72593_a(long var1) {
         return this.field_72598_b < var1;
      }

      public int func_72592_a() {
         return this.field_72599_c;
      }

      public byte[] func_72594_b() {
         return this.field_72597_e;
      }

      public byte[] func_72591_c() {
         return this.field_72596_d;
      }
   }
}
