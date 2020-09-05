import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CodeInputStream extends FilterInputStream {
    private BlockCipher bc;
    public boolean start = false;

    public CodeInputStream(InputStream is, byte[] k){
        super(is);
        bc = new BlockCipher(k);
    }

    @Override
    public int read() throws IOException {
        byte c = (byte)(super.read());
        if(start)
            return bc.CFBdecode(c);
        else 
            return c;
    }

}
