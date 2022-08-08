package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public abstract class Request<T extends Request<T>> {
   protected HttpURLConnection connection;
   private boolean connected;
   protected String url;
   private static final int DEFAULT_READ_TIMEOUT = 60000;
   private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

   public Request(String var1, int var2, int var3) {
      super();

      try {
         this.url = var1;
         Proxy var4 = RealmsClientConfig.getProxy();
         if (var4 != null) {
            this.connection = (HttpURLConnection)(new URL(var1)).openConnection(var4);
         } else {
            this.connection = (HttpURLConnection)(new URL(var1)).openConnection();
         }

         this.connection.setConnectTimeout(var2);
         this.connection.setReadTimeout(var3);
      } catch (MalformedURLException var5) {
         throw new RealmsHttpException(var5.getMessage(), var5);
      } catch (IOException var6) {
         throw new RealmsHttpException(var6.getMessage(), var6);
      }
   }

   public void cookie(String var1, String var2) {
      cookie(this.connection, var1, var2);
   }

   public static void cookie(HttpURLConnection var0, String var1, String var2) {
      String var3 = var0.getRequestProperty("Cookie");
      if (var3 == null) {
         var0.setRequestProperty("Cookie", var1 + "=" + var2);
      } else {
         var0.setRequestProperty("Cookie", var3 + ";" + var1 + "=" + var2);
      }

   }

   public T header(String var1, String var2) {
      this.connection.addRequestProperty(var1, var2);
      return this;
   }

   public int getRetryAfterHeader() {
      return getRetryAfterHeader(this.connection);
   }

   public static int getRetryAfterHeader(HttpURLConnection var0) {
      String var1 = var0.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(var1);
      } catch (Exception var3) {
         return 5;
      }
   }

   public int responseCode() {
      try {
         this.connect();
         return this.connection.getResponseCode();
      } catch (Exception var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   public String text() {
      try {
         this.connect();
         String var1;
         if (this.responseCode() >= 400) {
            var1 = this.read(this.connection.getErrorStream());
         } else {
            var1 = this.read(this.connection.getInputStream());
         }

         this.dispose();
         return var1;
      } catch (IOException var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   private String read(@Nullable InputStream var1) throws IOException {
      if (var1 == null) {
         return "";
      } else {
         InputStreamReader var2 = new InputStreamReader(var1, StandardCharsets.UTF_8);
         StringBuilder var3 = new StringBuilder();

         for(int var4 = var2.read(); var4 != -1; var4 = var2.read()) {
            var3.append((char)var4);
         }

         return var3.toString();
      }
   }

   private void dispose() {
      byte[] var1 = new byte[1024];

      try {
         InputStream var2 = this.connection.getInputStream();

         while(var2.read(var1) > 0) {
         }

         var2.close();
         return;
      } catch (Exception var9) {
         try {
            InputStream var3 = this.connection.getErrorStream();
            if (var3 != null) {
               while(var3.read(var1) > 0) {
               }

               var3.close();
               return;
            }
         } catch (IOException var8) {
            return;
         }
      } finally {
         if (this.connection != null) {
            this.connection.disconnect();
         }

      }

   }

   protected T connect() {
      if (this.connected) {
         return this;
      } else {
         Request var1 = this.doConnect();
         this.connected = true;
         return var1;
      }
   }

   protected abstract T doConnect();

   public static Request<?> get(String var0) {
      return new Get(var0, 5000, 60000);
   }

   public static Request<?> get(String var0, int var1, int var2) {
      return new Get(var0, var1, var2);
   }

   public static Request<?> post(String var0, String var1) {
      return new Post(var0, var1, 5000, 60000);
   }

   public static Request<?> post(String var0, String var1, int var2, int var3) {
      return new Post(var0, var1, var2, var3);
   }

   public static Request<?> delete(String var0) {
      return new Delete(var0, 5000, 60000);
   }

   public static Request<?> put(String var0, String var1) {
      return new Put(var0, var1, 5000, 60000);
   }

   public static Request<?> put(String var0, String var1, int var2, int var3) {
      return new Put(var0, var1, var2, var3);
   }

   public String getHeader(String var1) {
      return getHeader(this.connection, var1);
   }

   public static String getHeader(HttpURLConnection var0, String var1) {
      try {
         return var0.getHeaderField(var1);
      } catch (Exception var3) {
         return "";
      }
   }

   public static class Get extends Request<Get> {
      public Get(String var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public Get doConnect() {
         try {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("GET");
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }

   public static class Post extends Request<Post> {
      private final String content;

      public Post(String var1, String var2, int var3, int var4) {
         super(var1, var3, var4);
         this.content = var2;
      }

      public Post doConnect() {
         try {
            if (this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            OutputStream var1 = this.connection.getOutputStream();
            OutputStreamWriter var2 = new OutputStreamWriter(var1, "UTF-8");
            var2.write(this.content);
            var2.close();
            var1.flush();
            return this;
         } catch (Exception var3) {
            throw new RealmsHttpException(var3.getMessage(), var3);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }

   public static class Delete extends Request<Delete> {
      public Delete(String var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public Delete doConnect() {
         try {
            this.connection.setDoOutput(true);
            this.connection.setRequestMethod("DELETE");
            this.connection.connect();
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }

   public static class Put extends Request<Put> {
      private final String content;

      public Put(String var1, String var2, int var3, int var4) {
         super(var1, var3, var4);
         this.content = var2;
      }

      public Put doConnect() {
         try {
            if (this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoOutput(true);
            this.connection.setDoInput(true);
            this.connection.setRequestMethod("PUT");
            OutputStream var1 = this.connection.getOutputStream();
            OutputStreamWriter var2 = new OutputStreamWriter(var1, "UTF-8");
            var2.write(this.content);
            var2.close();
            var1.flush();
            return this;
         } catch (Exception var3) {
            throw new RealmsHttpException(var3.getMessage(), var3);
         }
      }

      // $FF: synthetic method
      public Request doConnect() {
         return this.doConnect();
      }
   }
}
