package io.netty.handler.codec.socksx.v5;

import java.util.List;

public interface Socks5InitialRequest extends Socks5Message {
   List<Socks5AuthMethod> authMethods();
}
