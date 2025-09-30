package net.minecraft.server.jsonrpc.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.List;

public class WebSocketToJsonCodec extends MessageToMessageDecoder<TextWebSocketFrame> {
   public WebSocketToJsonCodec() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, TextWebSocketFrame var2, List<Object> var3) {
      JsonElement var4 = JsonParser.parseString(var2.text());
      var3.add(var4);
   }

   // $FF: synthetic method
   protected void decode(final ChannelHandlerContext var1, final Object var2, final List var3) throws Exception {
      this.decode(var1, (TextWebSocketFrame)var2, var3);
   }
}
