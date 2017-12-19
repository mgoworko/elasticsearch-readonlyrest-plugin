/*
 *    This file is part of ReadonlyREST.
 *
 *    ReadonlyREST is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    ReadonlyREST is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with ReadonlyREST.  If not, see http://www.gnu.org/licenses/
 */

package tech.beshu.ror.es;

/**
 * Created by sscarduzio on 28/11/2016.
 */

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.env.Environment;
import org.elasticsearch.http.netty.NettyHttpServerTransport;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.ssl.SslContext;
import tech.beshu.ror.commons.SSLCertParser;
import tech.beshu.ror.commons.settings.BasicSettings;
import tech.beshu.ror.commons.settings.RawSettings;
import tech.beshu.ror.commons.shims.es.LoggerShim;
import tech.beshu.ror.commons.utils.TempFile;

import java.io.File;
import java.util.Optional;

public class SSLTransport extends NettyHttpServerTransport {

  private final BasicSettings sslSettings;
  private final LoggerShim loggerShim;

  @Inject
  public SSLTransport(Settings settings, NetworkService networkService, BigArrays bigArrays) {
    super(settings, networkService, bigArrays);
    loggerShim = ESContextImpl.mkLoggerShim(logger);
    Environment env = new Environment(settings);
    BasicSettings baseSettings = BasicSettings.fromFile(loggerShim, env.configFile().toAbsolutePath(), settings.getAsStructuredMap());
    this.sslSettings = baseSettings;
    if (sslSettings.isSSLEnabled()) {
      logger.info("creating SSL transport");
    }
  }

  @Override
  public ChannelPipelineFactory configureServerChannelPipelineFactory() {
    return new HttpSslChannelPipelineFactory(this);
  }

  private class HttpSslChannelPipelineFactory extends HttpChannelPipelineFactory {

    private Optional<SslContext> context = Optional.empty();

    public HttpSslChannelPipelineFactory(NettyHttpServerTransport transport) {
      super(transport, true);
      new SSLCertParser(sslSettings, ESContextImpl.mkLoggerShim(logger), (certChain, privateKey) -> {
        try {
          File chainFile = TempFile.newFile("fullchain", "pem", certChain);
          File privatekeyFile = TempFile.newFile("privkey", "pem", privateKey);

          if (sslSettings.getAllowedSSLProtocols().isPresent() ||sslSettings.getAllowedSSLCiphers().isPresent()) {
            logger.error("ROR SSL: setting accepted protocols or ciphers not available for ES < 6.0!");
//            List<String> protocols = basicSettings.getAllowedSSLProtocols().get();
//            sslcb.protocols(basicSettings.getAllowedSSLProtocols().get().toArray(new String[protocols.size()]));
//            logger.info("ROR SSL accepted protocols: " + Joiner.on(",").join(protocols));
          }

          // #TODO expose configuration of sslPrivKeyPem password? Letsencrypt never sets one..
          context = Optional.of(SslContext.newServerContext(chainFile, privatekeyFile, null));

        } catch (Exception e) {
          context = Optional.empty();
          logger.error("Failed to load SSL CertChain & private key from Keystore!");
          e.printStackTrace();
        }
      });

    }

    public ChannelPipeline getPipeline() throws Exception {
      ChannelPipeline pipeline = super.getPipeline();
      if (sslSettings.isSSLEnabled()) {
        SslContext sslCtx = context.get();
        pipeline.addFirst("ssl_netty3_handler", sslCtx.newHandler());
      }
      return pipeline;
    }
  }
}
