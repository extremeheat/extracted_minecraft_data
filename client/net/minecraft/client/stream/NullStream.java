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

   @Override
   public void func_152923_i() {
   }

   @Override
   public void func_152935_j() {
   }

   @Override
   public void func_152922_k() {
   }

   @Override
   public boolean func_152936_l() {
      return false;
   }

   @Override
   public boolean func_152924_m() {
      return false;
   }

   @Override
   public boolean func_152934_n() {
      return false;
   }

   @Override
   public void func_152911_a(Metadata var1, long var2) {
   }

   @Override
   public boolean func_152919_o() {
      return false;
   }

   @Override
   public void func_152931_p() {
   }

   @Override
   public void func_152916_q() {
   }

   @Override
   public void func_152933_r() {
   }

   @Override
   public void func_152915_s() {
   }

   @Override
   public void func_152930_t() {
   }

   @Override
   public void func_152914_u() {
   }

   @Override
   public IngestServer[] func_152925_v() {
      return new IngestServer[0];
   }

   @Override
   public void func_152909_x() {
   }

   @Override
   public IngestServerTester func_152932_y() {
      return null;
   }

   @Override
   public boolean func_152908_z() {
      return false;
   }

   @Override
   public int func_152920_A() {
      return 0;
   }

   @Override
   public boolean func_152927_B() {
      return false;
   }

   @Override
   public String func_152921_C() {
      return null;
   }

   @Override
   public ChatUserInfo func_152926_a(String var1) {
      return null;
   }

   @Override
   public void func_152917_b(String var1) {
   }

   @Override
   public boolean func_152928_D() {
      return false;
   }

   @Override
   public ErrorCode func_152912_E() {
      return null;
   }

   @Override
   public boolean func_152913_F() {
      return false;
   }

   @Override
   public void func_152910_a(boolean var1) {
   }

   @Override
   public boolean func_152929_G() {
      return false;
   }

   @Override
   public IStream$AuthFailureReason func_152918_H() {
      return IStream$AuthFailureReason.ERROR;
   }

   public Throwable func_152937_a() {
      return this.field_152938_a;
   }
}
