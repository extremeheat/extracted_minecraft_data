package net.minecraft.server.jsonrpc.websocket;

import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.List;

public class JsonToWebSocketEncoder extends MessageToMessageEncoder<JsonElement> {
   public JsonToWebSocketEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, JsonElement var2, List<Object> var3) {
      var3.add(new TextWebSocketFrame(var2.toString()));
   }

   // $FF: synthetic method
   protected void encode(final ChannelHandlerContext var1, final Object var2, final List var3) throws Exception {
      this.encode(var1, (JsonElement)var2, var3);
   }
}
