package io.netty.handler.codec.http2;

public interface Http2SettingsFrame extends Http2Frame {
   Http2Settings settings();

   String name();
}
