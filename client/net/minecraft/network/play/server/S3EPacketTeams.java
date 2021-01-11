package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class S3EPacketTeams implements Packet<INetHandlerPlayClient> {
   private String field_149320_a = "";
   private String field_149318_b = "";
   private String field_149319_c = "";
   private String field_149316_d = "";
   private String field_179816_e;
   private int field_179815_f;
   private Collection<String> field_149317_e;
   private int field_149314_f;
   private int field_149315_g;

   public S3EPacketTeams() {
      super();
      this.field_179816_e = Team.EnumVisible.ALWAYS.field_178830_e;
      this.field_179815_f = -1;
      this.field_149317_e = Lists.newArrayList();
   }

   public S3EPacketTeams(ScorePlayerTeam var1, int var2) {
      super();
      this.field_179816_e = Team.EnumVisible.ALWAYS.field_178830_e;
      this.field_179815_f = -1;
      this.field_149317_e = Lists.newArrayList();
      this.field_149320_a = var1.func_96661_b();
      this.field_149314_f = var2;
      if (var2 == 0 || var2 == 2) {
         this.field_149318_b = var1.func_96669_c();
         this.field_149319_c = var1.func_96668_e();
         this.field_149316_d = var1.func_96663_f();
         this.field_149315_g = var1.func_98299_i();
         this.field_179816_e = var1.func_178770_i().field_178830_e;
         this.field_179815_f = var1.func_178775_l().func_175746_b();
      }

      if (var2 == 0) {
         this.field_149317_e.addAll(var1.func_96670_d());
      }

   }

   public S3EPacketTeams(ScorePlayerTeam var1, Collection<String> var2, int var3) {
      super();
      this.field_179816_e = Team.EnumVisible.ALWAYS.field_178830_e;
      this.field_179815_f = -1;
      this.field_149317_e = Lists.newArrayList();
      if (var3 != 3 && var3 != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (var2 != null && !var2.isEmpty()) {
         this.field_149314_f = var3;
         this.field_149320_a = var1.func_96661_b();
         this.field_149317_e.addAll(var2);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149320_a = var1.func_150789_c(16);
      this.field_149314_f = var1.readByte();
      if (this.field_149314_f == 0 || this.field_149314_f == 2) {
         this.field_149318_b = var1.func_150789_c(32);
         this.field_149319_c = var1.func_150789_c(16);
         this.field_149316_d = var1.func_150789_c(16);
         this.field_149315_g = var1.readByte();
         this.field_179816_e = var1.func_150789_c(32);
         this.field_179815_f = var1.readByte();
      }

      if (this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4) {
         int var2 = var1.func_150792_a();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.field_149317_e.add(var1.func_150789_c(40));
         }
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149320_a);
      var1.writeByte(this.field_149314_f);
      if (this.field_149314_f == 0 || this.field_149314_f == 2) {
         var1.func_180714_a(this.field_149318_b);
         var1.func_180714_a(this.field_149319_c);
         var1.func_180714_a(this.field_149316_d);
         var1.writeByte(this.field_149315_g);
         var1.func_180714_a(this.field_179816_e);
         var1.writeByte(this.field_179815_f);
      }

      if (this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4) {
         var1.func_150787_b(this.field_149317_e.size());
         Iterator var2 = this.field_149317_e.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.func_180714_a(var3);
         }
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147247_a(this);
   }

   public String func_149312_c() {
      return this.field_149320_a;
   }

   public String func_149306_d() {
      return this.field_149318_b;
   }

   public String func_149311_e() {
      return this.field_149319_c;
   }

   public String func_149309_f() {
      return this.field_149316_d;
   }

   public Collection<String> func_149310_g() {
      return this.field_149317_e;
   }

   public int func_149307_h() {
      return this.field_149314_f;
   }

   public int func_149308_i() {
      return this.field_149315_g;
   }

   public int func_179813_h() {
      return this.field_179815_f;
   }

   public String func_179814_i() {
      return this.field_179816_e;
   }
}
