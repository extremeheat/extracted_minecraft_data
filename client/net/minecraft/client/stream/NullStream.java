package net.minecraft.client.stream;

import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public class NullStream implements IStream {
   private final Throwable field_152938_a;

   public NullStream(Throwable var1) {
      super();
      this.field_152938_a = var1;
   }

   public void func_152923_i() {
   }

   public void func_152935_j() {
   }

   public void func_152922_k() {
   }

   public boolean func_152936_l() {
      return false;
   }

   public boolean func_152924_m() {
      return false;
   }

   public boolean func_152934_n() {
      return false;
   }

   public void func_152911_a(Metadata var1, long var2) {
   }

   public void func_176026_a(Metadata var1, long var2, long var4) {
   }

   public boolean func_152919_o() {
      return false;
   }

   public void func_152931_p() {
   }

   public void func_152916_q() {
   }

   public void func_152933_r() {
   }

   public void func_152915_s() {
   }

   public void func_152930_t() {
   }

   public void func_152914_u() {
   }

   public IngestServer[] func_152925_v() {
      return new IngestServer[0];
   }

   public void func_152909_x() {
   }

   public IngestServerTester func_152932_y() {
      return null;
   }

   public boolean func_152908_z() {
      return false;
   }

   public int func_152920_A() {
      return 0;
   }

   public boolean func_152927_B() {
      return false;
   }

   public String func_152921_C() {
      return null;
   }

   public ChatUserInfo func_152926_a(String var1) {
      return null;
   }

   public void func_152917_b(String var1) {
   }

   public boolean func_152928_D() {
      return false;
   }

   public ErrorCode func_152912_E() {
      return null;
   }

   public boolean func_152913_F() {
      return false;
   }

   public void func_152910_a(boolean var1) {
   }

   public boolean func_152929_G() {
      return false;
   }

   public IStream.AuthFailureReason func_152918_H() {
      return IStream.AuthFailureReason.ERROR;
   }

   public Throwable func_152937_a() {
      return this.field_152938_a;
   }
}
