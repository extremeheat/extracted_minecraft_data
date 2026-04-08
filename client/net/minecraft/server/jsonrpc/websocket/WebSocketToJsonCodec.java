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

   protected void decode(final ChannelHandlerContext ctx, final TextWebSocketFrame msg, final List<Object> out) {
      JsonElement json = JsonParser.parseString(msg.text());
      out.add(json);
   }
}
