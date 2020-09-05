import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CodeOutputStream extends FilterOutputStream {

    private BlockCipher bc;
    public boolean start = false;

    public CodeOutputStream(OutputStream os, byte[] k){
        super(os);
        bc = new BlockCipher(k);
    }

    @Override
    public void write(int c) throws IOException {
        byte p = (byte)c;
        if(start)
            super.write(bc.CFBcode(p));
        else 
            super.write(p);
    }

}
