package net.gudenau.minecraft.customskins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class UrlDataHandler extends URLStreamHandler {
    private static final Pattern PATTERN_CHARSET = Pattern.compile("charset=([\\s\\S]+?)[;,]");

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        return openConnection(u);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        if(!u.getProtocol().equals("data")){
            throw new IOException("Invalid data URL");
        }

        String payload = u.getFile();
        String[] split = payload.split(",", 2);
        boolean base64 = split[0].contains("base64");
        Charset charset = StandardCharsets.US_ASCII;
        Matcher charsetMatcher = PATTERN_CHARSET.matcher(payload);
        if(charsetMatcher.find()){
            charset = Charset.forName(charsetMatcher.group(1));
        }
        byte[] data = split[1].getBytes(charset);
        if(base64){
            data = Base64.getDecoder().decode(data);
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        // CHEESE IT
        return new HttpURLConnection(u) {
            @Override
            public int getResponseCode(){
                return 200;
            }

            @Override
            public void disconnect(){}

            @Override
            public boolean usingProxy(){
                return false;
            }

            @Override
            public void connect(){}

            @Override
            public InputStream getInputStream(){
                return stream;
            }
        };
    }
}
