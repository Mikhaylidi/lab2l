import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexey on 02.06.2017.
 */
public class main {

    private static void download(URI link, HashSet<URI> visited) throws IOException, MimeTypeParseException {
        if(visited.contains(link)||visited.size()>=100){return;}
        visited.add(link);
        System.out.println(link);
        URL url = link.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String ct = conn.getHeaderField("Content-Type");
        MimeType mt = new MimeType(ct);
        String cs = mt.getParameter("charset");

        try(InputStream is = conn.getInputStream();) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while (true) {
                int c = is.read();
                if (c < 0)
                    break;
                bos.write(c);
            }
            String html = bos.toString();
            Pattern p = Pattern.compile("href\\s*=\\s*([^ >]+|\"[^\"]*\"|'[^']*')");
            Matcher m = p.matcher(html);

            while (m.find()){
                String href = m.group(1);
                if(href.startsWith("\"")||href.startsWith("\'")){
                    href = href.substring(1,href.length()-1);
                }
                //переход от относительной ссылки к абсолютной
                URI child = link.resolve(href.trim());
                //System.out.println(child);
                download(child, visited);
            }

            conn.disconnect();
        }
    }

    public static void main(String[] args) throws URISyntaxException, IOException, MimeTypeParseException {
        HashSet<URI> hs = new HashSet<>();
        URI link = new URI("https://www.mirea.ru/");
        download(link, hs);
    }


}
